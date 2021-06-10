package gov.epa.bencloud.server.routes;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import gov.epa.bencloud.api.AirQualityApi;
import gov.epa.bencloud.server.util.FreeMarkerRenderUtil;
import spark.Service;

public class SecuredRoutes extends RoutesBase {

	private static final Logger log = LoggerFactory.getLogger(SecuredRoutes.class);
	private Service service = null;

	public SecuredRoutes(Service service, Configuration freeMarkerConfiguration){
		this.service = service;
		addRoutes(freeMarkerConfiguration);
	}

	private void addRoutes(Configuration freeMarkerConfiguration) {

		service.get("/logged-in", (req, res) -> {

			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			
			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "logged-in.ftl");
		});

		service.get("/aqs", (req, res) -> {
			
			Map<String, Object> attributes = new HashMap<>();
			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/forms/aqs.ftl");
		});

		service.get("/hif", (req, res) -> {
			
			Map<String, Object> attributes = new HashMap<>();
			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/forms/hif.ftl");
		});

		service.get("/air-quality-layers", (req, res) -> {
			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
			return AirQualityApi.getAirQualityLayers(bcoUserIdentifier);
		});


		service.post("/delete-file", (req, res) -> {

			String bcoUserIdentifier = getOrSetOrExtendCookie(req, res);
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
