import axios from "axios";
import { ref } from "vue";

// Pass the store
// If we try to create it in this composable we'll get an injection error when running it from a watch()
export const loadAirQualityLayers = (store) => {
    const data = ref(null)
    const error = ref(null)
    const response = ref(null)
    const loading = ref(false)

    const fetch = async() => {
        loading.value = true;
        try {

            const result = await axios
                .get(process.env.API_SERVER + "/api/air-quality-data", {
            params: {
                page: 1,
                rowsPerPage: 9999999,
                pollutantId: store.state.exposure.pollutantId
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
    }

    return { fetch }
}

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



