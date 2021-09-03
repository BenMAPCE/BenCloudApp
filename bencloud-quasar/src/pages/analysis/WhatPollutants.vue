<template>
    <p class="pollutant-header">What pollutant do you want to assess?</p>
  <Suspense>
    <Pollutant></Pollutant>
  </Suspense>
</template>

<script>
import { defineComponent } from "vue";
import { useStore } from "vuex";
import Pollutant from "../../components/analysis/Pollutant.vue";

export default defineComponent({
  name: "WhatPollutants",
  components: {
    Pollutant,
  },
  setup(props, context) {},
  /*
  methods: {
    updatePollutant(value, event) {
      console.log(value);
      this.$store.commit("analysis/updatePollutant", 9999, value);
      this.pollutantName = this.$store.state.analysis.pollutantName;
    },
  },
  data() {
    return {
      pollutant: "",
      pollutantName: "",
    };
  },
  */
  //
});
</script>

<style scoped>
  .pollutant-header {
    font-size: 16px;
    font-weight: 500
  }
</style>