package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.Tables.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jooq.JSONFormat;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.JSONFormat.RecordFormat;
import org.jooq.impl.DSL;

import gov.epa.bencloud.api.model.HIFConfig;
import gov.epa.bencloud.api.model.HIFTaskConfig;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.Routines;
import gov.epa.bencloud.server.database.jooq.tables.records.GetIncidenceRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.HealthImpactFunctionRecord;
import spark.Response;

public class IncidenceApi {

	public static Map<Long, Map<Integer, Double>> getIncidenceEntryGroups(HIFTaskConfig hifTaskConfig, HIFConfig hifConfig, HealthImpactFunctionRecord hifRecord) {

		//Return an average incidence for each population age range for a given hif
		//TODO: Need to add in handling for race, ethnicity, gender
		
		Map<Long, Result<GetIncidenceRecord>> incRecords = Routines.getIncidence(JooqUtil.getJooqConfiguration(), 
				hifConfig.incidence,
				hifConfig.incidenceYear,
				hifRecord.getEndpointId(), 
				null, 
				null, 
				null, 
				hifConfig.startAge.shortValue(), 
				hifConfig.endAge.shortValue(), 
				null,
				null, 
				null,
				true, 
				AirQualityApi.getAirQualityLayerGridId(hifTaskConfig.aqBaselineId)).intoGroups(GET_INCIDENCE.GRID_CELL_ID);
		
		
		// Get the age groups for the population dataset
		Result<Record3<Integer, Short, Short>> popAgeRanges = PopulationApi.getPopAgeRanges(hifTaskConfig.popId);

		Map<Long, Map<Integer, Double>> incidenceMap = new HashMap<Long, Map<Integer, Double>>();

		// Build a nested map like <grid_cell_id, <age_group_id, incidence_value>>
		
		// FOR EACH GRID CELL
		for (Entry<Long, Result<GetIncidenceRecord>> cellIncidence : incRecords.entrySet()) {
			HashMap<Integer, Double> incidenceCellMap = new HashMap<Integer, Double>();

			// FOR EACH POPULATION AGE RANGE
			for (Record3<Integer, Short, Short> popAgeRange : popAgeRanges) {
				
				// FOR EACH INCIDENCE AGE RANGE
				int count=0;
				for (GetIncidenceRecord incidenceAgeRange : cellIncidence.getValue()) {
					Short popAgeStart = popAgeRange.value2();
					Short popAgeEnd = popAgeRange.value3();
					Short incAgeStart = incidenceAgeRange.getStartAge();
					Short incAgeEnd = incidenceAgeRange.getEndAge();
					//If the full population age range fits within the incidence age range, then apply this incidence rate to the population age range
					if (popAgeStart >= incAgeStart && popAgeEnd <= incAgeEnd) {
						incidenceCellMap.put(popAgeRange.value1(), incidenceCellMap.getOrDefault(popAgeRange.value1(), 0.0) + incidenceAgeRange.getValue().doubleValue());
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
	
	public static Object getAllIncidenceDatasets(Response response) {
		return getAllIncidencePrevalenceDatasets(response, false);
	}

	public static Object getAllPrevalenceDatasets(Response response) {
		return getAllIncidencePrevalenceDatasets(response, true);
	}
	
	public static Object getAllIncidencePrevalenceDatasets(Response response, boolean prevalence) {
		Result<Record4<String, Integer, Integer, Integer[]>> records = DSL.using(JooqUtil.getJooqConfiguration())
				.select(INCIDENCE_DATASET.NAME,
						INCIDENCE_DATASET.ID,
						INCIDENCE_DATASET.GRID_DEFINITION_ID,
						DSL.arrayAggDistinct(INCIDENCE_ENTRY.YEAR).orderBy(INCIDENCE_ENTRY.YEAR).as("years")
						)
				.from(INCIDENCE_DATASET)
				.join(INCIDENCE_ENTRY).on(INCIDENCE_DATASET.ID.eq(INCIDENCE_ENTRY.INCIDENCE_DATASET_ID))
				.where(INCIDENCE_ENTRY.PREVALENCE.eq(prevalence))
				.groupBy(INCIDENCE_DATASET.NAME,
						INCIDENCE_DATASET.ID,
						INCIDENCE_DATASET.GRID_DEFINITION_ID)
				.orderBy(INCIDENCE_DATASET.NAME)
				.fetch();
		
		response.type("application/json");
		return records.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
	}

}
