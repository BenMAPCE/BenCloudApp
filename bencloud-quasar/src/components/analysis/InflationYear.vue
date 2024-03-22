<template>
  <div class="q-pa-md">
    <q-select
      square
      dense
      outlined
      v-model="selectedItem"
      :options="rows"
      option-value="entry_year"
      option-label="entry_year"
      class="inflationYear-options"
      emit-value
      map-options
      label="Dollar Year"
    />
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";
import { loadInflation } from "../../composables/analysis/inflation";

export default defineComponent({
  model: ref(null),
  name: "InflationYearSelection",

  async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItem = ref(store.state.analysis.inflationYear); 

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          //console.log(rows.value.find((opt) => opt.id === currentSelectedItem).name);
          store.commit("analysis/updateInflationYear", currentSelectedItem);
          console.log("selected inflation year: " + store.state.analysis.inflationYear);
        }
      }
    );

    onBeforeMount(() => {
      (async () => {
        console.log("load Inflation Years");
        const response = await loadInflation().fetch();
        rows.value = response.data.value;
        selectedItem.value = rows.value[0]; //default to most recent year (usually 2020)
        if (store.state.analysis.inflationYear != null) {
        selectedItem.value = store.state.analysis.inflationYear;
        console.log("- selectedItem: " + selectedItem.value);
        }
        else{
          console.log(rows.value);
          
        }
      })();
    });

    onMounted(() => {
      
    })();

    return {
      rows,
      selectedItem,
    };
  },
});
</script>

<style lang="scss" scoped>
.inflationYear-options {
  width: 300px;
}
</style>
