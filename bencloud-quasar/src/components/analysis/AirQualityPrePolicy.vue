<template>


        <q-scroll-area class="aq-pre-policy-scroll-area" visible: true>
            <q-option-group
              v-model="selectedItem"
              :options="rows"
              color="primary"
              emit
            ></q-option-group>
          
        </q-scroll-area>

        
</template>

<script>

import { defineComponent } from "vue";
import { ref, watch, onBeforeMount } from "vue";
import { useStore } from "vuex";
import { loadAirQualityLayers } from '../../composables/analysis/air-quality';
import { convertAirQualityLayers } from '../../composables/analysis/air-quality';

export default defineComponent({
  model: ref(null),
  name: "AirQualityPrePolicy",

  components: {
  },

  async setup(props, { emit }) {

    const store = useStore();
    const rows = ref([]);
    const selectedItem  = ref(0);
    // const selectedPollutantId = ref(0)
    // const selectedPollutantFriendlyName = ref("OOPS")

    let airQualityLayers = null;

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " | " + prevSelectedItem)
        if (currentSelectedItem != prevSelectedItem) {
          console.log("currentSelectedItem: " + currentSelectedItem)
          var name = rows.value.find((opt) => opt.value === currentSelectedItem).label;
                    
          store.commit("analysis/updatePrePolicyAirQuality", 
          { 
            prePolicyAirQualityId: currentSelectedItem,  
            prePolicyAirQualityName: name
          });
        }
      });

   function updateMetric(value) {
      console.log("AQ Pre Selected... " + value);
    }

  onBeforeMount(() => {
        (async () => {
          const response = await loadAirQualityLayers().fetch();
          rows.value = convertAirQualityLayers(response.data.value);
          store.commit("analysis/updateAirQualityLayers", response.data.value)
          
          airQualityLayers = JSON.parse(JSON.stringify(response.data.value.records))

          console.log(airQualityLayers)


        if (store.state.analysis.prePolicyAirQualityId != null) {
          selectedItem.value = store.state.analysis.prePolicyAirQualityId;
        }

        // selectedPollutantId.value = store.state.analysis.pollutantId
        // selectedPollutantFriendlyName.value = store.state.analysis.pollutantFriendlyName

        // console.log("*********************")
        // console.log("pollutantId.value: " + selectedPollutantId.value)
        // console.log("pollutantFriendlyName.value: " + selectedPollutantFriendlyName.value)
        // console.log("*********************")

        })()
    })


    return {
      rows,
      selectedItem,
      updateMetric
      // selectedPollutantId,
      // selectedPollutantFriendlyName
    };
}
});

</script>

<style lang="scss" scoped>

.aq-pre-policy-scroll-area {
  border: 1px solid black;
  height: 200px; 
  max-width: 90%;
}

</style>