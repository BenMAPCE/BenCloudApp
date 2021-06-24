package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.Tables.*;

import java.math.BigDecimal;

import org.jooq.DSLContext;
import org.jooq.JSONFormat;
import org.jooq.Result;
import org.jooq.JSONFormat.RecordFormat;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record11;
import org.jooq.Record14;
import org.jooq.impl.DSL;

import gov.epa.bencloud.api.model.HIFTaskConfig;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.tables.records.HifResultDatasetRecord;
import spark.Request;
import spark.Response;

public class HIFApi {
	
	public static Object getHifResultDetails(Request request, Response response) {
		String uuid = request.params("uuid");
		BigDecimal tmpZero = new BigDecimal(0);
		
		DSLContext create = DSL.using(JooqUtil.getJooqConfiguration());

		Record1<Integer> id = create.select(HIF_RESULT_DATASET.ID).from(HIF_RESULT_DATASET)
				.where(HIF_RESULT_DATASET.TASK_UUID.eq(uuid)).fetchOne();

		Result<Record14<Integer, Integer, String, String, Integer, Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> hifRecords = create.select(
				HIF_RESULT.GRID_COL.as("column"),
				HIF_RESULT.GRID_ROW.as("row"),
				ENDPOINT.NAME.as("endpoint"),
				HEALTH_IMPACT_FUNCTION.AUTHOR,
				HIF_RESULT_FUNCTION_CONFIG.START_AGE,
				HIF_RESULT_FUNCTION_CONFIG.END_AGE,
				HIF_RESULT.RESULT.as("point_estimate"),
				HIF_RESULT.DELTA,
				HIF_RESULT.STANDARD_DEV.as("standard_deviation"),
				HIF_RESULT.POPULATION,
				HIF_RESULT.BASELINE,
				DSL.when(HIF_RESULT.BASELINE.eq(tmpZero), tmpZero).otherwise(HIF_RESULT.RESULT.div(HIF_RESULT.BASELINE)).as("percent_of_baseline"),
				HIF_RESULT.PCT2_5,
				HIF_RESULT.PCT97_5
				)
				.from(HIF_RESULT)
				.join(HIF_RESULT_FUNCTION_CONFIG).on(HIF_RESULT_FUNCTION_CONFIG.HIF_RESULT_DATASET_ID.eq(HIF_RESULT.HIF_RESULT_DATASET_ID).and(HIF_RESULT_FUNCTION_CONFIG.HIF_ID.eq(HIF_RESULT.HIF_ID)))
				.join(HEALTH_IMPACT_FUNCTION).on(HEALTH_IMPACT_FUNCTION.ID.eq(HIF_RESULT.HIF_ID))
				.join(ENDPOINT).on(ENDPOINT.ID.eq(HEALTH_IMPACT_FUNCTION.ENDPOINT_ID))
				.where(HIF_RESULT.HIF_RESULT_DATASET_ID.eq(id.value1()))
				.orderBy(HIF_RESULT.GRID_COL, HIF_RESULT.GRID_ROW).fetch();

		if (request.headers("Accept").equalsIgnoreCase("text/csv")) {
			response.type("text/csv");
			return hifRecords.formatCSV();
		} else {
			response.type("application/json");
			return hifRecords.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
		}
	}

	public static Result<Record10<Long, Integer, Integer, Integer, Integer, Integer, Integer, Integer, BigDecimal, BigDecimal>> getHifResultsForValuation(Integer id) {
		DSLContext create = DSL.using(JooqUtil.getJooqConfiguration());
Result<Record10<Long, Integer, Integer, Integer, Integer, Integer, Integer, Integer, BigDecimal, BigDecimal>> hifRecords = create.select(
				HIF_RESULT.GRID_CELL_ID,
				HIF_RESULT.GRID_COL,
				HIF_RESULT.GRID_ROW,
				HIF_RESULT.HIF_ID,
				HEALTH_IMPACT_FUNCTION.ENDPOINT_GROUP_ID,
				HEALTH_IMPACT_FUNCTION.ENDPOINT_ID,
				HIF_RESULT_FUNCTION_CONFIG.START_AGE,
				HIF_RESULT_FUNCTION_CONFIG.END_AGE,
				HIF_RESULT.RESULT,
				HIF_RESULT.POPULATION
				)
				.from(HIF_RESULT)
				.join(HIF_RESULT_FUNCTION_CONFIG).on(HIF_RESULT_FUNCTION_CONFIG.HIF_RESULT_DATASET_ID.eq(HIF_RESULT.HIF_RESULT_DATASET_ID).and(HIF_RESULT_FUNCTION_CONFIG.HIF_ID.eq(HIF_RESULT.HIF_ID)))
				.join(HEALTH_IMPACT_FUNCTION).on(HEALTH_IMPACT_FUNCTION.ID.eq(HIF_RESULT.HIF_ID))
				.where(HIF_RESULT.HIF_RESULT_DATASET_ID.eq(id))
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

}
