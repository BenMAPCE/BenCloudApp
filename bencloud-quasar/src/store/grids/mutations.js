
export function updateGridId(state, gridId) {
    state.gridId = gridId;
}

export function updateGridAddedDate (state, newDate) {
    state.gridAddedDate = newDate;
}

export function updateGridForceReloadValue (state, newValue) {
    state.gridForceReloadValue = newValue;
}

export function addVisibleLayer(state, layerName) {
    if (!state.visibleLayers.includes(layerName)) {
      state.visibleLayers.push(layerName);
    }
}
export function removeVisibleLayer(state, layerName) {
    state.visibleLayers = state.visibleLayers.filter(layer => layer !== layerName);
}