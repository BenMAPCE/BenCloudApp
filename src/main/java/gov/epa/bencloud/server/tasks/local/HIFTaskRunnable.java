package gov.epa.bencloud.server.tasks.local;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jooq.Record3;
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
import gov.epa.bencloud.server.database.jooq.data.tables.records.AirQualityCellRecord;
import gov.epa.bencloud.server.database.jooq.data.tables.records.GetPopulationRecord;
import gov.epa.bencloud.server.database.jooq.data.tables.records.HealthImpactFunctionRecord;
import gov.epa.bencloud.server.database.jooq.data.tables.records.HifResultRecord;
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

			TaskQueue.updateTaskPercentage(taskUuid, 1, "Loading air quality data");
			Map<Long, AirQualityCellRecord> baseline = AirQualityApi.getAirQualityLayerMap(hifTaskConfig.aqBaselineId);
			Map<Long, AirQualityCellRecord> scenario = AirQualityApi.getAirQualityLayerMap(hifTaskConfig.aqScenarioId);
			
			ArrayList<Expression> hifFunctionExpressionList = new ArrayList<Expression>();
			ArrayList<Expression> hifBaselineExpressionList = new ArrayList<Expression>();
			ArrayList<HealthImpactFunctionRecord> hifDefinitionList = new ArrayList<HealthImpactFunctionRecord>();
			
			// incidenceLists contains an array of incidence maps for each HIF
			ArrayList<Map<Long, Map<Integer, Double>>> incidenceLists = new ArrayList<Map<Long, Map<Integer, Double>>>();
			
			// incidenceCachepMap is used inside addIncidenceEntryGroups to avoid querying for datasets we already have
			Map<String, Integer> incidenceCacheMap = new HashMap<String, Integer>();
			
			ArrayList<double[]> hifBetaDistributionLists = new ArrayList<double[]>();
						
			
			// Inspect each selected HIF and create parallel lists of math expressions and
			// HIF config records
			int idx=0;
			for (HIFConfig hif : hifTaskConfig.hifs) {
				hif.arrayIdx = idx;
				TaskQueue.updateTaskPercentage(taskUuid, 2, "Loading incidence for function " + ++idx + " of " + hifTaskConfig.hifs.size());
				
				TaskWorker.updateTaskWorkerHeartbeat(taskWorkerUuid);

				Expression[] e = HIFUtil.getFunctionAndBaselineExpression(hif.hifId);
				hifFunctionExpressionList.add(e[0]);
				hifBaselineExpressionList.add(e[1]);
				
				HealthImpactFunctionRecord h = HIFUtil.getFunctionDefinition(hif.hifId);
				hifDefinitionList.add(h);

				// Override hif config where user has not provided a value
				updateHifConfigValues(hif, h);
				
				//This will get the incidence (or prevalence) dataset for the year specified in the hif config
				boolean ret = IncidenceApi.addIncidenceEntryGroups(hifTaskConfig, hif, h, incidenceLists, incidenceCacheMap);
				
				double[] distSamples = getDistributionSamples(h);
				double[] distBeta = new double[20];
				
				int idxMedian = 0 + distSamples.length / distBeta.length / 2; //the median of the first segment
				for(int i=0; i < distBeta.length; i++) {
					// Grab the median from each of the 20 slices of distSamples
					distBeta[i] = (distSamples[idxMedian]+distSamples[idxMedian-1])/2.0;
					idxMedian += distSamples.length / distBeta.length;
				}
				
				hifBetaDistributionLists.add(distBeta);
			}
			
			TaskQueue.updateTaskPercentage(taskUuid, 3, "Loading population data");
			TaskWorker.updateTaskWorkerHeartbeat(taskWorkerUuid);
			
			// For each HIF, keep track of which age groups (and what percentage) apply
			// Hashmap key is the population age range and the value is what percent of that range's population applies to the HIF
			ArrayList<HashMap<Integer, Double>> hifPopAgeRangeMapping = getPopAgeRangeMapping(hifTaskConfig, hifDefinitionList);
			
			// Load the population dataset
			Map<Long, Result<GetPopulationRecord>> populationMap = PopulationApi.getPopulationEntryGroups(hifDefinitionList, hifTaskConfig);

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
			
			//ArrayList<HifResultRecord> hifResults = new ArrayList<HifResultRecord>();
			Vector<HifResultRecord> hifResults = new Vector<HifResultRecord>();
			
			mXparser.setToOverrideBuiltinTokens();
			mXparser.disableUlpRounding();
			

			/*
			 * FOR EACH CELL IN THE BASELINE AIR QUALITY SURFACE
			 */
			for (Entry<Long, AirQualityCellRecord> baselineEntry : baseline.entrySet()) {
				// updating task percentage
				int currentPct = Math.round(currentCell * 100 / totalCells);
				currentCell++;

				if (prevPct != currentPct) {
					TaskQueue.updateTaskPercentage(taskUuid, currentPct, "Running health impact functions");
					TaskWorker.updateTaskWorkerHeartbeat(taskWorkerUuid);
					prevPct = currentPct;
				}

				AirQualityCellRecord baselineCell = baselineEntry.getValue();
				AirQualityCellRecord scenarioCell = scenario.getOrDefault(baselineEntry.getKey(), null);
				if (scenarioCell == null) {
					continue;
				}
				
				
				Result<GetPopulationRecord> populationCell = populationMap.getOrDefault(baselineEntry.getKey(), null);
				if (populationCell == null) {
					continue;
				}

				/*
				 * FOR EACH FUNCTION THE USER SELECTED
				 */
				
				//TODO: Convert this to a parallel loop. Make sure and tell mxparser how many functions we have (setThreadsNumber)
				
				hifTaskConfig.hifs.parallelStream().forEach((hifConfig) -> {
					Expression hifFunctionExpression = hifFunctionExpressionList.get(hifConfig.arrayIdx);
					Expression hifBaselineExpression = hifBaselineExpressionList.get(hifConfig.arrayIdx);
					//HIFConfig hifConfig = hifTaskConfig.hifs.get(hifConfig.arrayIdx);
					HealthImpactFunctionRecord hifDefinition = hifDefinitionList.get(hifConfig.arrayIdx);
					double[] betaDist = hifBetaDistributionLists.get(hifConfig.arrayIdx);
					

					double seasonalScalar = 1.0;
					if(hifDefinition.getMetricStatistic() == 0) { // NONE
						seasonalScalar = hifConfig.totalDays.doubleValue();
					}
					// If we have variable values, grab them for use in the standard deviation calc below. 
					// Else, set to 1 so they won't have any effect.
					//Double varA = hifDefinition.getValA().doubleValue() != 0 ? hifDefinition.getValA().doubleValue() : 1.0;
					//Double varB = hifDefinition.getValB().doubleValue() != 0 ? hifDefinition.getValB().doubleValue() : 1.0;
					//Double varC = hifDefinition.getValC().doubleValue() != 0 ? hifDefinition.getValC().doubleValue() : 1.0;
					
					double beta = hifDefinition.getBeta().doubleValue();

					// BenMAP-CE stores air quality values as floats but performs HIF estimates using doubles.
					// Testing has shown that float to double conversion can cause small changes in values 
					// Normal operation in BenCloud will use all doubles but, during validation with BenMAP results, it may be useful to preserve the legacy behavior
					double baselineValue = hifTaskConfig.preserveLegacyBehavior ? baselineCell.getValue().floatValue() : baselineCell.getValue().doubleValue();
					double scenarioValue = hifTaskConfig.preserveLegacyBehavior ? scenarioCell.getValue().floatValue() : scenarioCell.getValue().doubleValue();
					double deltaQ = baselineValue - scenarioValue;					
					
					hifFunctionExpression.setArgumentValue("DELTAQ",deltaQ);
					hifFunctionExpression.setArgumentValue("Q0", baselineCell.getValue().doubleValue());
					hifFunctionExpression.setArgumentValue("Q1", scenarioCell.getValue().doubleValue());

					hifBaselineExpression.setArgumentValue("DELTAQ",deltaQ);
					hifBaselineExpression.setArgumentValue("Q0", baselineCell.getValue().doubleValue());
					hifBaselineExpression.setArgumentValue("Q1", scenarioCell.getValue().doubleValue());

					HashMap<Integer, Double> popAgeRangeHifMap = hifPopAgeRangeMapping.get(hifConfig.arrayIdx);
					Map<Long, Map<Integer, Double>> incidenceMap = incidenceLists.get(hifConfig.arrayIdx);
					Map<Integer, Double> incidenceCell = incidenceMap.get(baselineCell.getGridCellId());

					/*
					 * ACCUMULATE THE ESTIMATE FOR EACH AGE CATEGORY IN THIS CELL
					 */

					double totalPop = 0.0;
					double hifFunctionEstimate = 0.0;
					double hifBaselineEstimate = 0.0;
					double[] resultPercentiles = new double[20];

					for (GetPopulationRecord popCategory : populationCell) {
						// <gridCellId, race, gender, ethnicity, agerange, pop>
						Integer popAgeRange = popCategory.getAgeRangeId();
						
						if (popAgeRangeHifMap.containsKey(popAgeRange)) {
							double rangePop = popCategory.getPopValue().doubleValue() * popAgeRangeHifMap.get(popAgeRange);
							double incidence = incidenceCell == null ? 0.0 : incidenceCell.getOrDefault(popAgeRange, 0.0);
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
					//This can happen if we're running multiple functions but we don't have any
					//of the population ranges that this function wants
					if(totalPop!=0.0) {
						HifResultRecord rec = new HifResultRecord();
						rec.setGridCellId(baselineEntry.getKey());
						rec.setGridCol(baselineCell.getGridCol());
						rec.setGridRow(baselineCell.getGridRow());
						rec.setHifId(hifConfig.hifId);
						rec.setPopulation(new BigDecimal(totalPop));
						rec.setDelta(BigDecimal.valueOf(deltaQ));
						rec.setResult(BigDecimal.valueOf(hifFunctionEstimate));
						rec.setPct_2_5(BigDecimal.valueOf(resultPercentiles[0]));
						rec.setPct_97_5(BigDecimal.valueOf(resultPercentiles[19]));
						
						BigDecimal[] tmp = new BigDecimal[resultPercentiles.length];
						for(int i=0; i < resultPercentiles.length; i++) {
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

						hifResults.add(rec);					}

				});
				
				/*
				for (int hifIdx = 0; hifIdx < hifTaskConfig.hifs.size(); hifIdx++) {
					Expression hifFunctionExpression = hifFunctionExpressionList.get(hifIdx);
					Expression hifBaselineExpression = hifBaselineExpressionList.get(hifIdx);
					HIFConfig hifConfig = hifTaskConfig.hifs.get(hifIdx);
					HealthImpactFunctionRecord hifDefinition = hifDefinitionList.get(hifIdx);
					double[] betaDist = hifBetaDistributionLists.get(hifIdx);
					

					double seasonalScalar = 1.0;
					if(hifDefinition.getMetricStatistic() == 0) { // NONE
						seasonalScalar = hifConfig.totalDays.doubleValue();
					}
					// If we have variable values, grab them for use in the standard deviation calc below. 
					// Else, set to 1 so they won't have any effect.
					//Double varA = hifDefinition.getValA().doubleValue() != 0 ? hifDefinition.getValA().doubleValue() : 1.0;
					//Double varB = hifDefinition.getValB().doubleValue() != 0 ? hifDefinition.getValB().doubleValue() : 1.0;
					//Double varC = hifDefinition.getValC().doubleValue() != 0 ? hifDefinition.getValC().doubleValue() : 1.0;
					
					double beta = hifDefinition.getBeta().doubleValue();

					// BenMAP-CE stores air quality values as floats but performs HIF estimates using doubles.
					// Testing has shown that float to double conversion can cause small changes in values 
					// Normal operation in BenCloud will use all doubles but, during validation with BenMAP results, it may be useful to preserve the legacy behavior
					double baselineValue = hifTaskConfig.preserveLegacyBehavior ? baselineCell.getValue().floatValue() : baselineCell.getValue().doubleValue();
					double scenarioValue = hifTaskConfig.preserveLegacyBehavior ? scenarioCell.getValue().floatValue() : scenarioCell.getValue().doubleValue();
					double deltaQ = baselineValue - scenarioValue;					
					
					hifFunctionExpression.setArgumentValue("DELTAQ",deltaQ);
					hifFunctionExpression.setArgumentValue("Q0", baselineCell.getValue().doubleValue());
					hifFunctionExpression.setArgumentValue("Q1", scenarioCell.getValue().doubleValue());

					hifBaselineExpression.setArgumentValue("DELTAQ",deltaQ);
					hifBaselineExpression.setArgumentValue("Q0", baselineCell.getValue().doubleValue());
					hifBaselineExpression.setArgumentValue("Q1", scenarioCell.getValue().doubleValue());

					HashMap<Integer, Double> popAgeRangeHifMap = hifPopAgeRangeMapping.get(hifIdx);
					Map<Long, Map<Integer, Double>> incidenceMap = incidenceLists.get(hifIdx);
					Map<Integer, Double> incidenceCell = incidenceMap.get(baselineCell.getGridCellId());
*/
					/*
					 * ACCUMULATE THE ESTIMATE FOR EACH AGE CATEGORY IN THIS CELL
					 */
/*
					double totalPop = 0.0;
					double hifFunctionEstimate = 0.0;
					double hifBaselineEstimate = 0.0;
					double[] resultPercentiles = new double[20];

					for (GetPopulationRecord popCategory : populationCell) {
						// <gridCellId, race, gender, ethnicity, agerange, pop>
						Integer popAgeRange = popCategory.getAgeRangeId();
						
						if (popAgeRangeHifMap.containsKey(popAgeRange)) {
							double rangePop = popCategory.getPopValue().doubleValue() * popAgeRangeHifMap.get(popAgeRange);
							double incidence = incidenceCell == null ? 0.0 : incidenceCell.getOrDefault(popAgeRange, 0.0);
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
					//This can happen if we're running multiple functions but we don't have any
					//of the population ranges that this function wants
					if(totalPop==0) {
						continue;
					}
					
					HifResultRecord rec = new HifResultRecord();
					rec.setGridCellId(baselineEntry.getKey());
					rec.setGridCol(baselineCell.getGridCol());
					rec.setGridRow(baselineCell.getGridRow());
					rec.setHifId(hifConfig.hifId);
					rec.setPopulation(new BigDecimal(totalPop));
					rec.setDelta(BigDecimal.valueOf(deltaQ));
					rec.setResult(BigDecimal.valueOf(hifFunctionEstimate));
					rec.setPct_2_5(BigDecimal.valueOf(resultPercentiles[0]));
					rec.setPct_97_5(BigDecimal.valueOf(resultPercentiles[19]));
					
					BigDecimal[] tmp = new BigDecimal[resultPercentiles.length];
					for(int i=0; i < resultPercentiles.length; i++) {
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
				*/
			}
			
			TaskQueue.updateTaskPercentage(taskUuid, 100, "Saving your results");
			TaskWorker.updateTaskWorkerHeartbeat(taskWorkerUuid);
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
		if(hif.prevalence == null) {
			hif.prevalence = h.getPrevalenceDatasetId();
		}

		//This is a temporary solution to the fact that user's can't select incidence and 
		//the standard EPA functions don't have incidence assigned in the db
		// If the UI passes the year and incidence hints to the methods that get health impact functions, these should already be set
		if(h.getFunctionText().toLowerCase().contains("incidence")) {
			if(hif.incidence==null) {
				if(h.getEndpointGroupId().equals(12)) {
					hif.incidence = 1; //Mortality Incidence
					hif.incidenceYear = 2020;
				} else {
					hif.incidence = 12; //Other Incidence
					hif.incidenceYear = 2014;
				}
			}			
		} else if (h.getFunctionText().toLowerCase().contains("prevalence")) {
			if(hif.prevalence==null) {
					hif.prevalence = 19; //Prevalence
					hif.prevalenceYear = 2008;
			}				
		}

		if(hif.variable == null) {
			hif.variable = h.getVariableDatasetId();
		}
		if(hif.startDay == null) {
			if(h.getStartDay() == null) {
				hif.startDay = 1;
			} else {
				hif.startDay = h.getStartDay();
			}
		}
		if(hif.endDay == null) {
			if(h.getEndDay() == null) {
				hif.endDay = 365;
			} else {
				hif.endDay = h.getEndDay();
			}
		}
		
		if(hif.startDay > hif.endDay) {
			hif.totalDays = 365 - (hif.startDay - hif.endDay) + 1;
		} else {
			hif.totalDays = hif.endDay - hif.startDay + 1;
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
			JsonNode aqLayers = params.get("air_quality_data");

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
			
			hifTaskConfig.preserveLegacyBehavior = params.has("preserveLegacyBehavior") ? params.get("preserveLegacyBehavior").asBoolean(false) : false;
			
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
			hifConfig.startAge = function.has("start_age") ? function.get("start_age").asInt() : null;
			hifConfig.endAge = function.has("end_age") ? function.get("end_age").asInt() : null;
			hifConfig.race = function.has("race_id") ? function.get("race_id").asInt() : null;
			hifConfig.ethnicity = function.has("ethnicity_id") ? function.get("ethnicity_id").asInt() : null;
			hifConfig.gender = function.has("gender_id") ? function.get("gender_id").asInt() : null;
			hifConfig.incidence = function.has("incidence_dataset_id") ? function.get("incidence_dataset_id").asInt() : null;
			hifConfig.incidenceYear = function.has("incidence_year") ? function.get("incidence_year").asInt() : null;
			hifConfig.prevalence = function.has("prevalence_dataset_id") ? function.get("prevalence_dataset_id").asInt() : null;
			hifConfig.prevalenceYear = function.has("prevalence_year") ? function.get("prevalence_year").asInt() : null;
			hifConfig.variable = function.has("variable") ? function.get("variable").asInt() : null;
			hifTaskConfig.hifs.add(hifConfig);
		}
	}
	
	private double[] getDistributionSamples(HealthImpactFunctionRecord h) {
		//TODO: At the moment, all HIFs are normal distribution. Need to build this out to support other types.
		double[] samples = new double[10000];
		
		RealDistribution distribution = new NormalDistribution(h.getBeta().doubleValue(), h.getP1Beta().doubleValue());
		
		Random rng = new Random(1);
		for (int i = 0; i < samples.length; i++)
		{
			double x = distribution.inverseCumulativeProbability(rng.nextDouble());
			samples[i]=x;
		}
		
		Arrays.sort(samples);
		return samples;
	}

}
