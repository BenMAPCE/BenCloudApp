<template>
  <q-tabs
    v-model="tab"
    dense
    class="text-grey"
    active-color="primary"
    indicator-color="primary"
    align="justify"
    narrow-indicator
    v-if="airQualityLayerId != 0"
  >
    <q-tab name="map" label="Map View" />
    <q-tab name="table" label="Tabular View" />
  </q-tabs>

  <q-tab-panels 
    v-model="tab" 
    animated
    v-if="airQualityLayerId != 0"
    >
    <q-tab-panel name="map">
      <div class="text-h6">Map View</div>
      Maps View
    </q-tab-panel>

    <q-tab-panel 
        name="table">
      <div class="text-h6">Air Quality Cells</div>
      <AirQualityCells></AirQualityCells>
    </q-tab-panel>
  </q-tab-panels>
</template>

<script>
import { ref } from "vue";
import { defineComponent } from "vue";

import AirQualityCells from "./AirQualityCells.vue";

export default defineComponent({
  model: ref(null),
  name: "AirQualityTabs",
  components: {
    AirQualityCells,
  },
  computed: {
    airQualityLayerId() {
      return this.$store.state.airquality.airQualityLayerId;
    },
  },
  setup() {
    return {
      tab: ref("table"),
    };
  },
});
</script>
