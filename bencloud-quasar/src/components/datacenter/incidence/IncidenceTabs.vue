<template>
        <div class="text-h6" v-if="IncidenceDatasetId != 0">Incidence Cells</div>
        <IncidenceCells></IncidenceCells>
  </template>
  
  <script>
  import { ref } from "vue";
  import { defineComponent } from "vue";
  
  import IncidenceCells from "./IncidenceCells.vue"
  
  export default defineComponent({
    model: ref(null),
    name: "IncidenceTabs",
    components: {
      IncidenceCells,
    },
    computed: {
      IncidenceDatasetId() {
        return this.$store.state.incidence.incidenceDatasetId
      },
    },
    setup() {
      return {
        tab: ref("table"),
      };
    },
  });
  </script>
  