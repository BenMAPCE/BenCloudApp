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
      class="valuation-options"
      emit-value
      map-options
      label="Valuation Selection"
    />
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "ValuationSelection",

  async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItem = ref(store.state.analysis.valuationSelection);

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          console.log(rows)
          store.commit("analysis/updateValuationSelection", currentSelectedItem);
        }
      }
    );

    onBeforeMount(() => {
      (async () => {
        console.log("load valuation dataset options");
        rows.value = ["Select my own value functions", "Use EPA's current default values"];
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
.valuation-options {
  width: 300px;
}
</style>
