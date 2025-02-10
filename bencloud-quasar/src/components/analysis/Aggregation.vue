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
        name: 'Nation (2010)',
        id: 20
      }, 
      {
        name: 'Nation (2020)',
        id: 70
      },
      {
        name: 'State (2010)',
        id: 19
      },
      {
        name: 'State (2020)',
        id: 69
      },
      {
        name: 'County (2010)',
        id: 18
      },
      {
        name: 'County (2020)',
        id: 68
      },
      {
        name: 'CMAQ 12km Nation',
        id: 28
      }
    ]
    const selectedItem = ref();
    var id = null;
    var defaultValue = "";


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
        //Hard-coded default value for 2020 testing. Show different grid definitions when different pop dataset is selected. 
        const gridOptionsFlt = gridOptions.filter(element=>{
          if (store.state.analysis.populationDatasetId == 50 || store.state.analysis.populationDatasetId == 51 || store.state.analysis.populationDatasetId == 52 || store.state.analysis.populationDatasetId == 53){
            defaultValue = "County (2020)";
            return element.id===70 || element.id===69 || element.id===68 || element.id===28;
          }
          else{
            defaultValue = "County (2010)";
            return element.id===20 || element.id===19 || element.id===18 || element.id===28;
          }
        })      
        
        gridOptionsFlt.forEach(element => {
          rows.value.push(element.name);
        })

        //set default or already selected values
        console.log("... " + store.state.analysis.aggregationScale);
        if (store.state.analysis.aggregationScale != null) {
          id = store.state.analysis.aggregationScale;
          gridOptions.forEach(element => {
            if(element.id === id && rows.value.includes(element.name)) {
              selectedItem.value = element.name;            
            }
            else{
              selectedItem.value=defaultValue;
            }
          })
        } else {
          selectedItem.value= defaultValue;          
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
.aggregation-options {
  width: 300px;
}
</style>
