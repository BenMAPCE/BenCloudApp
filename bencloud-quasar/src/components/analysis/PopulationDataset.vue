<template>
  <div class="q-pa-md">
    <q-select
      square
      dense
      outlined
      v-model="selectedItem"
      :options="rows"
      option-value="id"
      option-label="name"
      class="population-options"
      emit-value
      map-options
      label="Population"
    />
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";
import { loadPopulationOptions } from "../../composables/analysis/population";

export default defineComponent({
  model: ref(null),
  name: "PopulationDataset",

  async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItem = ref(store.state.analysis.populationDatasetId);

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          console.log("selectedItem: " + currentSelectedItem);
          console.log(rows);
          console.log(rows.value.find((opt) => opt.id === currentSelectedItem).name);
          var name = rows.value.find((opt) => opt.id === currentSelectedItem).name;
          var years = rows.value.find((opt) => opt.id === currentSelectedItem).years;
          console.log(years);
          store.commit("analysis/updatePopulationDataset", {
            populationDatasetId: currentSelectedItem,
            populationDatasetName: name,
          });
          store.commit("analysis/updatePopulationYears", years);
          console.log(store.state.analysis.populationYears);
        }
      }
    );

    onBeforeMount(() => {
      (async () => {
        console.log("loadPopulationOptions");
        const response = await loadPopulationOptions().fetch();
        rows.value = response.data.value;
        if (store.state.analysis.populationDatasetId) {
          var years = rows.value.find((opt) => opt.id === store.state.analysis.populationDatasetId).years;
          store.commit("analysis/updatePopulationYears", years);
        }
      })();
    });

    onMounted(() => {
      console.log("... " + store.state.analysis.populationDatasetId);
      if (store.state.analysis.populationDatasetId != null) {
        selectedItem.value = store.state.analysis.populationDatasetId;
        console.log("- selectedItem: " + selectedItem.value);
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
.population-options {
  width: 300px;
}
</style>
