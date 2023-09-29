
import { ref } from "vue";
import axios from "axios";
import { populationDatasetName } from "src/store/analysis/getters";

export const buildBatchTaskJSON = (taskName, store) => {

  const prePolicyAirQualityId = store.state.analysis.prePolicyAirQualityId;
  const postPolicyAirQuality = store.state.analysis.postPolicyAirQualityName;
  const populationDatasetId = store.state.analysis.populationDatasetId;
  const healthImpactFunctions = store.state.analysis.healthImpactFunctions;

  const valuationsForHealthImpactFunctionGroups =
    store.state.analysis.valuationsForHealthImpactFunctionGroups;

  var hifInfo = {};

  hifInfo["name"] = taskName;
  hifInfo["type"] = "HIF";

  var airQualityArray = [];
  var airQuality = {};

  airQuality["type"] = "baseline";
  airQuality["id"] = prePolicyAirQualityId;
  airQualityArray.push(airQuality);

  airQuality = {};
  airQuality["type"] = "scenario";
  airQuality["id"] = postPolicyAirQualityId;
  airQualityArray.push(airQuality);

  hifInfo["air_quality_data"] = airQualityArray;

  var population = {};
  population["id"] = populationDatasetId;

  hifInfo["population"] = population;

  var functionsArray = [];
  var functions = JSON.parse(JSON.stringify(healthImpactFunctions));
  for (var i = 0; i < functions.length; i++) {
    delete functions[i]["pollutant_id"];
    delete functions[i]["author"];
    delete functions[i]["qualifier"];

    delete functions[i]["race_name"];
    delete functions[i]["gender_name"];
    delete functions[i]["ethnicity_name"];
    delete functions[i]["incidence_dataset_name"];
    delete functions[i]["prevalence_dataset_name"];

    delete functions[i]["health_impact_function_dataset_id"];
    delete functions[i]["endpoint_group_id"];
    delete functions[i]["endpoint_id"];
    delete functions[i]["metric_id"];
    delete functions[i]["seasonal_metric_id"];
    delete functions[i]["metric_statistic"];
    delete functions[i]["function_year"];
    delete functions[i]["location"];
    delete functions[i]["other_pollutants"];
    delete functions[i]["reference"];
    delete functions[i]["function_text"];
    delete functions[i]["variable_dataset_id"];
    delete functions[i]["beta"];
    delete functions[i]["dist_beta"];
    delete functions[i]["p1_beta"];
    delete functions[i]["p2_beta"];
    delete functions[i]["val_a"];
    delete functions[i]["name_a"];
    delete functions[i]["val_b"];
    delete functions[i]["name_b"];
    delete functions[i]["val_c"];
    delete functions[i]["name_c"];
    delete functions[i]["baseline_function_text"];
    delete functions[i]["start_day"];
    delete functions[i]["end_day"];
    delete functions[i]["endpoint_group_name"];
    delete functions[i]["endpoint_name"];

    functionsArray.push(functions[i]);
  }
  hifInfo["functions"] = functionsArray;

  return hifInfo;
};

export const submitBatchTask = (batchTaskJSON, store) => {
  const data = ref(null);
  const error = ref(null);
  const response = ref(null);
  const loading = ref(false);

  const valuationsForHealthImpactFunctionGroups =
    store.state.analysis.valuationsForHealthImpactFunctionGroups;
    
  const fetch = async () => {
    console.log("submitting batchTask")
    console.log(batchTaskJSON)
    console.log(valuationsForHealthImpactFunctionGroups.length)
    loading.value = true;

    try {
      const {data:response} = await axios
      .post(process.env.API_SERVER + "/api/batch-tasks", batchTaskJSON)
      .then((response) => {
        data.value = response.data;
        //console.log(data.value);
        //return data.value;
        return { response, error, data, loading };
      });
    } catch (ex) {
        error.value = ex;
        if(!!error.value.response) {
          console.log(error.value.response.status + " error occured");
        }
    } 
    finally{
      loading.value=false;
      return {response, error, data, loading};
    }
  };
  return { fetch };
}