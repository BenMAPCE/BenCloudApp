<template>
    <q-tabs v-model="tab" dense class="text-grey" active-color="primary" indicator-color="primary" align="justify" narrow-indicator>
      <q-tab name="map" label="Map View"></q-tab>
      <q-tab name="table" label="Tabular View"></q-tab>
    </q-tabs>
  </template>
  
  <script>
  import { defineComponent, ref } from "vue";
  
  export default defineComponent({
    name: "IncidenceDataTabs",
    setup() {
      const tab = ref("map");
  
      return {
        tab,
      };
    },
  });
  </script>
  