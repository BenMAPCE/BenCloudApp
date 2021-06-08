package gov.epa.bencloud.server.tasks;

import static gov.epa.bencloud.server.database.jooq.Tables.TASK_COMPLETE;
import static gov.epa.bencloud.server.database.jooq.Tables.TASK_QUEUE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.tasks.model.Task;
import gov.epa.bencloud.server.util.DataUtil;

public class TaskQueue {

	private static Logger log = LoggerFactory.getLogger(TaskQueue.class);

	public static String getTaskFromQueue() {

		String uuid = null;;

		try {

			uuid = DSL.using(JooqUtil.getJooqConfiguration())
					.transactionResult(ctx -> {

						String taskUuid = null;

						Result<Record> result = DSL.using(ctx).select().from(TASK_QUEUE)
								.where(TASK_QUEUE.TASK_IN_PROCESS.isFalse())
								.orderBy(TASK_QUEUE.TASK_SUBMITTED_DATE.asc())
								.limit(1)
								.forUpdate()
								.fetch();

						if (result.size() == 0) {
							// System.out.println("no tasks to process");
						} else if (result.size() > 1) {
							System.out.println("recieved more than 1 task record");
						} else {
							Record record = result.get(0);

							//						System.out.println("get job from queue: " + 
							//								record.get(TASK_QUEUE.TASK_NAME));

							DSL.using(ctx).update(TASK_QUEUE)
							.set(TASK_QUEUE.TASK_IN_PROCESS, true)
							.where(TASK_QUEUE.TASK_ID.eq(record.getValue(TASK_QUEUE.TASK_ID)))
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

	public static void updateTaskPercentage(String taskUuid, int percentage) {

		try {

			DSL.using(JooqUtil.getJooqConfiguration())
			.transactionResult(ctx -> {

				Result<Record> result = DSL.using(ctx).select().from(TASK_QUEUE)
						.where(TASK_QUEUE.TASK_UUID.eq(taskUuid))
						.limit(1)
						.forUpdate()
						.fetch();

				if (result.size() == 0) {
					// System.out.println("no tasks to process");
				} else if (result.size() > 1) {
					System.out.println("recieved more than 1 task record");
				} else {
					Record record = result.get(0);

					DSL.using(ctx).update(TASK_QUEUE)
					.set(TASK_QUEUE.TASK_PERCENTAGE, percentage)
					.where(TASK_QUEUE.TASK_UUID.eq(taskUuid))
					.execute();
				}
				return taskUuid;
			});

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}


	public static void returnTaskToQueue(String uuid) {

		try {

			DSL.using(JooqUtil.getJooqConfiguration())
			.transaction(ctx -> {
				Result<Record> result = DSL.using(ctx).select().from(TASK_QUEUE)
						.where(TASK_QUEUE.TASK_IN_PROCESS.isTrue().and(TASK_QUEUE.TASK_UUID.eq(uuid)))
						.orderBy(TASK_QUEUE.TASK_SUBMITTED_DATE.asc())
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
					.set(TASK_QUEUE.TASK_IN_PROCESS, false)
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
					TASK_QUEUE.TASK_USER_IDENTIFIER,
					TASK_QUEUE.TASK_PRIORITY,
					TASK_QUEUE.TASK_UUID,
					TASK_QUEUE.TASK_NAME,
					TASK_QUEUE.TASK_DESCRIPTION,
					TASK_QUEUE.TASK_PARAMETERS,
					TASK_QUEUE.TASK_TYPE,
					TASK_QUEUE.TASK_IN_PROCESS,
					TASK_QUEUE.TASK_SUBMITTED_DATE)
			.values(
					task.getUserIdentifier(),
					Integer.valueOf(10),
					task.getUuid(),
					task.getName(),
					task.getDescription(),
					task.getParameters(),
					task.getType(),
					false,
					LocalDateTime.now())
			.execute();

		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static ObjectNode getPendingTasks(String userIdentifier, Map<String, String[]> postParameters) {

//		System.out.println("getPendingTasks");
//		System.out.println("userIdentifier: " + userIdentifier);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode data = mapper.createObjectNode();

		ArrayNode tasks = mapper.createArrayNode();
		ObjectNode task = mapper.createObjectNode();
		ObjectNode wrappedObject = mapper.createObjectNode();

		int records = 0;

		if (null != userIdentifier) {

			LocalDateTime now = LocalDateTime.now();
			try {

				Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration()).select().from(TASK_QUEUE)
						.where(TASK_QUEUE.TASK_USER_IDENTIFIER.eq(userIdentifier))
						.orderBy(TASK_QUEUE.TASK_SUBMITTED_DATE.asc())
						.fetch();

				for (Record record : result) {

					task = mapper.createObjectNode();

					task.put("task_name", record.getValue(TASK_COMPLETE.TASK_NAME));
					task.put("task_description", record.getValue(TASK_COMPLETE.TASK_DESCRIPTION));
					task.put("task_uuid", record.getValue(TASK_COMPLETE.TASK_UUID));
					task.put("task_submitted_date", record.getValue(TASK_COMPLETE.TASK_SUBMITTED_DATE).format(formatter));
					task.put("task_type", record.getValue(TASK_COMPLETE.TASK_TYPE));

					wrappedObject = mapper.createObjectNode();

					if (record.getValue(TASK_QUEUE.TASK_IN_PROCESS)) {

						wrappedObject.put("task_wait_time_display", DataUtil.getHumanReadableTime(
								record.getValue(TASK_QUEUE.TASK_SUBMITTED_DATE), 
								record.getValue(TASK_QUEUE.TASK_STARTED_DATE)));
						wrappedObject.put("task_wait_time_seconds", 
								ChronoUnit.SECONDS.between(record.getValue(TASK_QUEUE.TASK_SUBMITTED_DATE),
										record.getValue(TASK_QUEUE.TASK_STARTED_DATE)));
						task.set("task_wait_time", wrappedObject);
						
						wrappedObject = mapper.createObjectNode();
						wrappedObject.put("task_active_time_display", DataUtil.getHumanReadableTime(
								record.getValue(TASK_QUEUE.TASK_STARTED_DATE),
								now));

						wrappedObject.put("task_active_time_seconds", 
								ChronoUnit.SECONDS.between(
										record.getValue(TASK_QUEUE.TASK_STARTED_DATE), now));
						task.set("task_active_time", wrappedObject);
					} else {
						wrappedObject.put("task_wait_time_display", DataUtil.getHumanReadableTime(
								record.getValue(TASK_QUEUE.TASK_SUBMITTED_DATE), 
								now));
						wrappedObject.put("task_wait_time_seconds", 
								ChronoUnit.SECONDS.between(
										record.getValue(TASK_QUEUE.TASK_SUBMITTED_DATE), now));
						task.set("task_wait_time", wrappedObject);
						
						wrappedObject = mapper.createObjectNode();
						wrappedObject.put("task_active_time_display", "");
						wrappedObject.put("task_active_time_seconds", 0);
						task.set("task_active_time", wrappedObject);
					}

					task.put("task_status", record.getValue(TASK_QUEUE.TASK_IN_PROCESS));
					task.put("task_percentage", record.getValue(TASK_QUEUE.TASK_PERCENTAGE));

					tasks.add(task);
					records++;

				}

				data.set("data", tasks);
				data.put("success", true);
				data.put("recordsFiltered", records);
				data.put("recordsTotal", records);

			} catch (DataAccessException e) {
				data.put("success", false);
				data.put("error_message", e.getMessage());
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				data.put("success", false);
				data.put("error_message", e.getMessage());
				e.printStackTrace();
			}
		}

//				System.out.println("--------------------------------------------------");
//				System.out.println(data.toPrettyString());
//				System.out.println("--------------------------------------------------");

		return data;
	} 

	
	public static Task getTaskFromQueueRecord(String uuid) {

		// System.out.println("getTaskFromQueueRecord: " + uuid);

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
				task.setUserIdentifier(record.getValue(TASK_QUEUE.TASK_USER_IDENTIFIER));
				task.setPriority(record.getValue(TASK_QUEUE.TASK_PRIORITY));
				task.setUuid(record.getValue(TASK_QUEUE.TASK_UUID));
				task.setParameters(record.getValue(TASK_QUEUE.TASK_PARAMETERS));
				task.setType(record.getValue(TASK_QUEUE.TASK_TYPE));
				task.setSubmittedDate(record.getValue(TASK_QUEUE.TASK_SUBMITTED_DATE));
				task.setStartedDate(record.getValue(TASK_QUEUE.TASK_STARTED_DATE));
			}
		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return task;
	}

}
