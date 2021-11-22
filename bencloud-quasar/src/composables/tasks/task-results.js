import axios from "axios";
import { ref } from "vue";

export const getHIFTaskResults = (task_uuid) => {
    const data = ref(null)
    const error = ref(null)
    const response = ref(null)
    const loading = ref(false)

    const fetch = async() => {

        try {
            const result = await axios
                .get(process.env.API_SERVER + "/api/health-impact-result-datasets/" + task_uuid + "/contents?gridId=20", {
            params: {
                page: 1,
                rowsPerPage: 9999999,
                },
            })
            .then((response) => {
                // console.log("Tasks Completed")
                 data.value = response.data
            //     console.log("==========================")
            //     console.log(response.data)
            //     console.log("==========================")
            })
        } catch (ex) {
            error.value = ex;
        } finally {
            return {response, error, data }
        }
    }

    return { fetch }
}

export const getValuationTaskResults = (task_uuid) => {
    const data = ref(null)
    const error = ref(null)
    const response = ref(null)
    const loading = ref(false)

    const fetch = async() => {

        try {
            const result = await axios
                .get(process.env.API_SERVER + "/api/valuation-result-datasets/" + task_uuid + "/contents?gridId=20", {
            params: {
                page: 1,
                rowsPerPage: 9999999,
                },
            })
            .then((response) => {
                // console.log("Tasks Completed")
                 data.value = response.data
            //     console.log("==========================")
            //     console.log(response.data)
            //     console.log("==========================")
            })
        } catch (ex) {
            error.value = ex;
        } finally {
            return {response, error, data }
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


