# Deploying the API (BenCloudServer) and UI (BenCloudApp) to the IEc Sandbox Instance

## Build and Deploy the API

**Stop the API application**

`ssh ec2-user@18.118.212.202`

`cd bencloud`

`./stop-bencloud.sh`


**Build and upload to server**

`cd BenCloudServer`

`gradle fatJar`

`scp build/libs/BenCloudServer.jar ec2-user@18.118.212.202:~/bencloud`

**Start the API application**

`./start-bencloud.sh`

Log location: ~/bencloud/logs/bencloud.log

## Build and Deploy the UI

**Build the single page app**

`cd BenCloudApp`

`quasar build`

`zip -r spa.zip dist/spa`

**Upload the UI to the server**

`scp dist/spa.zip ec2-user@18.118.212.202:~/`

**Deploy the UI on the server**

`sudo unzip spa.zip -d /var/www/`

## Viewing the Application

<http://18.118.212.202/>

## Database Maintenance

**Remove all tasks and results**  
`truncate data.task_worker;`  
`truncate data.task_queue;`  
`truncate data.task_complete;`  
`truncate data.hif_result;`  
`truncate data.hif_result_function_config;`  
`truncate data.hif_result_dataset;`  
`truncate data.valuation_result_dataset;`  
`truncate data.valuation_result_function_config;`  
`truncate data.valuation_result;`  
`vacuum analyze;`  

**Remove an air quality layer**  
`delete from data.air_quality_cell where air_quality_layer_id = :id;`  
`delete from data.air_quality_layer where id = :id;`  

