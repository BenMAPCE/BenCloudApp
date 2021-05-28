
function checkNodesInCountriesSelected(treeList) {
	nodeList = treeList.getNodesByFilter(allNodesFilter);
	console.log(countriesSelected);
	//console.log(nodeList.length)
	for (var i = 0, l = nodeList.length; i < l; i++) {
		if (countriesSelected.indexOf("|" + nodeList[i].countryId + "|") > -1) {
			//console.log("should be checking: " + nodeList[i].countryId)
			treeList.checkNode(nodeList[i], true, true);
		}
	}
}

function uncheckNodesInCountriesSelected(treeList) {
	nodeList = treeList.getNodesByFilter(allNodesFilter);
	//console.log(countriesSelected);
	//console.log(nodeList.length)
	for (var i = 0, l = nodeList.length; i < l; i++) {
		if (countriesSelected.indexOf("|" + nodeList[i].countryId + "|") > -1) {
			//console.log("should be checking: " + nodeList[i].countryId)
			treeList.checkNode(nodeList[i], false, true);
		}
	}
}

function allNodesFilter(node) {
	return (true);
}

function showScenarioCard() {
	$("#rollback-settings-card").hide();
	$("#region-selections-card").hide();
	$("#mortality-valuation-card").hide();
	$("#execute-card").hide();
	$("#scenario-card").show();
};

function showSelectRegionsCard() {
	
	formValid = validateScenarioCardForm();
	if (!formValid) {
		return;
	}

	$("#scenario-card").hide();
	$("#rollback-settings-card").hide();
	$("#mortality-valuation-card").hide();
	$("#execute-card").hide();
	$("#region-selections-card").show();
};

function showRollbackSettingsCard() {
	
	formValid = validateRegionSelectionsCard();
	if (!formValid) {
		return;
	}
	
	$("#scenario-card").hide();
	$("#region-selections-card").hide();
	$("#mortality-valuation-card").hide();
	$("#execute-card").hide();
	$("#rollback-settings-card").show();
};

function showMortalityValuationCard() {

	formValid = validateRollbackCardForm();
	if (!formValid) {
		return;
	}

	$("#scenario-card").hide();
	$("#region-selections-card").hide();
	$("#rollback-settings-card").hide();
	$("#execute-card").hide();
	$("#mortality-valuation-card").show();
};

function showExecuteCard() {
	$("#scenario-card").hide();
	$("#region-selections-card").hide();
	$("#rollback-settings-card").hide();
	$("#mortality-valuation-card").hide();
	$("#execute-card").show();
};

function addToScenarios() {
	$("#mortality-valuation-card").hide();

	// if the scenario name exists... overwrite

	addScenario();

	mapLayer.setStyle(clearStyle);
	uncheckNodesInCountriesSelected(countriesTree)
	uncheckNodesInCountriesSelected(regionsTree)

	loadScenario(newScenario);

	$("#scenario-card").show();
}

function validateScenarioCardForm() {
	
	formValid = true;
	errorMessage = "";
	
	scenarioName = $('#scenarioName').val();
	
	if (scenarioName.trim() == "") {
		errorMessage = "A Scenario Name is required";
		formValid = false;	
	}
	
	if (!formValid) {
		$('#scenario-card-form-error').html(errorMessage);
		$('#scenario-card-form-error').show();
	} else {
		$('#scenario-card-form-error').html("");
		$('#scenario-card-form-error').hide();
	}

	return formValid;
}


function validateRegionSelectionsCard() {
	
	formValid = true;
	errorMessage = "";

	countriesCheckCount = countriesTree.getCheckedNodes(true).length;
	regionsCheckCount = regionsTree.getCheckedNodes(true).length;
	
	if ((regionsCheckCount + countriesCheckCount) == 0) {	
		errorMessage = "At least one country/region must be selected";
		formValid = false;
	}

	if (!formValid) {
		$('#regions-card-form-error').html(errorMessage);
		$('#regions-card-form-error').show();
	} else {
		$('#regions-card-form-error').html("");
		$('#regions-card-form-error').hide();
	}

	return formValid;
}

function validateRollbackCardForm() {
	
	formValid = true;
	errorMessage = "";
	
	rollbackTypeSelected = $('#rollback-type').find(":selected").val()
	
	switch(rollbackTypeSelected) {
	case "percentage":
		percentageValue = $('#percentageValue').val();
		if (percentageValue.trim() == "") {
			errorMessage = "Percentage Value is required";
			formValid = false;
		}
		if (isNaN(percentageValue)) {
			errorMessage = "Percentage Value must be a number";
			formValid = false;
		}
		if (parseInt(percentageValue) > 100) {
			errorMessage = "Percentage Value can not be > 100";
			formValid = false;
		}
		if (parseInt(percentageValue) < 0) {
			errorMessage = "Percentage Value must be > 0";
			formValid = false;
		}

		break;

	case "incremental":
		incrementValue = $('#incrementValue').val();
		
		if (incrementValue.trim() == "") {
			errorMessage = "Increment Value is required";
			formValid = false;
		}
		if (isNaN(incrementValue)) {
			errorMessage = "Increment Value must be a number";
			formValid = false;
		}
		break;

	}
	if (!formValid) {
		$('#rollback-card-form-error').html(errorMessage);
		$('#rollback-card-form-error').show();
	} else {
		$('#rollback-card-form-error').html("");
		$('#rollback-card-form-error').hide();
	}

	return formValid;
}