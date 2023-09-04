
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

export function updatePopulationDataset(state, populationDataset) {
    state.populationDatasetId = populationDataset.populationDatasetId;
    state.populationDatasetName = populationDataset.populationDatasetName;
}

export function updatePopulationYearsSelected(state, populationYearsSelected) {
    state.populationYearsSelected = populationYearsSelected;
}

export function updatePopulationYears(state, populationYears) {
    state.populationYears = populationYears;
}

export function updateExposureFunctionGroups(state, exposureFunctionGroups) {
    var ids = [];
    var names = [];
    for(var i = 0; i < exposureFunctionGroups.length; i++) {
        ids.push(exposureFunctionGroups[i].exposureGroupId);
        names.push(exposureFunctionGroups[i].exposureGroupName);
    }

    state.exposureFunctionGroupIds = ids;
    state.exposureFunctionGroupNames = names;
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

export function updatePostPolicyAirQualitySelection(state, postPolicyAirQualitySelection) {
    state.postPolicyAirQualitySelection = postPolicyAirQualitySelection;
}

export function updatePostPolicyAirQualityMetricId(state, metricId) {
    state.postPolicyAirQualityMetricId = metricId;
}

export function updateAirQualityLayers (state, airQualityLayers) {
    state.airQualityLayers = airQualityLayers;
}

export function updateAggregationScale (state, aggregationScale) {
    state.aggregationScale = aggregationScale;
}

export function updateApplyYearsToAll (state, applyYearsToAll) {
    state.applyYearsToAll = applyYearsToAll;
}

export function updateBatchTaskObject (state, batchTaskObject) {
    state.batchTaskObject = batchTaskObject;
}