<template>
  <div class="header-text">
    How do you want to value the health effects? (Choice applies to all tasks in a batch run)
  </div>
  <div class="row flex">
    <Suspense>
      <ValuationSelection></ValuationSelection>
    </Suspense>
    <Suspense>
      <InflationYear></InflationYear>
    </Suspense>
  </div>
  <div class="row">
    Select individual valuation functions via the edit(<q-icon class="edit-btn-note q-pt-xs" color="primary" name="mdi-pencil"></q-icon>) button in each row. Selected valuation functions will be run for each task (each Post-Policy/Year combination).
  </div>
  <div class="row">
    <Suspense>
      <ValueOfEffects></ValueOfEffects>
    </Suspense>
  </div>
  <div class="row prompt">
    At what scale do you want to value the effects?
  </div>
  <div class="row" v-if="stepHasError && atStep.value == 6">
    Please select an aggregation scale
  </div>
  <div class="row">
    <Suspense>
      <Aggregation></Aggregation>
    </Suspense>
  </div>
</template>

<script>
import { defineComponent, reactive, inject } from 'vue';
import ValueOfEffects from "../../components/analysis/ValueOfEffects.vue";
import Aggregation from "../../components/analysis/Aggregation.vue";
import ValuationSelection from "../../components/analysis/ValuationSelection.vue";
import InflationYear from 'src/components/analysis/InflationYear.vue';

export default defineComponent({
  name: 'WhatValueOfEffects',
  components: {
    ValueOfEffects,
    Aggregation,
    ValuationSelection,
    InflationYear
  },
  setup(props, context) {

    const stepHasError = reactive(inject("stepHasError"));
    const atStep = reactive(inject("atStep"));

    return {
      stepHasError,
      atStep
    };
  },
})
</script>

<style lang="scss">

  .prompt {
    margin-top: 30px;
  }
  
  .header-text {
    font-size: 16px;
    font-weight: 500;
  }

  .edit-btn-note:hover {
    cursor: text;
  }

  .flex {
    display: flex;
  }

</style>