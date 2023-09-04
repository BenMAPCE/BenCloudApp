
import { ref } from "vue";
import axios from "axios";

export const buildExposureBatchTask = (store) => {
  const data = ref(null);
  const error = ref(null);
  const response = ref(null);
  const loading = ref(false);


  const fetch = async (store) => {
    loading.value = true;
    try {
      var postPolicyItems = store.state.exposure.postPolicyAirQualityName;
      var postPolicyIds = store.state.exposure.postPolicyAirQualityId;
      var postPolicy = "";
      if(postPolicyItems.length === postPolicyIds.length) {
        for(var i = 0; i < postPolicyIds.length; i++) {
          postPolicy += postPolicyIds[i] + "|";
          for(var j = 0; j < postPolicyItems[i].years.length; j++) {
            postPolicy += postPolicyItems[i].years[j];
            if(j < postPolicyItems[i].years.length - 1) {
              postPolicy += "~";
            }
          }
          if(i < postPolicyIds.length - 1) {
            postPolicy += ",";
          }
        }
      }

      const result = await axios
      .get(
        process.env.API_SERVER +
          "/api/batch-task-config?" +
          "efGroupIds=" +
          store.state.exposure.exposureFunctionGroupIds +
          "&pollutantId=" +
          store.state.exposure.pollutantId +
          "&baselineId=" +
          store.state.exposure.prePolicyAirQualityId + 
          "&populationId=" +
          store.state.exposure.populationDatasetId +
          "&gridDefinitionId=" +
          store.state.exposure.aggregationScale +
          "&scenarios=" +
          postPolicy + 
          "&incidencePrevalenceDataset=" +
          store.state.exposure.incidenceId,
        {
          params: {},
        }
      )
      .then((response) => {
        data.value = response.data;
        console.log(data.value)
        store.commit("exposure/updateBatchTaskObject", data.value);
      });
    } catch (ex) {
      error.value = ex;
      console.log("oops!");
    } finally {
      loading.value = false;

      return { response, error, data, loading };
    }
  };

  return { fetch };
}

export const buildExposureTaskJSON = (taskName, store) => {

  const prePolicyAirQualityId = store.state.exposure.prePolicyAirQualityId;
  const postPolicyAirQualityId = store.state.exposure.postPolicyAirQualityId;
  const populationDatasetId = store.state.exposure.populationDatasetId;
  const populationGroups = store.state.exposure.populationGroups;

  var exposureInfo = {};

  exposureInfo["name"] = taskName;
  exposureInfo["type"] = "Exposure";

  var airQualityArray = [];
  var airQuality = {};

  airQuality["type"] = "baseline";
  airQuality["id"] = prePolicyAirQualityId;
  airQualityArray.push(airQuality);

  airQuality = {};
  airQuality["type"] = "scenario";
  airQuality["id"] = postPolicyAirQualityId;
  airQualityArray.push(airQuality);

  exposureInfo["air_quality_data"] = airQualityArray;

  var population = {};
  population["id"] = populationDatasetId;

  exposureInfo["population"] = population;

  exposureInfo["populationGroups"] = populationGroups;

  return exposureInfo;
};

export const submitExposureTask = (exposureTaskJSON, store) => {
  const data = ref(null);
  const error = ref(null);
  const loading = ref(false);
    
  const fetch = async () => {
    console.log("submitting exposureTask")
    console.log(exposureTaskJSON)
    loading.value = true;

    try {
      const {data:response} = await axios
      .post(process.env.API_SERVER + "/api/batch-tasks", exposureTaskJSON)
      .then((response) => {
        data.value = response.data;
        console.log(data.value);
        return data.value;
      });
    } catch (ex) {
        error.value = ex;
        if(!!error.value.response) {
          console.log(error.value.response.status + " error occured");
        }
    } 
  };
  return { fetch };
}