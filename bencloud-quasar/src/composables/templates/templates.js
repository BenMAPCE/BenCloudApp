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

export const saveTemplate = (name, type, template, store, templateNotification) => {
  const data = ref(null);
  const error = ref(null);
  const response = ref(null);
  const loading = ref(false);

  console.log("SUBMITTING TEMPLATE...")

  var taskConfig = {};
  taskConfig["name"] = name; 
  taskConfig["type"] = type; 
  taskConfig["parameters"] = template; 

  const fetch = async () => {
    loading.value = true;
    try {
      const result = await axios
        .post(process.env.API_SERVER + "/api/task-configs", taskConfig,
          {validateStatus: function (status) {
            return status < 500;
          }}
        )
        .then((response) => {
          if(response.status === 200) {
            data.value = response.data;
            console.log(data.value);
            templateNotification({
              spinner: false, // we reset the spinner setting so the icon can be displayed
              message: "Template Saved!",
              color: "green",
              timeout: 2000, 
            });
          } else if(response.status === 409){
            console.log("Unable to rename template: " + taskConfig.name);
            templateNotification({
              spinner: false, // we reset the spinner setting so the icon can be displayed
              message: response.data.message,
              color: "red",
              timeout: 4000, 
            });
          }
          else{
            templateNotification({
              spinner: false, // we reset the spinner setting so the icon can be displayed
              message: "Unknown error creating template: " + taskConfig.name,
              color: "red",
              timeout: 4000, 
            });
          }
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

export const createHifTemplate = (taskName, store) => {
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
  const incidenceId = store.state.analysis.incidenceId;
  const incidenceName = store.state.analysis.incidenceName;
  const pollutantId = store.state.analysis.pollutantId;
  const pollutantFriendlyName = store.state.analysis.pollutantFriendlyName;
  const valuationSelection = store.state.analysis.valuationSelection;

  const healthEffects = store.state.analysis.healthEffects;

  const healthImpactFunctions = store.state.analysis.healthImpactFunctions;
  const valuationsForHealthImpactFunctionGroups =
    store.state.analysis.valuationsForHealthImpactFunctionGroups;
  const batchTaskObject = store.state.analysis.batchTaskObject;

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

  var prePolicyAirQualityMetric = [];
  if(prePolicyAirQualityMetricId !== "") {
    prePolicyAirQualityMetric = prePolicyAirQualityMetricId.split("-");
  }
  var postPolicyAirQualityMetric = [];
  if(postPolicyAirQualityMetricId.length !== 0) {
    postPolicyAirQualityMetric = postPolicyAirQualityMetricId.split("-");
  }

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

  template["valuationSelection"] = valuationSelection;

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
  template["batchTaskObject"] = batchTaskObject;

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

export const createExposureTemplate = (taskName, store) => {
  const prePolicyAirQualityId = store.state.exposure.prePolicyAirQualityId;
  const postPolicyAirQualityId = store.state.exposure.postPolicyAirQualityId;
  const prePolicyAirQualityName = store.state.exposure.prePolicyAirQualityName;
  const postPolicyAirQualityName =
    store.state.exposure.postPolicyAirQualityName;
  const prePolicyAirQualityMetricId =
    store.state.exposure.prePolicyAirQualityMetricId;
  const postPolicyAirQualityMetricId =
    store.state.exposure.postPolicyAirQualityMetricId;
  const populationDatasetId = store.state.exposure.populationDatasetId;
  const populationDatasetName = store.state.exposure.populationDatasetName;
  const pollutantId = store.state.exposure.pollutantId;
  const pollutantFriendlyName = store.state.exposure.pollutantFriendlyName;
  const exposureFunctionGroups = store.state.exposure.exposureFunctionGroup;
  const batchTaskObject = store.state.exposure.batchTaskObject;

  var template = {};

  template["name"] = taskName;
  template["type"] = "Exposure";

  var pollutant = {};
  pollutant["pollutantId"] = pollutantId;
  pollutant["pollutantFriendlyName"] = pollutantFriendlyName;
  template["pollutant"] = pollutant;

  var airQualityArray = [];
  var airQuality = {};

  var prePolicyAirQualityMetric = [];
  if(prePolicyAirQualityMetricId !== "") {
    prePolicyAirQualityMetric = prePolicyAirQualityMetricId.split("-");
  }
  var postPolicyAirQualityMetric = [];
  if(postPolicyAirQualityMetricId.length !== 0) {
    postPolicyAirQualityMetric = postPolicyAirQualityMetricId.split("-");
  }

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

  template["population"] = population;

  var selectedExposureFunctionGroup = {};

  console.log("***** " + exposureFunctionGroups);
    selectedExposureFunctionGroup = {};
    selectedExposureFunctionGroup["exposureGroupId"] = exposureFunctionGroups.exposureGroupId;
    selectedExposureFunctionGroup["exposureGroupName"] =
      exposureFunctionGroups.exposureGroupName;
  template["exposureFunctionGroups"] = selectedExposureFunctionGroup;

  template["batchTaskObject"] = batchTaskObject;

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


export const loadHifTemplate = (model, store) => {
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

  console.log("**** " + population.populationDatasetId)
  console.log("**** " + population.populationDatasetName)
  console.log("**** " + population.incidenceId)
  console.log("**** " + population.incidenceName)

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

  console.log(parameters.valuationSelection);
  store.commit("analysis/updateValuationSelection", parameters.valuationSelection);

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

      var batchTaskObject = parameters.batchTaskObject;
      store.commit("analysis/updateBatchTaskObject", batchTaskObject);

      console.log(store.state.analysis.valuationsForHealthImpactFunctionGroups);
      //console.log(parameters.functions);

      //store.commit("analysis/setValuationsForHealthImpactFunctionGroups", valuationForHealthFunctions);

    })();
    console.log("=============================================");
  })();

  //airQuality["metricId"] = prePolicyAirQualityMetric[0];
  //airQuality["seasonalMetricId"] = prePolicyAirQualityMetric[1];
};

export const loadExposureTemplate = (model, store) => {
  var parameters = model.parameters;
  console.log(parameters);

  var pollutant = parameters.pollutant;

  store.commit("exposure/updatePollutantId", pollutant.pollutantId);
  store.commit(
    "exposure/updatePollutantFriendlyName",
    pollutant.pollutantFriendlyName
  );

  var air_quality_data = parameters.air_quality_data;
  if (air_quality_data[0].type === "baseline") {
    console.log("baseline: " + air_quality_data[0].id);
    store.commit("exposure/updatePrePolicyAirQuality", {
      prePolicyAirQualityId: air_quality_data[0].id,
      prePolicyAirQualityName: air_quality_data[0].name,
    });
    store.commit(
      "exposure/updatePrePolicyAirQualityMetricId",
      air_quality_data[0].metricId + "-" + air_quality_data[0].seasonalMetricId
    );

    store.commit("exposure/updatePostPolicyAirQuality", {
      postPolicyAirQualityId: air_quality_data[1].id,
      postPolicyAirQualityName: air_quality_data[1].name,
    });
    store.commit(
      "exposure/updatePostPolicyAirQualityMetricId",
      air_quality_data[1].metricId + "-" + air_quality_data[1].seasonalMetricId
    );
  } else {
    console.log("scenario: " + air_quality_data[0].id);
    store.commit("exposure/updatePostPolicyAirQuality", {
      postPolicyAirQualityId: air_quality_data[0].id,
      postPolicyAirQualityName: "",
    });
    store.commit(
      "exposure/updatePostPolicyAirQualityMetricId",
      air_quality_data[0].metricId + "-" + air_quality_data[0].seasonalMetricId
    );
    store.commit("exposure/updatePostPolicyAirQuality", {
      prePolicyAirQualityId: air_quality_data[1].id,
      prePolicyAirQualityName: "",
    });
    store.commit(
      "exposure/updatePrePolicyAirQualityMetricId",
      air_quality_data[1].metricId + "-" + air_quality_data[1].seasonalMetricId
    );
  }

  var population = parameters.population;

  console.log("**** " + population.populationDatasetId)
  console.log("**** " + population.populationDatasetName)
  console.log("**** " + population.incidenceId)
  console.log("**** " + population.incidenceName)

  store.commit("exposure/updatePopulationDataset", {
    populationDatasetId: population.populationDatasetId,
    populationDatasetName: population.populationDatasetName,
  });

  console.log(parameters.exposureFunctionGroups);
  store.commit("exposure/updateExposureFunctionGroup", parameters.exposureFunctionGroups);

  var batchTaskObject = parameters.batchTaskObject;
  store.commit("exposure/updateBatchTaskObject", batchTaskObject);

  //airQuality["metricId"] = prePolicyAirQualityMetric[0];
  //airQuality["seasonalMetricId"] = prePolicyAirQualityMetric[1];
};
