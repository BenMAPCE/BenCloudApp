
export function updateStepNumber(state, stepNumber ) {
    state.stepNumber = stepNumber;
}
export function updateLocation(state, location) {
    state.locationId = location.locationId;
    state.locationName = location.locationName;
}

export function updatePollutantId (state, pollutantId ) {
    state.pollutantId = pollutantId;
}

export function updatePollutantName (state, pollutantName ) {
    state.pollutantName = pollutantName;
}

export function updatePollutantFriendlyName (state, pollutantFriendlyName ) {
    state.pollutantFriendlyName = pollutantFriendlyName;
}

export function updateIncidence(state, incidence) {
    state.incidenceId = incidence.incidenceId;
    state.incidenceName = incidence.incidenceName;
}

export function updatePopulationDataset(state, populationDataset) {
    state.populationDatasetId = populationDataset.populationDatasetId;
    state.populationDatasetName = populationDataset.populationDatasetName;
}

export function updatePopulationYear(state, populationYear) {
    state.populationYear = populationYear;
}

export function updatePopulationYears(state, populationYears) {
    state.populationYears = populationYears;
}

export function updateHealthEffects(state, healthEffects) {
    state.healthEffects = healthEffects;
}

export function updateHealthImpactFunctions(state, healthImpactFunctions) {
    state.healthImpactFunctions = healthImpactFunctions;
}

export function updatePrePolicyAirQuality(state, prePolicyAirQuality) {
    state.prePolicyAirQualityId = prePolicyAirQuality.prePolicyAirQualityId;
    state.prePolicyAirQualityName = prePolicyAirQuality.prePolicyAirQualityName;
}

export function updatePrePolicyAirQualityId(state, prePolicyAirQualityId) {
    state.prePolicyAirQualityId = prePolicyAirQualityId;
}

export function updatePrePolicyAirQualityName(state, prePolicyAirQualityName) {
    state.prePolicyAirQualityName = prePolicyAirQualityName;
}

export function updatePrePolicyAirQualityMetricId(state, metricId) {
    state.prePolicyAirQualityMetricId = metricId;
}

export function updatePostPolicyAirQuality(state, postPolicyAirQuality) {
    state.postPolicyAirQualityId = postPolicyAirQuality.postPolicyAirQualityId;
    state.postPolicyAirQualityName = postPolicyAirQuality.postPolicyAirQualityName;
}

export function updatePostPolicyAirQualityId(state, postPolicyAirQualityId) {
    state.postPolicyAirQualityId = postPolicyAirQualityId;
}

export function updatePostPolicyAirQualityName(state, postPolicyAirQualityName) {
    state.postPolicyAirQualityName = postPolicyAirQualityName;
}

export function updatePostPolicyAirQualityMetricId(state, metricId) {
    state.postPolicyAirQualityMetricId = metricId;
}

export function setValuationsForHealthImpactFunctionGroups(state, payload) {
    state.valuationsForHealthImpactFunctionGroups = payload;
}

export function updateValuationsForHealthImpactFunctionGroups(state, payload) {
    console.log("--------------------------------------------");
    //console.log(payload)

    var healthFunctionIdFound = false;

    for (var v = 0; v < state.valuationsForHealthImpactFunctionGroups.length; v++) {
      if (
        "health_function_id" in state.valuationsForHealthImpactFunctionGroups[v]
       ) {
           if (state.valuationsForHealthImpactFunctionGroups[v].health_function_id === payload.health_function_id) {
            console.log("... found health_function_id: " + payload.health_function_id);
            console.log("   need to update")
            state.valuationsForHealthImpactFunctionGroups[v].endpoint_group_id = payload.endpoint_group_id;
            state.valuationsForHealthImpactFunctionGroups[v].valuation_ids = payload.valuation_ids;
            healthFunctionIdFound = true;
        }
      }
    }
  
    if (!healthFunctionIdFound) {
      var vfhifg = {};
      vfhifg.endpoint_group_id = payload.endpoint_group_id;
      vfhifg.valuation_ids = payload.valuation_ids;
      vfhifg.health_function_id = payload.health_function_id;
      
      //console.log("@@@")
      state.valuationsForHealthImpactFunctionGroups.push(vfhifg);
    }

    console.log(state.valuationsForHealthImpactFunctionGroups)
    console.log("--------------------------------------------");



    //state.valuationsForHealthImpactFunctionGroups = valuationsForHealthImpactFunctionGroups;
}

export function updateAirQualityLayers (state, airQualityLayers) {
    state.airQualityLayers = airQualityLayers;
}

export function updateAggregationScale (state, aggregationScale) {
    state.aggregationScale = aggregationScale;
}