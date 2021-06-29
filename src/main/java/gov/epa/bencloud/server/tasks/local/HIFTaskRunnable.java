package gov.epa.bencloud.server.tasks.local;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Result;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.mXparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.epa.bencloud.api.AirQualityApi;
import gov.epa.bencloud.api.IncidenceApi;
import gov.epa.bencloud.api.PopulationApi;
import gov.epa.bencloud.api.model.HIFConfig;
import gov.epa.bencloud.api.model.HIFTaskConfig;
import gov.epa.bencloud.api.util.HIFUtil;
import gov.epa.bencloud.server.database.jooq.tables.records.AirQualityCellRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.HealthImpactFunctionRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.HifResultRecord;
import gov.epa.bencloud.server.tasks.TaskComplete;
import gov.epa.bencloud.server.tasks.TaskQueue;
import gov.epa.bencloud.server.tasks.TaskWorker;
import gov.epa.bencloud.server.tasks.model.Task;

public class HIFTaskRunnable implements Runnable {

	private String taskUuid;
	private String taskWorkerUuid;

	public HIFTaskRunnable(String taskUuid, String taskWorkerUuid) {
		this.taskUuid = taskUuid;
		this.taskWorkerUuid = taskWorkerUuid;
	}

	private boolean taskSuccessful = true;
	private String taskCompleteMessage = "Task Complete";

