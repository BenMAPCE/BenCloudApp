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
import { loadAirQualityLayers } from '../../composables/exposure/air-quality';
import { convertAirQualityLayers } from '../../composables/exposure/air-quality';

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

          store.commit("exposure/updatePrePolicyAirQuality",
          {
            prePolicyAirQualityId: currentSelectedItem,
            prePolicyAirQualityName: name
          });
        }
      });

    // The airQualityForceReloadValue value is updated when a new file is uploaded
    // SO we want to watch it and when it changes we know we need to update our list of files
    watch(
      () => store.state.airquality.airQualityForceReloadValue,
      (newCount, oldCount) => {
        getRows();
      }
    );


   function updateMetric(value) {
      console.log("AQ Pre Selected... " + value);
    }

    function getRows() {
      (async () => {
        const response = await loadAirQualityLayers(store).fetch();
        rows.value = convertAirQualityLayers(response.data.value);
        // Setting this value will allow the AirQualityPostPolicy.vue component to pull the new list without making an api call
        store.commit("exposure/updateAirQualityLayers", response.data.value);

        if (store.state.exposure.prePolicyAirQualityId != null) {
          selectedItem.value = store.state.exposure.prePolicyAirQualityId;
        }
      })()
    }

    onBeforeMount(() => {
        getRows();
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
