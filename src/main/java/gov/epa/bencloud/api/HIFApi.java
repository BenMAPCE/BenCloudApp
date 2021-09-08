package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.data.Tables.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.JSONFormat;
import org.jooq.Result;
import org.jooq.exception.IOException;
import org.jooq.JSONFormat.RecordFormat;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record16;
import org.jooq.Record18;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record7;
import org.jooq.impl.DSL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.epa.bencloud.api.model.HIFTaskConfig;
import gov.epa.bencloud.api.util.AirQualityUtil;
import gov.epa.bencloud.api.util.HIFUtil;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.data.tables.records.HifResultDatasetRecord;
import gov.epa.bencloud.server.database.jooq.data.tables.records.TaskCompleteRecord;
import gov.epa.bencloud.server.tasks.TaskComplete;
import gov.epa.bencloud.server.util.ApplicationUtil;
import gov.epa.bencloud.server.util.ParameterUtil;
import spark.Request;
import spark.Response;

public class HIFApi {
	
	public static void getHifResultDetails(Request request, Response response) {
		String uuid = request.params("uuid");
		BigDecimal tmpZero = new BigDecimal(0);
		
		DSLContext create = DSL.using(JooqUtil.getJooqConfiguration());

		Record1<Integer> id = create.select(HIF_RESULT_DATASET.ID).from(HIF_RESULT_DATASET)
				.where(HIF_RESULT_DATASET.TASK_UUID.eq(uuid)).fetchOne();

		Result<Record18<Integer, Integer, String, String, Integer, String, Integer, Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> hifRecords = create.select(
				HIF_RESULT.GRID_COL.as("column"),
				HIF_RESULT.GRID_ROW.as("row"),
				ENDPOINT.NAME.as("endpoint"),
				HEALTH_IMPACT_FUNCTION.AUTHOR,
				HEALTH_IMPACT_FUNCTION.FUNCTION_YEAR.as("year"),
				HEALTH_IMPACT_FUNCTION.LOCATION,
				HIF_RESULT_FUNCTION_CONFIG.START_AGE,
				HIF_RESULT_FUNCTION_CONFIG.END_AGE,
				HIF_RESULT.RESULT.as("point_estimate"),
				HIF_RESULT.POPULATION,
				HIF_RESULT.DELTA,
				HIF_RESULT.RESULT_MEAN.as("mean"),
				HIF_RESULT.BASELINE,
				DSL.when(HIF_RESULT.BASELINE.eq(tmpZero), tmpZero).otherwise(HIF_RESULT.RESULT_MEAN.div(HIF_RESULT.BASELINE)).as("percent_of_baseline"),
				HIF_RESULT.STANDARD_DEV.as("standard_deviation"),
				HIF_RESULT.RESULT_VARIANCE.as("variance"),
				HIF_RESULT.PCT_2_5,
				HIF_RESULT.PCT_97_5
				)
				.from(HIF_RESULT)
				.join(HIF_RESULT_FUNCTION_CONFIG).on(HIF_RESULT_FUNCTION_CONFIG.HIF_RESULT_DATASET_ID.eq(HIF_RESULT.HIF_RESULT_DATASET_ID).and(HIF_RESULT_FUNCTION_CONFIG.HIF_ID.eq(HIF_RESULT.HIF_ID)))
				.join(HEALTH_IMPACT_FUNCTION).on(HEALTH_IMPACT_FUNCTION.ID.eq(HIF_RESULT.HIF_ID))
				.join(ENDPOINT).on(ENDPOINT.ID.eq(HEALTH_IMPACT_FUNCTION.ENDPOINT_ID))
				.where(HIF_RESULT.HIF_RESULT_DATASET_ID.eq(id.value1()))
				.orderBy(HIF_RESULT.GRID_COL, HIF_RESULT.GRID_ROW).fetch();

		if (request.headers("Accept").equalsIgnoreCase("text/csv")) {
			response.type("text/csv");
			String taskFileName = ApplicationUtil.replaceNonValidCharacters(TaskComplete.getTaskFromCompleteRecord(uuid).getName()) + ".csv";
			response.header("Content-Disposition", "attachment; filename=" + taskFileName);
			response.header("Access-Control-Expose-Headers", "Content-Disposition");
			try {
				hifRecords.formatCSV(response.raw().getWriter());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (java.io.IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			response.type("application/json");
			try {
				hifRecords.formatJSON(response.raw().getWriter(), new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (java.io.IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static Result<Record7<Long, Integer, Integer, Integer, Integer, BigDecimal, BigDecimal[]>> getHifResultsForValuation(Integer id, Integer hifId) {
		DSLContext create = DSL.using(JooqUtil.getJooqConfiguration());
		Result<Record7<Long, Integer, Integer, Integer, Integer, BigDecimal, BigDecimal[]>> hifRecords = create.select(
				HIF_RESULT.GRID_CELL_ID,
				HIF_RESULT.GRID_COL,
				HIF_RESULT.GRID_ROW,
				HIF_RESULT.HIF_ID,
				HEALTH_IMPACT_FUNCTION.ENDPOINT_GROUP_ID,
				HIF_RESULT.RESULT,
				HIF_RESULT.PERCENTILES
				)
				.from(HIF_RESULT)
				.join(HIF_RESULT_FUNCTION_CONFIG).on(HIF_RESULT_FUNCTION_CONFIG.HIF_RESULT_DATASET_ID.eq(HIF_RESULT.HIF_RESULT_DATASET_ID).and(HIF_RESULT_FUNCTION_CONFIG.HIF_ID.eq(HIF_RESULT.HIF_ID)))
				.join(HEALTH_IMPACT_FUNCTION).on(HEALTH_IMPACT_FUNCTION.ID.eq(HIF_RESULT.HIF_ID))
				.where(HIF_RESULT.HIF_RESULT_DATASET_ID.eq(id)
						.and(HIF_RESULT.HIF_ID.eq(hifId)))
				.orderBy(HIF_RESULT.GRID_COL, HIF_RESULT.GRID_ROW).fetch();

		return hifRecords;

	}
	
	public static Object getAllHealthImpactFunctions(Request request, Response response) {
		Result<Record> hifRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(HEALTH_IMPACT_FUNCTION.asterisk()
						, ENDPOINT_GROUP.NAME.as("endpoint_group_name")
						, ENDPOINT.NAME.as("endpoint_name")
						, RACE.NAME.as("race_name")
						, GENDER.NAME.as("gender_name")
						, ETHNICITY.NAME.as("ethnicity_name")
						)
				.from(HEALTH_IMPACT_FUNCTION)
				.join(ENDPOINT_GROUP).on(HEALTH_IMPACT_FUNCTION.ENDPOINT_GROUP_ID.eq(ENDPOINT_GROUP.ID))
				.join(ENDPOINT).on(HEALTH_IMPACT_FUNCTION.ENDPOINT_ID.eq(ENDPOINT.ID))
				.join(RACE).on(HEALTH_IMPACT_FUNCTION.RACE_ID.eq(RACE.ID))
				.join(GENDER).on(HEALTH_IMPACT_FUNCTION.GENDER_ID.eq(GENDER.ID))
				.join(ETHNICITY).on(HEALTH_IMPACT_FUNCTION.ETHNICITY_ID.eq(ETHNICITY.ID))
				.orderBy(ENDPOINT_GROUP.NAME, ENDPOINT.NAME, HEALTH_IMPACT_FUNCTION.AUTHOR)
				.fetch();
		
		response.type("application/json");
		return hifRecords.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
	}

	public static Object getAllHifGroups(Request request, Response response) {
			
		int pollutantId = ParameterUtil.getParameterValueAsInteger(request.raw().getParameter("pollutantId"), 0);
		
		Result<Record4<String, Integer, String, Integer[]>> hifGroupRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(HEALTH_IMPACT_FUNCTION_GROUP.NAME
						, HEALTH_IMPACT_FUNCTION_GROUP.ID
						, HEALTH_IMPACT_FUNCTION_GROUP.HELP_TEXT
						, DSL.arrayAggDistinct(HEALTH_IMPACT_FUNCTION_GROUP_MEMBER.HEALTH_IMPACT_FUNCTION_ID).as("functions")
						)
				.from(HEALTH_IMPACT_FUNCTION_GROUP)
				.join(HEALTH_IMPACT_FUNCTION_GROUP_MEMBER).on(HEALTH_IMPACT_FUNCTION_GROUP.ID.eq(HEALTH_IMPACT_FUNCTION_GROUP_MEMBER.HEALTH_IMPACT_FUNCTION_GROUP_ID))
				.join(HEALTH_IMPACT_FUNCTION).on(HEALTH_IMPACT_FUNCTION.ID.eq(HEALTH_IMPACT_FUNCTION_GROUP_MEMBER.HEALTH_IMPACT_FUNCTION_ID))
				.where(pollutantId == 0 ? DSL.noCondition() : HEALTH_IMPACT_FUNCTION.POLLUTANT_ID.eq(pollutantId))
				.groupBy(HEALTH_IMPACT_FUNCTION_GROUP.NAME
						, HEALTH_IMPACT_FUNCTION_GROUP.ID)
				.orderBy(HEALTH_IMPACT_FUNCTION_GROUP.NAME.desc())
				.fetch();
		
		response.type("application/json");
		return hifGroupRecords.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
	}

	public static Object getSelectedHifGroups(Request request, Response response) {
		
		String idsParam = request.params("ids");
		int popYear = ParameterUtil.getParameterValueAsInteger(request.raw().getParameter("popYear"), 0);
		int defaultIncidencePrevalenceDataset = ParameterUtil.getParameterValueAsInteger(request.raw().getParameter("incidencePrevalenceDataset"), 0);
		int pollutantId = ParameterUtil.getParameterValueAsInteger(request.raw().getParameter("pollutantId"), 0);
		int baselineId = ParameterUtil.getParameterValueAsInteger(request.raw().getParameter("baselineId"), 0);
		int scenarioId = ParameterUtil.getParameterValueAsInteger(request.raw().getParameter("scenarioId"), 0);
		
		List<Integer> ids = Stream.of(idsParam.split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
		
		List<Integer> supportedMetricIds = null; 
		if(baselineId != 0 && scenarioId != 0) {
			supportedMetricIds = AirQualityUtil.getSupportedMetricIds(baselineId, scenarioId);
		}

		
		
		Result<Record> hifGroupRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(HEALTH_IMPACT_FUNCTION_GROUP.NAME
						, HEALTH_IMPACT_FUNCTION_GROUP.ID
						, HEALTH_IMPACT_FUNCTION_GROUP.HELP_TEXT
						, HEALTH_IMPACT_FUNCTION.asterisk()
						, ENDPOINT_GROUP.NAME.as("endpoint_group_name")
						, ENDPOINT.NAME.as("endpoint_name")
						, RACE.NAME.as("race_name")
						, GENDER.NAME.as("gender_name")
						, ETHNICITY.NAME.as("ethnicity_name")
						)
				.from(HEALTH_IMPACT_FUNCTION_GROUP)
				.join(HEALTH_IMPACT_FUNCTION_GROUP_MEMBER).on(HEALTH_IMPACT_FUNCTION_GROUP.ID.eq(HEALTH_IMPACT_FUNCTION_GROUP_MEMBER.HEALTH_IMPACT_FUNCTION_GROUP_ID))
				.join(HEALTH_IMPACT_FUNCTION).on(HEALTH_IMPACT_FUNCTION_GROUP_MEMBER.HEALTH_IMPACT_FUNCTION_ID.eq(HEALTH_IMPACT_FUNCTION.ID))
				.join(ENDPOINT_GROUP).on(HEALTH_IMPACT_FUNCTION.ENDPOINT_GROUP_ID.eq(ENDPOINT_GROUP.ID))
				.join(ENDPOINT).on(HEALTH_IMPACT_FUNCTION.ENDPOINT_ID.eq(ENDPOINT.ID))
				.join(RACE).on(HEALTH_IMPACT_FUNCTION.RACE_ID.eq(RACE.ID))
				.join(GENDER).on(HEALTH_IMPACT_FUNCTION.GENDER_ID.eq(GENDER.ID))
				.join(ETHNICITY).on(HEALTH_IMPACT_FUNCTION.ETHNICITY_ID.eq(ETHNICITY.ID))
				.where(HEALTH_IMPACT_FUNCTION_GROUP.ID.in(ids)
						.and(HEALTH_IMPACT_FUNCTION.POLLUTANT_ID.eq(pollutantId))
						.and(supportedMetricIds == null ? DSL.noCondition() : HEALTH_IMPACT_FUNCTION.METRIC_ID.in(supportedMetricIds)))
				.orderBy(HEALTH_IMPACT_FUNCTION_GROUP.NAME)
				.fetch();
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode groups = mapper.createArrayNode();
		ObjectNode group = null;
		ArrayNode functions = null;
		int currentGroupId = -1;
		
		for(Record r : hifGroupRecords) {
			if(currentGroupId != r.getValue(HEALTH_IMPACT_FUNCTION_GROUP.ID)) {
				currentGroupId = r.getValue(HEALTH_IMPACT_FUNCTION_GROUP.ID);
				group = mapper.createObjectNode();
				group.put("id", currentGroupId);
				group.put("name", r.getValue(HEALTH_IMPACT_FUNCTION_GROUP.NAME));
				group.put("help_text", r.getValue(HEALTH_IMPACT_FUNCTION_GROUP.HELP_TEXT));
				functions = group.putArray("functions");
				groups.add(group);
			}
			
			ObjectNode function = mapper.createObjectNode();
			function.put("id", r.getValue(HEALTH_IMPACT_FUNCTION.ID));
			function.put("health_impact_function_dataset_id",r.getValue(HEALTH_IMPACT_FUNCTION.HEALTH_IMPACT_FUNCTION_DATASET_ID));
			function.put("endpoint_group_id",r.getValue(HEALTH_IMPACT_FUNCTION.ENDPOINT_GROUP_ID));
			function.put("endpoint_id",r.getValue(HEALTH_IMPACT_FUNCTION.ENDPOINT_ID));
			function.put("pollutant_id",r.getValue(HEALTH_IMPACT_FUNCTION.POLLUTANT_ID));
			function.put("metric_id",r.getValue(HEALTH_IMPACT_FUNCTION.METRIC_ID));
			function.put("seasonal_metric_id",r.getValue(HEALTH_IMPACT_FUNCTION.SEASONAL_METRIC_ID));
			function.put("metric_statistic",r.getValue(HEALTH_IMPACT_FUNCTION.METRIC_STATISTIC));
			function.put("author",r.getValue(HEALTH_IMPACT_FUNCTION.AUTHOR));
			function.put("function_year",r.getValue(HEALTH_IMPACT_FUNCTION.FUNCTION_YEAR));
			function.put("location",r.getValue(HEALTH_IMPACT_FUNCTION.LOCATION));
			function.put("other_pollutants",r.getValue(HEALTH_IMPACT_FUNCTION.OTHER_POLLUTANTS));
			function.put("qualifier",r.getValue(HEALTH_IMPACT_FUNCTION.QUALIFIER));
			function.put("reference",r.getValue(HEALTH_IMPACT_FUNCTION.REFERENCE));
			function.put("start_age",r.getValue(HEALTH_IMPACT_FUNCTION.START_AGE));
			function.put("end_age",r.getValue(HEALTH_IMPACT_FUNCTION.END_AGE));
			function.put("function_text",r.getValue(HEALTH_IMPACT_FUNCTION.FUNCTION_TEXT));
			function.put("variable_dataset_id",r.getValue(HEALTH_IMPACT_FUNCTION.VARIABLE_DATASET_ID));
			function.put("beta",r.getValue(HEALTH_IMPACT_FUNCTION.BETA));
			function.put("dist_beta",r.getValue(HEALTH_IMPACT_FUNCTION.DIST_BETA));
			function.put("p1_beta",r.getValue(HEALTH_IMPACT_FUNCTION.P1_BETA));
			function.put("p2_beta",r.getValue(HEALTH_IMPACT_FUNCTION.P2_BETA));
			function.put("val_a",r.getValue(HEALTH_IMPACT_FUNCTION.VAL_A));
			function.put("name_a",r.getValue(HEALTH_IMPACT_FUNCTION.NAME_A));
			function.put("val_b",r.getValue(HEALTH_IMPACT_FUNCTION.VAL_B));
			function.put("name_b",r.getValue(HEALTH_IMPACT_FUNCTION.NAME_B));
			function.put("val_c",r.getValue(HEALTH_IMPACT_FUNCTION.VAL_C));
			function.put("name_c",r.getValue(HEALTH_IMPACT_FUNCTION.NAME_C));
			function.put("baseline_function_text",r.getValue(HEALTH_IMPACT_FUNCTION.BASELINE_FUNCTION_TEXT));
			function.put("race_id",r.getValue(HEALTH_IMPACT_FUNCTION.RACE_ID));
			function.put("gender_id",r.getValue(HEALTH_IMPACT_FUNCTION.GENDER_ID));
			function.put("ethnicity_id",r.getValue(HEALTH_IMPACT_FUNCTION.ETHNICITY_ID));
			function.put("start_day",r.getValue(HEALTH_IMPACT_FUNCTION.START_DAY));
			function.put("end_day",r.getValue(HEALTH_IMPACT_FUNCTION.END_DAY));
			function.put("endpoint_group_name",r.getValue("endpoint_group_name",String.class));
			function.put("endpoint_name",r.getValue("endpoint_name", String.class));
			function.put("race_name",r.getValue("race_name", String.class));
			function.put("gender_name",r.getValue("gender_name", String.class));
			function.put("ethnicity_name",r.getValue("ethnicity_name", String.class));
			
			//This will select the most appropriate incidence/prevalence dataset and year based on user selection and function definition
			HIFUtil.setIncidencePrevalence(function, popYear, defaultIncidencePrevalenceDataset, r.getValue(HEALTH_IMPACT_FUNCTION.INCIDENCE_DATASET_ID), r.getValue(HEALTH_IMPACT_FUNCTION.PREVALENCE_DATASET_ID));
			
			functions.add(function);
			
		}
		
		response.type("application/json");
		return groups;
	}

	
	public static HIFTaskConfig getHifTaskConfigFromDb(Integer hifResultDatasetId) {
		DSLContext create = DSL.using(JooqUtil.getJooqConfiguration());

		HIFTaskConfig hifTaskConfig = new HIFTaskConfig();
		
		HifResultDatasetRecord hifTaskConfigRecord = create
				.selectFrom(HIF_RESULT_DATASET)
				.where(HIF_RESULT_DATASET.ID.eq(hifResultDatasetId))
				.fetchOne();
		
		hifTaskConfig.name = hifTaskConfigRecord.getName();
		hifTaskConfig.popId = hifTaskConfigRecord.getPopulationDatasetId();
		hifTaskConfig.popYear = hifTaskConfigRecord.getPopulationYear();
		hifTaskConfig.aqBaselineId = hifTaskConfigRecord.getBaselineAqLayerId();
		hifTaskConfig.aqScenarioId = hifTaskConfigRecord.getScenarioAqLayerId();
		//TODO: Add code to load the hif details
		
		
		return hifTaskConfig;
	}

	public static Object getHealthImpactFunction(Request request, Response response) {
		String id = request.params("id");
		
		Result<Record> hifRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(HEALTH_IMPACT_FUNCTION.asterisk()
						, ENDPOINT_GROUP.NAME.as("endpoint_group_name")
						, ENDPOINT.NAME.as("endpoint_name")
						, RACE.NAME.as("race_name")
						, GENDER.NAME.as("gender_name")
						, ETHNICITY.NAME.as("ethnicity_name")
						)
				.from(HEALTH_IMPACT_FUNCTION)
				.join(ENDPOINT_GROUP).on(HEALTH_IMPACT_FUNCTION.ENDPOINT_GROUP_ID.eq(ENDPOINT_GROUP.ID))
				.join(ENDPOINT).on(HEALTH_IMPACT_FUNCTION.ENDPOINT_ID.eq(ENDPOINT.ID))
				.join(RACE).on(HEALTH_IMPACT_FUNCTION.RACE_ID.eq(RACE.ID))
				.join(GENDER).on(HEALTH_IMPACT_FUNCTION.GENDER_ID.eq(GENDER.ID))
				.join(ETHNICITY).on(HEALTH_IMPACT_FUNCTION.ETHNICITY_ID.eq(ETHNICITY.ID))
				.where(HEALTH_IMPACT_FUNCTION.ID.eq(Integer.valueOf(id)))
				.orderBy(ENDPOINT_GROUP.NAME, ENDPOINT.NAME, HEALTH_IMPACT_FUNCTION.AUTHOR)
				.fetch();
		
		response.type("application/json");
		return hifRecords.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
	}

	public static String getHIFTaskStatus(String hifTaskUuid) {
		TaskCompleteRecord completedTask = DSL.using(JooqUtil.getJooqConfiguration())
				.select(TASK_COMPLETE.asterisk())
				.from(TASK_COMPLETE)
				.where(TASK_COMPLETE.TASK_UUID.eq(hifTaskUuid))
				.fetchOneInto(TASK_COMPLETE);
		
		if(completedTask == null) {
			return "pending";
		} else if(completedTask.getTaskSuccessful()) {
			return "success";
		}
		// We found the completed tasks, but it was not successful
		return "failed";
	}

	public static Integer getHIFResultDatasetId(String hifTaskUuid) {

		HifResultDatasetRecord hifResultDataset = DSL.using(JooqUtil.getJooqConfiguration())
		.select(HIF_RESULT_DATASET.asterisk())
		.from(HIF_RESULT_DATASET)
		.where(HIF_RESULT_DATASET.TASK_UUID.eq(hifTaskUuid))
		.fetchOneInto(HIF_RESULT_DATASET);
		
		if(hifResultDataset == null) {
			return null;
		}
		return hifResultDataset.getId();
	}

	public static int getHifResultsRecordCount(Integer hifResultDatasetId, ArrayList<Integer> hifIdList) {
		Record1<Integer> hifResultCount = DSL.using(JooqUtil.getJooqConfiguration())
		.select(DSL.count())
		.from(HIF_RESULT)
		.where(HIF_RESULT.HIF_RESULT_DATASET_ID.eq(hifResultDatasetId)
				.and(HIF_RESULT.HIF_ID.in(hifIdList)))
		.fetchOne();
		
		if(hifResultCount == null) {
			return 0;
		}
		return hifResultCount.value1().intValue();
	}

}
