<template>
    <HealthEffects></HealthEffects>
</template>

<script>
import { defineComponent } from "vue";
import HealthEffects from "../../components/analysis/HealthEffects.vue";

export default defineComponent({
  name: "WhatHealthEffects",

  components: {
    HealthEffects,
  },
});
</script>
