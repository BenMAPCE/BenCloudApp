package gov.epa.bencloud.server.tasks.local;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jooq.Record10;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record2;
import org.jooq.Result;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.mXparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.epa.bencloud.api.HIFApi;
import gov.epa.bencloud.api.model.HIFTaskConfig;
import gov.epa.bencloud.api.model.ValuationConfig;
import gov.epa.bencloud.api.model.ValuationTaskConfig;
import gov.epa.bencloud.api.util.ApiUtil;
import gov.epa.bencloud.api.util.ValuationUtil;
import gov.epa.bencloud.server.database.jooq.tables.records.HealthImpactFunctionRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.ValuationFunctionRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.ValuationResultRecord;
import gov.epa.bencloud.server.tasks.TaskComplete;
import gov.epa.bencloud.server.tasks.TaskQueue;
import gov.epa.bencloud.server.tasks.TaskWorker;
import gov.epa.bencloud.server.tasks.model.Task;

public class ValuationTaskRunnable implements Runnable {

	private String taskUuid;
	private String taskWorkerUuid;

	public ValuationTaskRunnable(String taskUuid, String taskWorkerUuid) {
		this.taskUuid = taskUuid;
		this.taskWorkerUuid = taskWorkerUuid;
	}

	private boolean taskSuccessful = true;
	private String taskCompleteMessage = "Task Complete";

