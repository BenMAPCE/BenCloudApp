
import { ref } from "vue";
import axios from "axios";
import { buildValuationTaskJSON } from "./valuation-task";
import { submitValuationTask } from "./valuation-task";

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

  const valuationsForHealthImpactFunctionGroups =
    store.state.exposure.valuationsForHealthImpactFunctionGroups;
    
  const fetch = async () => {
    console.log("submitting exposureTask")
    console.log(exposureTaskJSON)
    console.log(valuationsForHealthImpactFunctionGroups.length)
    loading.value = true;

    try {
      const {data:response} = await axios
      .post(process.env.API_SERVER + "/api/wxposure-tasks", exposureTaskJSON)
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