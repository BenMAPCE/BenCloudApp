export function stepNumber (state) {
  return state.stepNumber
}
export function locationId (state) {
  return state.locationId
}

export function pollutantId (state) {
    return state.pollutantId
}

export function pollutantName (state) {
  return state.pollutantName
}

export function pollutantFriendlyName (state) {
  return state.pollutantFriendlyName
}

export function incidenceId (state) {
  console.log("--- getting incidenceId: " + state.incidenceId)
  return state.incidenceId
}

export function incidenceName (state) {
  console.log("--- getting incidenceName: " + state.incidenceName)
  return state.incidenceName
}

export function populationDatasetId (state) {
  return state.populationDatasetId
}

export function populationDatasetName (state) {
  return state.populationDatasetName
}

export function populationYears (state) {
  return state.populationYears
}

export function healthEffects (state) {
  return state.healthEffects
}

export function healthImpactFunctions (state) {
  return state.healthImpactFunctions
}

export function prePolicyAirQualityId (state) {
  return state.prePolicyAirQualityId
}

export function prePolicyAirQualityName (state) {
  return state.prePolicyAirQualityName
}

export function prePolicyAirQualityMetricId (state) {
  return state.prePolicyAirQualityMetricId
}

export function postPolicyAirQualityId (state) {
  return state.postPolicyAirQualityId
}

export function postPolicyAirQualityName (state) {
  return state.postPolicyAirQualityName
}

export function postPolicyAirQualityMetricId (state) {
  return state.postPolicyAirQualityMetricId
}

export function valuationsForHealthImpactFunctionGroups (state) {
  return state.valuationsForHealthImpactFunctionGroups
}

export const getValuationsForHealthFunctionId = (state) => (healthFunctionId) => {

  // console.log("######")
  // console.log(healthFunctionId)
  // console.log(state.valuationsForHealthImpactFunctionGroups)
  // console.log("######")

  for (var v = 0; v < state.valuationsForHealthImpactFunctionGroups.length; v++) {
    // console.log("$$$")
    // console.log(state.valuationsForHealthImpactFunctionGroups[v])
    if (
      "health_function_id" in state.valuationsForHealthImpactFunctionGroups[v]
     ) {
         if (state.valuationsForHealthImpactFunctionGroups[v].health_function_id === healthFunctionId) {
          //console.log("... found health_function_id: " + healthFunctionId);
          return state.valuationsForHealthImpactFunctionGroups[v];
      }
    }
  }

  return {};
}

export function airQualityLayers (state) {
  return state.airQualityLayers
}

export function aggegationScale (state) {
  return state.aggegationScale;
}

export function applyYearsToAll (state) {
  return state.applyYearsToAll;
}

export function batchTaskObject (state) {
  return state.batchTaskObject;
}

