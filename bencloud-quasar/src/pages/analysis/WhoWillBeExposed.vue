<template>
  <div class="q-pa-md">
    <div class="q-gutter-sm">
      <div class="row">Step 1. Select the population dataset.</div>
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
        Step 3. What is the baseline health of the population exposed?
      </div>
      <div class="row">
        <Suspense>
          <Incidence></Incidence>
        </Suspense>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import PopulationDataset from "../../components/analysis/PopulationDataset.vue";
import PopulationYears from "../../components/analysis/PopulationYears.vue";
import Incidence from "../../components/analysis/Incidence.vue";
import { useStore } from "vuex";

export default defineComponent({
  name: "WhoWillBeExposed",
  components: { PopulationDataset, PopulationYears, Incidence },

  setup() {
    const store = useStore();

    return {
    };
  },
});
</script>
