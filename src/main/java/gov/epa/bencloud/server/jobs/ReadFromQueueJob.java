package gov.epa.bencloud.server.jobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.epa.bencloud.server.BenCloudServer;
import gov.epa.bencloud.server.tasks.Task;
import gov.epa.bencloud.server.tasks.TaskUtil;
import gov.epa.bencloud.server.util.ApplicationUtil;

@DisallowConcurrentExecution
public class ReadFromQueueJob implements Job {

	private static Logger log = LoggerFactory.getLogger(ReadFromQueueJob.class);

	public ReadFromQueueJob() {
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			
			List<String> taskFilePaths = new ArrayList<>();

			try (Stream<Path> filePathStream = 
					Files.walk(Paths.get(
							BenCloudServer.getQueueDirectory()))) {
			    filePathStream.forEach(filePath -> {
			        if (Files.isRegularFile(filePath)) {
			        	if (filePath.toString().endsWith(".task")) {
			        		taskFilePaths.add(filePath.toString());
			        	}
			        }
			    });
			}

			if (!taskFilePaths.isEmpty()) {
				Collections.sort(taskFilePaths);
				byte[] scenarioFileContent = Files.readAllBytes(Paths.get(taskFilePaths.get(0)));
				Task task = SerializationUtils.deserialize(scenarioFileContent);
				if (log.isDebugEnabled()) {
					log.debug("Processing " + task.getName());
				}

				TaskUtil.processTask(task);
				
				Files.delete(Paths.get(taskFilePaths.get(0)));
			} else {
				if (log.isDebugEnabled()) {
					log.debug("No tasks to process");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
