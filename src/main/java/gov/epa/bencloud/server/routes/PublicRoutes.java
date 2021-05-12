package gov.epa.bencloud.server.routes;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import gov.epa.bencloud.server.util.FreeMarkerRenderUtil;
import spark.Service;

public class PublicRoutes {

	private static final Logger log = LoggerFactory.getLogger(PublicRoutes.class);
	private Service service = null;

	public PublicRoutes(Service service, Configuration freeMarkerConfiguration){
		this.service = service;
		addRoutes(freeMarkerConfiguration);
	}

	private void addRoutes(Configuration freeMarkerConfiguration) {

		service.get("/", (req, res) -> {
			Map<String, Object> attributes = new HashMap<>();
			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "index.ftl");
		});

		service.get("/about", (req, res) -> {
			Map<String, Object> attributes = new HashMap<>();
			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "about.ftl");
		});

		service.notFound((request, response) -> {
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("page", request.pathInfo());
			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/404.ftl");
		});

		service.internalServerError((request, response) -> {
			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/500.ftl");
		});
	}
}
