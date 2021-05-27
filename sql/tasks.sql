DROP TABLE IF EXISTS data.task_queue ;

CREATE TABLE data.task_queue (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    user_identifier TEXT,
    priority INTEGER,
    task_uuid TEXT,
    task_name TEXT,
    task_description TEXT,
    task_data TEXT,
 	task_percentage INTEGER,
    in_process BOOLEAN DEFAULT false,
	submitted_date TIMESTAMP NULL,
	started_date TIMESTAMP NULL
);

CREATE INDEX task_queue_on_date ON data.task_queue (submitted_date);
CREATE INDEX task_queue_on_priority_submitted_date ON data.task_queue (priority, submitted_date);
CREATE INDEX task_queue_on_uuid ON data.task_queue (task_uuid);

DROP TABLE IF EXISTS data.task_complete;

CREATE TABLE data.task_complete (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    task_uuid TEXT,
    user_identifier TEXT,
    priority INTEGER,
    task_name TEXT,
    task_description TEXT,
    task_data TEXT,
    task_results TEXT,
    submitted_date TIMESTAMP NULL,
	started_date TIMESTAMP NULL,
	completed_date TIMESTAMP NULL
);

CREATE INDEX task_complete_on_date ON data.task_complete (completed_date);
CREATE INDEX task_complete_on_uuid ON data.task_complete (task_uuid);

DROP TABLE IF EXISTS data.task_worker;

CREATE TABLE data.task_worker (
  id BIGSERIAL PRIMARY KEY NOT NULL,
  task_worker_uuid TEXT,
  task_uuid TEXT,
  last_heartbeat_date TIMESTAMP NULL
);

CREATE INDEX task_worker_on_uuid ON data.task_worker (task_worker_uuid);
CREATE INDEX task_worker_on_task_uuid ON data.task_worker (task_uuid);
