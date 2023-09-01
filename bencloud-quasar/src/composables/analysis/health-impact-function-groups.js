import { useStore, mapGetters } from "vuex";
import axios from "axios";
import { ref } from "vue";
import { healthEffects } from "src/store/analysis/getters";
import { getValuationFunctionsForEndpointGroupId } from "./valuation-functions";

export const loadHealthImpactFunctionGroups = (store) => {
  const data = ref(null);
  const error = ref(null);
  const response = ref(null);
  const loading = ref(false);
  //const store = useStore();

  const fetch = async (store) => {
    loading.value = true;
    try {
      // console.log(store.state.analysis.healthEffects);
      //var selectedHealthEffects = store.state.analysis.healthEffects;
      //console.log(selectedHealthEffects.length);

      var heItems = store.state.analysis.healthEffects;

      var healthEffects = "";
      for (var i = 0; i < heItems.length; i++) {
        //console.log(heItems[i].healthEffectId);
        healthEffects += heItems[i].healthEffectId;
        if(i < heItems.length -1) {
          healthEffects += ",";
        }
      }

      /*
      var healthEffects = "";
      for (var he = 0; he < selectedHealthEffects.length; he++) {
        healthEffects = healthEffects + selectedHealthEffects[he] + ",";
      }
 */
      //console.log("healthEffects: " + healthEffects);

      // /health-impact-function-groups/{ids}?incidencePrevalenceDataset=39&popYear=2020&pollutantId=6\

      var postPolicyItems = store.state.analysis.postPolicyAirQualityName;
      var postPolicyIds = store.state.analysis.postPolicyAirQualityId;
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

      console.log(postPolicy);
      
      const result = await axios
        .get(
          process.env.API_SERVER +
            "/api/batch-task-config?" +
            "hifGroupIds=" +
            healthEffects +
            "&pollutantId=" +
            store.state.analysis.pollutantId +
            "&baselineId=" +
            store.state.analysis.prePolicyAirQualityId + 
            "&populationId=" +
            store.state.analysis.populationDatasetId +
            "&gridDefinitionId=" +
            store.state.analysis.aggregationScale +
            "&scenarios=" +
            postPolicy + 
            "&incidencePrevalenceDataset=" +
            store.state.analysis.incidenceId,
          {
            params: {},
          }
        )
        .then((response) => {
          data.value = response.data;
          store.commit("analysis/updateBatchTaskObject", data.value);
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
};

export const buildHealthImpactFunctionGroups = (
  data,
  valuationFunctions,
  store
) => {
  console.log("*******");

  var records = JSON.parse(JSON.stringify(data));
  //console.log(records);
  var groups = records.batchHifGroups;
  var options = [];
  var option = {};
  var functions = {};

  var healthImpactFunctions = [];

  for(var i = 0; i < records.aqScenarios.length; i++) {
    var scenario = records.aqScenarios[i];
    var hifConfigs = [];
    var name = scenario.name;
    for(var j = 0; j < scenario.popConfigs.length; j++) {
      var year = scenario.popConfigs[j].popYear;
      hifConfigs = scenario.popConfigs[j].scenarioHifConfigs;
      var config = {name: name, year: year, hifs: hifConfigs};
      store.commit("analysis/updatePostPolicyAirQualityHifs", config);
    }
  }

  for (var i = 0; i < groups.length; i++) {
    option = {};
    functions = groups[i].hifs;
    //console.log(functions.length);

    for (var f = 0; f < functions.length; f++) {
      healthImpactFunctions.push(functions[f].hifRecord);

      option = {};
      option.value = groups[i].id;
      option.group_name = groups[i].name;

      option.health_function_id = functions[f].hifRecord.id;

      option.endpoint_group_id = functions[f].hifRecord.endpoint_group_id;
      option.location = functions[f].hifRecord.location;
      option.endpoint_id = functions[f].hifRecord.endpoint_id;
      option.author_year =
        functions[f].hifRecord.author + " / " + functions[f].hifRecord.function_year;
      option.endpoint_name = functions[f].hifRecord.endpoint_name;
      option.age_range = functions[f].hifRecord.start_age + " - " + functions[f].hifRecord.end_age;
      option.end_age = functions[f].hifRecord.end_age;
      option.race_ethnicity_gender =
        functions[f].hifRecord.race_name +
        " / " +
        functions[f].hifRecord.ethnicity_name +
        " / " +
        functions[f].hifRecord.gender_name;

      if (functions[f].incidenceName === null) {
        option.incidence_prevalence = "";
      } else {
        option.incidence_prevalence = functions[f].incidenceName;
      }

  
      // load valuations
      //console.log("--- 001")
      var valuationsForEndpointGroupId =
        getValuationFunctionsForEndpointGroupId(
          valuationFunctions.value,
          functions[f].hifRecord.endpoint_group_id
        );
      let valuationsSelected = store.getters[
        "analysis/getValuationsForHealthFunctionId"
      ](functions[f].hifId);

      let valuationsArray = [];

      if(!!valuationsSelected.valuation_ids) {
        for(var j = 0; j < valuationsSelected.valuation_ids.length; j++) {
          for(var k = 0; k < valuationsForEndpointGroupId.length; k++) {
            if(valuationsForEndpointGroupId[k].id === valuationsSelected.valuation_ids[j]) {
              valuationsArray.push( {
                hifId: valuationsSelected.health_function_id,
                hifInstanceId: null,
                vfId: valuationsForEndpointGroupId[k].id,
                vfRecord: valuationsForEndpointGroupId[k]
              })
            }
          }
        }
      }

      var batchTaskObject = JSON.parse(JSON.stringify(store.state.analysis.batchTaskObject));
      for(var h = 0; h < batchTaskObject.batchHifGroups.length; h++) {
        if(batchTaskObject.batchHifGroups[h].name === groups[i].name) {
          for(var j = 0; j < batchTaskObject.batchHifGroups[h].hifs.length; j++) {
            if(batchTaskObject.batchHifGroups[h].hifs[j].hifId === valuationsSelected.health_function_id) {
              for(var k = 0; k < valuationsArray.length; k++) {
                valuationsArray[k].hifInstanceId = batchTaskObject.batchHifGroups[h].hifs[j].hifInstanceId;
              }
              batchTaskObject.batchHifGroups[h].hifs[j]['valuationFunctions'] = valuationsArray;
              break;
            }
          }
          break;
        }
      }

      store.commit("analysis/updateBatchTaskObject", batchTaskObject);

      var valuations = "";
      
      if (
        valuationsSelected != undefined &&
        valuationsSelected.valuation_ids != undefined
      ) {
        for (var vf = 0; vf < valuationFunctions.value.length; vf++) {
          for (var vs = 0; vs < valuationsSelected.valuation_ids.length; vs++) {
            if (
              valuationFunctions.value[vf].id ==
              valuationsSelected.valuation_ids[vs]
            ) {
              valuations =
                valuations +
                "<p>" +
                valuationFunctions.value[vf].endpoint_name +
                " | " +
                valuationFunctions.value[vf].start_age +
                " - " +
                valuationFunctions.value[vf].end_age +
                " | " +
                valuationFunctions.value[vf].qualifier +
                "</p>";
            }
          }
        }
      }

      option.valuation = valuations;

      options.push(option);
      //console.log(option);

      store.commit(
        "analysis/updateHealthImpactFunctions",
        healthImpactFunctions
      );
    }
  }
  return options;
};
