package gov.epa.bencloud.server;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;

import ch.qos.logback.classic.LoggerContext;
import freemarker.template.Configuration;
import gov.epa.bencloud.server.jobs.JobsUtil;
import gov.epa.bencloud.server.routes.AdminRoutes;
import gov.epa.bencloud.server.routes.ApiRoutes;
import gov.epa.bencloud.server.routes.PublicRoutes;
import gov.epa.bencloud.server.routes.SecuredRoutes;
import gov.epa.bencloud.server.util.ApplicationUtil;
import gov.epa.bencloud.server.util.FreeMarkerRenderUtil;
import spark.Service;

public class BenCloudServer {

	public static final String version = "0.1";
	
	private static Logger log = LoggerFactory.getLogger(BenCloudServer.class);
    
	private static String applicationPath;
	
	public static void main(String[] args) {

		String javaVersion = System.getProperty("java.version");

		if (!javaVersion.startsWith("1.8")) {
			System.out.println("Java 8 is required to run the BenCloud Demo");
			System.exit(0);
		}

		try {
			ApplicationUtil.loadProperties("bencloud-server.properties");
			ApplicationUtil.loadProperties("bencloud-local.properties", true);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

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
			
		try {
			applicationPath = new File(".").getCanonicalPath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Configuration freeMarkerConfiguration = FreeMarkerRenderUtil.configureFreemarker(
				applicationPath + ApplicationUtil.getProperties().getProperty(
						"template.files.directory"));

		Service benCloudService = Service.ignite()
				.port(Integer.parseInt(ApplicationUtil.getProperty("server.port")))
				.threadPool(20);

		benCloudService.staticFiles.externalLocation(applicationPath + 
				ApplicationUtil.getProperties().getProperty("static.files.directory"));

		new PublicRoutes(benCloudService, freeMarkerConfiguration);
		new AdminRoutes(benCloudService, freeMarkerConfiguration);
		new ApiRoutes(benCloudService, freeMarkerConfiguration);
		new SecuredRoutes(benCloudService, freeMarkerConfiguration);
		
		JobsUtil.startJobScheduler();
				
		System.out.println("\nStarting BenCloud Demo, version " + version);

	}

	public static String getApplicationPath() {
		return applicationPath;
	}
}
