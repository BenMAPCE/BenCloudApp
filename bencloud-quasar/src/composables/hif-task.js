import { useStore } from "vuex";
import axios from "axios";
import { ref } from "vue";

export const buildHifTask = (param) => {
  const store = useStore();

  const pollutantId = store.state.analysis.pollutantId;
  const pollutantFriendlyName = store.state.analysis.pollutantFriendlyName;
  const prePolicyAirQualityId = store.state.analysis.prePolicyAirQualityId;
  const prePolicyAirQualityName = store.state.analysis.prePolicyAirQualityName;
  const postPolicyAirQualityId = store.state.analysis.postPolicyAirQualityId;
  const postPolicyAirQualityName =
    store.state.analysis.postPolicyAirQualityName;

  const incidenceId = store.state.analysis.incidenceId;
  const incidenceName = store.state.analysis.incidenceName;
  const populationDatasetId = store.state.analysis.populationDatasetId;
  const populationDatasetName = store.state.analysis.populationDatasetName;
  const populationYear = store.state.analysis.populationYear;

  const healthEffectsIds = ref([]);
  const healthEffectsNames = ref("");

  const healthImpactFunctions = store.state.analysis.healthImpactFunctions;

  const fetch = async () => {
    var hifInfo = {};

    hifInfo["name"] = "TEST 001";
    hifInfo["type"] = "HIF";

    var airQualityArray = [];
    var airQuality = {};

    airQuality["type"] = "baseline";
    airQuality["id"] = prePolicyAirQualityId;
    airQualityArray.push(airQuality);

    airQuality = {};
    airQuality["type"] = "scenario";
    airQuality["id"] = prePolicyAirQualityId;
    airQualityArray.push(airQuality);

    hifInfo["airQualityData"] = airQualityArray

    var population = {};
    population["id"] = populationDatasetId
    population["year"] = populationYear

    hifInfo["population"] = population;

    var functionsArray = []
    var functions = JSON.parse(JSON.stringify(healthImpactFunctions))
    for (var i = 0; i < functions.length; i++) {
        delete functions[i]["pollutant_id"]
        delete functions[i]["author"]
        delete functions[i]["qualifier"]

        delete functions[i]["race_name"]
        delete functions[i]["gender_name"]
        delete functions[i]["ethnicity_name"]
        delete functions[i]["incidence_dataset_name"]
        delete functions[i]["prevalence_dataset_name"]



        delete functions[i]["health_impact_function_dataset_id"]
        delete functions[i]["endpoint_group_id"]
        delete functions[i]["endpoint_id"]
        delete functions[i]["metric_id"]
        delete functions[i]["seasonal_metric_id"]
        delete functions[i]["metric_statistic"]
        delete functions[i]["function_year"]
        delete functions[i]["location"]
        delete functions[i]["other_pollutants"]
        delete functions[i]["reference"]
        delete functions[i]["function_text"]
        delete functions[i]["variable_dataset_id"]
        delete functions[i]["beta"]
        delete functions[i]["dist_beta"]
        delete functions[i]["p1_beta"]
        delete functions[i]["p2_beta"]
        delete functions[i]["val_a"]
        delete functions[i]["name_a"]
        delete functions[i]["val_b"]
        delete functions[i]["name_b"]
        delete functions[i]["val_c"]
        delete functions[i]["name_c"]
        delete functions[i]["baseline_function_text"]
        delete functions[i]["start_day"]
        delete functions[i]["end_day"]
        delete functions[i]["endpoint_group_name"]
        delete functions[i]["endpoint_name"]

        functionsArray.push(functions[i])
    }


    hifInfo["functions"] = functionsArray;


    console.log(JSON.stringify(hifInfo));

    /*
        loading.value = true;
        try {

            const result = await axios
                .get(store.state.app.apiServerURL + "/api/air-quality-data", {
            params: {
                page: 1,
                rowsPerPage: 9999999,
                pollutantId: store.state.analysis.pollutantId              
                },
            })
            .then((response) => {
                data.value = response.data
            })
        } catch (ex) {
            error.value = ex;
        } finally {
            loading.value = false;
            
            return {response, error, data, loading }
        }
*/
  };

  return { fetch };
};

export const loadAirQualityLayers = (url) => {
  const data = ref(null);
  const error = ref(null);
  const response = ref(null);
  const loading = ref(false);
  const store = useStore();

  const fetch = async () => {
    loading.value = true;
    try {
      const result = await axios
        .get(store.state.app.apiServerURL + "/api/air-quality-data", {
          params: {
            page: 1,
            rowsPerPage: 9999999,
            pollutantId: store.state.analysis.pollutantId,
          },
        })
        .then((response) => {
          data.value = response.data;
        });
    } catch (ex) {
      error.value = ex;
    } finally {
      loading.value = false;

      return { response, error, data, loading };
    }
  };

  return { fetch };
};

export const convertAirQualityLayers = (data) => {
  var records = JSON.parse(JSON.stringify(data)).records;
  console.log(records);
  var options = [];
  var option = {};

  for (var i = 0; i < records.length; i++) {
    option = {};
    option.value = records[i].id;
    option.label = records[i].name;
    console.log(option);
    options.push(option);
  }

  return options;
};
