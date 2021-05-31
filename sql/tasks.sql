DROP TABLE IF EXISTS data.task_queue ;

CREATE TABLE data.task_queue (
    task_id BIGSERIAL PRIMARY KEY NOT NULL,
    task_user_identifier TEXT,
    task_priority INTEGER,
    task_uuid TEXT,
    task_name TEXT,
    task_description TEXT,
    task_type TEXT,
    task_parameters TEXT,
 	task_percentage INTEGER,
    task_in_process BOOLEAN DEFAULT false,
	task_submitted_date TIMESTAMP NULL,
	task_started_date TIMESTAMP NULL
);

CREATE INDEX task_queue_on_date ON data.task_queue (task_submitted_date);
CREATE INDEX task_queue_on_priority_submitted_date ON data.task_queue (task_priority, task_submitted_date);
CREATE INDEX task_queue_on_uuid ON data.task_queue (task_uuid);

DROP TABLE IF EXISTS data.task_complete;

CREATE TABLE data.task_complete (
    task_id BIGSERIAL PRIMARY KEY NOT NULL,
    task_uuid TEXT,
    task_user_identifier TEXT,
    task_priority INTEGER,
    task_name TEXT,
    task_description TEXT,
    task_type TEXT,
    task_parameters TEXT,
    task_results TEXT,
    task_successful BOOLEAN,
    task_complete_message TEXT,
    task_submitted_date TIMESTAMP NULL,
	task_started_date TIMESTAMP NULL,
	task_completed_date TIMESTAMP NULL
);

CREATE INDEX task_complete_on_date ON data.task_complete (task_completed_date);
CREATE INDEX task_complete_on_uuid ON data.task_complete (task_uuid);

DROP TABLE IF EXISTS data.task_worker;

CREATE TABLE data.task_worker (
  task_id BIGSERIAL PRIMARY KEY NOT NULL,
  task_worker_uuid TEXT,
  task_uuid TEXT,
  last_heartbeat_date TIMESTAMP NULL
);

CREATE INDEX task_worker_on_uuid ON data.task_worker (task_worker_uuid);
CREATE INDEX task_worker_on_task_uuid ON data.task_worker (task_uuid);