	public void run() {

		Task task = TaskQueue.getTaskFromQueueRecord(taskUuid);

		try {
			HIFTaskConfig hifTaskConfig = parseTaskParameters(task);

			ArrayList<Expression> hifFunctionExpressionList = new ArrayList<Expression>();
			ArrayList<Expression> hifBaselineExpressionList = new ArrayList<Expression>();
			ArrayList<HealthImpactFunctionRecord> hifDefinitionList = new ArrayList<HealthImpactFunctionRecord>();
			ArrayList<Map<Long, Map<Integer, Double>>> incidenceLists = new ArrayList<Map<Long, Map<Integer, Double>>>();
			ArrayList<double[]> hifBetaDistributionLists = new ArrayList<double[]>();
			
			TaskQueue.updateTaskPercentage(taskUuid, 1);
			TaskWorker.updateTaskWorkerHeartbeat(taskWorkerUuid);
			
			
			// Inspect each selected HIF and create parallel lists of math expressions and
			// HIF config records
			for (HIFConfig hif : hifTaskConfig.hifs) {
				Expression[] e = HIFUtil.getFunctionAndBaselineExpression(hif.hifId);
				hifFunctionExpressionList.add(e[0]);
				hifBaselineExpressionList.add(e[1]);
				
				HealthImpactFunctionRecord h = HIFUtil.getFunctionDefinition(hif.hifId);
				hifDefinitionList.add(h);

				// Override hif config where user has not provided a value
				updateHifConfigValues(hif, h);
				
				Map<Long, Map<Integer, Double>> incidenceMap = IncidenceApi.getIncidenceEntryGroups(hifTaskConfig, hif, h);
				incidenceLists.add(incidenceMap);
				
				DescriptiveStatistics stats = getDistributionStats(h);
				double[] betaDist = new double[20];
				double idx = 2.5;
				for(int i=0; i < 20; i++) {
					betaDist[i] = stats.getPercentile(idx);
					idx += 5;
				}
				hifBetaDistributionLists.add(betaDist);
			}

			TaskQueue.updateTaskPercentage(taskUuid, 2);
			TaskWorker.updateTaskWorkerHeartbeat(taskWorkerUuid);
			
			// For each HIF, keep track of which age groups (and what percentage) apply
			// Hashmap key is the population age range and the value is what percent of that range's population applies to the HIF
			ArrayList<HashMap<Integer, Double>> hifPopAgeRangeMapping = getPopAgeRangeMapping(hifTaskConfig, hifDefinitionList);
			
			Map<Long, AirQualityCellRecord> baseline = AirQualityApi.getAirQualityLayerMap(hifTaskConfig.aqBaselineId);
			Map<Long, AirQualityCellRecord> scenario = AirQualityApi.getAirQualityLayerMap(hifTaskConfig.aqScenarioId);

			// Load the population dataset
			Map<Long, Result<Record6<Long, Integer, Integer, Integer, Integer, BigDecimal>>> populationMap = PopulationApi.getPopulationEntryGroups(hifDefinitionList, hifTaskConfig);

			// Load data for the selected HIFs
			// Determine the race/gender/ethnicity groups and age ranges needed for the
			// selected HIFs
			// Load incidence, prevalence, and variables
			// For each AQ grid cell
			// For each population category
			// Run each HIF
			// Create list of results for each HIF. Columns include col, row, start age, end
			// age, point estimate, population, delta, mean, baseline, pct

			int totalCells = baseline.size();
			int currentCell = 0;
			int prevPct = -999;
			
			ArrayList<HifResultRecord> hifResults = new ArrayList<HifResultRecord>();
			mXparser.setToOverrideBuiltinTokens();

			/*
			 * FOR EACH CELL IN THE BASELINE AIR QUALITY SURFACE
			 */
			for (Entry<Long, AirQualityCellRecord> baselineEntry : baseline.entrySet()) {
				// updating task percentage
				int currentPct = Math.round(currentCell * 100 / totalCells);
				currentCell++;

				if (prevPct != currentPct) {
					TaskQueue.updateTaskPercentage(taskUuid, currentPct);
					TaskWorker.updateTaskWorkerHeartbeat(taskWorkerUuid);
					prevPct = currentPct;
				}

				AirQualityCellRecord baselineCell = baselineEntry.getValue();
				AirQualityCellRecord scenarioCell = scenario.getOrDefault(baselineEntry.getKey(), null);
				if (scenarioCell == null) {
					continue;
				}
				if (baselineCell.getValue().equals(scenarioCell.getValue())) {
					continue;
				}
				Result<Record6<Long, Integer, Integer, Integer, Integer, BigDecimal>> populationCell = populationMap.getOrDefault(baselineEntry.getKey(), null);
				if (populationCell == null) {
					continue;
				}

				/*
				 * FOR EACH FUNCTION THE USER SELECTED
				 */
				for (int hifIdx = 0; hifIdx < hifTaskConfig.hifs.size(); hifIdx++) {
					// TODO: Loop over all the population cells and sum the results for all the
					// relevant bins
					Expression hifFunctionExpression = hifFunctionExpressionList.get(hifIdx);
					Expression hifBaselineExpression = hifBaselineExpressionList.get(hifIdx);
					HIFConfig hifConfig = hifTaskConfig.hifs.get(hifIdx);
					HealthImpactFunctionRecord hifDefinition = hifDefinitionList.get(hifIdx);
					double[] betaDist = hifBetaDistributionLists.get(hifIdx);
					
					//TODO: kludge to scale ozone results by the number of days between May 1 - Sept 30
					//IF the HIF metric statistic == 0 (None) then it's a daily function and we need to scale by the year or global season
					
					Double seasonalScalar = 1.0;
					if(hifDefinition.getMetricStatistic() == 0) { // NONE
						if(hifDefinition.getPollutantId() == 4) { // Ozone
							seasonalScalar = 153.0;
						} else {
							seasonalScalar = 365.0;
						}
					}
					// If we have variable values, grab them for use in the standard deviation calc below. 
					// Else, set to 1 so they won't have any effect.
					Double varA = hifDefinition.getValA().doubleValue() != 0 ? hifDefinition.getValA().doubleValue() : 1.0;
					Double varB = hifDefinition.getValB().doubleValue() != 0 ? hifDefinition.getValB().doubleValue() : 1.0;
					Double varC = hifDefinition.getValC().doubleValue() != 0 ? hifDefinition.getValC().doubleValue() : 1.0;
					
					hifFunctionExpression.setArgumentValue("DELTAQ",baselineCell.getValue().subtract(scenarioCell.getValue()).doubleValue());
					hifFunctionExpression.setArgumentValue("Q0", baselineCell.getValue().doubleValue());
					hifFunctionExpression.setArgumentValue("Q1", scenarioCell.getValue().doubleValue());

					hifBaselineExpression.setArgumentValue("DELTAQ",baselineCell.getValue().subtract(scenarioCell.getValue()).doubleValue());
					hifBaselineExpression.setArgumentValue("Q0", baselineCell.getValue().doubleValue());
					hifBaselineExpression.setArgumentValue("Q1", scenarioCell.getValue().doubleValue());

					HashMap<Integer, Double> popAgeRangeHifMap = hifPopAgeRangeMapping.get(hifIdx);
					
					Map<Long, Map<Integer, Double>> incidenceMap = incidenceLists.get(hifIdx);
					Map<Integer, Double> incidenceCell = incidenceMap.get(baselineCell.getGridCellId());

					/*
					 * ACCUMULATE THE ESTIMATE FOR EACH AGE CATEGORY IN THIS CELL
					 */
					Double beta = hifDefinition.getBeta().doubleValue();
					Double p1Beta = hifDefinition.getP1Beta().doubleValue();
					Double deltaQ = baselineCell.getValue().subtract(scenarioCell.getValue()).doubleValue();
					
					Double totalPop = 0.0;
					Double hifFunctionEstimate = 0.0;
					Double hifBaselineEstimate = 0.0;
					double[] resultPercentiles = new double[20];
					
					for (Record6<Long, Integer, Integer, Integer, Integer, BigDecimal> popCategory : populationCell) {
						// <gridCellId, race, gender, ethnicity, agerange, pop>
						Integer popAgeRange = popCategory.value5();
						
						if (popAgeRangeHifMap.containsKey(popAgeRange)) {
							Double rangePop = popCategory.value6().doubleValue() * popAgeRangeHifMap.get(popAgeRange);
							Double incidence = incidenceCell.getOrDefault(popAgeRange, 0.0);
							totalPop += rangePop;

							hifFunctionExpression.setArgumentValue("BETA", beta);
							hifFunctionExpression.setArgumentValue("INCIDENCE", incidence);
							hifFunctionExpression.setArgumentValue("POPULATION", rangePop);
							hifFunctionEstimate += hifFunctionExpression.calculate() * seasonalScalar;
							
							for(int i=0; i < resultPercentiles.length; i++) {
								hifFunctionExpression.setArgumentValue("BETA", betaDist[i]);								
								resultPercentiles[i] += hifFunctionExpression.calculate() * seasonalScalar;
							}
							
							hifBaselineExpression.setArgumentValue("INCIDENCE", incidence);
							hifBaselineExpression.setArgumentValue("POPULATION", rangePop);
							hifBaselineEstimate += hifBaselineExpression.calculate() * seasonalScalar;
						}
					}
					
					HifResultRecord rec = new HifResultRecord();
					rec.setGridCellId(baselineEntry.getKey());
					rec.setGridCol(baselineCell.getGridCol());
					rec.setGridRow(baselineCell.getGridRow());
					rec.setHifId(hifConfig.hifId);
					rec.setPopulation(new BigDecimal(totalPop));
					rec.setDelta(BigDecimal.valueOf(deltaQ));
					rec.setResult(BigDecimal.valueOf(hifFunctionEstimate));
					rec.setPct2_5(BigDecimal.valueOf(resultPercentiles[0]));
					rec.setPct97_5(BigDecimal.valueOf(resultPercentiles[19]));
					
					BigDecimal[] tmp = new BigDecimal[resultPercentiles.length];
					for(int i=0; i<resultPercentiles.length; i++) {
						tmp[i] = BigDecimal.valueOf(resultPercentiles[i]);
					}
					rec.setPercentiles(tmp);
					
					DescriptiveStatistics stats = new DescriptiveStatistics();
					for( int i = 0; i < resultPercentiles.length; i++) {
				        stats.addValue(resultPercentiles[i]);
					}
					rec.setStandardDev(BigDecimal.valueOf(stats.getStandardDeviation()));
					rec.setResultMean(BigDecimal.valueOf(stats.getMean()));
					rec.setResultVariance(BigDecimal.valueOf(stats.getVariance()));
					rec.setBaseline(BigDecimal.valueOf(hifBaselineEstimate));
					hifResults.add(rec);
				}
			}
			HIFUtil.storeResults(task, hifTaskConfig, hifResults);

			TaskComplete.addTaskToCompleteAndRemoveTaskFromQueue(taskUuid, taskWorkerUuid, taskSuccessful, taskCompleteMessage);

		} catch (Exception e) {
			TaskComplete.addTaskToCompleteAndRemoveTaskFromQueue(taskUuid, taskWorkerUuid, false, "Task Failed");
			e.printStackTrace();
		}
	}

