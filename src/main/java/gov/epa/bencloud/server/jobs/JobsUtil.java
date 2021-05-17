package gov.epa.bencloud.server.jobs;

import static org.quartz.JobBuilder.newJob;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class JobsUtil {

	public static void startJobScheduler() {
		
      try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();

		    JobDetail job = newJob(ReadFromQueueJob.class)
		    		.withIdentity("readQueueJob", "bencloud")
		    		.build();

		    Trigger trigger = TriggerBuilder.newTrigger()
		    	    .withIdentity("readQueueTrigger", "bencloud")
		    	    .withSchedule(SimpleScheduleBuilder
		    	    		.simpleSchedule().withIntervalInSeconds(10).repeatForever()
		    	    		.withMisfireHandlingInstructionNextWithRemainingCount())
		    	    .build();
		    
		    scheduler.scheduleJob(job, trigger);
			  
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}

	}
}
