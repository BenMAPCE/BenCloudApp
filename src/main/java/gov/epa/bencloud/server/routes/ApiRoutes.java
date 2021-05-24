package gov.epa.bencloud.server.routes;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import javax.servlet.MultipartConfigElement;


import org.jooq.InsertValuesStep6;
import org.jooq.JSONFormat;
import org.jooq.JSONFormat.RecordFormat;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.tools.csv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static gov.epa.bencloud.server.database.jooq.Tables.*;

import freemarker.template.Configuration;
import gov.epa.bencloud.api.util.AirQualityUtil;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.tables.records.AirQualityCellRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.AirQualityLayerRecord;
import spark.Service;

public class ApiRoutes extends RoutesBase {

	private static final Logger log = LoggerFactory.getLogger(ApiRoutes.class);
	private Service service = null;

	public ApiRoutes(Service service, Configuration freeMarkerConfiguration){
		this.service = service;
		addRoutes(freeMarkerConfiguration);
	}

	private void addRoutes(Configuration freeMarkerConfiguration) {

		// GET
		
		service.get("/api/v1/air-quality-data", (request, response) -> {
			Result<AirQualityLayerRecord> aqRecords = DSL.using(JooqUtil.getJooqConfiguration())
					.selectFrom(AIR_QUALITY_LAYER)
					.orderBy(AIR_QUALITY_LAYER.NAME)
					.fetch();
			
			response.type("application/json");
			return aqRecords.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
		});

		service.get("/api/v1/air-quality-data/:id/definition", (request, response) -> {
			AirQualityLayerRecord aqRecord = DSL.using(JooqUtil.getJooqConfiguration())
					.selectFrom(AIR_QUALITY_LAYER)
					.where(AIR_QUALITY_LAYER.ID.eq(Integer.valueOf(request.params("id"))))
					.orderBy(AIR_QUALITY_LAYER.NAME)
					.fetchOne();
			response.type("application/json");
			return aqRecord.formatJSON(new JSONFormat().header(false).recordFormat(RecordFormat.OBJECT));
		});

		service.get("/api/v1/air-quality-data/:id/details", (request, response) -> {
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

		});
		
		// POST

		service.post("/api/v1/air-quality-data", (request, response) -> {

			request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			String layerName = getPostParameterValue(request, "name");
			Integer pollutantId = Integer.valueOf(getPostParameterValue(request, "pollutantId"));
			Integer gridId = Integer.valueOf(getPostParameterValue(request, "gridId"));
			String layerType = getPostParameterValue(request, "type");
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
				InsertValuesStep6<AirQualityCellRecord, Integer, Integer, Integer, Integer, Integer, BigDecimal> batch = DSL.using(JooqUtil.getJooqConfiguration())
						.insertInto(
								AIR_QUALITY_CELL, 
								AIR_QUALITY_CELL.AIR_QUALITY_LAYER_ID,
								AIR_QUALITY_CELL.GRID_COL, 
								AIR_QUALITY_CELL.GRID_ROW,
								AIR_QUALITY_CELL.METRIC_ID,
								AIR_QUALITY_CELL.SEASONAL_METRIC_ID,
								AIR_QUALITY_CELL.VALUE
								);
				
				while ((record = csvReader.readNext()) != null) {
					batch.values(
							aqRecord.value1(), 
							Integer.valueOf(record[columnIdx]), 
							Integer.valueOf(record[rowIdx]),
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
		});

	}

}
