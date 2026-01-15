<template>
  <div class="q-pt-sm q-pb-sm aq-post-policy-selection">
    <q-select square dense outlined v-model="selectedGroup" :options="groupOptions" emit-value
      label="Filter to Group (optional)">
    </q-select>
  </div>
  <div>
    <q-scroll-area class="aq-post-policy-scroll-area" visible: true>
      <q-option-group v-model="selectedItems" :options="rowsFiltered" color="primary" type="checkbox"
        emit></q-option-group>
    </q-scroll-area>
  </div>

</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount } from "vue";
import { useStore } from "vuex";
import { convertAirQualityLayers } from '../../composables/analysis/air-quality';

export default defineComponent({
  model: ref(null),
  name: "AirQualityPostPolicy",

  async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const rowsFiltered = ref([]);
    const selectedItems = ref([]);

    const groupOptions = ref([]);
    const selectedGroup = ref(null);

    watch(
      () => selectedItems.value,
      (currentSelectedItems, prevSelectedItems) => {
        console.log("watch: " + currentSelectedItems + " |" + prevSelectedItems)
        if ((currentSelectedItems != prevSelectedItems) && (currentSelectedItems.length + prevSelectedItems.length > 0)) {

          updateSelectedItems(currentSelectedItems, false);
        }
      });

    watch(
      () => selectedGroup.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " | " + prevSelectedItem)
        if (currentSelectedItem != prevSelectedItem) {
          console.log("currentSelectedItem: " + currentSelectedItem)

          store.commit("analysis/updatePostPolicyAirQualityGroupName", currentSelectedItem);
          //filter to selected group if any
          try {
            if (selectedGroup.value) {
              rowsFiltered.value = rows.value.filter(r => r.groupName === selectedGroup.value);
              //de-select aq layer if it is not in the filtered list
              updateSelectedItems(selectedItems.value, true);
            }
            else {
              rowsFiltered.value = rows.value;
            }
          } catch (e) {
            rowsFiltered.value = rows.value;
            console.log("Error applying filter. No group filter applied");
          }
        }
      }

    );

    // The airQualityLayers property is set when the AirQualityPrePolicy.vue component pulls a new list of layers
    // We monitor the change so we don't have to make a duplicate api call.  Instead we pull the data fro the store.
    watch(
      () => store.state.analysis.airQualityLayers,
      (newData, oldData) => {
        rows.value = convertAirQualityLayers(newData);
        if (store.state.analysis.postPolicyAirQualityId != null) {
          selectedItems.value = store.state.analysis.postPolicyAirQualityId;
        }
        //get unique group names for the dropdown
        if (!rows.value || rows.value.length == 0) {
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
          rowsFiltered = rows.value;
          console.log("Error applying filter. No group filter applied");
        }
      }
    );

    onBeforeMount(() => {
      // (async () => {
      //   const response = await loadAirQualityLayers().fetch();
      //   rows.value = convertAirQualityLayers(response.data.value);
      //
      // if (store.state.analysis.postPolicyAirQualityId != null) {
      //   selectedItems.value = store.state.analysis.postPolicyAirQualityId;
      // }
      //
      // })()
    })

    function updateSelectedItems(currentSelectedItems, updateSelItems) {
      //if updateSelItems is true, we want to check selected items agains the filtered list and update it
      //Only update selectedItems when group filter changes to avoid watch feedback loop
      var names = [];
      var ids = [];
      var gridIds = [];
      var gridNames = [];
      if (currentSelectedItems.length > 0) {
        //only add selected items that are in the filtered list
        var newSelectedItems = [];
        rowsFiltered.value.forEach(element => {
          if (currentSelectedItems.includes(element.value)) {
            newSelectedItems.push(element.value);
            var scenario = null;
            if (!!store.state.analysis.postPolicyAirQualityName) {
              scenario = store.state.analysis.postPolicyAirQualityName.find(e => e.name === element.label)
            }
            if (!!store.state.analysis.postPolicyAirQualityGroupName) {
              selectedGroup.value = store.state.analysis.postPolicyAirQualityGroupName;
            }
            //if this element is already stored, there may be corresponding years stored
            if (!!scenario) {
              names.push(scenario);
              ids.push(element.value);
              //if the element is not stored, the years value is empty for now
            } else {
              var scenario = { name: element.label, years: [], popYears: [] };
              names.push(scenario);
              ids.push(element.value);
            }
            gridIds.push(element.gridId);
            gridNames.push(element.gridName);
          }
        })
        if(updateSelItems){
          selectedItems.value = newSelectedItems;
        }
        store.commit("analysis/updatePostPolicyAirQuality",
          {
            postPolicyAirQualityId: ids,
            postPolicyAirQualityName: names,
            postPolicyGridDefinitionId: gridIds,
            postPolicyGridDefinitionName: gridNames
          });

      }
      else {
        //if nothing is selected, clear the store values
        selectedItems.value = [];
        store.commit("analysis/updatePostPolicyAirQuality",
          {
            postPolicyAirQualityId: [],
            postPolicyAirQualityName: [],
            postPolicyGridDefinitionId: [],
            postPolicyGridDefinitionName: []
          });
      }
    }

    return {
      rows,
      rowsFiltered,
      selectedItems,
      groupOptions,
      selectedGroup,
    };
  }
});

</script>

<style lang="scss" scoped>
.aq-post-policy-scroll-area {
  border: 1px solid black;
  height: 200px;
  max-width: 90%;
}

.aq-post-policy-selection {
  max-width: 90%;
}
</style>
