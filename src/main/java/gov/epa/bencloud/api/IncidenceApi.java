package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.Tables.*;

import java.math.BigDecimal;
import java.util.Map;

import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.impl.DSL;
import gov.epa.bencloud.server.database.JooqUtil;

public class IncidenceApi {

	public static Map<Long, Result<Record8<Long, Integer, Integer, Integer, Integer, Short, Short, BigDecimal>>> getIncidenceEntryGroups(Integer id, Integer endpointId) {
		
		//TODO Maybe we could limit this to only the endpoints and other attributes that we need?
		
		Map<Long, Result<Record8<Long, Integer, Integer, Integer, Integer, Short, Short, BigDecimal>>> incRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(INCIDENCE_VALUE.GRID_CELL_ID,
						INCIDENCE_ENTRY.ENDPOINT_ID,
						INCIDENCE_ENTRY.RACE_ID,
						INCIDENCE_ENTRY.GENDER_ID,
						INCIDENCE_ENTRY.ETHNICITY_ID,
						INCIDENCE_ENTRY.START_AGE,
						INCIDENCE_ENTRY.END_AGE,
						DSL.sum(INCIDENCE_VALUE.VALUE))
				.from(INCIDENCE_ENTRY)
				.join(INCIDENCE_VALUE).on(INCIDENCE_VALUE.INCIDENCE_ENTRY_ID.eq(INCIDENCE_ENTRY.ID))
				.where(INCIDENCE_ENTRY.INCIDENCE_DATASET_ID.eq(id)
						.and(INCIDENCE_ENTRY.ENDPOINT_ID.eq(endpointId)))
				.groupBy(INCIDENCE_VALUE.GRID_CELL_ID,
						INCIDENCE_ENTRY.ENDPOINT_ID,
						INCIDENCE_ENTRY.RACE_ID,
						INCIDENCE_ENTRY.GENDER_ID,
						INCIDENCE_ENTRY.ETHNICITY_ID,
						INCIDENCE_ENTRY.START_AGE,
						INCIDENCE_ENTRY.END_AGE)
				.fetchGroups(INCIDENCE_VALUE.GRID_CELL_ID);
		return incRecords;
	}

}
