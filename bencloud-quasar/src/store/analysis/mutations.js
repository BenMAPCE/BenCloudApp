export function updatePollutantId (state, pollutantId ) {
    state.pollutantId = pollutantId;
}

export function updatePollutantName (state, pollutantName ) {
    state.pollutantName = pollutantName;
}

export function updatePollutantFriendlyName (state, pollutantFriendlyName ) {
    state.pollutantFriendlyName = pollutantFriendlyName;
}

export function updateIncidence(state, incidenceId, incidenceName) {
    state.incidenceId = incidenceId;
    state.incidenceName = incidenceName;
}

export function updatePopulationDataset(state, populationDatasetId, populationDatasetName) {
    state.populationDatasetId = populationDatasetId;
    state.populationDatasetName = populationDatasetName;
}

export function updatePopulationYear(state, populationYear) {
    state.populationYear = populationYear;
}

export function updatePopulationYears(state, populationYears) {
    state.populationYears = populationYears;
}

export function updateAQ(state, aq) {
    state.aq = aq;
}

export function updateHealthEffects(state, healthEffects) {
    state.healthEffects = healthEffects;
}

export function updatePrePolicyAirQuality(state, prePolicyAirQuality) {
    state.prePolicyAirQuality = prePolicyAirQuality;
}

export function updatePostPolicyAirQuality(state, postPolicyAirQuality) {
    state.postPolicyAirQuality = postPolicyAirQuality;
}

export function updateValuationsForHealthImpactFunctionGroups(state, payload) {
    console.log("--------------------------------------------");
    console.log(payload)

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
      
      console.log("@@@")
      state.valuationsForHealthImpactFunctionGroups.push(vfhifg);
    }

    console.log(state.valuationsForHealthImpactFunctionGroups)
    console.log("--------------------------------------------");



    //state.valuationsForHealthImpactFunctionGroups = valuationsForHealthImpactFunctionGroups;
}

