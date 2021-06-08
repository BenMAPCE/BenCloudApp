package gov.epa.bencloud.api;

import static gov.epa.bencloud.server.database.jooq.Tables.AIR_QUALITY_CELL;
import static gov.epa.bencloud.server.database.jooq.Tables.AIR_QUALITY_LAYER;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.MultipartConfigElement;

import org.jooq.Field;
import org.jooq.InsertValuesStep6;
import org.jooq.InsertValuesStep7;
import org.jooq.JSONFormat;
import org.jooq.Result;
import org.jooq.JSONFormat.RecordFormat;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.jooq.tools.csv.CSVReader;

import gov.epa.bencloud.api.util.AirQualityUtil;
import gov.epa.bencloud.api.util.ApiUtil;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.tables.records.AirQualityCellRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.AirQualityLayerRecord;
import spark.Request;
import spark.Response;

public class AirQualityApi {

	public static Object getAllAirQualityLayerDefinitions(Response response) {
		Result<AirQualityLayerRecord> aqRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.selectFrom(AIR_QUALITY_LAYER)
				.orderBy(AIR_QUALITY_LAYER.NAME)
				.fetch();
		
		response.type("application/json");
		return aqRecords.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
	}
	
	public static Object getAirQualityLayerDefinition(Request request, Response response) {
		AirQualityLayerRecord aqRecord = DSL.using(JooqUtil.getJooqConfiguration())
				.selectFrom(AIR_QUALITY_LAYER)
				.where(AIR_QUALITY_LAYER.ID.eq(Integer.valueOf(request.params("id"))))
				.orderBy(AIR_QUALITY_LAYER.NAME)
				.fetchOne();
		response.type("application/json");
		return aqRecord.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
	}
	
	public static Object getAirQualityLayerDetails(Request request, Response response) {
		Integer id = Integer.valueOf(request.params("id"));
		
		Result<AirQualityCellRecord> aqRecords = DSL.using(JooqUtil.getJooqConfiguration())
				.selectFrom(AIR_QUALITY_CELL)
				.where(AIR_QUALITY_CELL.AIR_QUALITY_LAYER_ID.eq(id))
				.orderBy(AIR_QUALITY_CELL.GRID_COL, AIR_QUALITY_CELL.GRID_ROW)
				.fetch();
		
		if(request.headers("Accept").equalsIgnoreCase("text/csv")) {
			response.type("text/csv");
			return aqRecords.formatCSV();
		} else {
			response.type("application/json");
			return aqRecords.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
		}
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
			
			//TODO: Add logic to determine column positions
			int columnIdx=-999;
			int rowIdx=-999;
			int metricIdx=-999;
			int seasonalMetricIdx=-999;
			int statisticIdx=-999;
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
				case "statistic":
					statisticIdx=i;
					break;
				case "values":
					valuesIdx=i;
					break;
				}
			}
			String tmp = AirQualityUtil.validateModelColumnHeadings(columnIdx, rowIdx, metricIdx, seasonalMetricIdx, statisticIdx, valuesIdx);
			if(tmp.length() > 0) {
				response.status(400);
				return "The following columns are missing: " + tmp;
			}
			//TODO: Validate each record and abort before the batch.execute() if there's a problem.
			//We might also need to clean up the header. Or, maybe we should make this a transaction?
			
			//Create the air_quality_layer record
			aqRecord = DSL.using(JooqUtil.getJooqConfiguration())
			.insertInto(AIR_QUALITY_LAYER, AIR_QUALITY_LAYER.NAME, AIR_QUALITY_LAYER.POLLUTANT_ID, AIR_QUALITY_LAYER.GRID_DEFINITION_ID)
			.values(layerName, pollutantId, gridId)
			.returning(AIR_QUALITY_LAYER.ID, AIR_QUALITY_LAYER.NAME, AIR_QUALITY_LAYER.POLLUTANT_ID, AIR_QUALITY_LAYER.GRID_DEFINITION_ID)
			.fetchOne();
			
			// Read the data rows and write to the db	
			InsertValuesStep7<AirQualityCellRecord, Integer, Integer, Integer, Long, Integer, Integer, BigDecimal> batch = DSL.using(JooqUtil.getJooqConfiguration())
					.insertInto(
							AIR_QUALITY_CELL, 
							AIR_QUALITY_CELL.AIR_QUALITY_LAYER_ID,
							AIR_QUALITY_CELL.GRID_COL, 
							AIR_QUALITY_CELL.GRID_ROW,
							AIR_QUALITY_CELL.GRID_CELL_ID,
							AIR_QUALITY_CELL.METRIC_ID,
							AIR_QUALITY_CELL.SEASONAL_METRIC_ID,
							AIR_QUALITY_CELL.VALUE
							);
			
			while ((record = csvReader.readNext()) != null) {
				batch.values(
						aqRecord.value1(), 
						Integer.valueOf(record[columnIdx]), 
						Integer.valueOf(record[rowIdx]),
						Long.valueOf(ApiUtil.getCellId(Integer.valueOf(record[columnIdx]), Integer.valueOf(record[rowIdx]))),
						Integer.valueOf(11),
						Integer.valueOf(3), 
						BigDecimal.valueOf(Double.valueOf(record[valuesIdx]))
					);
			}
			
		    batch.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.type("application/json");
		return aqRecord.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
	}
}
