package gov.epa.bencloud.server.tasks.local;

import gov.epa.bencloud.server.tasks.TaskComplete;
import gov.epa.bencloud.server.tasks.TaskQueue;
import gov.epa.bencloud.server.tasks.TaskWorker;
import gov.epa.bencloud.server.tasks.model.Task;

public class TaskWorkerRunable implements Runnable {

	private String taskUuid;
	private String taskWorkerUuid;

	public TaskWorkerRunable(String taskUuid, String taskWorkerUuid) {
		this.taskUuid = taskUuid;
		this.taskWorkerUuid = taskWorkerUuid;
	}

	private int minRandomNumber = 1;
	private int maxRandomNumber = 30;

	public void run() {

		Task task = TaskQueue.getTaskFromQueueRecord(taskUuid);

		int loopTimes = (int) Math.floor(
				Math.random() * (maxRandomNumber - minRandomNumber + 1) + minRandomNumber);

		try {

			for (int i = 0; i < loopTimes; i++) {

				TaskWorker.updateTaskWorkerHeartbeat(taskWorkerUuid);

				Thread.sleep( (long) (Math.random() * 5000) );

			}

			TaskComplete.addTaskToCompleteAndRemoveTaskFromQueue(taskUuid, taskWorkerUuid);

		} catch (InterruptedException e) {
		}
	}
}


