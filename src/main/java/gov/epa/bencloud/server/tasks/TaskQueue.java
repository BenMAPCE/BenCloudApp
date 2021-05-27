package gov.epa.bencloud.server.tasks;

import static gov.epa.bencloud.server.database.jooq.Tables.TASK_QUEUE;

import java.time.LocalDateTime;
import java.util.UUID;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.tasks.model.Task;

public class TaskQueue {

	private static Logger log = LoggerFactory.getLogger(TaskQueue.class);
	
	public static String getTaskFromQueue() {
		
		String uuid = null;;
		
		try {
			
			uuid = DSL.using(JooqUtil.getJooqConfiguration())
			   .transactionResult(ctx -> {
				   
				    String taskUuid = null;
				    
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

						DSL.using(ctx).update(TASK_QUEUE)
						.set(TASK_QUEUE.IN_PROCESS, true)
						.where(TASK_QUEUE.ID.eq(record.getValue(TASK_QUEUE.ID)))
						.execute();

						taskUuid = record.getValue(TASK_QUEUE.TASK_UUID);
					}
					
					return taskUuid;
					
			   });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}

		return uuid;
		
	}
	
	public static void returnTaskToQueue(String uuid) {
		
		try {
			
			DSL.using(JooqUtil.getJooqConfiguration())
			   .transaction(ctx -> {
					Result<Record> result = DSL.using(ctx).select().from(TASK_QUEUE)
							.where(TASK_QUEUE.IN_PROCESS.isTrue().and(TASK_QUEUE.TASK_UUID.eq(uuid)))
							.orderBy(TASK_QUEUE.SUBMITTED_DATE.asc())
							.forUpdate()
							.fetch();

					if (result.size() == 0) {
						System.out.println("no task for uuid: " + uuid);
					} else if (result.size() > 1) {
						System.out.println("recieved more than 1 task record for uuid: " + uuid);
					} else {
						Record record = result.get(0);

						System.out.println("making job available again: " + 
								record.get(TASK_QUEUE.TASK_NAME));

						DSL.using(ctx).update(TASK_QUEUE)
						.set(TASK_QUEUE.IN_PROCESS, false)
						.where(TASK_QUEUE.TASK_UUID.eq(record.getValue(TASK_QUEUE.TASK_UUID)))
						.execute();
					}
					System.out.println("returning task to Queue: " + uuid);
			   });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}		
	}
	
	public static void writeTaskToQueue(Task task) {

		// System.out.println("writeTaskToQueue: " + task.getUuid());

		try {
				DSL.using(JooqUtil.getJooqConfiguration()).insertInto(TASK_QUEUE,
					TASK_QUEUE.USER_IDENTIFIER,
					TASK_QUEUE.PRIORITY,
					TASK_QUEUE.TASK_UUID,
					TASK_QUEUE.TASK_NAME,
					TASK_QUEUE.TASK_DESCRIPTION,
					TASK_QUEUE.TASK_DATA,
					TASK_QUEUE.IN_PROCESS,
					TASK_QUEUE.SUBMITTED_DATE)
			.values(
					task.getUserIdentifier(),
					Integer.valueOf(10),
					UUID.randomUUID().toString(),
					task.getName(),
					task.getDescription(),
					"{}",
					false,
					LocalDateTime.now())
			.execute();
			
		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static Task getTaskFromQueueRecord(String uuid) {

		System.out.println("getTaskFromQueueRecord: " + uuid);

		Task task = new Task();
		
		try {
			Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration()).select().from(TASK_QUEUE)
			.where(TASK_QUEUE.TASK_UUID.eq(uuid))
			.fetch();
		
			if (result.size() == 0) {
				System.out.println("no uuid in queue");
			} else if (result.size() > 1) {
				System.out.println("recieved more than 1 uuid record");
			} else {
				Record record = result.get(0);
				task.setName(record.getValue(TASK_QUEUE.TASK_NAME));
				task.setDescription(record.getValue(TASK_QUEUE.TASK_DESCRIPTION));
				task.setUserIdentifier(record.getValue(TASK_QUEUE.USER_IDENTIFIER));
				task.setPriority(record.getValue(TASK_QUEUE.PRIORITY));
				task.setUuid(record.getValue(TASK_QUEUE.TASK_UUID));
				task.setSubmittedDate(record.getValue(TASK_QUEUE.SUBMITTED_DATE));
				task.setStartedDate(record.getValue(TASK_QUEUE.STARTED_DATE));
			}
		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return task;
	}

}
