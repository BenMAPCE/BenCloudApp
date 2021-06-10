package gov.epa.bencloud.server.routes;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.MultipartConfigElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import gov.epa.bencloud.server.util.FreeMarkerRenderUtil;
import spark.Service;

public class PublicRoutes extends RoutesBase {

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
			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/error/404.ftl");
		});

		service.internalServerError((request, response) -> {
			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/error/500.ftl");
		});

		service.post("/test-ajax", (req, res) -> {

		    req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

//			for file upload...
//
//		    try (InputStream is = req.raw().getPart("uploaded_file").getInputStream()) {
//		        // Use the input stream to create a file
//		    }
//
			
//		    System.out.println(req.raw());
		    
//			Collection<Part> parts = req.raw().getParts();
//			for (Part part : parts) {
//			   System.out.println("Name: " + part.getName());
//			   System.out.println("Size: " + part.getSize());
//			   System.out.println("Filename: " + part.getSubmittedFileName());
//			}
			
//			System.out.println(getPostParameterValue(req, "name"));

			Map<String, Object> attributes = new HashMap<>();

			return true;
		});
	}
}
