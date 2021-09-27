<template>
  <q-stepper
    v-model="step"
    ref="stepper"
    alternative-labels
    color="primary"
    animated
    width="100%"
  >
    <q-step :name="1" title="Where?" :done="step > 1" prefix="1">
      <Where></Where>
    </q-step>

    <q-step :name="2" title="What pollutant?" :done="step > 2" prefix="2">
      <WhatPollutants></WhatPollutants>
    </q-step>

    <q-step :name="3" title="What air quality?" :done="step > 3" prefix="3">
      <WhatAirQuality></WhatAirQuality>
    </q-step>

    <q-step :name="4" title="Who will be exposed?" :done="step > 4" prefix="4">
      <WhoWillBeExposed></WhoWillBeExposed>
    </q-step>

    <q-step :name="5" title="What health effects?" :done="step > 5" prefix="5">
      <WhatHealthEffects></WhatHealthEffects>
    </q-step>

    <q-step :name="6" title="Value of effects?" :done="step > 6" prefix="6">
      <ValueOfEffects></ValueOfEffects>
    </q-step>

    <q-step :name="7" title="Review &amp; Submit" :done="step > 7" prefix="7">
      <ReviewAndSubmit></ReviewAndSubmit>
    </q-step>

    <template v-slot:navigation>
      <q-stepper-navigation>
        <q-btn
          v-if="step > 1"
          color="black"
          @click="$refs.stepper.previous()"
          label="Back"
          class="q-ml-sm back-button"
        />
        <q-btn
          @click="validateStep(); $refs.stepper.next()"
          v-if="step < 7"
          color="primary"
          label="Continue"
        />
      </q-stepper-navigation>
    </template>
  </q-stepper>
</template>

<script>
import { ref } from "vue";

import Where from "src/pages/analysis/Where.vue";
import WhatPollutants from "src/pages/analysis/WhatPollutants.vue";
import WhatAirQuality from "src/pages/analysis/WhatAirQuality.vue";
import WhoWillBeExposed from "src/pages/analysis/WhoWillBeExposed.vue";
import WhatHealthEffects from "src/pages/analysis/WhatHealthEffects.vue";
import ValueOfEffects from "src/pages/analysis/WhatValueOfEffects.vue";
import ReviewAndSubmit from "src/pages/analysis/ReviewAndSubmit.vue";

export default {
  components: {
    Where,
    WhatPollutants,
    WhatAirQuality,
    WhoWillBeExposed,
    WhatHealthEffects,
    ValueOfEffects,
    ReviewAndSubmit,
  },
  setup() {
    function validateStep() {
      console.log("*** validating step " + this.step);
    }
    return {
      step: ref(1),
      validateStep
    };
  },
};
</script>

<style scoped>
.back-button {
  margin-right: 15px;
}
</style>