

export const pollutantIdHasValue = (store) => {

    var isValid = true;

    if (store.state.exposure.pollutantId) {
        if (store.state.exposure.pollutantId === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const postPolicyAirQualityIdHasValue = (store) => {

    var isValid = true;

    if (store.state.exposure.postPolicyAirQualityId.length) {
        if (store.state.exposure.postPolicyAirQualityId === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const prePolicyAirQualityIdHasValue = (store) => {

    var isValid = true;

    if (store.state.exposure.prePolicyAirQualityId) {
        if (store.state.exposure.prePolicyAirQualityId === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const populationDatasetIdHasValue = (store) => {

    var isValid = true;

    if (store.state.exposure.populationDatasetId) {
        if (store.state.exposure.populationDatasetId === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const populationYearsHaveValue = (store) => {

    var isValid = true;

    if (store.state.exposure.postPolicyAirQualityName) {
        for(let i = 0; i < store.state.exposure.postPolicyAirQualityName.length; i++) {
            if(store.state.exposure.postPolicyAirQualityName[i].years.length === 0) {
                isValid = false;
            }
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const incidenceIdHasValue = (store) => {

    var isValid = true;

    if (store.state.exposure.incidenceId) {
        if (store.state.exposure.incidenceId === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const exposureFunctionGroupsHasValue = (store) => {

    var isValid = true;

    if (store.state.exposure.exposureFunctionGroupIds) {
        if ((store.state.exposure.exposureFunctionGroupIds).length === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}