package gov.epa.bencloud.server.routes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import freemarker.template.Configuration;
import gov.epa.bencloud.api.util.ApiUtil;
import gov.epa.bencloud.server.tasks.TaskComplete;
import gov.epa.bencloud.server.tasks.TaskQueue;
import gov.epa.bencloud.server.tasks.model.Task;
import gov.epa.bencloud.server.util.FreeMarkerRenderUtil;
import spark.Service;

public class TaskRoutes extends RoutesBase {

	private static final Logger log = LoggerFactory.getLogger(SecuredRoutes.class);
	private Service service = null;

	public TaskRoutes(Service service, Configuration freeMarkerConfiguration){
		this.service = service;
		addRoutes(freeMarkerConfiguration);
	}

	private void addRoutes(Configuration freeMarkerConfiguration) {

		// Submit a new task
		service.post("/api/v1/tasks", (req, res) -> {

			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			String body = req.body();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode params = mapper.readTree(body);
			
			
			Task task = new Task();
			task.setName(params.get("name").asText());
			
			//task.setDescription(faker.funnyName().name());
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

		service.get("/api/v1/tasks/pending", (req, res) -> {
			
			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			ObjectNode data = TaskQueue.getPendingTasks(bcoUserIdentifier, getPostParametersAsMap(req));
			res.type("application/json");
			return data;

		});

		service.get("/tasks/pending-tasks", (req, res) -> {

			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/tasks/pending-tasks.ftl");

		});
		
		service.get("/api/v1/tasks/completed", (req, res) -> {
			
			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			ObjectNode data = TaskComplete.getCompletedTasks(bcoUserIdentifier, getPostParametersAsMap(req));
			res.type("application/json");
			return data;

		});

		service.get("/tasks/completed-tasks", (req, res) -> {

			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/tasks/completed-tasks.ftl");

		});

		service.get("/api/v1/tasks/:uuid/results", (req, res) -> {
			
			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			return ApiUtil.getTaskResultDetails(req, res);

		});
	}
}