	private void updateHifConfigValues(HIFConfig hif, HealthImpactFunctionRecord h) {
		if(hif.startAge == null) {
			hif.startAge = h.getStartAge();
		}
		if(hif.endAge == null) {
			hif.endAge = h.getEndAge();	
		}
		if(hif.race == null) {
			hif.race = h.getRaceId();
		}
		if(hif.gender == null) {
			hif.gender = h.getGenderId();
		}
		if(hif.ethnicity == null) {
			hif.ethnicity = h.getEthnicityId();
		}
		if(hif.incidence == null) {
			hif.incidence = h.getIncidenceDatasetId();
		}
		//This is a temporary solution to the fact that user's can't select incidence and 
		//the standard EPA functions don't have incidence assigned in the db
		if(hif.incidence==null) {
			if(h.getEndpointGroupId().equals(12)) {
				hif.incidence = 100; //Mortality Incidence (2020)
			} else {
				hif.incidence = 101; //Other Incidence (2014)
			}
		}
		if(hif.prevalence == null) {
			hif.prevalence = h.getPrevalenceDatasetId();
		}
		if(hif.variable == null) {
			hif.variable = h.getVariableDatasetId();
		}
		
	}

	private ArrayList<HashMap<Integer, Double>> getPopAgeRangeMapping(HIFTaskConfig hifTaskConfig, ArrayList<HealthImpactFunctionRecord> hifDefinitionList) {
		ArrayList<HashMap<Integer, Double>> hifPopAgeRangeMapping = new ArrayList<HashMap<Integer, Double>>();
		
		// Get the full list of age ranges for the population
		// for each hif, add a map of the relevant age ranges and percentages
		Result<Record3<Integer, Short, Short>> popAgeRanges = PopulationApi.getPopAgeRanges(hifTaskConfig.popId);
		
		for(HIFConfig hif : hifTaskConfig.hifs) {
			HashMap<Integer, Double> hifPopAgeRanges = new HashMap<Integer, Double>();
			for(Record3<Integer, Short, Short> ageRange : popAgeRanges) {
				Integer ageRangeId = ageRange.value1();
				Short startAge = ageRange.value2();
				Short endAge = ageRange.value3();
				
				if(hif.startAge <= startAge && hif.endAge >= endAge) {
					// population age range is fully contained in the HIF age range
					hifPopAgeRanges.put(ageRangeId, 1.0);
				}
				//TODO: Handle partial overlap here
			}
			hifPopAgeRangeMapping.add(hifPopAgeRanges);
		}
		
		return hifPopAgeRangeMapping;
	}

