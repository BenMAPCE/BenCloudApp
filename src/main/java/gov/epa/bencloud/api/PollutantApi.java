package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.Tables.*;

import org.jooq.JSONFormat;
import org.jooq.Result;
import org.jooq.JSONFormat.RecordFormat;
import org.jooq.Record;
import org.jooq.impl.DSL;
import gov.epa.bencloud.server.database.JooqUtil;
import spark.Response;

public class PollutantApi {

	public static Object getAllPollutantDefinitions(Response response) {
		Result<Record> aqRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(POLLUTANT.asterisk())
				.from(POLLUTANT)
				.orderBy(POLLUTANT.NAME)
				.fetch();
		
		response.type("application/json");
		return aqRecords.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
	}
}
