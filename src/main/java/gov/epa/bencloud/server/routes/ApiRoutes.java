package gov.epa.bencloud.server.routes;

import static gov.epa.bencloud.server.database.jooq.Tables.AIR_QUALITY_LAYER;
import static gov.epa.bencloud.server.database.jooq.Tables.ENDPOINT;
import static gov.epa.bencloud.server.database.jooq.Tables.ETHNICITY;
import static gov.epa.bencloud.server.database.jooq.Tables.GENDER;
import static gov.epa.bencloud.server.database.jooq.Tables.GRID_DEFINITION;
import static gov.epa.bencloud.server.database.jooq.Tables.HEALTH_IMPACT_FUNCTION;
import static gov.epa.bencloud.server.database.jooq.Tables.HIF_RESULT_DATASET;
import static gov.epa.bencloud.server.database.jooq.Tables.HIF_RESULT_FUNCTION_CONFIG;
import static gov.epa.bencloud.server.database.jooq.Tables.POLLUTANT;
import static gov.epa.bencloud.server.database.jooq.Tables.POPULATION_DATASET;
import static gov.epa.bencloud.server.database.jooq.Tables.RACE;
import static gov.epa.bencloud.server.database.jooq.Tables.VALUATION_FUNCTION;

import java.util.UUID;

import javax.servlet.MultipartConfigElement;

