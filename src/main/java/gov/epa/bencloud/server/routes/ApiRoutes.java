package gov.epa.bencloud.server.routes;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import gov.epa.bencloud.server.util.FreeMarkerRenderUtil;
import spark.Service;

public class ApiRoutes {

	private static final Logger log = LoggerFactory.getLogger(ApiRoutes.class);
	private Service service = null;

	public ApiRoutes(Service service, Configuration freeMarkerConfiguration){
		this.service = service;
		addRoutes(freeMarkerConfiguration);
	}

	private void addRoutes(Configuration freeMarkerConfiguration) {

		// GET
		
		service.get("/api/v1/air-quality-data", (request, response) -> {
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("route", "GET /api/v1/air-quality-data");
			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/route.ftl");
		});

		service.get("/api/v1/air-quality-data/:id", (request, response) -> {
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("route", 
					"GET /api/v1/air-quality-data" + " id:" + request.params("id"));
			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/route.ftl");
		});

		// POST

		service.post("/api/v1/air-quality-data", (request, response) -> {
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("route", "POST /api/v1/air-quality-data");
			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/route.ftl");
		});

	}

}
