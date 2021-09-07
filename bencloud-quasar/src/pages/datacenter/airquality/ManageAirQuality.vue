<template>
  <q-page>
    <div class="q-pa-md  q-gutter-sm">
      
    <div class="row">
      <div class="col">      
        Manage Air Quality
      </div>
    </div>
    <div class="row">
      <div class="col">
        <Suspense>
          <Pollutants updateState></Pollutants>
        </Suspense>
      </div>
      <div class="col justify-end">
        <AirQualityAdd></AirQualityAdd>
      </div>
    </div>

    <div class="q-pa-md">
      <AirQualityLayers></AirQualityLayers>
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
import Pollutants from "./Pollutants.vue";
import AirQualityAdd from "./AirQualityAdd.vue";

export default defineComponent({
  model: ref(null),
  name: "ManageAirQuality",
  components: {
    AirQualityTabs,
    AirQualityLayers,
    AirQualityAdd,
    Pollutants,
  },
});
</script>

<style lang="scss" scoped>
.pollutant-options {
  width: 250px;
}
</style>
