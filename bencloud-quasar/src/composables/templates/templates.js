import { useStore } from "vuex";
import axios from "axios";
import { ref } from "vue";
import { v5 as uuidv5 } from "uuid";

import { loadHealthImpactFunctionGroups } from "../analysis/health-impact-function-groups";
import { buildHealthImpactFunctionGroups } from "../analysis/health-impact-function-groups";
import { loadValuationFunctions } from "../../composables/analysis/valuation-functions";

export const getTemplates = () => {
  console.log("getting templates...");

  const data = ref(null);
  const error = ref(null);
  const response = ref(null);
  const loading = ref(false);
  const store = useStore();

  const fetch = async () => {
    loading.value = true;
    try {
      const result = await axios
        .get(process.env.API_SERVER + "/api/task-configs", {})
        .then((response) => {
          data.value = response.data;
          console.log("----- TEMPLATES -----");
          console.log(JSON.parse(JSON.stringify(data.value)));
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

export const saveTemplate = (name, type, template, store) => {
  const data = ref(null);
  const error = ref(null);
  const response = ref(null);
  const loading = ref(false);

  console.log("SUBMITTING TEMPLATE...")

  var taskConfig = {};
  taskConfig["name"] = name; 
  taskConfig["type"] = type; 
  taskConfig["parameters"] = template; 

  console.log(taskConfig);

  const fetch = async () => {
    loading.value = true;
    try {
      const result = await axios
        .post(process.env.API_SERVER + "/api/task-configs", taskConfig)
        .then((response) => {
          data.value = response.data;
          console.log(data.value);
        });
    } catch (ex) {
      error.value = ex;
      console.log(ex)
    } finally {
      loading.value = false;

      return { response, error, data, loading };
    }
  };

  return { fetch };
};

export const createTemplate = (taskName, store) => {
  const prePolicyAirQualityId = store.state.analysis.prePolicyAirQualityId;
  const postPolicyAirQualityId = store.state.analysis.postPolicyAirQualityId;
  const prePolicyAirQualityName = store.state.analysis.prePolicyAirQualityName;
  const postPolicyAirQualityName =
    store.state.analysis.postPolicyAirQualityName;
  const prePolicyAirQualityMetricId =
    store.state.analysis.prePolicyAirQualityMetricId;
  const postPolicyAirQualityMetricId =
    store.state.analysis.postPolicyAirQualityMetricId;
  const populationDatasetId = store.state.analysis.populationDatasetId;
  const populationDatasetName = store.state.analysis.populationDatasetName;
  const populationYear = store.state.analysis.populationYear;
  const incidenceId = store.state.analysis.incidenceId;
  const incidenceName = store.state.analysis.incidenceName;
  const pollutantId = store.state.analysis.pollutantId;
  const pollutantFriendlyName = store.state.analysis.pollutantFriendlyName;

  const healthEffects = store.state.analysis.healthEffects;

  const healthImpactFunctions = store.state.analysis.healthImpactFunctions;
  const valuationsForHealthImpactFunctionGroups =
    store.state.analysis.valuationsForHealthImpactFunctionGroups;

  var template = {};

  console.log("-------------------------------");
  console.log(store.state.analysis.valuationsForHealthImpactFunctionGroups);
  console.log("-------------------------------");

  template["name"] = taskName;
  template["type"] = "HIF";

  var pollutant = {};
  pollutant["pollutantId"] = pollutantId;
  pollutant["pollutantFriendlyName"] = pollutantFriendlyName;
  template["pollutant"] = pollutant;

  var airQualityArray = [];
  var airQuality = {};

  var prePolicyAirQualityMetric = prePolicyAirQualityMetricId.split("-");
  var postPolicyAirQualityMetric = postPolicyAirQualityMetricId.split("-");

  airQuality["type"] = "baseline";
  airQuality["id"] = prePolicyAirQualityId;
  airQuality["metricId"] = prePolicyAirQualityMetric[0];
  airQuality["seasonalMetricId"] = prePolicyAirQualityMetric[1];
  airQuality["name"] = prePolicyAirQualityName;
  airQualityArray.push(airQuality);

  airQuality = {};
  airQuality["type"] = "scenario";
  airQuality["id"] = postPolicyAirQualityId;
  airQuality["metricId"] = postPolicyAirQualityMetric[0];
  airQuality["seasonalMetricId"] = postPolicyAirQualityMetric[1];
  airQuality["name"] = postPolicyAirQualityName;
  airQualityArray.push(airQuality);

  template["air_quality_data"] = airQualityArray;

  var population = {};
  population["populationDatasetId"] = populationDatasetId;
  population["populationDatasetName"] = populationDatasetName;
  population["populationYear"] = populationYear;
  population["incidenceId"] = incidenceId;
  population["incidenceName"] = incidenceName;

  template["population"] = population;

  var selectedHealthEffects = [];
  var selectedHealthEffect = {};

  console.log("***** " + healthEffects.length);
  for (var he = 0; he < healthEffects.length; he++) {
    selectedHealthEffect = {};
    selectedHealthEffect["healthEffectId"] = healthEffects[he].healthEffectId;
    selectedHealthEffect["healthEffectName"] =
      healthEffects[he].healthEffectName;
    selectedHealthEffects.push(selectedHealthEffect);
  }
  template["healthEffects"] = selectedHealthEffects;

  var functionsArray = [];
  var hifFunction = {};
  var functions = JSON.parse(JSON.stringify(healthImpactFunctions));

  for (var hf = 0; hf < functions.length; hf++) {
    hifFunction = {};
    hifFunction["hifId"] = functions[hf]["id"];

    var valuationFunctions = [];
    var valuationFunction = {};

    for (var i = 0; i < valuationsForHealthImpactFunctionGroups.length; i++) {
      for (
        var j = 0;
        j < valuationsForHealthImpactFunctionGroups[i].valuation_ids.length;
        j++
      ) {
        if (
          functions[hf]["id"] ===
          valuationsForHealthImpactFunctionGroups[i].health_function_id
        ) {
          valuationFunction = {};
          valuationFunction["vfId"] =
            valuationsForHealthImpactFunctionGroups[i].valuation_ids[j];
          valuationFunctions.push(valuationFunction);
        }
        //console.log("v " + valuationsForHealthImpactFunctionGroups[i].health_function_id);
        //valuationFunction = {};
        //valuationFunction["hif_id"] = valuationsForHealthImpactFunctionGroups[i].health_function_id;
        //valuationFunction["vf_id"] = valuationsForHealthImpactFunctionGroups[i].valuation_ids[j];
        //valuationFunctions.push(valuationFunction);
      }
    }
    if (valuationFunctions.length > 0) {
      hifFunction["vfIds"] = valuationFunctions;
    }

    hifFunction["start_age"] = functions[hf]["start_age"];
    hifFunction["end_age"] = functions[hf]["end_age"];
    hifFunction["race_id"] = functions[hf]["race_id"];
    hifFunction["gender_id"] = functions[hf]["gender_id"];
    hifFunction["ethnicity_id"] = functions[hf]["ethnicity_id"];
    hifFunction["incidence_dataset_id"] = functions[hf]["incidence_dataset_id"];
    hifFunction["incidence_year"] = functions[hf]["incidence_year"];
    hifFunction["prevalence_dataset_id"] =
      functions[hf]["prevalence_dataset_id"];
    hifFunction["prevalence_year"] = functions[hf]["prevalence_year"];

    functionsArray.push(hifFunction);
  }

  template["functions"] = functionsArray;

  const MY_NAMESPACE = "1b671a64-40d5-491e-99b0-da01ff1f3341";
  const hash = uuidv5(JSON.stringify(template), MY_NAMESPACE);
  console.log(hash);

  template["hash"] = hash;
  const hashWithHash = uuidv5(JSON.stringify(template), MY_NAMESPACE);
  console.log(hashWithHash);
  console.log(template);

  //delete template["hash"]
  //const hashRemoveHash = uuidv5(JSON.stringify(template), MY_NAMESPACE);
  //console.log(hashRemoveHash)

  return template;
};


export const loadTemplate = (model, store) => {
  var parameters = model.parameters;
  console.log(parameters);

  var pollutant = parameters.pollutant;

  store.commit("analysis/updatePollutantId", pollutant.pollutantId);
  store.commit(
    "analysis/updatePollutantFriendlyName",
    pollutant.pollutantFriendlyName
  );

  var air_quality_data = parameters.air_quality_data;
  if (air_quality_data[0].type === "baseline") {
    console.log("baseline: " + air_quality_data[0].id);
    store.commit("analysis/updatePrePolicyAirQuality", {
      prePolicyAirQualityId: air_quality_data[0].id,
      prePolicyAirQualityName: air_quality_data[0].name,
    });
    store.commit(
      "analysis/updatePrePolicyAirQualityMetricId",
      air_quality_data[0].metricId + "-" + air_quality_data[0].seasonalMetricId
    );

    store.commit("analysis/updatePostPolicyAirQuality", {
      postPolicyAirQualityId: air_quality_data[1].id,
      postPolicyAirQualityName: air_quality_data[1].name,
    });
    store.commit(
      "analysis/updatePostPolicyAirQualityMetricId",
      air_quality_data[1].metricId + "-" + air_quality_data[1].seasonalMetricId
    );
  } else {
    console.log("scenario: " + air_quality_data[0].id);
    store.commit("analysis/updatePostPolicyAirQuality", {
      postPolicyAirQualityId: air_quality_data[0].id,
      postPolicyAirQualityName: "",
    });
    store.commit(
      "analysis/updatePostPolicyAirQualityMetricId",
      air_quality_data[0].metricId + "-" + air_quality_data[0].seasonalMetricId
    );
    store.commit("analysis/updatePostPolicyAirQuality", {
      prePolicyAirQualityId: air_quality_data[1].id,
      prePolicyAirQualityName: "",
    });
    store.commit(
      "analysis/updatePrePolicyAirQualityMetricId",
      air_quality_data[1].metricId + "-" + air_quality_data[1].seasonalMetricId
    );
  }

  var population = parameters.population;

  console.log("**** " + population.populationYear)
  console.log("**** " + population.populationDatasetId)
  console.log("**** " + population.populationDatasetName)
  console.log("**** " + population.incidenceId)
  console.log("**** " + population.incidenceName)

  store.commit("analysis/updatePopulationYear", population.populationYear);
  store.commit("analysis/updatePopulationDataset", {
    populationDatasetId: population.populationDatasetId,
    populationDatasetName: population.populationDatasetName,
  });

  store.commit("analysis/updateIncidence", {
    incidenceId: population.incidenceId,
    incidenceName: population.incidenceName,
  });

  console.log(parameters.healthEffects);
  store.commit("analysis/updateHealthEffects", parameters.healthEffects);

  const valuationFunctions = ref([]);

  (async () => {
    console.log("loadValuationFunctions");
    const valuationsResponse = await loadValuationFunctions(store).fetch();
    console.log("....");
    valuationFunctions.value = valuationsResponse.data.value;
    console.log("!!!!!!!!!!!!!!!!!!!!!!");

    (async () => {
      console.log("loadHealthImpactFunctionGroups");
      const response = await loadHealthImpactFunctionGroups(store).fetch(store);
      console.log("*** response");
      console.log(response.data.value);

      var values = buildHealthImpactFunctionGroups(
        response.data.value,
        valuationFunctions,
        store
      );
    })();

    console.log("=============================================");

    (async () => {
      while (store.state.analysis.healthImpactFunctions.length === 0) {
        await new Promise((resolve) => setTimeout(resolve, 1000));
      }

      var hifgs = store.state.analysis.healthImpactFunctions;
      var functions = parameters.functions;

      var valuationForHealthFunctions = [];
      var valuationForHealthFunction = {};
      var valuationFunctions = [];

      for (var i = 0; i < functions.length; i++) {
        if (functions[i].vfIds) {
          valuationForHealthFunction = {};
          valuationFunctions = [];

          var vfIds = functions[i].vfIds;
          for (var j = 0; j < vfIds.length; j++) {
            valuationFunctions.push(vfIds[j].vfId);
          }
          valuationForHealthFunction["health_function_id"] = functions[i].hifId;
          valuationForHealthFunction["valuation_ids"] = valuationFunctions;
          valuationForHealthFunction["endpoint_group_id"] = 99; // JUST A TEST
          //valuationForHealthFunctions.push(valuationForHealthFunction);
          console.log("~~~")
          console.log(store.state.analysis.valuationsForHealthImpactFunctionGroups)
          store.commit("analysis/updateValuationsForHealthImpactFunctionGroups", valuationForHealthFunction);
          console.log(store.state.analysis.valuationsForHealthImpactFunctionGroups)
        }
      }

      console.log(store.state.analysis.valuationsForHealthImpactFunctionGroups);
      //console.log(parameters.functions);

      //store.commit("analysis/setValuationsForHealthImpactFunctionGroups", valuationForHealthFunctions);

    })();
    console.log("=============================================");
  })();

  //airQuality["metricId"] = prePolicyAirQualityMetric[0];
  //airQuality["seasonalMetricId"] = prePolicyAirQualityMetric[1];
};
