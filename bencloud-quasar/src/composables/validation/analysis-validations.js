

export const pollutantIdHasValue = (store) => {

    var isValid = true;

    if (store.state.analysis.pollutantId) {
        if (store.state.analysis.pollutantId === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const postPolicyAirQualityIdHasValue = (store) => {

    var isValid = true;

    if (store.state.analysis.postPolicyAirQualityId) {
        if (store.state.analysis.postPolicyAirQualityId.length === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const prePolicyAirQualityIdHasValue = (store) => {

    var isValid = true;

    if (store.state.analysis.prePolicyAirQualityId) {
        if (store.state.analysis.prePolicyAirQualityId === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const populationDatasetIdHasValue = (store) => {

    var isValid = true;

    if (store.state.analysis.populationDatasetId) {
        if (store.state.analysis.populationDatasetId === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const populationYearsHaveValue = (store) => {

    var isValid = true;

    if (store.state.analysis.postPolicyAirQualityName) {
        for(let i = 0; i < store.state.analysis.postPolicyAirQualityName.length; i++) {
            if(store.state.analysis.postPolicyAirQualityName[i].years.length === 0) {
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

    if (store.state.analysis.incidenceId) {
        if (store.state.analysis.incidenceId === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const healthEffectsHasValue = (store) => {

    var isValid = true;

    if (store.state.analysis.healthEffects) {
        if ((store.state.analysis.healthEffects).length === 0) {
            isValid = false;
        }
    } else {
        isValid = false;
    }

    return isValid
}

export const aggregationScaleHasValue = (store) => {

    var isValid = true
    if(store.state.analysis.aggregationScale) {
        if((store.state.analysis.aggregationScale) === 0) {
            isValid = false;
        } 
    } else {
        isValid = false;
    }
    return isValid;
}
