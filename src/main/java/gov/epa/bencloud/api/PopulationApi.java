package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.Tables.POPULATION_ENTRY;

import java.math.BigDecimal;
import java.util.Map;

import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.impl.DSL;
import gov.epa.bencloud.server.database.JooqUtil;

public class PopulationApi {

	public static Map<Long, Result<Record6<Long, Integer, Integer, Integer, Integer, BigDecimal>>> getPopulationEntryGroups(Integer id, Integer popYear) {

		//TODO: Need to grow the population using the selected popYear
		
		Map<Long, Result<Record6<Long, Integer, Integer, Integer, Integer, BigDecimal>>> popRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(POPULATION_ENTRY.GRID_CELL_ID, 
						POPULATION_ENTRY.RACE_ID,
						POPULATION_ENTRY.GENDER_ID,
						POPULATION_ENTRY.ETHNICITY_ID,
						POPULATION_ENTRY.AGE_RANGE_ID,
						DSL.sum(POPULATION_ENTRY.POP_VALUE))
				.from(POPULATION_ENTRY)
				.where(POPULATION_ENTRY.POP_DATASET_ID.eq(id))
				.groupBy(POPULATION_ENTRY.GRID_CELL_ID,
						POPULATION_ENTRY.RACE_ID,
						POPULATION_ENTRY.GENDER_ID,
						POPULATION_ENTRY.ETHNICITY_ID,
						POPULATION_ENTRY.AGE_RANGE_ID)
				.fetchGroups(POPULATION_ENTRY.GRID_CELL_ID);
		return popRecords;
	}

}
