<template>
  <Suspense>
    <ValueOfEffects></ValueOfEffects>
  </Suspense>
</template>

<script>
import { defineComponent } from 'vue';
import ValueOfEffects from "../../components/analysis/ValueOfEffects.vue";

export default defineComponent({
  name: 'WhatValueOfEffects',
  components: {
    ValueOfEffects
  },
})
</script>
