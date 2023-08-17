<template>
        <div class="text-h6" v-if="GridDefinitionId != 0">Grid Definition Cells</div>
        <GridDefinitionCells></GridDefinitionCells>
  </template>
  
  <script>
  import { ref } from "vue";
  import { defineComponent } from "vue";
  
  import GridDefinitionCells from "./GridDefinitionCells.vue"
  
  export default defineComponent({
    model: ref(null),
    name: "GridTabs",
    components: {
      GridDefinitionCells,
    },
    computed: {
      GridDefinitionId() {
        return this.$store.state.grids.gridId
      },
    },
    setup() {
      return {
        tab: ref("table"),
      };
    },
  });
  </script>
  