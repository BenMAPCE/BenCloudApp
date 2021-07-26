package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.Tables.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Map;

import org.jooq.InsertValuesStep7;
import org.jooq.InsertValuesStep8;
import org.jooq.JSONFormat;
import org.jooq.Result;
import org.jooq.JSONFormat.RecordFormat;
import org.jooq.exception.DataAccessException;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.impl.DSL;
import org.jooq.tools.csv.CSVReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.epa.bencloud.api.util.AirQualityUtil;
import gov.epa.bencloud.api.util.ApiUtil;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.tables.records.AirQualityCellRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.AirQualityLayerRecord;
import spark.Request;
import spark.Response;

public class AirQualityApi {

	public static Object getAirQualityLayerDefinitions(Request request, Response response) {
		String pollutantParam = request.raw().getParameter("pollutant");
		
		Integer pollutantId = null;
		
		if(pollutantParam != null && !pollutantParam.isEmpty()) {
			pollutantId = Integer.valueOf(pollutantParam);
		}
		
		Result<Record13<Integer, String, Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Boolean, Integer, String, Integer, String>> aqRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(
						AIR_QUALITY_LAYER.ID, 
						AIR_QUALITY_LAYER.NAME,
						AIR_QUALITY_LAYER.CELL_COUNT,
						AIR_QUALITY_LAYER.MIN_VALUE,
						AIR_QUALITY_LAYER.MAX_VALUE,
						AIR_QUALITY_LAYER.MEAN_VALUE,
						AIR_QUALITY_LAYER.PCT_2_5,
						AIR_QUALITY_LAYER.PCT_97_5,
						AIR_QUALITY_LAYER.LOCKED,
						AIR_QUALITY_LAYER.GRID_DEFINITION_ID,
						GRID_DEFINITION.NAME.as("grid_definition_name"),
						AIR_QUALITY_LAYER.POLLUTANT_ID,
						POLLUTANT.NAME.as("pollutant_name")
						)
				.from(AIR_QUALITY_LAYER)
				.join(POLLUTANT).on(POLLUTANT.ID.eq(AIR_QUALITY_LAYER.POLLUTANT_ID))				
				.join(GRID_DEFINITION).on(GRID_DEFINITION.ID.eq(AIR_QUALITY_LAYER.GRID_DEFINITION_ID))
				.where(pollutantId==null ? DSL.noCondition() : AIR_QUALITY_LAYER.POLLUTANT_ID.eq(pollutantId))
				.orderBy(AIR_QUALITY_LAYER.NAME)
				.fetch();
		
		response.type("application/json");
		return aqRecords.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
	}
	
	/**
	 * @param request - expected to contain id param
	 * @param response
	 * @return Single air quality layer definition as json string 
	 */
	public static Object getAirQualityLayerDefinition(Request request, Response response) {
		Integer id = Integer.valueOf(request.params("id"));
		
		Record13<Integer, String, Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Boolean, Integer, Integer, String, String> aqRecord = getAirQualityLayerDefinition(id);
		response.type("application/json");
		return aqRecord.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
	}
	
	private static Record13<Integer, String, Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Boolean, Integer, Integer, String, String> getAirQualityLayerDefinition(Integer id) {
		return DSL.using(JooqUtil.getJooqConfiguration())
		.select(
				AIR_QUALITY_LAYER.ID, 
				AIR_QUALITY_LAYER.NAME,
				AIR_QUALITY_LAYER.CELL_COUNT,
				AIR_QUALITY_LAYER.MIN_VALUE,
				AIR_QUALITY_LAYER.MAX_VALUE,
				AIR_QUALITY_LAYER.MEAN_VALUE,
				AIR_QUALITY_LAYER.PCT_2_5,
				AIR_QUALITY_LAYER.PCT_97_5,
				AIR_QUALITY_LAYER.LOCKED,
				AIR_QUALITY_LAYER.GRID_DEFINITION_ID,
				AIR_QUALITY_LAYER.POLLUTANT_ID,
				POLLUTANT.NAME.as("pollutant_name"), 
				GRID_DEFINITION.NAME.as("grid_definition_name"))
		.from(AIR_QUALITY_LAYER)
		.join(POLLUTANT).on(POLLUTANT.ID.eq(AIR_QUALITY_LAYER.POLLUTANT_ID))				
		.join(GRID_DEFINITION).on(GRID_DEFINITION.ID.eq(AIR_QUALITY_LAYER.GRID_DEFINITION_ID))
		.where(AIR_QUALITY_LAYER.ID.eq(id))
		.fetchOne();
	}

