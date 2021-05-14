package gov.epa.bencloud.server.jobs;

import static gov.epa.bencloud.server.database.jooq.Tables.TASK_QUEUE;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.epa.bencloud.server.database.ConnectionManager;
import gov.epa.bencloud.server.tasks.TaskUtil;

@DisallowConcurrentExecution
public class ReadFromQueueJob implements Job {

	private static Logger log = LoggerFactory.getLogger(ReadFromQueueJob.class);

	public ReadFromQueueJob() {
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		System.out.println("ReadFromQueueJob execute");
		
		Connection connection = ConnectionManager.getConnection();
		DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
		
		String uuid = null;
		
		try {
			Result<Record> result = create.select().from(TASK_QUEUE)
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
				
				create.update(TASK_QUEUE)
				.set(TASK_QUEUE.IN_PROCESS, true)
				.where(TASK_QUEUE.ID.eq(record.getValue(TASK_QUEUE.ID)))
				.execute();
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ConnectionManager.releaseConnection(connection);
		}
		
		TaskUtil.processTask(uuid);

	}

}
