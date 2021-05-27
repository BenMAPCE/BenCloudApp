package gov.epa.bencloud.server.tasks;

import static gov.epa.bencloud.server.database.jooq.Tables.TASK_QUEUE;
import static gov.epa.bencloud.server.database.jooq.Tables.TASK_WORKER;

import java.time.LocalDateTime;
import java.util.UUID;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.tasks.local.TaskWorkerRunable;
import gov.epa.bencloud.server.tasks.model.Task;
import gov.epa.bencloud.server.util.ApplicationUtil;

public class TaskWorker {

	private static Logger log = LoggerFactory.getLogger(TaskWorker.class);

	public static int maxTaskWorkers = 0;


	static {
		maxTaskWorkers = Integer.parseInt(ApplicationUtil.getProperty("max.task.workers"));
	}

	public static int getMaxTaskWorkers() {

		return maxTaskWorkers;
	}

	public static int getTaskWorkersCount() {

		return  DSL.using(JooqUtil.getJooqConfiguration())
				.selectCount()
				.from(TASK_WORKER)
				.fetchOne(0, int.class);
	}


	public static void startTaskWorker(Task task) {

		String taskWorkerUuid = UUID.randomUUID().toString();
		
		Boolean transactionSuccessful = false;
		
		try {

			transactionSuccessful = DSL.using(JooqUtil.getJooqConfiguration())
			.transactionResult(ctx -> {

				DSL.using(ctx).insertInto(TASK_WORKER,
						TASK_WORKER.TASK_UUID,
						TASK_WORKER.TASK_WORKER_UUID,
						TASK_WORKER.LAST_HEARTBEAT_DATE
						)
				.values(
						task.getUuid(),
						taskWorkerUuid,
						LocalDateTime.now()
						)
				.execute();

				Result<Record> result = DSL.using(ctx).select().from(TASK_WORKER)
						.where(TASK_WORKER.TASK_WORKER_UUID.eq(taskWorkerUuid))
						.forUpdate()
						.fetch();

				if (result.size() == 0) {
					System.out.println("no task worker for task uuid: " + taskWorkerUuid);
				} else if (result.size() > 1) {
					System.out.println("recieved more than 1 task worker record for task uuid: " + taskWorkerUuid);
				} else {

					DSL.using(ctx).update(TASK_WORKER)
					.set(TASK_WORKER.TASK_UUID, task.getUuid())
					.where(TASK_WORKER.TASK_WORKER_UUID.eq(taskWorkerUuid))
					.execute();

					DSL.using(ctx).update(TASK_QUEUE)
					.set(TASK_QUEUE.STARTED_DATE, LocalDateTime.now())
					.where(TASK_QUEUE.TASK_UUID.eq(task.getUuid()))
					.execute();
										
					return true;
				}
				
				return false;
			});

		} catch (DataAccessException e1) {
			e1.printStackTrace();
		}

		if (transactionSuccessful) {
			 Thread t = new Thread(new TaskWorkerRunable(task.getUuid(), taskWorkerUuid));
			 t.start();
		} else {
			TaskQueue.returnTaskToQueue(task.getUuid());
		}
	}

	public static void updateTaskWorkerHeartbeat(String taskWorkerUuid) {
		
		try {

			DSL.using(JooqUtil.getJooqConfiguration())
			.transaction(ctx -> {

				System.out.println("updating heartbeat for: " + taskWorkerUuid);
				
				DSL.using(ctx).update(TASK_WORKER)
				.set(TASK_WORKER.LAST_HEARTBEAT_DATE, LocalDateTime.now())
				.where(TASK_WORKER.TASK_WORKER_UUID.eq(taskWorkerUuid))
				.execute();

			});

		} catch (DataAccessException e1) {
			e1.printStackTrace();
		}

		
	}
}
