DROP TABLE IF EXISTS data.health_impact_functions ;

CREATE TABLE data.health_impact_functions (
    id SERIAL PRIMARY KEY NOT NULL,
    hif_dataset_id INTEGER,
    function TEXT
);

DROP TABLE IF EXISTS data.health_impact_function_datasets ;

CREATE TABLE data.health_impact_function_datasets (
    id SERIAL PRIMARY KEY NOT NULL,
    name TEXT
);

DROP TABLE IF EXISTS data.hif_result_datasets ;

CREATE TABLE data.hif_result_datasets (
    id SERIAL PRIMARY KEY NOT NULL,
    task_uuid TEXT
);

DROP TABLE IF EXISTS data.hif_results ;

CREATE TABLE data.hif_results (
    hif_result_dataset_id INTEGER,
    hif_id INTEGER,
    grid_col int4 NULL,
	grid_row int4 NULL,
	grid_cell_id int8 NULL,
	population numeric,
	delta numeric,
    result numeric
);
