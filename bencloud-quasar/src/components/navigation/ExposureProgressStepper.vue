<template>
  <q-stepper
    v-model="step"
    ref="stepper"
    alternative-labels
    color="primary"
    animated
    width="100%"
    @transition="scrollToTop()"
  >

    <q-step :name="1" title="What pollutant?" :done="step > 1" prefix="1">
      <WhatPollutants></WhatPollutants>
    </q-step>

    <q-step :name="2" title="What air quality?" :done="step > 2" prefix="2">
      <WhatAirQuality></WhatAirQuality>
    </q-step>

    <q-step :name="3" title="Who will be exposed?" :done="step > 3" prefix="3">
      <WhoIsExposed></WhoIsExposed>
    </q-step>

    <q-step :name="4" title="Review &amp; Submit" :done="step > 4" prefix="4">
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
          v-if="step < 4"
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

import WhatPollutants from "src/pages/exposure/WhatPollutants.vue";
import WhatAirQuality from "src/pages/exposure/WhatAirQuality.vue";
import WhoIsExposed from "src/pages/exposure/WhoIsExposed.vue";
//import WhatExposures from "src/pages/exposure/WhatExposures.vue";
import ReviewAndSubmit from "src/pages/exposure/ReviewAndSubmit.vue";

import { pollutantIdHasValue } from "../../composables/validation/exposure-validations";
import { prePolicyAirQualityIdHasValue } from "../../composables/validation/exposure-validations";
import { postPolicyAirQualityIdHasValue } from "../../composables/validation/exposure-validations";
import { populationDatasetIdHasValue } from "../../composables/validation/exposure-validations";
import { populationYearsHaveValue } from "../../composables/validation/exposure-validations";
import { exposureFunctionGroupsHasValue } from "../../composables/validation/exposure-validations";

export default {
  components: {
    WhatPollutants,
    WhatAirQuality,
    WhoIsExposed,
    //WhatExposures,
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
      store.commit("exposure/updateStepNumber", 1);
      console.log("****** step: " + store.state.exposure.stepNumber)
      step.value = store.state.exposure.stepNumber;
    });

    function scrollToTop() {
      window.scrollTo({top: 0, behavior: 'smooth'});
    }

    function validatePreviousStep(thisStepper, step) {
      validateStep(thisStepper, step - 1);
      thisStepper.previous();
    }

    function validateStep(thisStepper, step) {
      console.log(thisStepper);
      console.log("*** validating step " + step);


      if (step == 1) {
        if (pollutantIdHasValue(store)) {
          stepHasError.value = false;
          thisStepper.next();
        } else {
          stepHasError.value = true;
          atStep.value = 1;
        }
      }

      if (step == 2) {
        if (
          prePolicyAirQualityIdHasValue(store) &&
          postPolicyAirQualityIdHasValue(store)
        ) {
          stepHasError.value = false;
          thisStepper.next();
        } else {
          stepHasError.value = true;
          atStep.value = 2;
        }
      }

      if (step == 3) {
        if (
          populationDatasetIdHasValue(store) &&
          populationYearsHaveValue(store) &&
          exposureFunctionGroupsHasValue(store)
        ) {
          stepHasError.value = false;
          thisStepper.next();
        } else {
          stepHasError.value = true;
          atStep.value = 3;
        }
      }

     if (step == 4) {
          stepHasError.value = false;
          thisStepper.next();
      }

}
    return {
      step,
      stepHasError,
      validateStep,
      validatePreviousStep,
      scrollToTop
    };
  },
};
</script>

<style scoped>
  .back-button {
    margin-right: 15px;
  }
</style>
