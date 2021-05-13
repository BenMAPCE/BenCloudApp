package gov.epa.bencloud.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import gov.epa.bencloud.server.BenCloudServer;

public class ApplicationUtil {

	public static Properties properties = new Properties();

	public static boolean usingLocalProperties() {

		try {
			String applicationPath = new File(".").getCanonicalPath();
					
			String propertiesPath = applicationPath + File.separator + "bencloud-local.properties";
			File propertiesFile = new File(propertiesPath);

			if (propertiesFile.exists()) {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	public static void loadProperties(String propertiesFileName) throws IOException {
		loadProperties(propertiesFileName, false);
	}
	
	public static void loadProperties(
			String propertiesFileName, boolean optional) throws IOException {

		String applicationPath = new File(".").getCanonicalPath();
				
		String propertiesPath = applicationPath + File.separator + propertiesFileName;
		File propertiesFile = new File(propertiesPath);

		if (!propertiesFile.exists()) {
			if (!optional) {
				System.out.println("Sorry, unable to find " + propertiesFileName);
				return;
			}
		} else {
			try (InputStream input = new FileInputStream(propertiesFile)) {

				properties.load(input);
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static String getProperty(String property) {

		return properties.getProperty(property);
	}

	private static void setProperty(String propertyName, String propertyValue) {

		properties.setProperty(propertyName, propertyValue);
		return;
	}

	public static boolean validateProperties() throws IOException {

		boolean propertiesOK = true;

		if (!checkPropertyDirectory("config.directory")) {
			propertiesOK = false;
		}

		return propertiesOK;
	}

	private static boolean checkPropertyDirectory(String propertyName) throws IOException {

		boolean propertyOK = true;

		String directory = getProperty(propertyName);
		if (null == directory) {
			System.out.println(propertyName + " property not defined");
			propertyOK = false;
		} else {
			
			if (directory.startsWith("../")) {
				String applicationPath = new File(".").getCanonicalPath();
				directory = directory.replace("../", applicationPath + File.separator);
				setProperty(propertyName, directory);
			}
			
			if (!new File(directory).exists()) {
				System.out.println(directory + " does not exist");
				propertyOK = false;
			}
		}

		return propertyOK;
	}

	public static void configureLogging() {

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			// Call context.reset() to clear any previous configuration, e.g. default 
			// configuration. For multi-step configuration, omit calling context.reset().
			context.reset(); 
			configurator.doConfigure(getProperty("config.directory") + File.separator + "logback.xml");
		} catch (JoranException je) {
			// StatusPrinter will handle this
		}

		// uncomment to check log configuration
		// StatusPrinter.printInCaseOfErrorsOrWarnings(context);

	}

	public static Properties getProperties() {
		return properties;
	}

	public static String replaceNonValidCharacters(String inputString) {
		
		String outputString = inputString;
		outputString = outputString.replaceAll(" ", "_");
		outputString = outputString.replaceAll("[^a-zA-Z0-9\\_\\-\\s]", "_");
		return outputString;
	}
	
	public static String createQueueDirectoryIfNecessary() {
		
		String queueDirectory = BenCloudServer.getApplicationPath() + 
				ApplicationUtil.getProperty("queue.directory");

		if (!new File(queueDirectory).exists()) {
			new File(queueDirectory).mkdirs();
		}

		return queueDirectory;
	}

	public static String createOutputDirectoryIfNecessary() {
		
		String outputDirectory = BenCloudServer.getApplicationPath() + 
				ApplicationUtil.getProperty("output.directory");
				
		if (!new File(outputDirectory).exists()) {
			new File(outputDirectory).mkdirs();
		}
		
		return outputDirectory;
	}
}
