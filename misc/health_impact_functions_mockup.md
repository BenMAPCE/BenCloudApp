# Estimate Health Impacts

## Updates to the Task Queue

Add a link to the header to "Estimate Health Impacts"

This will navigate to a form with the following fields:

- name (text)
- type (hidden. hardcoded as "HIF")
- baseline air quality (select using /air-quality-data API call)
- scenario air quality (select using /air-quality-data API call)
- population dataset (select using /population API call. Needs to be implemented. Can hardcode to 40 for now.)
- population year
- functions (multi select list. Use /health-impact-functions API call as data source. Show endpoint, author, start age, end age for now)

## Example JSON created by the above form that is posted to /tasks

```
{
	"name":"Example Task Name",
	"type":"HIF",
	"airQualityData" : [
		{
			"type":"baseline",
			"id":4
		},
		{
			"type":"scenario",
			"id":5
		}
	],
	"population": {
		"id":40,
		"year":2020
	},
	"functions": [
		{
			"id":710,
			"startAge":30,
			"endAge":99,
			"race":null,
			"ethnicity":null,
			"gender":null,
			"incidence":100,
			"prevalence":null,
			"variable":null
		},
		{
			"id":711,
			"startAge":30,
			"endAge":99,
			"race":null,
			"ethnicity":null,
			"gender":null,
			"incidence":100,
			"prevalence":null,
			"variable":null
		}
	]
}
```


## Add Ability to download results of completed HIF tasks
This can be added to an ellipses menu next to each completed task

It will utilize the /tasks/{uuid}/results API call. Need to set the "Accept" header to text/csv. Might have some work to do on the API to make this present as a file, and not just text content.

## Delete Task
FUTURE TODO: We can add a delete option to the ellipses menu in the queue to remove the task entry and the corresponding results records.



