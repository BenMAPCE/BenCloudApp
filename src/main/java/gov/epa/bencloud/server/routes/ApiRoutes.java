package gov.epa.bencloud.server.routes;

import static gov.epa.bencloud.server.database.jooq.Tables.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.MultipartConfigElement;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import freemarker.template.Configuration;
import gov.epa.bencloud.api.AirQualityApi;
import gov.epa.bencloud.api.GridDefinitionApi;
import gov.epa.bencloud.api.HIFApi;
import gov.epa.bencloud.api.PollutantApi;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.util.FreeMarkerRenderUtil;
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
		service.get("/api/v1/grid-definitions", (request, response) -> {
			return GridDefinitionApi.getAllGridDefinitions(response);
		});
		
		service.get("/api/v1/pollutants", (request, response) -> {
			return PollutantApi.getAllPollutantDefinitions(response);
		});
		
		service.get("/api/v1/air-quality-data", (request, response) -> {
			return AirQualityApi.getAllAirQualityLayerDefinitions(response);
		});

		service.get("/api/v1/air-quality-data/:id/definition", (request, response) -> {
			return AirQualityApi.getAirQualityLayerDefinition(request, response);
		});

		service.get("/api/v1/air-quality-data/:id/details", (request, response) -> {
			return AirQualityApi.getAirQualityLayerDetails(request, response);
		});

		service.get("/api/v1/health-impact-functions", (request, response) -> {
			return HIFApi.getAllHealthImpactFunctions(request, response);
		});
		
		// POST
		service.post("/api/v1/air-quality-data", (request, response) -> {
			
			request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			String layerName = getPostParameterValue(request, "name");
			Integer pollutantId = Integer.valueOf(getPostParameterValue(request, "pollutantId"));
			Integer gridId = Integer.valueOf(getPostParameterValue(request, "gridId"));
			String layerType = getPostParameterValue(request, "type");
			
			return AirQualityApi.postAirQualityLayer(request, layerName, pollutantId, gridId, layerType, response);
		});

		service.get("/api/load-air-quality-options", (request, response) -> {
			
			ObjectMapper mapper = new ObjectMapper();

			ArrayNode options = mapper.createArrayNode();
			ObjectNode option = mapper.createObjectNode();

			Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration())
					.select(AIR_QUALITY_LAYER.asterisk())
					.from(AIR_QUALITY_LAYER)
					.orderBy(AIR_QUALITY_LAYER.NAME)
					.fetch();
			for (Record r : result) {
				option = mapper.createObjectNode();
				option.put("id", r.getValue(AIR_QUALITY_LAYER.ID));
				option.put("text", 
						r.getValue(AIR_QUALITY_LAYER.NAME)
					);

				options.add(option);
			}
						
			return options;
		});
		

		service.get("/api/load-pollutant-options", (request, response) -> {
			
			ObjectMapper mapper = new ObjectMapper();

			ArrayNode options = mapper.createArrayNode();
			ObjectNode option = mapper.createObjectNode();

			Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration())
					.select(POLLUTANT.asterisk())
					.from(POLLUTANT)
					.orderBy(POLLUTANT.NAME)
					.fetch();
			for (Record r : result) {
				option = mapper.createObjectNode();
				option.put("id", r.getValue(POLLUTANT.ID));
				option.put("text", 
						r.getValue(POLLUTANT.NAME)
					);

				options.add(option);
			}
						
			return options;
			
		});
		
		service.get("/api/load-grid-options", (request, response) -> {
			
			ObjectMapper mapper = new ObjectMapper();

			ArrayNode options = mapper.createArrayNode();
			ObjectNode option = mapper.createObjectNode();

			Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration())
					.select(GRID_DEFINITION.asterisk())
					.from(GRID_DEFINITION)
					.orderBy(GRID_DEFINITION.NAME)
					.fetch();
			for (Record r : result) {
				option = mapper.createObjectNode();
				option.put("id", r.getValue(GRID_DEFINITION.ID));
				option.put("text", 
						r.getValue(GRID_DEFINITION.NAME)
					);

				options.add(option);
			}
						
			return options;
			
		});
		
		service.get("/api/load-population-options", (request, response) -> {
			
			ObjectMapper mapper = new ObjectMapper();

			ArrayNode options = mapper.createArrayNode();
			ObjectNode option = mapper.createObjectNode();

			Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration())
					.select(POPULATION_DATASET.asterisk())
					.from(POPULATION_DATASET)
					.orderBy(POPULATION_DATASET.NAME)
					.fetch();
			for (Record r : result) {
				option = mapper.createObjectNode();
				option.put("id", r.getValue(POPULATION_DATASET.ID));
				option.put("text", 
						r.getValue(POPULATION_DATASET.NAME)
					);

				options.add(option);
			}
						
			return options;
			
		});

		service.get("/api/load-functions", (request, response) -> {
			
			ObjectMapper mapper = new ObjectMapper();

			ArrayNode options = mapper.createArrayNode();
			ObjectNode option = mapper.createObjectNode();

			Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration())
					.select(HEALTH_IMPACT_FUNCTION.asterisk(), ENDPOINT.NAME, RACE.NAME, GENDER.NAME, ETHNICITY.NAME)
					.from(HEALTH_IMPACT_FUNCTION)
					.join(ENDPOINT).on(HEALTH_IMPACT_FUNCTION.ENDPOINT_ID.eq(ENDPOINT.ID))
					.join(RACE).on(HEALTH_IMPACT_FUNCTION.RACE_ID.eq(RACE.ID))
					.join(GENDER).on(HEALTH_IMPACT_FUNCTION.GENDER_ID.eq(GENDER.ID))
					.join(ETHNICITY).on(HEALTH_IMPACT_FUNCTION.ETHNICITY_ID.eq(ETHNICITY.ID))
					.orderBy(ENDPOINT.NAME, HEALTH_IMPACT_FUNCTION.AUTHOR, 
							HEALTH_IMPACT_FUNCTION.START_AGE, HEALTH_IMPACT_FUNCTION.END_AGE)
					.fetch();
			for (Record r : result) {
				option = mapper.createObjectNode();
				option.put("id", r.getValue(HEALTH_IMPACT_FUNCTION.ID));
				option.put("text", 
						r.getValue(ENDPOINT.NAME) + " | " + 
						r.getValue(HEALTH_IMPACT_FUNCTION.AUTHOR) + " | " + 
						r.getValue(HEALTH_IMPACT_FUNCTION.START_AGE) + "-" + 
						r.getValue(HEALTH_IMPACT_FUNCTION.END_AGE) + " | " + 
						r.getValue(RACE.NAME) + " | " + 
						r.getValue(GENDER.NAME) + " | " + 
						r.getValue(ETHNICITY.NAME)
					);

				options.add(option);
			}
						
			return options;
		});

	}

}
