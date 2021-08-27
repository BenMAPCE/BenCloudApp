<template>
  <q-stepper
    v-model="step"
    ref="stepper"
    alternative-labels
    color="primary"
    animated
    width="100%"
  >
    <q-step :name="1" title="What Pollutant" :done="step > 1" prefix="1">
      <WhatPollutants></WhatPollutants>
    </q-step>

    <q-step :name="2" title="What Air Quality" :done="step > 2" prefix="2">
      <WhatAirQuality></WhatAirQuality>
    </q-step>

    <q-step :name="3" title="Who will be exposed?" :done="step > 3" prefix="3">
      <WhoWillBeExposed></WhoWillBeExposed>
    </q-step>

    <q-step :name="4" title="What health effects?" :done="step > 4" prefix="4">
      <WhatHealthEffects></WhatHealthEffects>
    </q-step>

    <q-step :name="5" title="Value of effects?" :done="step > 5" prefix="5">
      <ValueOfEffects></ValueOfEffects>
    </q-step>

    <q-step :name="6" title="Review &amp; Submit" :done="step > 6" prefix="6">
      <ValueOfEffects></ValueOfEffects>
    </q-step>

    <template v-slot:navigation>
      <q-stepper-navigation>
        <q-btn
          v-if="step > 1"
          flat
          color="primary"
          @click="$refs.stepper.previous()"
          label="Back"
          class="q-ml-sm"
        />
        <q-btn
          @click="$refs.stepper.next()"
          color="primary"
          :label="step === 6 ? 'Finish' : 'Continue'"
        />      </q-stepper-navigation>
    </template>
  </q-stepper>
</template>

<script>
import { ref } from "vue";

import WhatPollutants from "src/pages/analysis/WhatPollutants.vue";
import WhatAirQuality from "src/pages/analysis/WhatAirQuality.vue";
import WhoWillBeExposed from "src/pages/analysis/WhoWillBeExposed.vue";
import WhatHealthEffects from "src/pages/analysis/WhatHealthEffects.vue";
import ValueOfEffects from "src/pages/analysis/WhatValueOfEffects.vue";

export default {
  components: {
    WhatPollutants,
    WhatAirQuality,
    WhoWillBeExposed,
    WhatHealthEffects,
    ValueOfEffects
  },
  setup() {
    return {
      step: ref(1),
    };
  },
};
</script>
