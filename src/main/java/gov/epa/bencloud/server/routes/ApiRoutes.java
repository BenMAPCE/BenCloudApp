package gov.epa.bencloud.server.routes;

import javax.servlet.MultipartConfigElement;

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


		
		
		service.get("/api/load-states", (request, response) -> {
			
			ObjectMapper mapper = new ObjectMapper();

			ArrayNode states = mapper.createArrayNode();
			ObjectNode state = mapper.createObjectNode();

			state = mapper.createObjectNode();
			state.put("state_abbr", "OR");
			state.put("state_name", "Oregon");
			states.add(state);
			
			state = mapper.createObjectNode();
			state.put("state_abbr", "PA");
			state.put("state_name", "Pensylvania");
			states.add(state);
			
			
			return states;
		});

	}

}
