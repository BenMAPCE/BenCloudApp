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
      class="incidence-options"
      emit-value
      map-options
      label="Incidence"
    />
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";
import { loadIncidence } from "../../composables/analysis/incidence";

export default defineComponent({
  model: ref(null),
  name: "PopulationDataset",

  async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItem = ref(store.state.analysis.incidenceId);

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          console.log(rows.value.find((opt) => opt.id === currentSelectedItem).name);
          var name = rows.value.find((opt) => opt.id === currentSelectedItem).name;
          store.commit("analysis/updateIncidence", {
            incidenceId: currentSelectedItem,
            incidenceName: name,
          });
        }
      }
    );

    onBeforeMount(() => {
      (async () => {
        console.log("loadPopulationOptions");
        const response = await loadIncidence().fetch();
        rows.value = response.data.value;
        console.log("before: " + store.state.analysis.incidenceId);
      })();
    });

    onMounted(() => {
      console.log("... " + store.state.analysis.incidenceId);
      if (store.state.analysis.incidenceId != null) {
        selectedItem.value = store.state.analysis.incidenceId;
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
.incidence-options {
  width: 300px;
}
</style>
