<template>
  <div class="q-ma-md">
    <div class="row">
      <div class="col-4">
        <p>Step 1. Select your pre-policy air quality data</p>
        <Suspense>
          <AirQualityPrePolicy></AirQualityPrePolicy>
        </Suspense>
      </div>
      <div class="col-4">
        <p>Step 2. Select your post-policy air quality data</p>
       <Suspense>
          <AirQualityPostPolicy></AirQualityPostPolicy>
        </Suspense>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref } from "vue";
import AirQualityPrePolicy from "../../components/analysis/AirQualityPrePolicy.vue";
import AirQualityPostPolicy from "../../components/analysis/AirQualityPostPolicy.vue";

export default defineComponent({
  name: "WhatAirQuality",
  components: {
    AirQualityPrePolicy,
    AirQualityPostPolicy,
  },
  setup() {
    return {
      group: ref(null),
    };
  },
});
</script>

<style scoped>
.aq-pre-policy-scroll-area {
  border: 1px solid black;
  height: 200px;
  max-width: 300px;
}
</style>
