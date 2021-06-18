package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.Tables.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;

import gov.epa.bencloud.api.model.HIFConfig;
import gov.epa.bencloud.api.model.HIFTaskConfig;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.tables.records.HealthImpactFunctionRecord;

public class IncidenceApi {

	public static Map<Long, Map<Integer, Double>> getIncidenceEntryGroups(HIFTaskConfig hifTaskConfig, HIFConfig hifConfig, HealthImpactFunctionRecord hifRecord) {

		//Return an average incidence for each population age range for a given hif
		//TODO: Need to add in handling for race, ethnicity, gender
		
		Map<Long, Result<Record4<Long, Short, Short, BigDecimal>>> incRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(
						INCIDENCE_VALUE.GRID_CELL_ID
						,INCIDENCE_ENTRY.START_AGE
						,INCIDENCE_ENTRY.END_AGE
						,DSL.avg(INCIDENCE_VALUE.VALUE))
				.from(INCIDENCE_ENTRY)
				.join(INCIDENCE_VALUE).on(INCIDENCE_VALUE.INCIDENCE_ENTRY_ID.eq(INCIDENCE_ENTRY.ID))
					.where(INCIDENCE_ENTRY.INCIDENCE_DATASET_ID.eq(hifConfig.incidence)
						.and(INCIDENCE_ENTRY.ENDPOINT_ID.eq(hifRecord.getEndpointId()))
						.and(INCIDENCE_ENTRY.END_AGE.greaterOrEqual(hifConfig.startAge.shortValue()))
						.and(INCIDENCE_ENTRY.START_AGE.lessOrEqual(hifConfig.endAge.shortValue()))
						)
				.groupBy(INCIDENCE_VALUE.GRID_CELL_ID
						,INCIDENCE_ENTRY.START_AGE
						,INCIDENCE_ENTRY.END_AGE)
				.fetchGroups(INCIDENCE_VALUE.GRID_CELL_ID);
		
		// Get the age groups for the population dataset
		Result<Record3<Integer, Short, Short>> popAgeRanges = PopulationApi.getPopAgeRanges(hifTaskConfig.popId);

		Map<Long, Map<Integer, Double>> incidenceMap = new HashMap<Long, Map<Integer, Double>>();

		// Build a nested map like <grid_cell_id, <age_group_id, incidence_value>>
		
		// FOR EACH GRID CELL
		for (Entry<Long, Result<Record4<Long, Short, Short, BigDecimal>>> cellIncidence : incRecords.entrySet()) {
			HashMap<Integer, Double> incidenceCellMap = new HashMap<Integer, Double>();

			// FOR EACH POPULATION AGE RANGE
			for (Record3<Integer, Short, Short> popAgeRange : popAgeRanges) {
				
				// FOR EACH INCIDENCE AGE RANGE
				int count=0;
				for (Record4<Long, Short, Short, BigDecimal> incidenceAgeRange : cellIncidence.getValue()) {
					Short popAgeStart = popAgeRange.value2();
					Short popAgeEnd = popAgeRange.value3();
					Short incAgeStart = incidenceAgeRange.value2();
					Short incAgeEnd = incidenceAgeRange.value3();
					//If the full population age range fits within the incidence age range, then apply this incidence rate to the population age range
					if (popAgeStart >= incAgeStart && popAgeEnd <= incAgeEnd) {
						incidenceCellMap.put(popAgeRange.value1(), incidenceCellMap.getOrDefault(popAgeRange.value1(), 0.0) + incidenceAgeRange.value4().doubleValue());
						count++;
					} //TODO: add more intricate checks here to handle overlap with appropriate weighting
					
				}
				//Now calculate the average (if more than one incidence rate was applied to this population age range
				if(count > 0) {
					incidenceCellMap.put(popAgeRange.value1(), incidenceCellMap.getOrDefault(popAgeRange.value1(), 0.0)/count);
				}
			}
			incidenceMap.put(cellIncidence.getKey(), incidenceCellMap);
		}
		return incidenceMap;
	}

}
