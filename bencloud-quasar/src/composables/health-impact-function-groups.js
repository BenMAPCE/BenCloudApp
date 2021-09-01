import { useStore, mapGetters } from "vuex";
import axios from "axios";
import { ref } from "vue";
import { healthEffects } from "src/store/analysis/getters";
import { getValuationFunctionsForEndpointGroupId } from "./valuation-functions";

export const loadHealthImpactFunctionGroups = (url) => {
  const data = ref(null);
  const error = ref(null);
  const response = ref(null);
  const loading = ref(false);
  const store = useStore();

  const fetch = async () => {
    loading.value = true;
    try {
      console.log(store.state.analysis.healthEffects);
      //var selectedHealthEffects = store.state.analysis.healthEffects;
      //console.log(selectedHealthEffects.length);


      var heItems = store.state.analysis.healthEffects
      var healthEffects = "";
      for (var i = 0; i < heItems.length; i++) {
          console.log(heItems[i].healthEffectId)
          healthEffects = healthEffects + heItems[i].healthEffectId + ",";
      }
/*
      var healthEffects = "";
      for (var he = 0; he < selectedHealthEffects.length; he++) {
        healthEffects = healthEffects + selectedHealthEffects[he] + ",";
      }
 */ 
      console.log("healthEffects: " + healthEffects);

      // /health-impact-function-groups/{ids}?incidencePrevalenceDataset=39&popYear=2020&pollutantId=6

      const result = await axios
        .get(
          store.state.app.apiServerURL +
            "/api/health-impact-function-groups/" +
            healthEffects + "?" + 
            "incidencePrevalenceDataset=" + store.state.analysis.incidenceId + 
            "&popYear=" + store.state.analysis.populationYear + 
            "&pollutantId=" + store.state.analysis.pollutantId
            
            
            ,
          {
            params: {},
          }
        )
        .then((response) => {
          data.value = response.data;
          console.log(data.value);
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
  console.log(records);
  var options = [];
  var option = {};
  var functions = {};

  var healthImpactFunctions = []

  for (var i = 0; i < records.length; i++) {
    option = {};
    functions = records[i].functions;
    console.log(functions.length);

    for (var f = 0; f < functions.length; f++) {

      healthImpactFunctions.push(functions[f])
      
      option = {};
      option.value = records[i].id;
      option.group_name = records[i].name;

      console.log(functions[f].id);
      option.health_function_id = functions[f].id;

      
      option.endpoint_group_id = functions[f].endpoint_group_id;
      option.author_year =
        functions[f].author + " / " + functions[f].function_year;
      option.endpoint_name = functions[f].endpoint_name;
      option.age_range = functions[f].start_age + " - " + functions[f].end_age;
      option.end_age = functions[f].end_age;
      option.race_ethnicity_gender =
        functions[f].race_name +
        " / " +
        functions[f].ethnicity_name +
        " / " +
        functions[f].gender_name;
      if (functions[f].prevalence_dataset_name === null) {
        option.incidence_prevalence =
          functions[f].incidence_dataset_name +
          " (" +
          functions[f].incidence_year +
          ")";
      } else {
        option.incidence_prevalence =
          functions[f].prevalence_dataset_name +
          " (" +
          functions[f].prevalence_year +
          ")";
      }

      // load valuations

      var valuationsForEndpointGroupId =
        getValuationFunctionsForEndpointGroupId(
          valuationFunctions.value,
          functions[f].endpoint_group_id
        );
      let valuationsSelected = store.getters[
        "analysis/getValuationsForHealthFunctionId"
      ](functions[f].id);

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
                valuationFunctions.value[vf].qualifier +
                "</p>";
            }
          }
        }
      }

      option.valuation = valuations;

      options.push(option);
      console.log(option);

      store.commit("analysis/updateHealthImpactFunctions", healthImpactFunctions);

    }
  }

  return options;
};
