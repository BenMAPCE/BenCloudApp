import { useStore } from 'vuex';
import axios from "axios";
import { ref } from "vue";

// GET array of all health impact function result datasets

export const getHifResultsDatasets = () => {
    const data = ref(null)
    const error = ref(null)
    const response = ref(null)
    const loading = ref(false)
    const store = useStore()

    const fetch = async() => {
        loading.value = true;
        try {

            const result = await axios
                .get(process.env.API_SERVER + "/api/results/hif", {
            params: {
                page: 1,
                rowsPerPage: 9999999,
                pollutantId: store.state.analysis.pollutantId              
                },
            })
            .then((response) => {
                data.value = response.data
                console.log("HIF Datasets")
                console.log(data.value)
            })
        } catch (ex) {
            error.value = ex;
        } finally {
            loading.value = false;
            
            return {response, error, data, loading }
        }
    }

    return { fetch }
}
/*
export const convertAirQualityLayers = (data) => {

    var records = JSON.parse(JSON.stringify(data)).records;
    console.log(records)
    var options = [];
    var option = {};

      for (var i = 0; i < records.length; i++){
          option = {};
          option.value = records[i].id;
          option.label = records[i].name;
          console.log(option)
          options.push(option);
      }

      return options;
}
*/


