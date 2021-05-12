package gov.epa.bencloud.server.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class ReadFromQueueJob implements Job {

	private static Logger log = LoggerFactory.getLogger(ReadFromQueueJob.class);

	public ReadFromQueueJob() {
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

//		try {
//			
//			List<String> scenarioFilePaths = new ArrayList<>();
//
//			try (Stream<Path> filePathStream = 
//					Files.walk(Paths.get(ApplicationUtil.getProperties().get("queue.directory").toString()))) {
//			    filePathStream.forEach(filePath -> {
//			        if (Files.isRegularFile(filePath)) {
//			        	if (filePath.toString().endsWith(".scenario")) {
//			        		scenarioFilePaths.add(filePath.toString());
//			        	}
//			        }
//			    });
//			}
//
//			if (!scenarioFilePaths.isEmpty()) {
//				Collections.sort(scenarioFilePaths);
//				byte[] scenarioFileContent = Files.readAllBytes(Paths.get(scenarioFilePaths.get(0)));
//				Scenario scenario = SerializationUtils.deserialize(scenarioFileContent);
//				if (logger.isDebugEnabled()) {
//					logger.debug("Processing " + scenario.getName());
//				}
//
//				ProcessScenarios.processScenario(scenario);
//				Files.delete(Paths.get(scenarioFilePaths.get(0)));
//			} else {
//				if (logger.isDebugEnabled()) {
//					logger.debug("No scenarios to process");
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

}
