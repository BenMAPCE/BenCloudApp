package gov.epa.bencloud.server.jobs;

import static gov.epa.bencloud.server.database.jooq.Tables.TASK_QUEUE;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.tasks.TaskUtil;

@DisallowConcurrentExecution
public class ReadFromQueueJob implements Job {

	private static Logger log = LoggerFactory.getLogger(ReadFromQueueJob.class);

	private String uuid = null;
	
	public ReadFromQueueJob() {
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		System.out.println("ReadFromQueueJob execute");
		
		try {
			
			DSL.using(JooqUtil.getJooqConfiguration())
			   .transaction(ctx -> {
					Result<Record> result = DSL.using(ctx).select().from(TASK_QUEUE)
							.where(TASK_QUEUE.IN_PROCESS.isFalse())
							.orderBy(TASK_QUEUE.SUBMITTED_DATE.asc())
							.limit(1)
							.forUpdate()
							.fetch();

					if (result.size() == 0) {
						System.out.println("no tasks to process");
					} else if (result.size() > 1) {
						System.out.println("recieved more than 1 task record");
					} else {
						Record record = result.get(0);

						System.out.println("get job from queue: " + 
								record.get(TASK_QUEUE.TASK_NAME));

						uuid = record.getValue(TASK_QUEUE.TASK_UUID);


						DSL.using(ctx).update(TASK_QUEUE)
						.set(TASK_QUEUE.IN_PROCESS, true)
						.where(TASK_QUEUE.ID.eq(record.getValue(TASK_QUEUE.ID)))
						.execute();

					}
			   });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		

		TaskUtil.processTask(uuid);

	}

}
