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

export const buildGridDefinitionOptions = (data, populationDatasetId) => {

    var gridsExcluded = [];

    //exclude 2010 or 2020 grids based on population dataset year
    if(populationDatasetId == 50 || populationDatasetId == 51 || populationDatasetId == 52 || populationDatasetId == 53) {
        gridsExcluded.push(18);
        gridsExcluded.push(19);
        gridsExcluded.push(20);
    } else if(populationDatasetId == 40) {
        gridsExcluded.push(68);
        gridsExcluded.push(69);
        gridsExcluded.push(70);
    }

    //do not show the non-clipped CMAQ 12km in export options
    gridsExcluded.push(28);

    var records = JSON.parse(JSON.stringify(data));
    var options = [];
    var option = {};

      for (var i = 0; i < records.length; i++){
        if(!gridsExcluded.includes(records[i].id)) {
            option = {};
            option.value = records[i].id;
            option.label = records[i].name;
            options.push(option);
        }
          
      }

      return options;
}


