//import { useStore } from 'vuex';
import axios from "axios";
import { ref } from "vue";

export const loadValuationFunctions = (store) => {
    const data = ref(null)
    const error = ref(null)
    const response = ref(null)
    const loading = ref(false)
    //const store = useStore()

    const fetch = async() => {
        //console.log("001")
        loading.value = true;
        try {

            //console.log("002")
            const result = await axios
                .get(process.env.API_SERVER + "/api/valuation-functions", {
            params: {
                  
                },
            })
            .then((response) => {
                //console.log("003")

                data.value = response.data
                //console.log(data.value)
            })
        } catch (ex) {
            error.value = ex;
            //console.log("004")

        } finally {
            loading.value = false;
            //console.log("005")

            return {response, error, data, loading }
        }
    }

    return { fetch }
}

export const getValuationFunctionsForEndpointGroupId = (records, endpointGroupId) => {

    var options = [];

    for (var i = 0; i < records.length; i++){
        if (records[i].endpoint_group_id === endpointGroupId) {
            records[i]['label'] = 
                records[i].endpoint_name + " | " + 
                records[i].start_age + " - " + 
                records[i].end_age + " | " + 
                records[i].qualifier 
            options.push(JSON.parse(JSON.stringify(records[i])))
        }
    }

    console.log(options)
    return options;

}

export const convertValuationFunctions = (data) => {

    var records = JSON.parse(JSON.stringify(data));
    console.log(records)
    var option = {};
    var options = [];

      for (var i = 0; i < records.length; i++){
          option = {};
          option.value = records[i].id;
          option.label = records[i].name;
          console.log(option)
          options.push(option);
      }

      return options;


}

export const updateValuationFunctions = (store, data) => {
    const groups = data.batchHifGroups;
    var valuationForHealthFunction = {};
    var vfs = [];

    for (var j = 0; j < groups.length; j++)
    {
      var groupId = groups[j].id;
      for (var i = 0; i < groups[j].hifs.length; i++) {
        var hif = groups[j].hifs[i];
        if (hif.valuationFunctions) {
          valuationForHealthFunction = {};
          vfs = [];

          for (var h = 0; h < hif.valuationFunctions.length; h++) {
            vfs.push(hif.valuationFunctions[h].vfId);
          }
          valuationForHealthFunction["health_function_id"] = hif.hifId;
          valuationForHealthFunction["valuation_ids"] = vfs;
          valuationForHealthFunction["endpoint_group_id"] = groupId;
          //valuationForHealthFunctions.push(valuationForHealthFunction);
          store.commit("analysis/updateValuationsForHealthImpactFunctionGroups", valuationForHealthFunction);
        }
      }
    }
}