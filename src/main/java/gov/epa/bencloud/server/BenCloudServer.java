package gov.epa.bencloud.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import freemarker.template.Configuration;
import gov.epa.bencloud.server.util.ApplicationUtil;
import gov.epa.bencloud.server.util.FreeMarkerRenderUtil;
import spark.Request;
import spark.Response;
import spark.Service;

public class BenCloudServer {

	public static final String version = "0.1";
	
	private static Logger logger = LoggerFactory.getLogger(BenCloudServer.class);
    
	public static void main(String[] args) {

		String javaVersion = System.getProperty("java.version");

		if (!javaVersion.startsWith("1.8")) {
			System.out.println("Java 8 is required to run the BenCloud Demo");
			System.exit(0);
		}

		try {
			ApplicationUtil.loadProperties("bencloud-server.properties");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

//
//		try {
//			if (!ApplicationUtil.validateProperties()) {
//				System.out.println("properties are not all valid, application exiting");
//				System.exit(-1);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(-1);
//		}

		ApplicationUtil.configureLogging();
		LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
		Logger logger = loggerContext.getLogger("gov.epa.bencloud");
	
		
//        try {
//			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//			scheduler.start();
//
//		    JobDetail job = newJob(ReadFromQueueJob.class)
//		    		.withIdentity("readQueueJob", "bencloud")
//		    		.build();
//
//		    Trigger trigger = TriggerBuilder.newTrigger()
//		    	    .withIdentity("readQueueTrigger", "bencloud")
//		    	    .withSchedule(SimpleScheduleBuilder
//		    	    		.simpleSchedule().withIntervalInSeconds(30).repeatForever()
//		    	    		.withMisfireHandlingInstructionNextWithRemainingCount())
//		    	    .build();
//		    
//		    scheduler.scheduleJob(job, trigger);
//			  
//		} catch (SchedulerException e1) {
//			e1.printStackTrace();
//		}

		String applicationPath = "";
		
		try {
			applicationPath = new File(".").getCanonicalPath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}


		Configuration freeMarkerConfiguration = FreeMarkerRenderUtil.configureFreemarker(
				applicationPath + ApplicationUtil.getProperties().getProperty("template.files.directory"));

		Service benCloudService = Service.ignite()
				.port(Integer.parseInt(ApplicationUtil.getProperty("server.port")))
				.threadPool(20);

		benCloudService.staticFiles.externalLocation(applicationPath + 
				ApplicationUtil.getProperties().getProperty("static.files.directory"));

		benCloudService.get("/", (req, res) -> {
			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "index.ftl");
		});

		benCloudService.get("/about", (req, res) -> {
			Map<String, Object> attributes = new HashMap<>();

			return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "about.ftl");
		});

		benCloudService.get("/exit", (req, res) -> {
			benCloudService.stop();
			System.exit(0);
			System.out.println("shutting down....");
			return "";
		});

		try {
//			benCloudService.get("/exit", (req, res) -> {
//				service.shutdown();
//				System.exit(0);
//				return "";
//			});

			benCloudService.notFound((request, response) -> {
				Map<String, Object> attributes = new HashMap<>();
				attributes.put("page", request.pathInfo());
				return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/404.ftl");
			});

			benCloudService.internalServerError((request, response) -> {
				Map<String, Object> attributes = new HashMap<>();

				return FreeMarkerRenderUtil.render(freeMarkerConfiguration, attributes, "/500.ftl");
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("\nStarting BenCloud Demo, version " + version);

	}

	private static Object getFile(Request request, Response responce, String userIdentifier, String fileName) {

		String scenarioFileDirectory = ApplicationUtil.getProperty("output.directory") + 
				File.separator + userIdentifier;

		File downloadFile = new File(scenarioFileDirectory + File.separator + fileName);
		
		responce.raw().setContentType("application/octet-stream");
		responce.raw().setHeader("Content-Disposition","attachment; filename="+downloadFile.getName());
		try {

			try(OutputStream outputStream = new BufferedOutputStream(responce.raw().getOutputStream());
					BufferedInputStream bufferedInputStream = 
							new BufferedInputStream(new FileInputStream(downloadFile))) {
				byte[] buffer = new byte[1024];
				int len;
				while ((len = bufferedInputStream.read(buffer)) > 0) {
					outputStream.write(buffer,0,len);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}
}
