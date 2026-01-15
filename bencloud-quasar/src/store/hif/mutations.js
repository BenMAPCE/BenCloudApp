
export function updatePollutantId (state, pollutantId) {
    state.pollutantId = pollutantId;
}

export function updatePollutantFriendlyName (state, pollutantFriendlyName ) {
    state.pollutantFriendlyName = pollutantFriendlyName;
}

export function updateHifGroupId (state, hifGroupId) {
    state.hifGroupId = hifGroupId;
}

export function updateHifGroupName (state, hifGroupName ) {
    state.hifGroupName = hifGroupName;
}

export function updateHifLayerAddedDate (state, newDate) {
    state.hifLayerAddedDate = newDate;
}

export function updateHifForceReloadValue (state, newValue) {
    state.hifForceReloadValue = newValue;
}

export function updateHifGroupForceReloadValue (state, newValue) {
    state.hifGroupForceReloadValue = newValue;
}