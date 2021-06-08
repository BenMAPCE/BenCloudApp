package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.Tables.*;

import org.jooq.DSLContext;
import org.jooq.JSONFormat;
import org.jooq.Result;
import org.jooq.JSONFormat.RecordFormat;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.tables.HealthImpactFunction;
import gov.epa.bencloud.server.database.jooq.tables.records.AirQualityLayerRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.HifResultsRecord;
import spark.Request;
import spark.Response;

public class HIFApi {
	
	public static Object getHifResultDetails(Request request, Response response) {
		String uuid = request.params("uuid");

		DSLContext create = DSL.using(JooqUtil.getJooqConfiguration());

		Record1<Integer> id = create.select(HIF_RESULT_DATASETS.ID).from(HIF_RESULT_DATASETS)
				.where(HIF_RESULT_DATASETS.TASK_UUID.eq(uuid)).fetchOne();

		Result<HifResultsRecord> hifRecords = create.selectFrom(HIF_RESULTS)
				.where(HIF_RESULTS.HIF_RESULT_DATASET_ID.eq(id.value1()))
				.orderBy(HIF_RESULTS.GRID_COL, HIF_RESULTS.GRID_ROW).fetch();

		if (request.headers("Accept").equalsIgnoreCase("text/csv")) {
			response.type("text/csv");
			return hifRecords.formatCSV();
		} else {
			response.type("application/json");
			return hifRecords.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
		}
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

}
