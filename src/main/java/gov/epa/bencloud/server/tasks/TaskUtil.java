package gov.epa.bencloud.server.tasks;

import static gov.epa.bencloud.server.database.jooq.Tables.TASK_COMPLETE;
import static gov.epa.bencloud.server.database.jooq.Tables.TASK_QUEUE;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.epa.bencloud.server.BenCloudServer;
import gov.epa.bencloud.server.database.ConnectionManager;

public class TaskUtil {

	private static Logger log = LoggerFactory.getLogger(TaskUtil.class);

	public static void writeTaskToQueue(Task task) {

		System.out.println("writeTaskToQueue: " + task.getUuid());

		DSLContext create = DSL.using(ConnectionManager.getConnection(), SQLDialect.POSTGRES);

		try {
			create.insertInto(TASK_QUEUE,
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

	public static void addTaskToCompleteAndRemoveTaskFromQueue(String uuid) {

		if (null == uuid) {
			return;
		}
		
		System.out.println("addTaskToCompleteAndRemoveTaskFromQueue: " + uuid);

		Task task = getTaskFromQueueRecord(uuid);

		DSLContext create = DSL.using(ConnectionManager.getConnection(), SQLDialect.POSTGRES);

		try {
			create.insertInto(TASK_COMPLETE,
					TASK_COMPLETE.USER_IDENTIFIER,
					TASK_COMPLETE.PRIORITY,
					TASK_COMPLETE.TASK_UUID,
					TASK_COMPLETE.TASK_NAME,
					TASK_COMPLETE.TASK_DESCRIPTION,
					TASK_COMPLETE.TASK_RESULTS,
					TASK_COMPLETE.SUBMITTED_DATE,
					TASK_COMPLETE.COMPLETED_DATE)
			.values(
					task.getUserIdentifier(),
					task.getPriority(),
					task.getUuid(),
					task.getName(),
					task.getDescription(),
					"{}",
					task.getSubmittedDate(),
					LocalDateTime.now())
			.execute();

			create.delete(TASK_QUEUE)
			.where(TASK_QUEUE.TASK_UUID.eq(task.getUuid()))
			.execute();
			

		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static void processTask(String uuid) {

		System.out.println("processTask: " + uuid);

		DSLContext create = DSL.using(ConnectionManager.getConnection(), SQLDialect.POSTGRES);

		 try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		    
		try {
			create.update(TASK_QUEUE)
			.set(TASK_QUEUE.TASK_DATA, "{'complete':true}")
			.where(TASK_QUEUE.TASK_UUID.eq(uuid))
			.execute();
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

		addTaskToCompleteAndRemoveTaskFromQueue(uuid);
	}

	public static List<Task> getCompletedTasks(String userIdentifier) {

		System.out.println("getCompletedTasks: " + userIdentifier);

		List<Task> tasks = new ArrayList<Task>();
		Task task = new Task();
		
		if (null != userIdentifier) {

			DSLContext create = DSL.using(ConnectionManager.getConnection(), SQLDialect.POSTGRES);

			try {
				Result<Record> result = create.select().from(TASK_COMPLETE)
						.where(TASK_COMPLETE.USER_IDENTIFIER.eq(userIdentifier))
						.orderBy(TASK_COMPLETE.COMPLETED_DATE.desc())
						.fetch();
						
				for (Record record : result) {
					
					task = new Task();
					task.setName(record.getValue(TASK_COMPLETE.TASK_NAME));
					task.setDescription(record.getValue(TASK_COMPLETE.TASK_DESCRIPTION));
					task.setUserIdentifier(record.getValue(TASK_COMPLETE.USER_IDENTIFIER));
					task.setPriority(record.getValue(TASK_COMPLETE.PRIORITY));
					task.setUuid(record.getValue(TASK_COMPLETE.TASK_UUID));
					task.setSubmittedDate(record.getValue(TASK_COMPLETE.SUBMITTED_DATE));
					task.setCompletedDate(record.getValue(TASK_COMPLETE.COMPLETED_DATE));
				    
					tasks.add(task);
				}
			} catch (DataAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		return tasks;
	} 

	private static Task getTaskFromQueueRecord(String uuid) {

		System.out.println("getTaskFromQueueRecord: " + uuid);

		Task task = new Task();
		
		DSLContext create = DSL.using(ConnectionManager.getConnection(), SQLDialect.POSTGRES);

		try {
			Result<Record> result = create.select().from(TASK_QUEUE)
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
			}
		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return task;
	}

	private static Task getTaskFromComplete(String uuid) {
		
		Task task = new Task();
		
		DSLContext create = DSL.using(ConnectionManager.getConnection(), SQLDialect.POSTGRES);

		try {
			Result<Record> result = create.select().from(TASK_COMPLETE)
			.where(TASK_COMPLETE.TASK_UUID.eq(uuid))
			.fetch();
		
			if (result.size() == 0) {
				System.out.println("no uuid in complete");
			} else if (result.size() > 1) {
				System.out.println("recieved more than 1 uuid record");
			} else {
				Record record = result.get(0);
				task.setName(record.getValue(TASK_COMPLETE.TASK_NAME));
				task.setDescription(record.getValue(TASK_COMPLETE.TASK_DESCRIPTION));
				task.setUserIdentifier(record.getValue(TASK_COMPLETE.USER_IDENTIFIER));
				task.setPriority(record.getValue(TASK_COMPLETE.PRIORITY));
				task.setUuid(record.getValue(TASK_COMPLETE.TASK_UUID));
				task.setSubmittedDate(record.getValue(TASK_COMPLETE.SUBMITTED_DATE));
				task.setCompletedDate(record.getValue(TASK_COMPLETE.COMPLETED_DATE));
			}
		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return task;
	}

}
