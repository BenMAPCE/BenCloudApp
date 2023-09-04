<template>
  <div v-if="stepHasError && atStep.value == 3">
    Please make all selections
  </div>
  <div class="q-pa-md">
    <div class="q-gutter-sm">
      <div class="row">Step 1. What population dataset do you want to use?</div>
      <div class="row">
        <Suspense>
          <PopulationDataset></PopulationDataset>
        </Suspense>
      </div>
      <div class="row">Step 2. In what year will people be exposed?</div>
      <div class="row">
          <PopulationYears></PopulationYears>
      </div>
      <div class="row">
        Step 3. Which exposure functions would you like to include?
      </div>
      <div class="row">
        <Suspense>
          <ExposureFunctionGroups></ExposureFunctionGroups>
        </Suspense>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, reactive, inject } from "vue";
import PopulationDataset from "../../components/exposure/PopulationDataset.vue";
import PopulationYears from "../../components/exposure/PopulationYears.vue";
import ExposureFunctionGroups from "../../components/exposure/ExposureFunctionGroups.vue";
//import Incidence from "../../components/analysis/Incidence.vue";
import { useStore } from "vuex";

export default defineComponent({
  name: "WhoWillBeExposed",
  components: { PopulationDataset, PopulationYears, ExposureFunctionGroups },

  setup() {
    const store = useStore();

    const stepHasError = reactive(inject("stepHasError"));
    const atStep = reactive(inject("atStep"));

    return {
      stepHasError,
      atStep
    };
  },
});
</script>
