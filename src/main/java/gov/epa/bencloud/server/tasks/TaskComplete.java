package gov.epa.bencloud.server.tasks;

import static gov.epa.bencloud.server.database.jooq.data.Tables.TASK_COMPLETE;
import static gov.epa.bencloud.server.database.jooq.data.Tables.TASK_QUEUE;
import static gov.epa.bencloud.server.database.jooq.data.Tables.TASK_WORKER;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

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

public class TaskComplete {

	private static Logger log = LoggerFactory.getLogger(TaskComplete.class);

	public static void addTaskToCompleteAndRemoveTaskFromQueue(
			String taskUuid, String taskWorkerUuid, 
			boolean taskSuccessful, String taskCompleteMessage) {

		if (null == taskUuid) {
			return;
		}

		Task task = TaskQueue.getTaskFromQueueRecord(taskUuid);

		try {

			DSL.using(JooqUtil.getJooqConfiguration()).transaction(ctx -> {

				DSL.using(ctx).delete(TASK_WORKER)
				.where(TASK_WORKER.TASK_WORKER_UUID.eq(taskWorkerUuid))
				.execute();

				DSL.using(ctx).insertInto(TASK_COMPLETE,
						TASK_COMPLETE.TASK_USER_IDENTIFIER,
						TASK_COMPLETE.TASK_PRIORITY,
						TASK_COMPLETE.TASK_UUID,
						TASK_COMPLETE.TASK_NAME,
						TASK_COMPLETE.TASK_DESCRIPTION,
						TASK_COMPLETE.TASK_TYPE,
						TASK_COMPLETE.TASK_PARAMETERS,
						TASK_COMPLETE.TASK_RESULTS,
						TASK_COMPLETE.TASK_SUCCESSFUL,
						TASK_COMPLETE.TASK_COMPLETE_MESSAGE,
						TASK_COMPLETE.TASK_SUBMITTED_DATE,
						TASK_COMPLETE.TASK_STARTED_DATE,
						TASK_COMPLETE.TASK_COMPLETED_DATE)
				.values(
						task.getUserIdentifier(),
						task.getPriority(),
						task.getUuid(),
						task.getName(),
						task.getDescription(),
						task.getType(),
						task.getParameters(),
						"{}",
						taskSuccessful,
						taskCompleteMessage,
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

	public static ObjectNode getCompletedTasks(String userIdentifier, Map<String, String[]> postParameters) {

//		System.out.println("getCompletedTasks");
//		System.out.println("userIdentifier: " + userIdentifier);
		
//		System.out.println("length: " + postParameters.get("length")[0]);
//		System.out.println("start: " + postParameters.get("start")[0]);
//		System.out.println("searchValue: " + postParameters.get("searchValue")[0]);
//		System.out.println("sortColumn: " + postParameters.get("sortColumn")[0]);
//		System.out.println("sortDirection: " + postParameters.get("sortDirection")[0]);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        
        ArrayNode tasks = mapper.createArrayNode();
        ObjectNode task = mapper.createObjectNode();
        ObjectNode wrappedObject = mapper.createObjectNode();

        int records = 0;
        
//		if (null != userIdentifier) {

			try {

				Result<Record> result = DSL.using(JooqUtil.getJooqConfiguration()).select().from(TASK_COMPLETE)
						//.where(TASK_COMPLETE.TASK_USER_IDENTIFIER.eq(userIdentifier))
						.orderBy(TASK_COMPLETE.TASK_COMPLETED_DATE.asc())
						.fetch();

				for (Record record : result) {

					task = mapper.createObjectNode();

					task.put("task_name", record.getValue(TASK_COMPLETE.TASK_NAME));
					task.put("task_type", record.getValue(TASK_COMPLETE.TASK_TYPE));
					task.put("task_description", record.getValue(TASK_COMPLETE.TASK_DESCRIPTION));
					task.put("task_uuid", record.getValue(TASK_COMPLETE.TASK_UUID));
					task.put("task_submitted_date", record.getValue(TASK_COMPLETE.TASK_SUBMITTED_DATE).format(formatter));
					task.put("task_started_date", record.getValue(TASK_COMPLETE.TASK_STARTED_DATE).format(formatter));
					task.put("task_completed_date", record.getValue(TASK_COMPLETE.TASK_COMPLETED_DATE).format(formatter));
					
					wrappedObject = mapper.createObjectNode();
					wrappedObject.put("task_wait_time_display", DataUtil.getHumanReadableTime(
							record.getValue(TASK_COMPLETE.TASK_SUBMITTED_DATE), 
							record.getValue(TASK_COMPLETE.TASK_STARTED_DATE)));
					wrappedObject.put("task_wait_time_seconds", 
							ChronoUnit.SECONDS.between(record.getValue(TASK_COMPLETE.TASK_SUBMITTED_DATE),
									record.getValue(TASK_COMPLETE.TASK_STARTED_DATE)));
					task.set("task_wait_time", wrappedObject);

					wrappedObject = mapper.createObjectNode();
					wrappedObject.put("task_execution_time_display", DataUtil.getHumanReadableTime(
							record.getValue(TASK_COMPLETE.TASK_STARTED_DATE), 
							record.getValue(TASK_COMPLETE.TASK_COMPLETED_DATE)));
					wrappedObject.put("task_execution_time_seconds", 
							ChronoUnit.SECONDS.between(record.getValue(TASK_COMPLETE.TASK_STARTED_DATE),
									record.getValue(TASK_COMPLETE.TASK_COMPLETED_DATE)));
					task.set("task_execution_time", wrappedObject);

					task.put("task_elapsed_time", DataUtil.getHumanReadableTime(
							record.getValue(TASK_COMPLETE.TASK_STARTED_DATE), 
							record.getValue(TASK_COMPLETE.TASK_COMPLETED_DATE)));

					System.out.println(DataUtil.getHumanReadableTime(
							record.getValue(TASK_COMPLETE.TASK_STARTED_DATE), 
							record.getValue(TASK_COMPLETE.TASK_COMPLETED_DATE)));
					
					task.put("task_successful", record.getValue(TASK_COMPLETE.TASK_SUCCESSFUL));
					task.put("task_message", record.getValue(TASK_COMPLETE.TASK_COMPLETE_MESSAGE));
				    					
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
//		}

//		System.out.println("--------------------------------------------------");
//		System.out.println(data.toPrettyString());
//		System.out.println("--------------------------------------------------");
		
		return data;
	} 

	public static Task getTaskFromCompleteRecord(String uuid) {

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
				task.setUserIdentifier(record.getValue(TASK_COMPLETE.TASK_USER_IDENTIFIER));
				task.setPriority(record.getValue(TASK_COMPLETE.TASK_PRIORITY));
				task.setUuid(record.getValue(TASK_COMPLETE.TASK_UUID));
				task.setParameters(record.getValue(TASK_COMPLETE.TASK_PARAMETERS));
				task.setType(record.getValue(TASK_COMPLETE.TASK_TYPE));
				task.setSubmittedDate(record.getValue(TASK_COMPLETE.TASK_SUBMITTED_DATE));
				task.setStartedDate(record.getValue(TASK_COMPLETE.TASK_STARTED_DATE));
				task.setCompletedDate(record.getValue(TASK_COMPLETE.TASK_COMPLETED_DATE));
			}
		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return task;
	}
}