	public static Object getAirQualityLayerDetails(Request request, Response response) {
		Integer id = Integer.valueOf(request.params("id"));
				
		Result<Record6<Integer, Integer, String, String, String, BigDecimal>> aqRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.select(
						AIR_QUALITY_CELL.GRID_COL,
						AIR_QUALITY_CELL.GRID_ROW,
						POLLUTANT_METRIC.NAME.as("metric"),
						SEASONAL_METRIC.NAME.as("seasonal_metric"),
						AIR_QUALITY_CELL.ANNUAL_METRIC,
						AIR_QUALITY_CELL.VALUE
						)
				.from(AIR_QUALITY_CELL)
				.leftJoin(POLLUTANT_METRIC).on(AIR_QUALITY_CELL.METRIC_ID.eq(POLLUTANT_METRIC.ID))
				.leftJoin(SEASONAL_METRIC).on(AIR_QUALITY_CELL.SEASONAL_METRIC_ID.eq(SEASONAL_METRIC.ID))
				.where(AIR_QUALITY_CELL.AIR_QUALITY_LAYER_ID.eq(id))
				.orderBy(AIR_QUALITY_CELL.GRID_COL, AIR_QUALITY_CELL.GRID_ROW)
				.fetch();
		
		Record1<String> layerInfo = DSL.using(JooqUtil.getJooqConfiguration())
				.select(AIR_QUALITY_LAYER.NAME)
				.from(AIR_QUALITY_LAYER)
				.where(AIR_QUALITY_LAYER.ID.eq(id))
				.fetchOne();
		
		
		String fileName = createFilename(layerInfo.value1());
		
		
		if(request.headers("Accept").equalsIgnoreCase("text/csv")) {
			response.type("text/csv");
			response.raw().setHeader("Content-disposition", "attachment; filename="+ fileName);
			return aqRecords.formatCSV();
		} else {
			response.type("application/json");
			return aqRecords.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
		}
	}

	public static String createFilename(String layerName) {
		// Currently allowing periods so we don't break extensions. Need to improve this.
		return layerName.replaceAll("[^A-Za-z0-9._-]+", "") + ".csv";
	}


	public static Map<Long, AirQualityCellRecord> getAirQualityLayerMap(Integer id) {

		Map<Long, AirQualityCellRecord> aqRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.selectFrom(AIR_QUALITY_CELL)
				.where(AIR_QUALITY_CELL.AIR_QUALITY_LAYER_ID.eq(id))
				.orderBy(AIR_QUALITY_CELL.GRID_COL, AIR_QUALITY_CELL.GRID_ROW)
				.fetchMap(AIR_QUALITY_CELL.GRID_CELL_ID);		
		return aqRecords;
	}
	
