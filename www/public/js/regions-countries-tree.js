

var setting = {
		view: {
			selectedMulti: false
		},
		check: {
			enable: true
		},
		data: {
			simpleData: {
				enable: true
			}
		},
		callback: {
			onCheck: colorCountry
		},
		view: {
			showIcon: false
		},
};

var clearFlag = false;

function colorCountry(event, treeId, treeNode, clickFlag) {
    if (treeNode.isParent) {
        var childNodes = treeNode.children;
        for (var i = 0; i < childNodes.length; i++) {
            if (treeNode.checked) {
                addCountry(childNodes[i].countryId)
            } else {
                removeCountry(childNodes[i].countryId)
            }
            mapLayer.setStyle(mystyle);
            //console.log(countriesSelected)
        }

    } else {
        if (treeNode.checked) {
            addCountry(treeNode.countryId)
        } else {
            removeCountry(treeNode.countryId)
        }
        mapLayer.setStyle(mystyle);
    }
}

function addCountry(countryId) {
    if (countriesSelected.indexOf("|" + countryId + "|") == -1) {
        countriesSelected = countriesSelected.concat("|" + countryId + "|")
    }
}

function removeCountry(countryId) {
    countriesSelected = countriesSelected.replace("|" + countryId + "|", "");
}

function createTree() {
    regionsTree = $.fn.zTree.init($("#regionsTree"), setting, regionCountries);
    countriesTree = $.fn.zTree.init($("#countriesTree"), setting, countries);
    //count();
    clearFlag = $("#last").attr("checked");
}
