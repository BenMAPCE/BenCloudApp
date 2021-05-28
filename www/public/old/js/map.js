
var map;
var mapLayer;
var myGeoJSONPath = 'geo/custom.geo-50.json';

function clearStyle(feature) {
	//console.log(getColor(feature.properties.sov_a3))
	return {
		stroke: true,

		fillColor: "#fff",
		opacity: 1,
		color: '#999',
		fillOpacity: 0.7,
		weight: .5,

	};
}

function mystyle(feature) {
	//console.log(getColor(feature.properties.sov_a3))
	return {
		stroke: true,

		fillColor: getColor(feature.properties.iso_a3),
		opacity: 1,
		color: '#999',
		fillOpacity: 0.7,
		weight: .5,

	};
}

function getColor(d) {
	//console.log(countriesSelected.length)
	if (countriesSelected.indexOf("|" + d + "|") > -1) {
		return '#FC4E2A'
	} else {
		return "#fff"
	}
}



function style(feature) {
    //console.log(getColor(feature.properties.iso_a3))
    return {
        stroke: true,
        fillColor: '#fff',
        opacity: 1,
        color: '#999',
        fillOpacity: 0.7,
        weight: .5,

    };
}



$.getJSON(myGeoJSONPath, function(data) {
    mapLayer = L.geoJson(data, {
        clickable: false,
        style: style,
        onEachFeature: onEachFeature

    }).addTo(map);
})




var onEachFeature = function(feature, layer) {

    layer.on('click', function (e) {
        var popup = L.popup()
            .setLatLng(e.latlng)
            .setContent(e.target.feature.properties.sovereignt + 
                        " (" + e.target.feature.properties.iso_a3 + ")" + "<br/>" + 
                       "Population: " + countryPopulation[e.target.feature.properties.iso_a3])
            .openOn(map);   
        });
};

