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
      class="aggregation-options"
      emit-value
      map-options
      label="Aggregation Scale"
    />
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "Aggregation",

  async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const gridOptions = [
      {
        name: 'Nation',
        id: 20
      }, 
      {
        name: 'State',
        id: 19
      },
      {
        name: 'County',
        id: 18
      },
      {
        name: 'CMAQ 12km Nation',
        id: 28
      }
    ]
    const selectedItem = ref();
    var id = null;


    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          gridOptions.forEach(element => {
            if(currentSelectedItem === element.name) {
              id = element.id;
            }
          })
          store.commit("analysis/updateAggregationScale", id);
        }
      }
    );

    onBeforeMount(() => {
      (async () => {
        gridOptions.forEach(element => {
          rows.value.push(element.name);
        })
      })();
    });

    onMounted(() => {
      console.log("... " + store.state.analysis.aggregationScale);
      if (store.state.analysis.aggregationScale != null) {
        id = store.state.analysis.aggregationScale;
        gridOptions.forEach(element => {
          if(element.id === id) {
            selectedItem.value = element.name;
          }
        })
      } else {
        selectedItem.value = rows.value[2]; //default to county
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
.aggregation-options {
  width: 300px;
}
</style>
