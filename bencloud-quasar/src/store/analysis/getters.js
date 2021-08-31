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

export function populationYear (state) {
  return state.populationYears
}

export function aq (state) {
  return state.aq
}

export function healthEffects (state) {
  return state.healthEffects
}

export function prePolicyAirQuality (state) {
  return state.prePolicyAirQuality
}

export function postPolicyAirQuality (state) {
  return state.postPolicyAirQuality
}

export function valuationsForHealthImpactFunctionGroups (state) {
  return state.valuationsForHealthImpactFunctionGroups
}

export const getValuationsForHealthFunctionId = (state) => (healthFunctionId) => {

  console.log("######")
  console.log(healthFunctionId)
  console.log(state.valuationsForHealthImpactFunctionGroups)
  console.log("######")

  for (var v = 0; v < state.valuationsForHealthImpactFunctionGroups.length; v++) {
    console.log("$$$")
    console.log(state.valuationsForHealthImpactFunctionGroups[v])
    if (
      "health_function_id" in state.valuationsForHealthImpactFunctionGroups[v]
     ) {
         if (state.valuationsForHealthImpactFunctionGroups[v].health_function_id === healthFunctionId) {
          console.log("... found health_function_id: " + healthFunctionId);
          return state.valuationsForHealthImpactFunctionGroups[v];
      }
    }
  }

  return {};
}



