var scenarios = [];

var newScenario = {
		scenarioName: "",
		scenarioDescription: "",
		color: "",
		totalCountries: 0,
		countriesSelected: "",
		totalPopulation: 0,
		typeOfRollback: "",
		function: "",
		vslStandard: ""
}        

function loadScenario(scenario) {
	$('#scenarioName').val(scenario.scenarioName);
	$('#scenarioDescription').val(scenario.scenarioDescription);
	countriesSelected = scenario.countriesSelected

	checkNodesInCountriesSelected(countriesTree);
	checkNodesInCountriesSelected(regionsTree);
	mapLayer.setStyle(mystyle);
}

function addScenario() {

	var scenario = {
			scenarioName: $('#scenarioName').val(), 
			scenarioDescription: $('#scenarioDescription').val(), 
			color: "", 
			totalCountries: getTotalCountries(),
			countriesSelected: countriesSelected,
			totalPopulation: getTotalPopulation(),
			typeOfRollback: getRollbackString(),
			percentageValue: $('#percentageValue').val(),
			incrementValue: $('#incrementValue').val(),
			standardsValue: $('#standards-list').find(":selected").val(),
			rollbackName: $('#rollback-type').find(":selected").val(),
			functionId: $('#function').find(":selected").val(),
			function: $('#function').find(":selected").text(),
			vslStandard: $('#vsl-standard').find(":selected").text(),
			vslStandardId: $('#vsl-standard').find(":selected").val(),
			negativeStandard: $('#negativeRollback').prop('checked')
	};

	hasScenario = findScenario(scenario.scenarioName);

	scenarios.push(scenario);

	if (scenarios.length > 0) {
		$("#scenarios-table-panel").show()
	}

	$("#scenario-table tbody").append(
			"<tr id='" + scenario.scenarioName + "'>" +
			"<td><input type='checkbox' id='EXECUTE-" + 
			$('#scenarioName').val() + "'></td>" + 
			"<td>" + scenario.scenarioName + "</td>" +
			"<td>" + scenario.totalCountries + "</td>" +
			"<td>" + scenario.totalPopulation.toLocaleString() + "</td>" +
			"<td>" + scenario.typeOfRollback + "</td>" +
			"<td>" + scenario.function + "</td>" +
			"<td>" + scenario.vslStandard + "</td>" +
			"<td><button type='button' id='EDIT-" +  $('#scenarioName').val() + "' " + 
			"class='btn btn-outline-secondary btn-sm' onClick='processButton(this)'>Edit</button> " + 
			"<button type='button' id='DELETE_" +  $('#scenarioName').val() + "' " + 
			"class='btn btn-outline-secondary btn-sm' onClick='processButton(this)'>Delete</button></td>"  + 
			"</tr>"
	);

}
function processButton(button) {

	var row = button.closest('tr');
	if ((button.id).startsWith("DELETE")) {
		var r = confirm("Delete this scenario?");
		if (r == true) {
			var row = button.closest('tr');
			removeScenario(row);
		}
	}

	if ((button.id).startsWith("EDIT")) {
		var row = button.closest('tr');

		for (i = 0; i < scenarios.length; i++) {
			if (scenarios[i].scenarioName == row.id) {
				loadScenario(scenarios[i])
			}
		}

		hasScenario = findScenario(row);
		if (hasScenario) {
			removeScenario(row)
		}
	}

	if (scenarios.length == 0) {
		$("#scenarios-table-panel").hide()
	}
}

function removeScenario(row) {
	for (i = 0; i < scenarios.length; i++) {
		if (scenarios[i].scenarioName == row.id) {
			scenarios.splice(i, 1);
			row.remove()
		}
	} 
}


function findScenario(row) {
	for (i = 0; i < scenarios.length; i++) {
		if (scenarios[i].scenarioName == row.id) {
			return scenarios[i]
		}
	}
}

function getTotalCountries() {
	countries = countriesSelected.replace(/\|\|/g, ",");
	countries = countries.replace(/\|/g, "");
	countriesSplit = countries.split(",");

	return countriesSplit.length;
}

function getTotalPopulation() {
	countries = countries.replace(/\|/g, "");
	countriesSplit = countries.split(",");
	totalPopulation = 0;

	for (i = 0; i < countriesSplit.length; i++) { 
		if (countryPopulation[countriesSplit[i]]) {
			totalPopulation = totalPopulation + parseInt(countryPopulation[countriesSplit[i]])
		}
	}

	return totalPopulation;
}

function getRollbackString() {
	rollbackString = "";

	rollbackTypeSelected = $('#rollback-type').find(":selected").val()
	switch(rollbackTypeSelected) {
	case "percentage":
		rollbackString = $('#percentageValue').val() + "% Rollback"
		break;

	case "incremental":
		rollbackString = $('#incrementValue').val() + "ug/m^3 Rollback"
		break;

	case "standard":
		if ($('#negativeRollback').prop('checked')) {
			rollbackString = "Negative Rollback to "
		} else {
			rollbackString = "Rollback to :"
		}
		rollbackString = rollbackString + $('#standards-list').find(":selected").text()
		break;
	}

	return rollbackString;
}

function setCookie(name, value, days) {
	var d = new Date();
	  d.setTime(d.getTime() + (days*24*60*60*1000));
	  var expires = "expires="+ d.toUTCString();
	  document.cookie = name + "=" + value + ";" + expires + ";path=/";
}

function getCookie(name) {
    var v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return v ? v[2] : null;
}
