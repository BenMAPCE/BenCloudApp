CREATE TABLE data.task_queue (
    id SERIAL NOT NULL,
    user_identifier TEXT,
    priority INTEGER,
    task_uuid TEXT,
    task_name TEXT,
    task_description TEXT,
    task_data TEXT,
    in_process BOOLEAN DEFAULT false,
	submitted_date TIMESTAMP NULL
);

CREATE INDEX task_queue_by_date ON data.task_queue (submitted_date);
CREATE INDEX task_queue_by_priority_date ON data.task_queue (priority, submitted_date);
CREATE INDEX task_queue_by_uuid ON data.task_queue (task_uuid);

CREATE TABLE data.task_complete (
    id SERIAL NOT NULL,
    task_uuid TEXT,
    user_identifier TEXT,
    priority INTEGER,
    task_name TEXT,
    task_description TEXT,
    task_results TEXT,
    submitted_date TIMESTAMP NULL,
	completed_date TIMESTAMP NULL
);

CREATE INDEX task_complete_by_date ON data.task_complete (completed_date);
CREATE INDEX task_complete_by_uuid ON data.task_complete (task_uuid);
