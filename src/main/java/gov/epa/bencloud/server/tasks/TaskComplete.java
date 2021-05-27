package gov.epa.bencloud.server.tasks;

import static gov.epa.bencloud.server.database.jooq.Tables.TASK_COMPLETE;
import static gov.epa.bencloud.server.database.jooq.Tables.TASK_QUEUE;
import static gov.epa.bencloud.server.database.jooq.Tables.TASK_WORKER;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.tasks.model.Task;

public class TaskComplete {

	private static Logger log = LoggerFactory.getLogger(TaskComplete.class);

	public static void addTaskToCompleteAndRemoveTaskFromQueue(String taskUuid, String taskWorkerUuid) {

		if (null == taskUuid) {
			return;
		}

		System.out.println("addTaskToCompleteAndRemoveTaskFromQueue: " + taskUuid);

		Task task = TaskQueue.getTaskFromQueueRecord(taskUuid);

		try {

			DSL.using(JooqUtil.getJooqConfiguration()).transaction(ctx -> {

				DSL.using(ctx).delete(TASK_WORKER)
				.where(TASK_WORKER.TASK_WORKER_UUID.eq(taskWorkerUuid))
				.execute();

				DSL.using(ctx).insertInto(TASK_COMPLETE,
						TASK_COMPLETE.USER_IDENTIFIER,
						TASK_COMPLETE.PRIORITY,
						TASK_COMPLETE.TASK_UUID,
						TASK_COMPLETE.TASK_NAME,
						TASK_COMPLETE.TASK_DESCRIPTION,
						TASK_COMPLETE.TASK_RESULTS,
						TASK_COMPLETE.SUBMITTED_DATE,
						TASK_COMPLETE.STARTED_DATE,
						TASK_COMPLETE.COMPLETED_DATE)
				.values(
						task.getUserIdentifier(),
						task.getPriority(),
						task.getUuid(),
						task.getName(),
						task.getDescription(),
						"{}",
						task.getSubmittedDate(),
						task.getStartedDate(),
						LocalDateTime.now())
				.execute();

				DSL.using(ctx).delete(TASK_QUEUE)
				.where(TASK_QUEUE.TASK_UUID.eq(task.getUuid()))
				.execute();

			});

		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static List<List> getCompletedTasks(String userIdentifier) {

		System.out.println("getCompletedTasks: " + userIdentifier);

		List<List> tasks = new ArrayList<List>();
		List<Object> task = new ArrayList<Object>();

		//List<Task> tasks = new ArrayList<Task>();
		//Task task = new Task();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		if (null != userIdentifier) {

			try {

				Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration()).select().from(TASK_COMPLETE)
						.where(TASK_COMPLETE.USER_IDENTIFIER.eq(userIdentifier))
						.orderBy(TASK_COMPLETE.COMPLETED_DATE.asc())
						.fetch();

				for (Record record : result) {

//					task = new Task();
//					task.setName(record.getValue(TASK_COMPLETE.TASK_NAME));
//					task.setDescription(record.getValue(TASK_COMPLETE.TASK_DESCRIPTION));
//					task.setUserIdentifier(record.getValue(TASK_COMPLETE.USER_IDENTIFIER));
//					task.setPriority(record.getValue(TASK_COMPLETE.PRIORITY));
//					task.setUuid(record.getValue(TASK_COMPLETE.TASK_UUID));
//					task.setSubmittedDate(record.getValue(TASK_COMPLETE.SUBMITTED_DATE));
//					task.setStartedDate(record.getValue(TASK_COMPLETE.STARTED_DATE));
//					task.setCompletedDate(record.getValue(TASK_COMPLETE.COMPLETED_DATE));

					task = new ArrayList<Object>();

					task.add(record.getValue(TASK_COMPLETE.TASK_NAME));
					task.add(record.getValue(TASK_COMPLETE.TASK_DESCRIPTION));
					task.add(record.getValue(TASK_COMPLETE.TASK_UUID));
					task.add(record.getValue(TASK_COMPLETE.SUBMITTED_DATE).format(formatter));
					task.add(record.getValue(TASK_COMPLETE.STARTED_DATE).format(formatter));
					task.add(record.getValue(TASK_COMPLETE.COMPLETED_DATE).format(formatter));
					task.add(getHumanReadableTime(
							record.getValue(TASK_COMPLETE.SUBMITTED_DATE), 
							record.getValue(TASK_COMPLETE.STARTED_DATE)));

					task.add(getHumanReadableTime(
							record.getValue(TASK_COMPLETE.STARTED_DATE), 
							record.getValue(TASK_COMPLETE.COMPLETED_DATE)));
				    
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

	private static Task getTaskFromCompleteRecord(String uuid) {

		Task task = new Task();

		try {
			Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration()).select().from(TASK_COMPLETE)
					.where(TASK_COMPLETE.TASK_UUID.eq(uuid))
					.fetch();

			if (result.size() == 0) {
				System.out.println("no uuid in complete");
			} else if (result.size() > 1) {
				System.out.println("received more than 1 uuid record");
			} else {
				Record record = result.get(0);
				task.setName(record.getValue(TASK_COMPLETE.TASK_NAME));
				task.setDescription(record.getValue(TASK_COMPLETE.TASK_DESCRIPTION));
				task.setUserIdentifier(record.getValue(TASK_COMPLETE.USER_IDENTIFIER));
				task.setPriority(record.getValue(TASK_COMPLETE.PRIORITY));
				task.setUuid(record.getValue(TASK_COMPLETE.TASK_UUID));
				task.setSubmittedDate(record.getValue(TASK_COMPLETE.SUBMITTED_DATE));
				task.setStartedDate(record.getValue(TASK_COMPLETE.STARTED_DATE));
				task.setCompletedDate(record.getValue(TASK_COMPLETE.COMPLETED_DATE));
			}
		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return task;
	}

	private static String getHumanReadableTime(LocalDateTime start, LocalDateTime end) {
		
		long totalSeconds = ChronoUnit.SECONDS.between(start, end);

		StringBuilder humanReadableTime = new StringBuilder();

		getHumanReadableTime(totalSeconds, humanReadableTime);
		return humanReadableTime.toString();
		
	}
	
	private static void getHumanReadableTime(long totalSeconds, StringBuilder humanReadableTime) {
	
		long hours = ((totalSeconds/60) / 60);
		long minutes = ((totalSeconds / 60) % 60);
		long seconds = (totalSeconds % 60);

		if (hours > 0) {
			if (hours < 2) {
				humanReadableTime.append(hours).append(" hour").append(" ");
			} else {
				humanReadableTime.append(hours).append(" hours").append(" ");
			}
		}
		
		if (minutes > 0) {
			if (minutes < 2) {
				humanReadableTime.append(minutes).append(" minute").append(" ");
			} else {
				humanReadableTime.append(minutes).append(" minutes").append(" ");
			}
		}
		
		if (seconds > 0) {
			if (seconds < 2) {
				humanReadableTime.append(seconds).append(" second").append(" ");
			} else {
				humanReadableTime.append(seconds).append(" seconds").append(" ");
			}
		}
	}

}
