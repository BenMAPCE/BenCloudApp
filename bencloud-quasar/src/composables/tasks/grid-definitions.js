import axios from "axios";
import { ref } from "vue";

export const getGridDefinitions = () => {
    const data = ref(null)
    const error = ref(null)
    const response = ref(null)
    const loading = ref(false)

    const fetch = async() => {

        try {
            const result = await axios
                .get(process.env.API_SERVER + "/api/grid-definitions", {
            })
            .then((response) => {
                // console.log("Tasks Completed")
                 data.value = response.data
                // console.log("==========================")
                // console.log(response.data)
                // console.log("==========================")
            })
        } catch (ex) {
            error.value = ex;
        } finally {
            return {response, error, data }
        }
    }

    return { fetch }
}

export const buildGridDefinitionOptions = (data) => {

    var records = JSON.parse(JSON.stringify(data));
    var options = [];
    var option = {};

      for (var i = 0; i < records.length; i++){
          option = {};
          option.value = records[i].id;
          option.label = records[i].name;
          options.push(option);
      }

      return options;
}


