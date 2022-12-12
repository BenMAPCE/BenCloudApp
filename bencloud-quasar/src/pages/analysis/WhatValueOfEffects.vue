<template>
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

export default defineComponent({
  name: 'WhatValueOfEffects',
  components: {
    ValueOfEffects,
    Aggregation
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

</style>