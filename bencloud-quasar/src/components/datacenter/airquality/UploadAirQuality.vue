<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <div class="row">
        <div class="col">Upload Air Quality</div>
      </div>
      <div class="row">
  
        <div class="col justify-end">
          <AirQualityAdd></AirQualityAdd>
        </div>
      </div>

      <div class="q-pa-md">
        <AirQualityLayers includeLayerName></AirQualityLayers>
      </div>

      <div class="q-pa-md">
        <AirQualityTabs></AirQualityTabs>
      </div>
    </div>
  </q-page>
</template>

<script>
import { defineComponent } from "vue";
import { ref } from "vue";

import AirQualityTabs from "./AirQualityTabs.vue";

import AirQualityLayers from "./AirQualityLayers.vue";
import AirQualityAdd from "../../../components/common/AirQualityAdd.vue";

import { watch, onBeforeMount } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "ManageAirQuality",
  components: {
    AirQualityTabs,
    AirQualityLayers,
    AirQualityAdd,
  },

  setup() {
    const store = useStore();
    const currentPollutantId = ref(0);

    onBeforeMount(() => {
      store.commit("airquality/updatePollutantId", null);
      store.commit("airquality/updateAirQualityLayerId", 0);
    });
  },
});
</script>

<style lang="scss" scoped>
.pollutant-options {
  width: 250px;
}
</style>
