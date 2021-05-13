package gov.epa.bencloud.server.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.epa.bencloud.server.BenCloudServer;

public class TaskUtil {

	private static DateTimeFormatter dateTimeformatter = 
			DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
	private static Logger log = LoggerFactory.getLogger(TaskUtil.class);

	public static void writeTaskToQueue(Task task) {

		LocalDateTime now = LocalDateTime.now();

		byte[] serializedScenerio = SerializationUtils.serialize(task);

		String queueFile = BenCloudServer.getQueueDirectory() + File.separator + now.format(dateTimeformatter) 
			+ "-" + task.getUuid() + ".task";

		Path path = Paths.get(queueFile);
		try {
			Files.write(path, serializedScenerio);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeTaskToOutput(Task task, String dateString) {
		

		String taskOutputFileName = 
				"TASK" + "_" +
				task.getName() + "_" +
				dateString +
				".task";
				
		String taskFile = BenCloudServer.getOutputDirectory() + 
				File.separator + 
				getUserIdentifierPath(task) + File.separator + 
				taskOutputFileName;

		
		task.setOutputFileName(taskOutputFileName);
				
		byte[] serializedTask = SerializationUtils.serialize(task);

		Path path = Paths.get(taskFile);
		try {
			Files.write(path, serializedTask);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void processTask(Task task) {
				
	    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	    LocalDateTime now = LocalDateTime.now();
	    String dateString = now.format(dateFormatter);

	    writeTaskToOutput(task, dateString);
	}
	
	public static String getUserIdentifierPath(Task task) {
				
		String outputDirectory = BenCloudServer.getOutputDirectory() + File.separator + task.getUserIdentifier();
				
		if (!new File(outputDirectory).exists()) {
			new File(outputDirectory).mkdirs();
		}
		
		return task.getUserIdentifier(); 
	}

	public static List<Task> getCompletedTasks(String userIdentifier) {
		
		List<Task> tasks = new ArrayList<Task>();
		
		if (null != userIdentifier) {

			Path userOutputPath = 
					Paths.get(BenCloudServer.getOutputDirectory() + 
					File.separator + userIdentifier);
		    
			if (!userOutputPath.toFile().exists()) {
				userOutputPath.toFile().mkdirs();
			}

			try {

				List<String> taskFilePaths = new ArrayList<>();

				try (Stream<Path> filePathStream = 
						Files.walk(
								Paths.get(
										BenCloudServer.getOutputDirectory() + 
								File.separator + userIdentifier))) {
					filePathStream.forEach(filePath -> {
						if (Files.isRegularFile(filePath)) {
							if (filePath.toString().endsWith(".task")) {
								taskFilePaths.add(filePath.toString());
							}
						}
					});
				}

				Collections.sort(taskFilePaths);
				for (String taskFilePath : taskFilePaths) {
					byte[] taskFileContent = Files.readAllBytes(Paths.get(taskFilePath));
					Task task = SerializationUtils.deserialize(taskFileContent);
					tasks.add(task);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return tasks;
	} 

}
