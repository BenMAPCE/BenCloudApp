<template>
  <div class="q-pt-sm q-pb-sm aq-pre-policy-selection">
    <q-select square dense outlined v-model="selectedGroup" :options="groupOptions" emit-value
      label="Filter to Group (optional)">
    </q-select>
  </div>
  <div>
    <q-scroll-area class="aq-pre-policy-scroll-area" visible: true>
      <q-option-group v-model="selectedItem" :options="rowsFiltered" color="primary" emit></q-option-group>
    </q-scroll-area>
  </div>
</template>

<script>

import { defineComponent } from "vue";
import { ref, watch, onBeforeMount } from "vue";
import { useStore } from "vuex";
import { loadAirQualityLayers } from '../../composables/exposure/air-quality';
import { convertAirQualityLayers } from '../../composables/exposure/air-quality';

export default defineComponent({
  model: ref(null),
  name: "AirQualityPrePolicy",

  components: {
  },

  async setup(props, { emit }) {

    const store = useStore();
    const rows = ref([]);
    const rowsFiltered = ref([]);
    const selectedItem = ref(0);

    const groupOptions = ref([]);
    const selectedGroup = ref(null);

    // const selectedPollutantId = ref(0)
    // const selectedPollutantFriendlyName = ref("OOPS")

    let airQualityLayers = null;

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " | " + prevSelectedItem)
        if (currentSelectedItem != prevSelectedItem) {
          console.log("currentSelectedItem: " + currentSelectedItem)
          if(currentSelectedItem){
            var selectedRow = rows.value.find((opt) => opt.value === currentSelectedItem);
          var name = selectedRow.label;
          var gridId = selectedRow.gridId;
          var gridName = selectedRow.gridName;
          store.commit("exposure/updatePrePolicyAirQuality",
            {
              prePolicyAirQualityId: currentSelectedItem,
              prePolicyAirQualityName: name,
              prePolicyGridDefinitionId: gridId,
              prePolicyGridDefinitionName: gridName
            });
          }
          else {
            store.commit("exposure/updatePrePolicyAirQuality",
            {
              prePolicyAirQualityId: null,
              prePolicyAirQualityName: null,
              prePolicyGridDefinitionId: null,
              prePolicyGridDefinitionName: null
            });
          }
          

          
        }
      });

    watch(
      () => selectedGroup.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " | " + prevSelectedItem)
        if (currentSelectedItem != prevSelectedItem) {
          console.log("currentSelectedItem: " + currentSelectedItem)

          store.commit("exposure/updatePrePolicyAirQualityGroupName", currentSelectedItem);
          //filter to selected group if any
          try {
            if (selectedGroup.value) {
              rowsFiltered.value = rows.value.filter(r => r.groupName === selectedGroup.value);
              //de-select aq layer if it is not in the filtered list
              if(!rowsFiltered.value.find(r => r.value === selectedItem.value)){
                selectedItem.value = null;
              }
            }
            else {
              rowsFiltered.value = rows.value;
            }
          } catch (e) {
            rowsFiltered = rows.value;
            console.log("Error applying filter. No group filter applied");
          }
        }
      }

    );

    // The airQualityForceReloadValue value is updated when a new file is uploaded
    // SO we want to watch it and when it changes we know we need to update our list of files
    watch(
      () => store.state.airquality.airQualityForceReloadValue,
      (newCount, oldCount) => {
        getRows();
      }
    );


    function updateMetric(value) {
      console.log("AQ Pre Selected... " + value);
    }

    function getRows() {
      (async () => {
        const response = await loadAirQualityLayers(store).fetch();
        rows.value = convertAirQualityLayers(response.data.value);
        rows
        //get unique group names for the dropdown
        if(!rows.value || rows.value.length == 0){
          groupOptions.value = [];
          return;
        }         
        const uniqueGroupNames = [null, ...new Set(rows.value.map(item => item.groupName).filter(v=> v != null && v != ""))];
        groupOptions.value = uniqueGroupNames.map(g => ({ label: g, value: g }));
        console.log("groupOptions: ", groupOptions.value);

        //filter to selected group if any
        try {
          if (selectedGroup.value) {
            rowsFiltered.value = rows.value.filter(r => r.groupName === selectedGroup.value);
          }
          else {
            rowsFiltered.value = rows.value;
          }
        } catch (e) {
          rowsFiltered.value = rows.value;
          console.log("Error applying filter. No group filter applied");
        }

        // Setting this value will allow the AirQualityPostPolicy.vue component to pull the new list without making an api call
        store.commit("exposure/updateAirQualityLayers", response.data.value);

        if (store.state.exposure.prePolicyAirQualityId != null) {
          selectedItem.value = store.state.exposure.prePolicyAirQualityId;
        }
        if(store.state.exposure.prePolicyAirQualityGroupName != null){
          selectedGroup.value = store.state.exposure.prePolicyAirQualityGroupName;
        }
      })()
    }

    onBeforeMount(() => {
      getRows();
    })


    return {
      rows,
      rowsFiltered,
      selectedItem,
      updateMetric,
      groupOptions,
      selectedGroup,
      // selectedPollutantId,
      // selectedPollutantFriendlyName
    };
  }
});

</script>

<style lang="scss" scoped>
.aq-pre-policy-scroll-area {
  border: 1px solid black;
  height: 200px;
  max-width: 90%;
}
.aq-pre-policy-selection {
  max-width: 90%;
}
</style>