	public void run() {

		Task task = TaskQueue.getTaskFromQueueRecord(taskUuid);

		try {
			ValuationTaskConfig valuationTaskConfig = parseTaskParameters(task);

			List<Expression> valuationFunctionExpressionList = new ArrayList<Expression>();

			List<ValuationFunctionRecord> vfDefinitionList = new ArrayList<ValuationFunctionRecord>();
			ArrayList<double[]> vfBetaDistributionLists = new ArrayList<double[]>();
			
			// Inspect each selected valuation function and create parallel lists of math expressions and HIF config records
			for (ValuationConfig vfConfig : valuationTaskConfig.valuationFunctions) {
				valuationFunctionExpressionList.add(ValuationUtil.getFunctionExpression(vfConfig.vfId));
				ValuationFunctionRecord vfDefinition = ValuationUtil.getFunctionDefinition(vfConfig.vfId);
				vfDefinitionList.add(vfDefinition);
				
				DescriptiveStatistics stats = getDistributionStats(vfDefinition);
				double[] betaDist = new double[100];

				//distList = stats.getSortedValues();
				for(int i=0; i < 100; i++) {
					betaDist[i] = stats.getPercentile(i+1);
				}
				vfBetaDistributionLists.add(betaDist);
			}
			
			HIFTaskConfig hifTaskConfig = HIFApi.getHifTaskConfigFromDb(valuationTaskConfig.hifResultDatasetId);
			Result<Record13<Long, Integer, Integer, Integer, Integer, Integer, Integer, Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal[]>> hifResults = HIFApi.getHifResultsForValuation(valuationTaskConfig.hifResultDatasetId);

			Integer inflationYear = hifTaskConfig.popYear > 2020 ? 2020 : hifTaskConfig.popYear;
			
			Map<String, Double> inflationIndices = ApiUtil.getInflationIndices(4, inflationYear);
			Map<Short, Record2<Short, BigDecimal>> incomeGrowthFactors = ApiUtil.getIncomeGrowthFactors(2, hifTaskConfig.popYear);
			
			//<variableName, <gridCellId, value>>
			Map<String, Map<Long, Double>> variables = ApiUtil.getVariablesForValuation(valuationTaskConfig, vfDefinitionList);
			
			int totalCells = hifResults.size();
			int currentCell = 0;
			int prevPct = -999;

			ArrayList<ValuationResultRecord> valuationResults = new ArrayList<ValuationResultRecord>();
			mXparser.setToOverrideBuiltinTokens();

			/*
			 * FOR EACH ROW IN THE HIF RESULTS
			 */
			for (Record13<Long, Integer, Integer, Integer, Integer, Integer, Integer, Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal[]> hifResult : hifResults) {
				//<	HIF_RESULT.GRID_CELL_ID,HIF_RESULT.GRID_COL,HIF_RESULT.GRID_ROW,HIF_RESULT.HIF_ID,
				//HEALTH_IMPACT_FUNCTION.ENDPOINT_GROUP_ID,HEALTH_IMPACT_FUNCTION.ENDPOINT_ID,
				//HIF_RESULT_FUNCTION_CONFIG.START_AGE,HIF_RESULT_FUNCTION_CONFIG.END_AGE,HIF_RESULT.RESULT,HIF_RESULT.POPULATION>
				// updating task percentage
				int currentPct = Math.round(currentCell * 100 / totalCells);
				currentCell++;

				if (prevPct != currentPct) {
					TaskQueue.updateTaskPercentage(taskUuid, currentPct);
					TaskWorker.updateTaskWorkerHeartbeat(taskWorkerUuid);
					prevPct = currentPct;
				}

				/*
				 * RUN THE APPROPRIATE VALUATION FUNCTION(S) AND CAPTURE RESULTS
				 */
				for (int vfIdx = 0; vfIdx < valuationTaskConfig.valuationFunctions.size(); vfIdx++) {

					ValuationConfig vfConfig = valuationTaskConfig.valuationFunctions.get(vfIdx);
					if (vfConfig.hifId.equals(hifResult.value4())) {
						Record2<Short, BigDecimal> tmp = incomeGrowthFactors.getOrDefault(hifResult.value5().shortValue(), null);
						Double incomeGrowthFactor = tmp == null ? 1.0 : tmp.value2().doubleValue();
						
						Double hifEstimate = hifResult.value9().doubleValue();
						
						Expression valuationFunctionExpression = valuationFunctionExpressionList.get(vfIdx);

						ValuationFunctionRecord vfDefinition = vfDefinitionList.get(vfIdx);
						double[] betaDist = vfBetaDistributionLists.get(vfIdx);

						//If the function uses a variable that was loaded, set the appropriate argument value for this cell
						for(Entry<String, Map<Long, Double>> variable  : variables.entrySet()) {
							if(valuationFunctionExpression.getArgument(variable.getKey()) != null) {
								valuationFunctionExpression.setArgumentValue(variable.getKey(), variable.getValue().getOrDefault(hifResult.value1(), 0.0));		
							}
						}
						valuationFunctionExpression.setArgumentValue("AllGoodsIndex", inflationIndices.get("AllGoodsIndex"));
						valuationFunctionExpression.setArgumentValue("MedicalCostIndex", inflationIndices.get("MedicalCostIndex"));
						valuationFunctionExpression.setArgumentValue("WageIndex", inflationIndices.get("WageIndex"));

						Double valuationFunctionEstimate = valuationFunctionExpression.calculate();
						valuationFunctionEstimate = valuationFunctionEstimate * incomeGrowthFactor * hifEstimate;
						
						DescriptiveStatistics distStats = new DescriptiveStatistics();
						BigDecimal[] hifPercentiles = hifResult.value13();
						
						for(int hifPctIdx=0; hifPctIdx < hifPercentiles.length; hifPctIdx++) {
							for(int betaIdx=0; betaIdx < betaDist.length; betaIdx++) {
								//valuation estimate * hif percentiles * betaDist / hif point estimate * A
								if(vfDefinition.getValA() == null || vfDefinition.getValA().doubleValue() == 0.0) {
									distStats.addValue(valuationFunctionEstimate * hifPercentiles[hifPctIdx].doubleValue() / hifEstimate);
									
								} else {
									distStats.addValue(valuationFunctionEstimate * hifPercentiles[hifPctIdx].doubleValue() * betaDist[betaIdx] / (hifEstimate * vfDefinition.getValA().doubleValue()));			
								}
							}
						}
						
						ValuationResultRecord rec = new ValuationResultRecord();
						rec.setGridCellId(hifResult.value1());
						rec.setGridCol(hifResult.value2());
						rec.setGridRow(hifResult.value3());
						rec.setHifId(vfConfig.hifId);
						rec.setVfId(vfConfig.vfId);

						rec.setResult(BigDecimal.valueOf(valuationFunctionEstimate)); 
						try {
							rec.setPct2_5(BigDecimal.valueOf(distStats.getPercentile(2.5)));
							rec.setPct97_5(BigDecimal.valueOf(distStats.getPercentile(97.5)));
							rec.setStandardDev(BigDecimal.valueOf(distStats.getStandardDeviation()));
							rec.setResultMean(BigDecimal.valueOf(distStats.getMean()));
							rec.setResultVariance(BigDecimal.valueOf(distStats.getVariance()));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						
						valuationResults.add(rec);

					}
				}
			}
			ValuationUtil.storeResults(task, valuationTaskConfig, valuationResults);

			TaskComplete.addTaskToCompleteAndRemoveTaskFromQueue(taskUuid, taskWorkerUuid, taskSuccessful, taskCompleteMessage);

		} catch (Exception e) {
			TaskComplete.addTaskToCompleteAndRemoveTaskFromQueue(taskUuid, taskWorkerUuid, false, "Task Failed");
			e.printStackTrace();
		}
	}

	public ValuationTaskConfig parseTaskParameters(Task task) {

		ValuationTaskConfig valuationTaskConfig = new ValuationTaskConfig();

		try {
			valuationTaskConfig.name = task.getName();
			
			String paramString = task.getParameters();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode params = mapper.readTree(paramString);

			valuationTaskConfig.hifResultDatasetId = params.get("hifResultDatasetId").asInt();
			valuationTaskConfig.variableDatasetId = params.get("variableDatasetId")==null || params.get("variableDatasetId").isEmpty() ? 1 : params.get("variableDatasetId").asInt();

			JsonNode functions = params.get("functions");
			parseFunctions(functions, valuationTaskConfig);
			
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return valuationTaskConfig;
	}

	private void parseFunctions(JsonNode functions, ValuationTaskConfig valuationTaskConfig) {
		for (JsonNode function : functions) {
			ValuationConfig valuationConfig = new ValuationConfig();
			valuationConfig.hifId = function.get("hifId").asInt();
			valuationConfig.vfId = function.get("vfId").asInt();
			valuationTaskConfig.valuationFunctions.add(valuationConfig);
		}
	}
	
	private DescriptiveStatistics getDistributionStats(ValuationFunctionRecord vfRecord) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		double[] samples;
		switch (vfRecord.getDistA().toLowerCase()) {
		case "normal":
			NormalDistribution normalDistribution = new NormalDistribution(vfRecord.getValA().doubleValue(), vfRecord.getP1a().doubleValue());
			samples = normalDistribution.sample(10000);
			break;
		case "weibull":
			WeibullDistribution weibullDistribution = new WeibullDistribution(vfRecord.getP2a().doubleValue(), vfRecord.getP1a().doubleValue());
			samples = weibullDistribution.sample(10000);
			break;
		default:
			return null;
		}

		for( int i = 0; i < samples.length; i++) {
	        stats.addValue(samples[i]);
		}
		return stats;
	}

}