	public static Object postAirQualityLayer(Request request, String layerName, Integer pollutantId, Integer gridId, String layerType, Response response) {

		AirQualityLayerRecord aqRecord=null;
		
		//Create the air_quality_cell records
		try (InputStream is = request.raw().getPart("file").getInputStream()) {
			
			CSVReader csvReader = new CSVReader (new InputStreamReader(is));
			
			int columnIdx=-999;
			int rowIdx=-999;
			int metricIdx=-999;
			int seasonalMetricIdx=-999;
			int annualMetricIdx=-999;
			int valuesIdx=-999;

			String[] record;
			// Read the header
			record = csvReader.readNext();
			for(int i=0; i < record.length; i++) {
				switch(record[i].toLowerCase().replace(" ", "")) {
				case "column":
					columnIdx=i;
					break;
				case "row":
					rowIdx=i;
					break;
				case "metric":
					metricIdx=i;
					break;
				case "seasonalmetric":
					seasonalMetricIdx=i;
					break;
				case "annualmetric":
					annualMetricIdx=i;
					break;
				case "values":
					valuesIdx=i;
					break;
				}
			}
			String tmp = AirQualityUtil.validateModelColumnHeadings(columnIdx, rowIdx, metricIdx, seasonalMetricIdx, annualMetricIdx, valuesIdx);
			if(tmp.length() > 0) {
				response.status(400);
				return "The following columns are missing: " + tmp;
			}
			
			//TODO: Finish creating lookups for these metrics so we can fill them in properly in the insert below
			//HashMap<String, Integer> pollutantMetricIdLookup = AirQualityUtil.getPollutantMetricIdLookup(pollutantId);
			//HashMap<String, Integer> seasonalMetricIdLookup = AirQualityUtil.getSeasonalMetricIdLookup(pollutantId);
			
			
			//TODO: Validate each record and abort before the batch.execute() if there's a problem.
			//We might also need to clean up the header. Or, maybe we should make this a transaction?
			
			//Create the air_quality_layer record
			aqRecord = DSL.using(JooqUtil.getJooqConfiguration())
			.insertInto(AIR_QUALITY_LAYER, AIR_QUALITY_LAYER.NAME, AIR_QUALITY_LAYER.POLLUTANT_ID, AIR_QUALITY_LAYER.GRID_DEFINITION_ID, AIR_QUALITY_LAYER.LOCKED)
			.values(layerName, pollutantId, gridId, false)
			.returning(AIR_QUALITY_LAYER.ID, AIR_QUALITY_LAYER.NAME, AIR_QUALITY_LAYER.POLLUTANT_ID, AIR_QUALITY_LAYER.GRID_DEFINITION_ID)
			.fetchOne();
			
			// Read the data rows and write to the db	
			InsertValuesStep8<AirQualityCellRecord, Integer, Integer, Integer, Long, Integer, Integer, String, BigDecimal> batch = DSL.using(JooqUtil.getJooqConfiguration())
					.insertInto(
							AIR_QUALITY_CELL, 
							AIR_QUALITY_CELL.AIR_QUALITY_LAYER_ID,
							AIR_QUALITY_CELL.GRID_COL, 
							AIR_QUALITY_CELL.GRID_ROW,
							AIR_QUALITY_CELL.GRID_CELL_ID,
							AIR_QUALITY_CELL.METRIC_ID,
							AIR_QUALITY_CELL.SEASONAL_METRIC_ID,
							AIR_QUALITY_CELL.ANNUAL_METRIC,
							AIR_QUALITY_CELL.VALUE
							);
			
			while ((record = csvReader.readNext()) != null) {
				batch.values(
						aqRecord.value1(), 
						Integer.valueOf(record[columnIdx]), 
						Integer.valueOf(record[rowIdx]),
						Long.valueOf(ApiUtil.getCellId(Integer.valueOf(record[columnIdx]), Integer.valueOf(record[rowIdx]))),
						Integer.valueOf(11), //TODO
						Integer.valueOf(3), //TODO
						record[annualMetricIdx],
						BigDecimal.valueOf(Double.valueOf(record[valuesIdx]))
					);
			}
			
		    batch.execute();
		    
		    
			//Now that the rows are in the database, let's get the cell count, mean, min, max and update the layer's header record
			DSL.using(JooqUtil.getJooqConfiguration())
			.update(AIR_QUALITY_LAYER)
			.set(DSL.row(AIR_QUALITY_LAYER.CELL_COUNT, AIR_QUALITY_LAYER.MIN_VALUE, AIR_QUALITY_LAYER.MAX_VALUE, AIR_QUALITY_LAYER.MEAN_VALUE, AIR_QUALITY_LAYER.PCT_2_5, AIR_QUALITY_LAYER.PCT_97_5),	
    		DSL.select(
				DSL.count().as("cell_count"),
				DSL.min(AIR_QUALITY_CELL.VALUE).as("min_value"),
				DSL.max(AIR_QUALITY_CELL.VALUE).as("max_value"),
				DSL.avg(AIR_QUALITY_CELL.VALUE).as("mean_value"),
				DSL.percentileCont(0.025).withinGroupOrderBy(AIR_QUALITY_CELL.VALUE).as("pct_2_5"),
				DSL.percentileCont(0.975).withinGroupOrderBy(AIR_QUALITY_CELL.VALUE).as("pct_97_5")
    		)
    		.from(AIR_QUALITY_CELL)
			.where(AIR_QUALITY_CELL.AIR_QUALITY_LAYER_ID.eq(aqRecord.value1()))
			)
			.where(AIR_QUALITY_LAYER.ID.eq(aqRecord.value1()))
			.execute();
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.type("application/json");
		return getAirQualityLayerDefinition(aqRecord.value1()).formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
	}
	
	public static ObjectNode getAirQualityLayers(String userIdentifier) {

//		System.out.println("getCompletedTasks");
//		System.out.println("userIdentifier: " + userIdentifier);
		
//		System.out.println("length: " + postParameters.get("length")[0]);
//		System.out.println("start: " + postParameters.get("start")[0]);
//		System.out.println("searchValue: " + postParameters.get("searchValue")[0]);
//		System.out.println("sortColumn: " + postParameters.get("sortColumn")[0]);
//		System.out.println("sortDirection: " + postParameters.get("sortDirection")[0]);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        
        ArrayNode airQualityLayers = mapper.createArrayNode();
        ObjectNode airQualityLayer = mapper.createObjectNode();

        int records = 0;
        
		if (null != userIdentifier) {

			try {

				Result<Record12<Integer, String, Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, Integer, Integer, String, String>> aqRecords = DSL.using(JooqUtil.getJooqConfiguration())
						.select(
								AIR_QUALITY_LAYER.ID, 
								AIR_QUALITY_LAYER.NAME,
								AIR_QUALITY_LAYER.CELL_COUNT,
								AIR_QUALITY_LAYER.MIN_VALUE,
								AIR_QUALITY_LAYER.MAX_VALUE,
								AIR_QUALITY_LAYER.MEAN_VALUE,
								AIR_QUALITY_LAYER.PCT_2_5,
								AIR_QUALITY_LAYER.PCT_97_5,
								AIR_QUALITY_LAYER.GRID_DEFINITION_ID,
								AIR_QUALITY_LAYER.POLLUTANT_ID,
								POLLUTANT.NAME, 
								GRID_DEFINITION.NAME)
						.from(AIR_QUALITY_LAYER)
						.join(POLLUTANT).on(POLLUTANT.ID.eq(AIR_QUALITY_LAYER.POLLUTANT_ID))				
						.join(GRID_DEFINITION).on(GRID_DEFINITION.ID.eq(AIR_QUALITY_LAYER.GRID_DEFINITION_ID))
						.orderBy(AIR_QUALITY_LAYER.NAME)
						.fetch();

				for (Record record : aqRecords) {

					airQualityLayer = mapper.createObjectNode();

					airQualityLayer.put("id", record.getValue(AIR_QUALITY_LAYER.ID));
					airQualityLayer.put("name", record.getValue(AIR_QUALITY_LAYER.NAME));
					airQualityLayer.put("pollutant_name", record.getValue(POLLUTANT.NAME));
					airQualityLayer.put("grid_definition_name", record.getValue(GRID_DEFINITION.NAME));
					
					airQualityLayers.add(airQualityLayer);
					records++;
					
				}
				
				data.set("data", airQualityLayers);
				data.put("success", true);
				data.put("recordsFiltered", records);
				data.put("recordsTotal", records);
				
			} catch (DataAccessException e) {
				data.put("success", false);
				data.put("error_message", e.getMessage());
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				data.put("success", false);
				data.put("error_message", e.getMessage());
				e.printStackTrace();
			}
		}

		return data;
	} 

}
