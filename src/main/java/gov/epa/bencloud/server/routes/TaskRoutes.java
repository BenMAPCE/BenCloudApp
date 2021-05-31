package gov.epa.bencloud.server.routes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;

import freemarker.template.Configuration;
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

		service.get("/tasks/submit-task", (req, res) -> {

			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			Faker faker = new Faker();
			
			Task task = new Task();
			task.setName(faker.pokemon().name());
			task.setDescription(faker.funnyName().name());
			task.setUuid(UUID.randomUUID().toString());
			task.setUserIdentifier(bcoUserIdentifier);
			task.setType("");

			// Fake Task Type
			
			int randomNumber = (int) Math.floor(
					Math.random() * (2 - 1 + 1) + 1);

			if (randomNumber == 1) {
				task.setType("HIF");
			}
			
			TaskQueue.writeTaskToQueue(task);

			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/tasks/task-submitted.ftl");

		});

		service.post("/tasks/pending-tasks/data", (req, res) -> {
			
			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			ObjectNode data = TaskQueue.getPendingTasks(bcoUserIdentifier, getPostParametersAsMap(req));
			
			return data;

		});

		service.get("/tasks/pending-tasks", (req, res) -> {

			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/tasks/pending-tasks.ftl");

		});

		service.post("/tasks/completed-tasks/data", (req, res) -> {
			
			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			ObjectNode data = TaskComplete.getCompletedTasks(bcoUserIdentifier, getPostParametersAsMap(req));
			
			return data;

		});

		service.get("/tasks/completed-tasks", (req, res) -> {

			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/tasks/completed-tasks.ftl");

		});

	}
}
