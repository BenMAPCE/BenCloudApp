<template>
  <q-stepper
    v-model="step"
    ref="stepper"
    alternative-labels
    color="primary"
    animated
    width="100%"
  >
    <q-step
      :name="1"
      title="Analysis Type"
      icon="settings"
      :done="step > 1"
      prefix="1"
    >
      <AnalysysType></AnalysysType>
    </q-step>

    <q-step :name="2" title="Where?" :done="step > 2" prefix="2">
      <Where></Where>
    </q-step>

    <q-step :name="3" title="What Pollutants" :done="step > 3" prefix="3">
      <WhatPollutants></WhatPollutants>
    </q-step>

    <q-step :name="4" title="What Air Quality" :done="step > 4" prefix="4">
      <WhatAirQuality></WhatAirQuality>
    </q-step>

    <q-step :name="5" title="Who will be exposed?" :done="step > 5" prefix="5">
      <WhoWillBeExposed></WhoWillBeExposed>
    </q-step>

    <q-step :name="6" title="What health effects?" :done="step > 6" prefix="6">
      <WhatHealthEffects></WhatHealthEffects>
    </q-step>

    <q-step :name="7" title="Value of effects?" prefix="7">
      <ValueOfEffects></ValueOfEffects>
    </q-step>

    <template v-slot:navigation>
      <q-stepper-navigation>
        <q-btn
          @click="$refs.stepper.next()"
          color="primary"
          :label="step === 7 ? 'Finish' : 'Continue'"
        />
        <q-btn
          v-if="step > 1"
          flat
          color="primary"
          @click="$refs.stepper.previous()"
          label="Back"
          class="q-ml-sm"
        />
      </q-stepper-navigation>
    </template>
  </q-stepper>
</template>

<script>
import { ref } from "vue";

import AnalysysType from "src/components/analysis/AnalysisType.vue";
import Where from "src/components/analysis/Where.vue";
import WhatPollutants from "src/components/analysis/WhatPollutants.vue";
import WhatAirQuality from "src/components/analysis/WhatAirQuality.vue";
import WhoWillBeExposed from "src/components/analysis/WhoWillBeExposed.vue";
import WhatHealthEffects from "src/components/analysis/WhatHealthEffects.vue";
import ValueOfEffects from "src/components/analysis/ValueOfEffects.vue";

export default {
  components: {
    AnalysysType,
    Where,
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
