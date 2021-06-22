# Valuation of Health Impacts

## Updates to the UI

Add a link to the header titled "Valuation" 

Background: The valuation step takes a hif_result_dataset as its input and applies selected valuation functions to the results of each health impact function.
 
The Valuation page will present a form with the following fields:

- name (text)
- type (hidden. hardcoded as "Valuation")
- hifResultDatasetId (The user will selected a previously generated hif_result_dataset. The /api/load-hif-result-dataset endpoint needs to be implemented.
- variableDatasetId (This can be hardcoded to 1 for now)

Here's the tricky bit. Once the hif_result_dataset is selected, we need to present a list of all health impact functions that were part of that dataset and allow the user to assign one or more valuation functions to each health impact function. This will create an array of objects that each contain a valuation function id and health impact function id pair.

The /api/load-valuation-functions endpoint needs to be implemented.

For the valuation function selection field, show a concatenation of "Endpoint | Start Age - End Age | Function Text | Qualifier"

## Example JSON created by the above form that is posted to /tasks

```
{
	"name":"Three Valuation Functions",
	"type":"Valuation",
	"hifResultDatasetId":2,
	"variableDatasetId":1,
	"functions": [
		{
			"vfId":281,
			"hifId":770
		},
		{
			"vfId":282,
			"hifId":770
		},
		{
			"vfId":297,
			"hifId":770
		}
	]
}

```


## Add Ability to download results of completed Valuation tasks
This is already done

## Delete Task
FUTURE TODO: We can add a delete option to the ellipses menu in the queue to remove the task entry and the corresponding results records.



