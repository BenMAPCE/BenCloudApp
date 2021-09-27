import { ref } from "vue";
import axios from "axios";

export const buildValuationTaskJSON = (taskName, hifTaskUuid, valuationsForHealthImpactFunctionGroups) => {

//   const valuationsForHealthImpactFunctionGroups =
//     store.state.analysis.valuationsForHealthImpactFunctionGroups;

 console.log("**********************************")
 console.log(valuationsForHealthImpactFunctionGroups)
 console.log("**********************************")
 

  var valuationInfo = {};

  valuationInfo["name"] = taskName;
  valuationInfo["type"] = "Valuation";
  valuationInfo["hif_result_dataset_id"] = null;
  valuationInfo["hif_task_uuid"] = hifTaskUuid;

  var valuationFunctions = [];
  var valuationFunction = {};

  for (var i = 0; i < valuationsForHealthImpactFunctionGroups.length; i++) {
    for (var j = 0; j < valuationsForHealthImpactFunctionGroups[i].valuation_ids.length; j++) {
        valuationFunction = {};
        valuationFunction["hif_id"] = valuationsForHealthImpactFunctionGroups[i].health_function_id;
        valuationFunction["vf_id"] = valuationsForHealthImpactFunctionGroups[i].valuation_ids[j];
        valuationFunctions.push(valuationFunction);
    }
   }

  valuationInfo["functions"] = valuationFunctions;

  return valuationInfo;
};

export const submitValuationTask = (valuationTaskJSON, store) => {
    const data = ref(null);
    const error = ref(null);
    const response = ref(null);
    const loading = ref(false);

    const fetch = async () => {
      console.log("submitting hifTask")
      console.log(valuationTaskJSON) 
      loading.value = true;
      try {
        const {data:response} = await axios
          .post(process.env.API_SERVER + "/api/tasks", valuationTaskJSON)
          .then((response) => {
            data.value = response.data;
            console.log(data.value);
            return data.value;
          });
      } catch (ex) {
        error.value = ex;
        console.log(error.value)
        return error.value;
      }
    };
  
    return { fetch };
  };
  
