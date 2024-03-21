import { useStore } from 'vuex';
import axios from "axios";
import { ref } from "vue";

export const loadIncidence = (url) => {
    const data = ref(null)
    const error = ref(null)
    const response = ref(null)
    const loading = ref(false)
    const store = useStore()

    const fetch = async() => {
        loading.value = true;
        try {

            const result = await axios
                .get(process.env.API_SERVER + "/api/incidence", {
            params: {
                  
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

export const getIncidenceDatasetYears = (incidence_dataset_id) => {
    const data = ref(null)
    const error = ref(null)
    const response = ref(null)
    const loading = ref(false)

    const fetch = async() => {
        loading.value = true;
        try {

            const result = await axios
                .get(process.env.API_SERVER + "/api/incidence-dataset-years", {
            params: {
                  incidenceDatasetId: incidence_dataset_id
                },
            })
            .then((response) => {
                data.value = response.data
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

export const buildIncidenceYearOptions = (data) => {

    var records = JSON.parse(JSON.stringify(data));
    var options = [];
    options.push("All years");

      for (var i = 0; i < records.length; i++){
          options.push(records[i].year);
      }

      return options;
}
