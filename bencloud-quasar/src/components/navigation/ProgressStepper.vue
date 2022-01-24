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
          @click="validatePreviousStep(this.$refs.stepper, step)"
          label="Back"
          class="q-ml-sm back-button"
        />
        <q-btn
          @click="validateStep(this.$refs.stepper, step)"
          v-if="step < 7"
          color="primary"
          label="Continue"
        />
      </q-stepper-navigation>
    </template>
  </q-stepper>
</template>

<script>
import { ref, reactive, provide, computed, onMounted } from "vue";
import { useStore } from "vuex";

import Where from "src/pages/analysis/Where.vue";
import WhatPollutants from "src/pages/analysis/WhatPollutants.vue";
import WhatAirQuality from "src/pages/analysis/WhatAirQuality.vue";
import WhoWillBeExposed from "src/pages/analysis/WhoWillBeExposed.vue";
import WhatHealthEffects from "src/pages/analysis/WhatHealthEffects.vue";
import ValueOfEffects from "src/pages/analysis/WhatValueOfEffects.vue";
import ReviewAndSubmit from "src/pages/analysis/ReviewAndSubmit.vue";

import { pollutantIdHasValue } from "../../composables/validation/analysis-validations";
import { prePolicyAirQualityIdHasValue } from "../../composables/validation/analysis-validations";
import { postPolicyAirQualityIdHasValue } from "../../composables/validation/analysis-validations";
import { populationDatasetIdHasValue } from "../../composables/validation/analysis-validations";
import { populationYearHasValue } from "../../composables/validation/analysis-validations";
import { incidenceIdHasValue } from "../../composables/validation/analysis-validations";
import { healthEffectsHasValue } from "../../composables/validation/analysis-validations";

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
    const store = useStore();

    const stepHasError = reactive(ref(false));
    const atStep = reactive(ref(null));
    const step = ref(1);

    provide(
      "stepHasError",
      computed(() => stepHasError.value)
    );
    provide(
      "atStep",
      computed(() => atStep)
    );

    onMounted(() => {
      console.log("****** step: " + store.state.analysis.stepNumber)
      step.value = store.state.analysis.stepNumber;
    });

    function validatePreviousStep(thisStepper, step) {
      validateStep(thisStepper, step - 1);
      thisStepper.previous();
    }

    function validateStep(thisStepper, step) {
      console.log(thisStepper);
      console.log("*** validating step " + step);
      if (step == 1) {
        thisStepper.next();
      }

      if (step == 2) {
        if (pollutantIdHasValue(store)) {
          stepHasError.value = false;
          thisStepper.next();
        } else {
          stepHasError.value = true;
          atStep.value = 2;
        }
      }

      if (step == 3) {
        if (
          prePolicyAirQualityIdHasValue(store) &&
          postPolicyAirQualityIdHasValue(store)
        ) {
          stepHasError.value = false;
          thisStepper.next();
        } else {
          stepHasError.value = true;
          atStep.value = 3;
        }
      }

      if (step == 4) {
        if (
          populationDatasetIdHasValue(store) &&
          populationYearHasValue(store) &&
          incidenceIdHasValue(store)
        ) {
          stepHasError.value = false;
          thisStepper.next();
        } else {
          stepHasError.value = true;
          atStep.value = 4;
        }
      }

      if (step == 5) {
        if (
          healthEffectsHasValue(store) 
        ) {
          stepHasError.value = false;
          thisStepper.next();
        } else {
          stepHasError.value = true;
          atStep.value = 5;
        }
      }

     if (step == 6) {
          stepHasError.value = false;
          thisStepper.next();
      }

     if (step == 7) {
          stepHasError.value = false;
          thisStepper.next();
      }
}
    return {
      step,
      stepHasError,
      validateStep,
      validatePreviousStep,
    };
  },
};
</script>

<style scoped>
.back-button {
  margin-right: 15px;
}
</style>