	public HIFTaskConfig parseTaskParameters(Task task) {

		HIFTaskConfig hifTaskConfig = new HIFTaskConfig();

		try {
			String paramString = task.getParameters();

			ObjectMapper mapper = new ObjectMapper();
			JsonNode params = mapper.readTree(paramString);
			JsonNode aqLayers = params.get("airQualityData");

			hifTaskConfig.name = task.getName();

			for (JsonNode aqLayer : aqLayers) {
				switch (aqLayer.get("type").asText().toLowerCase()) {
				case "baseline":
					hifTaskConfig.aqBaselineId = aqLayer.get("id").asInt();
					break;
				case "scenario":
					hifTaskConfig.aqScenarioId = aqLayer.get("id").asInt();
					break;
				}
			}
			JsonNode popConfig = params.get("population");

			hifTaskConfig.popId = popConfig.get("id").asInt();
			hifTaskConfig.popYear = popConfig.get("year").asInt();
			JsonNode functions = params.get("functions");
			parseFunctions(functions, hifTaskConfig);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hifTaskConfig;
	}

	private void parseFunctions(JsonNode functions, HIFTaskConfig hifTaskConfig) {
		for (JsonNode function : functions) {
			HIFConfig hifConfig = new HIFConfig();
			hifConfig.hifId = function.get("id").asInt();
//			hifConfig.startAge = function.get("startAge").asInt();
//			hifConfig.endAge = function.get("endAge").asInt();
//			hifConfig.race = function.get("race").asInt();
//			hifConfig.ethnicity = function.get("ethnicity").asInt();
//			hifConfig.gender = function.get("gender").asInt();
//			hifConfig.incidence = function.get("incidence").asInt();
//			hifConfig.prevalence = function.get("prevalence").asInt();
//			hifConfig.variable = function.get("variable").asInt();
			hifTaskConfig.hifs.add(hifConfig);
		}
	}
	
	private DescriptiveStatistics getDistributionStats(HealthImpactFunctionRecord h) {
		//TODO: At the moment, all HIFs are normal distribution. Need to build this out to support other types.
		NormalDistribution normalDistribution = new NormalDistribution(h.getBeta().doubleValue(), h.getP1Beta().doubleValue());
		double[] samples = normalDistribution.sample(10000);
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for( int i = 0; i < samples.length; i++) {
	        stats.addValue(samples[i]);
		}
		return stats;
	}

}
