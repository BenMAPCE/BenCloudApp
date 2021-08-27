<template>
  <div class="q-pa-md">
    <q-select
      square
      dense
      outlined
      v-model="selectedItem"
      :options="rows"
      class="population-year-options"
      emit-value
      map-options
      label="Year"
    />
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onMounted } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "PopulationYears",

  setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItem = ref(store.state.analysis.populationYear);

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          store.commit("analysis/updatePopulationYear", currentSelectedItem);
        }
      }
    );

    watch(
      () => store.state.analysis.populationYears,
      (currentSelectedItem, prevSelectedItem) => {
        var years = [];
        for (let val of currentSelectedItem) {
          years.push({ label: val, value: val });
        }
        rows.value = years;

        if (currentSelectedItem != prevSelectedItem) {
          store.commit("analysis/updatePopulationYear", null);
        }
      }
    );

    onMounted(() => {
      var years = store.state.analysis.populationYears;

      if (store.state.analysis.populationYears != null) {
        var years = [];
        for (let val of store.state.analysis.populationYears) {
          years.push({ label: val, value: val });
        }
        rows.value = years;
      }

      if (store.state.analysis.populationYear != null) {
        selectedItem.value = store.state.analysis.populationYear;
      }
    })();

    return {
      rows,
      selectedItem,
    };
  },
});
</script>

<style lang="scss" scoped>
.population-year-options {
  width: 300px;
}
</style>
