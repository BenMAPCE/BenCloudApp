package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.Tables.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.impl.DSL;

import gov.epa.bencloud.api.model.HIFConfig;
import gov.epa.bencloud.api.model.HIFTaskConfig;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.tables.records.HealthImpactFunctionRecord;

public class PopulationApi {

	public static Map<Long, Result<Record6<Long, Integer, Integer, Integer, Integer, BigDecimal>>> getPopulationEntryGroups(ArrayList<HealthImpactFunctionRecord> hifDefinitionList, HIFTaskConfig hifTaskConfig) {

		//TODO: Need to grow the population using the selected popYear
		//NOTE: For now, we're focusing on age groups and not dealing with race, gender, ethnicity
		/*
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
		*/
		
		// Get the array of age ranges to include based on the configured hifs
		ArrayList<Integer> ageRangeIds = getAgeRangesForHifs(hifTaskConfig);
		
		Map<Long, Result<Record6<Long, Integer, Integer, Integer, Integer, BigDecimal>>> popRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(POPULATION_ENTRY.GRID_CELL_ID, 
						DSL.val(6).as("RACE_ID"),
						DSL.val(3).as("GENDER_ID"),
						DSL.val(3).as("ETHNICITY_ID"),
						POPULATION_ENTRY.AGE_RANGE_ID,
						DSL.sum(POPULATION_ENTRY.POP_VALUE))
				.from(POPULATION_ENTRY)
				.where(POPULATION_ENTRY.POP_DATASET_ID.eq(hifTaskConfig.popId)
						.and(POPULATION_ENTRY.AGE_RANGE_ID.in(ageRangeIds)))
				.groupBy(POPULATION_ENTRY.GRID_CELL_ID,
						DSL.val(6).as("RACE_ID"),
						DSL.val(3).as("GENDER_ID"),
						DSL.val(3).as("ETHNICITY_ID"),
						POPULATION_ENTRY.AGE_RANGE_ID)
				.fetchGroups(POPULATION_ENTRY.GRID_CELL_ID);
		return popRecords;
	}

	private static ArrayList<Integer> getAgeRangesForHifs(HIFTaskConfig hifTaskConfig) {
		
		int minHifAge = 999;
		int maxHifAge = 0;
		
		for(HIFConfig hif : hifTaskConfig.hifs) {
			minHifAge = hif.startAge < minHifAge ? hif.startAge : minHifAge;
			maxHifAge = hif.endAge > maxHifAge ? hif.endAge : maxHifAge;
		}
		
		Record1<Integer> popConfig = DSL.using(JooqUtil.getJooqConfiguration())
		.select(POPULATION_DATASET.POP_CONFIG_ID)
		.from(POPULATION_DATASET)
		.where(POPULATION_DATASET.ID.eq(hifTaskConfig.popId))
		.fetchOne();
		
		
		Result<Record3<Integer, Short, Short>> popAgeRanges = DSL.using(JooqUtil.getJooqConfiguration())
				.select(AGE_RANGE.ID, AGE_RANGE.START_AGE, AGE_RANGE.END_AGE)
				.from(AGE_RANGE)
				.where(AGE_RANGE.POP_CONFIG_ID.eq(popConfig.value1())
						.and(AGE_RANGE.END_AGE.greaterOrEqual((short) minHifAge))
						.and(AGE_RANGE.START_AGE.lessOrEqual((short) maxHifAge))
						)
				.fetch();
		
		ArrayList<Integer> ageRangeIds = new  ArrayList<Integer>();
		
		for(Record3<Integer, Short, Short> ageRange : popAgeRanges) {
			ageRangeIds.add(ageRange.value1());
		}
		
		return ageRangeIds;
	}

	public static Result<Record3<Integer, Short, Short>> getPopAgeRanges(Integer id) {

		Record1<Integer> popConfig = DSL.using(JooqUtil.getJooqConfiguration())
		.select(POPULATION_DATASET.POP_CONFIG_ID)
		.from(POPULATION_DATASET)
		.where(POPULATION_DATASET.ID.eq(id))
		.fetchOne();
		
		Result<Record3<Integer, Short, Short>> popAgeRanges = DSL.using(JooqUtil.getJooqConfiguration())
				.select(AGE_RANGE.ID, AGE_RANGE.START_AGE, AGE_RANGE.END_AGE)
				.from(AGE_RANGE)
				.where(AGE_RANGE.POP_CONFIG_ID.eq(popConfig.value1()))
				.fetch();
		
		return popAgeRanges;
	}
}
