<template>
  <div class="q-pa-md">
    <div class="q-gutter-sm">
      <div class="row">
        <div v-for="row in rows" :key="row.id">
          <div class="col-2 pollutant-options">
            <q-radio v-model="selectedItem" :val="row.id" :label="row.friendly_name" />
          </div>
        </div>
      </div>
     <div class="row">
        <div v-for="row in rows" :key="row.id">
          <div class="col-4 pollutant-options">
            <q-img
              
              :src="`/images/pollutant_${row.id}.png`"
              spinner-color="white"
              style="height: 100px; max-width: 100px;"
            />
          </div>
        </div>
      </div>
     </div>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";
import { loadPollutants } from "../../composables/common/pollutants";

import { getHifResultsDatasets } from "../../composables/results/hif/hif-results-datasets";
import { getHifResultDatasetFunctions } from "../../composables/results/hif/hif-result-dataset-functions";
import { getHifResultDatasetDetails } from "../../composables/results/hif/hif-result-dataset-details";

export default defineComponent({
  model: ref(null),
  name: "Pollutant",

  async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItem = ref(store.state.exposure.pollutantId);
    const selectedItemName = ref(store.state.exposure.pollutantFriendlyName);
    const selectedItemFriendlyName = ref(store.state.exposure.pollutantFriendlyName);

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          console.log(rows.value.find((opt) => opt.id === currentSelectedItem).name);
          var name = rows.value.find((opt) => opt.id === currentSelectedItem).name;
          var friendlyName = rows.value.find((opt) => opt.id === currentSelectedItem).friendly_name;
          console.log("----- " + friendlyName)
          store.commit("exposure/updatePollutantId", currentSelectedItem);
          store.commit("exposure/updatePollutantName", name);
          store.commit("exposure/updatePollutantFriendlyName", friendlyName);
          
          store.commit("exposure/updatePrePolicyAirQuality", 
          { 
            prePolicyAirQualityId: null,  
            prePolicyAirQualityName: ""
          });

          store.commit("exposure/updatePostPolicyAirQuality", 
          { 
            postPolicyAirQualityId: null,  
            postPolicyAirQualityName: ""
          });

          selectedItemFriendlyName.value = friendlyName
        }
      }
    );

    onBeforeMount(() => {
      (async () => {
        console.log("loadPollutantsOptions");
        const response = await loadPollutants().fetch();
        rows.value = response.data.value;
      })();

    (async () => {
        console.log("getHifResultDatasets");
        const response = await getHifResultsDatasets().fetch();
        //rows.value = response.data.value;
      })();
      (async () => {
        console.log("getHifResultDatasetFunctions");
        const response = await getHifResultDatasetFunctions(32).fetch();
        //rows.value = response.data.value;
      })();
      (async () => {
        console.log("getHifResultDatasetDetails");
        const response = await getHifResultDatasetDetails(32).fetch();
        //rows.value = response.data.value;
      })();

/*
      (async () => {
        console.log("getValuationResultsDatasets");
        const response = await getValuationResultsDatasets().fetch();
        //rows.value = response.data.value;
      })();
      (async () => {
        console.log("getValuationResultDatasetFunctions");
        const response = await getValuationResultDatasetFunctions(12).fetch();
        //rows.value = response.data.value;
      })();
      
      (async () => {
        console.log("getValuationResultDatasetDetails");
        const response = await getValuationResultDatasetDetails(12).fetch();
        //rows.value = response.data.value;
      })();
*/
    });

    onMounted(() => {
      console.log("... " + store.state.exposure.pollutantId);
      if (store.state.exposure.pollutantId != null) {
        selectedItem.value = store.state.exposure.pollutantId;
        selectedItemName.value = store.state.exposure.pollutantName;
        console.log("- selectedItem: " + selectedItem.value);
        console.log("- selectedItemName: " + selectedItemName.value);
      }
    })();

    return {
      rows,
      selectedItem,
      selectedItemName,
      selectedItemFriendlyName
    };
  },
});
</script>

<style lang="scss" scoped>
.pollutant-options {
  width: 250px;
}
</style>
