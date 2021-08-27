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

