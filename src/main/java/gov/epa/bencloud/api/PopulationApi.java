package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.Tables.POPULATION_ENTRY;

import java.math.BigDecimal;
import java.util.Map;

import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.tables.records.PopulationEntryRecord;

public class PopulationApi {

	public static Map<Long, Result<Record2<Long, BigDecimal>>> getPopulationEntryGroups(Integer id) {

		Map<Long, Result<Record2<Long, BigDecimal>>> popRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(POPULATION_ENTRY.GRID_CELL_ID, DSL.sum(POPULATION_ENTRY.POP_VALUE))
				.from(POPULATION_ENTRY)
				.where(POPULATION_ENTRY.POP_DATASET_ID.eq(id))
				.groupBy(POPULATION_ENTRY.GRID_CELL_ID)
				.fetchGroups(POPULATION_ENTRY.GRID_CELL_ID);
		return popRecords;
	}

}
