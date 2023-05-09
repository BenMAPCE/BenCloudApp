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
import { loadPopulationOptions } from "../../composables/exposure/population";

export default defineComponent({
  model: ref(null),
  name: "PopulationDataset",

  async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItem = ref(store.state.exposure.populationDatasetId);

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          //console.log("selectedItem: " + currentSelectedItem);
          //console.log(rows);
          //console.log(rows.value.find((opt) => opt.id === currentSelectedItem).name);
          var name = rows.value.find((opt) => opt.id === currentSelectedItem).name;
          var years = rows.value.find((opt) => opt.id === currentSelectedItem).years;
          //console.log(years);
          store.commit("exposure/updatePopulationDataset", {
            populationDatasetId: currentSelectedItem,
            populationDatasetName: name,
          });
          store.commit("exposure/updatePopulationYears", years);
          //console.log(store.state.exposure.populationYears);
        }
      }
    );

    onBeforeMount(() => {
      (async () => {
        const response = await loadPopulationOptions().fetch();
        rows.value = response.data.value;
        if (store.state.exposure.populationDatasetId) {
          var years = rows.value.find((opt) => opt.id === store.state.exposure.populationDatasetId).years;
          store.commit("exposure/updatePopulationYears", years);
        }
      })();
    });

    onMounted(() => {
      if (store.state.exposure.populationDatasetId != null) {
        selectedItem.value = store.state.exposure.populationDatasetId;
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
