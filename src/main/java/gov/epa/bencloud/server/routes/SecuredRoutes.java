package gov.epa.bencloud.server.routes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javafaker.Faker;

import freemarker.template.Configuration;
import gov.epa.bencloud.server.tasks.TaskComplete;
import gov.epa.bencloud.server.tasks.TaskQueue;
import gov.epa.bencloud.server.tasks.model.Task;
import gov.epa.bencloud.server.util.FreeMarkerRenderUtil;
import spark.Service;

public class SecuredRoutes {

	private static final Logger log = LoggerFactory.getLogger(SecuredRoutes.class);
	private Service service = null;

	public SecuredRoutes(Service service, Configuration freeMarkerConfiguration){
		this.service = service;
		addRoutes(freeMarkerConfiguration);
	}

	private void addRoutes(Configuration freeMarkerConfiguration) {

		service.get("/submit-task", (req, res) -> {

			String bcoUserIdentifier = RoutesUtil.getOrSetOrExtendCookie(req, res);
			
			Faker faker = new Faker();
			
			Task task = new Task();
			task.setName(faker.pokemon().name());
			task.setDescription(faker.funnyName().name());
			task.setUuid(UUID.randomUUID().toString());
			task.setUserIdentifier(bcoUserIdentifier);
			
			TaskQueue.writeTaskToQueue(task);

			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "task-submitted.ftl");

		});

		service.get("/logged-in", (req, res) -> {

			String bcoUserIdentifier = RoutesUtil.getOrSetOrExtendCookie(req, res);
			
			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "logged-in.ftl");
		});
		
		service.get("/get-tasks-results", (req, res) -> {

			String bcoUserIdentifier = RoutesUtil.getOrSetOrExtendCookie(req, res);
			
			List<List> tasks = TaskComplete.getCompletedTasks(bcoUserIdentifier);
			
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("tasks", tasks);

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "get-tasks-results.ftl");

		});

		service.post("/delete-file", (req, res) -> {

			String bcoUserIdentifier = RoutesUtil.getOrSetOrExtendCookie(req, res);
			String fileSetToDelete = req.body();
			
//			try (Stream<Path> filePathStream = 
//					Files.walk(Paths.get(BenCloudServer.getOutputDirectory() + 
//							File.separator + bcoUserIdentifier))) {
//			    filePathStream.forEach(filePath -> {
//			        if (Files.isRegularFile(filePath)) {
//			        	if (filePath.toString().contains(fileSetToDelete)) {
//			        		try {
//								Files.delete(filePath);
//							} catch (IOException e) {
//								log.error(filePath.toString() + " was not deleted");
//							}
//			        	}
//			        }
//			    });
//			}
//						
			return "";

		});
	}
}