import org.jooq.Record;
import org.jooq.Record13;
import org.jooq.Record14;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import freemarker.template.Configuration;
import gov.epa.bencloud.api.AirQualityApi;
import gov.epa.bencloud.api.GridDefinitionApi;
import gov.epa.bencloud.api.HIFApi;
import gov.epa.bencloud.api.PollutantApi;
import gov.epa.bencloud.api.util.ApiUtil;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.tasks.TaskComplete;
import gov.epa.bencloud.server.tasks.TaskQueue;
import gov.epa.bencloud.server.tasks.model.Task;
import spark.Request;
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
		service.get("/api/grid-definitions", (request, response) -> {
			return GridDefinitionApi.getAllGridDefinitions(response);
		});
		
		service.get("/api/pollutants", (request, response) -> {
			return PollutantApi.getAllPollutantDefinitions(response);
		});
		
		//Supports optional ?pollutant=:id query string parameter
		service.get("/api/air-quality-data", (request, response) -> {
			return AirQualityApi.getAirQualityLayerDefinitions(request, response);
		});

		service.get("/api/air-quality-data/:id/definition", (request, response) -> {
			return AirQualityApi.getAirQualityLayerDefinition(request, response);
		});

		service.get("/api/air-quality-data/:id/details", (request, response) -> {
			return AirQualityApi.getAirQualityLayerDetails(request, response);
		});

		service.get("/api/health-impact-functions", (request, response) -> {
			return HIFApi.getAllHealthImpactFunctions(request, response);
		});
		
		// POST
		service.post("/api/air-quality-data", (request, response) -> {
			
			request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			String layerName = getPostParameterValue(request, "name");
			Integer pollutantId = Integer.valueOf(getPostParameterValue(request, "pollutantId"));
			Integer gridId = Integer.valueOf(getPostParameterValue(request, "gridId"));
			String layerType = getPostParameterValue(request, "type");
			
			return AirQualityApi.postAirQualityLayer(request, layerName, pollutantId, gridId, layerType, response);
		});

		//TODO: Revise UI to use /api/air-quality-data and then remove this method
		service.get("/api/load-air-quality-options", (request, response) -> {
			
			ObjectMapper mapper = new ObjectMapper();

			ArrayNode options = mapper.createArrayNode();
			ObjectNode option = mapper.createObjectNode();

			String pollutantParam = request.raw().getParameter("pollutant");
			Integer pollutant= -999;
			if(pollutantParam != null) {
				pollutant = Integer.valueOf(pollutantParam);
			}
			
			Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration())
					.select(AIR_QUALITY_LAYER.asterisk())
					.from(AIR_QUALITY_LAYER)
					.where(pollutant==-999 ? DSL.noCondition() : AIR_QUALITY_LAYER.POLLUTANT_ID.eq(pollutant))
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
		
		//TODO: Revise UI to use /api/pollutants and then remove this method
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
		
		//TODO: Revise UI to use /api/grid-definitions and then remove this method
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
		
		//TODO: Change to /api/population
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

		service.get("/api/load-hif-result-dataset-options", (request, response) -> {
			
			ObjectMapper mapper = new ObjectMapper();

			ArrayNode options = mapper.createArrayNode();
			ObjectNode option = mapper.createObjectNode();

			option = mapper.createObjectNode();
			option.put("id", "0");
			option.put("text", "Select HIF Result Dataset");
			options.add(option);
			
			Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration())
					.select(HIF_RESULT_DATASET.asterisk())
					.from(HIF_RESULT_DATASET)
					.orderBy(HIF_RESULT_DATASET.NAME)
					.fetch();
			for (Record r : result) {
				option = mapper.createObjectNode();
				option.put("id", r.getValue(HIF_RESULT_DATASET.ID));
				option.put("text", 
						r.getValue(HIF_RESULT_DATASET.NAME)
					);

				options.add(option);
			}
						
			return options;
			
		});	

		service.get("/api/load-hif-result-functions", (request, response) -> {
			
			String resultsetIdParameter = request.raw().getParameter("resultsetId");

			ObjectMapper mapper = new ObjectMapper();

			ArrayNode options = mapper.createArrayNode();
			ObjectNode option = mapper.createObjectNode();

			Integer resultsetId;
			try {
				resultsetId = Integer.parseInt(resultsetIdParameter);

				Result<Record14<Integer, Integer, Integer, Integer, String, String, Integer, String, Integer, Integer, String, String, String, String>> result = DSL.using(JooqUtil.getJooqConfiguration())
						.select(HIF_RESULT_FUNCTION_CONFIG.HIF_RESULT_DATASET_ID, 
								HIF_RESULT_FUNCTION_CONFIG.HIF_ID,
								HEALTH_IMPACT_FUNCTION.ENDPOINT_ID,
								HEALTH_IMPACT_FUNCTION.ID,
								HIF_RESULT_DATASET.NAME,
								ENDPOINT.NAME,
								HEALTH_IMPACT_FUNCTION.ENDPOINT_GROUP_ID,
								HEALTH_IMPACT_FUNCTION.AUTHOR,
								HEALTH_IMPACT_FUNCTION.START_AGE,
								HEALTH_IMPACT_FUNCTION.END_AGE,
								RACE.NAME,
								GENDER.NAME,
								ETHNICITY.NAME,
								HEALTH_IMPACT_FUNCTION.QUALIFIER
								)
						.from(HIF_RESULT_FUNCTION_CONFIG)
						.join(HIF_RESULT_DATASET).on(HIF_RESULT_DATASET.ID.eq(HIF_RESULT_FUNCTION_CONFIG.HIF_RESULT_DATASET_ID))
						.join(HEALTH_IMPACT_FUNCTION).on(HEALTH_IMPACT_FUNCTION.ID.eq(HIF_RESULT_FUNCTION_CONFIG.HIF_ID))
						.join(ENDPOINT).on(HEALTH_IMPACT_FUNCTION.ENDPOINT_ID.eq(ENDPOINT.ID))
						.join(RACE).on(HEALTH_IMPACT_FUNCTION.RACE_ID.eq(RACE.ID))
						.join(GENDER).on(HEALTH_IMPACT_FUNCTION.GENDER_ID.eq(GENDER.ID))
						.join(ETHNICITY).on(HEALTH_IMPACT_FUNCTION.ETHNICITY_ID.eq(ETHNICITY.ID))
						.where(HIF_RESULT_FUNCTION_CONFIG.HIF_RESULT_DATASET_ID.eq(resultsetId))
						.fetch();
				
				for (Record r : result) {
					option = mapper.createObjectNode();
					option.put("hif_result_dataset_id", r.getValue(HIF_RESULT_FUNCTION_CONFIG.HIF_RESULT_DATASET_ID));
					option.put("hif_result_dataset_name", r.getValue(HIF_RESULT_DATASET.NAME));
					option.put("hif_id", r.getValue(HEALTH_IMPACT_FUNCTION.ID));
					option.put("hif_endpoint_id", r.getValue(HEALTH_IMPACT_FUNCTION.ENDPOINT_ID));
					option.put("hif_endpoint_group_id", r.getValue(HEALTH_IMPACT_FUNCTION.ENDPOINT_GROUP_ID));
					String hif_options = 
						r.getValue(ENDPOINT.NAME) + " | " + 
						r.getValue(HEALTH_IMPACT_FUNCTION.AUTHOR) + " | " + 
						r.getValue(HEALTH_IMPACT_FUNCTION.START_AGE) + "-" + 
						r.getValue(HEALTH_IMPACT_FUNCTION.END_AGE) + " | " + 
						r.getValue(RACE.NAME) + " | " + 
						r.getValue(GENDER.NAME) + " | " + 
						r.getValue(ETHNICITY.NAME) + 
						  " | " + r.getValue(HEALTH_IMPACT_FUNCTION.QUALIFIER);
					option.put("hif_options", hif_options);

					options.add(option);
				}
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

						
			return options;
			
		});	

		service.get("/api/load-valuation-functions", (request, response) -> {

			String endpointGroupIdParamater = request.raw().getParameter("endpointGroupId");

			ObjectMapper mapper = new ObjectMapper();

			ArrayNode options = mapper.createArrayNode();
			ObjectNode option = mapper.createObjectNode();

			Integer endpointGroupId;

			try {
				endpointGroupId = Integer.parseInt(endpointGroupIdParamater);

				Result<Record6<Integer, Integer, Integer, String, String, String>> result = DSL.using(JooqUtil.getJooqConfiguration())
						.select(VALUATION_FUNCTION.ID, 
								VALUATION_FUNCTION.START_AGE, 
								VALUATION_FUNCTION.END_AGE, 
								VALUATION_FUNCTION.FUNCTION_TEXT, 
								VALUATION_FUNCTION.QUALIFIER,  
								ENDPOINT.NAME)
						.from(VALUATION_FUNCTION)
						.join(ENDPOINT).on(VALUATION_FUNCTION.ENDPOINT_ID.eq(ENDPOINT.ID))
						.where(VALUATION_FUNCTION.ENDPOINT_GROUP_ID.eq(endpointGroupId))
						.orderBy(ENDPOINT.NAME, 
								VALUATION_FUNCTION.START_AGE,
								VALUATION_FUNCTION.END_AGE,
								VALUATION_FUNCTION.FUNCTION_TEXT,
								VALUATION_FUNCTION.QUALIFIER)
						.fetch();
				
				for (Record r : result) {
					option = mapper.createObjectNode();
					option.put("id", r.getValue(VALUATION_FUNCTION.ID));
					
					
					String vf_options = 
							r.getValue(ENDPOINT.NAME) + " | " + 
							r.getValue(VALUATION_FUNCTION.START_AGE) + "-" + 
							r.getValue(VALUATION_FUNCTION.END_AGE) + " | " + 
							r.getValue(VALUATION_FUNCTION.FUNCTION_TEXT) + " | " + 
							r.getValue(VALUATION_FUNCTION.QUALIFIER);
					
					option.put("text", vf_options);

					options.add(option);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			return options;
			
		});	
		
		service.get("/api/load-functions", (request, response) -> {
			
			ObjectMapper mapper = new ObjectMapper();

			ArrayNode options = mapper.createArrayNode();
			ObjectNode option = mapper.createObjectNode();

			String pollutantParam = request.raw().getParameter("pollutant");
			Integer pollutant= -999;
			if(pollutantParam != null) {
				pollutant = Integer.valueOf(pollutantParam);
			}
			
			Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration())
					.select(HEALTH_IMPACT_FUNCTION.asterisk(), ENDPOINT.NAME, RACE.NAME, GENDER.NAME, ETHNICITY.NAME)
					.from(HEALTH_IMPACT_FUNCTION)
					.join(ENDPOINT).on(HEALTH_IMPACT_FUNCTION.ENDPOINT_ID.eq(ENDPOINT.ID))
					.join(RACE).on(HEALTH_IMPACT_FUNCTION.RACE_ID.eq(RACE.ID))
					.join(GENDER).on(HEALTH_IMPACT_FUNCTION.GENDER_ID.eq(GENDER.ID))
					.join(ETHNICITY).on(HEALTH_IMPACT_FUNCTION.ETHNICITY_ID.eq(ETHNICITY.ID))
					.where(pollutant==-999 ? DSL.noCondition() : HEALTH_IMPACT_FUNCTION.POLLUTANT_ID.eq(pollutant))
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
						r.getValue(ETHNICITY.NAME) + 
				
						(null == r.getValue(HEALTH_IMPACT_FUNCTION.QUALIFIER) 
							? "" : " | " + r.getValue(HEALTH_IMPACT_FUNCTION.QUALIFIER)
							) + 
						
						(null == r.getValue(HEALTH_IMPACT_FUNCTION.LOCATION) 
							? "" : " | " + r.getValue(HEALTH_IMPACT_FUNCTION.LOCATION)
						)
					);

				options.add(option);
			}
						
			return options;
		});

		service.get("/api/tasks/:uuid/results", (req, res) -> {
			
			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			return ApiUtil.getTaskResultDetails(req, res);

		});

		service.get("/api/tasks/:uuid/results/delete", (req, res) -> {
			
			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			return ApiUtil.deleteTaskResults(req, res);

		});
		
		service.get("/api/tasks/pending", (req, res) -> {
			
			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			ObjectNode data = TaskQueue.getPendingTasks(bcoUserIdentifier, getPostParametersAsMap(req));
			res.type("application/json");
			return data;

		});
		
		service.get("/api/tasks/completed", (req, res) -> {
			
			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			ObjectNode data = TaskComplete.getCompletedTasks(bcoUserIdentifier, getPostParametersAsMap(req));
			res.type("application/json");
			return data;

		});
		
		// Submit a new task
		service.post("/api/tasks", (req, res) -> {

			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			String body = req.body();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode params = mapper.readTree(body);
			
			
			Task task = new Task();
			task.setName(params.get("name").asText());
			
			task.setParameters(body);
			task.setUuid(UUID.randomUUID().toString());
			task.setUserIdentifier(bcoUserIdentifier);
			task.setType(params.get("type").asText());
			
			TaskQueue.writeTaskToQueue(task);

			ObjectNode ret = mapper.createObjectNode();
			ret.put("task_uuid", task.getUuid());
			res.type("application/json");
			return ret;

		});
		
	}

}
