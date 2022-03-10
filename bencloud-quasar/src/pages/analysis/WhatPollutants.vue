<template>
  <div class="pollutant-header">What pollutant do you want to assess?</div>

  <div v-if="stepHasError && atStep.value == 2">
    Please select an Air Quality
  </div>

  <Suspense>
    <Pollutant></Pollutant>
  </Suspense>
</template>

<script>
import { defineComponent, ref, reactive, watch, inject, onMounted } from "vue";
import { useStore } from "vuex";
import Pollutant from "../../components/analysis/Pollutant.vue";

export default defineComponent({
  name: "WhatPollutants",
  components: {
    Pollutant,
  },
  setup(props, context) {

    const stepHasError = reactive(inject("stepHasError"));
    const atStep = reactive(inject("atStep"));

    return {
      stepHasError,
      atStep
    };
  },
});
</script>

<style scoped>
.pollutant-header {
  font-size: 16px;
  font-weight: 500;
}
</style>
