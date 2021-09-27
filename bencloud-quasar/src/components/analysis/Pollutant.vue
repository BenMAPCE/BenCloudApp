<template>
  <div class="q-pa-md">
    <div class="q-gutter-sm">
      <div class="row">
        <div v-for="row in rows" :key="row.id">
          <div class="col-2 pollutant-options">
            <q-radio v-model="selectedItem" :val="row.id" :label="row.friendly_name" />
          </div>
        </div>
      </div>
     <div class="row">
        <div v-for="row in rows" :key="row.id">
          <div class="col-4 pollutant-options">
            <q-img
              
              :src="`/images/pollutant_${row.id}.png`"
              spinner-color="white"
              style="height: 100px; max-width: 100px;"
            />
          </div>
        </div>
      </div>
     </div>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";
import { loadPollutants } from "../../composables/common/pollutants";

export default defineComponent({
  model: ref(null),
  name: "Pollutant",

  async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItem = ref(store.state.analysis.pollutantId);
    const selectedItemName = ref(store.state.analysis.pollutantFriendlyName);
    const selectedItemFriendlyName = ref(store.state.analysis.pollutantFriendlyName);

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          console.log(rows.value.find((opt) => opt.id === currentSelectedItem).name);
          var name = rows.value.find((opt) => opt.id === currentSelectedItem).name;
          var friendlyName = rows.value.find((opt) => opt.id === currentSelectedItem).friendly_name;
          console.log("----- " + friendlyName)
          store.commit("analysis/updatePollutantId", currentSelectedItem);
          store.commit("analysis/updatePollutantName", name);
          store.commit("analysis/updatePollutantFriendlyName", friendlyName);
          
          store.commit("analysis/updatePrePolicyAirQuality", 
          { 
            prePolicyAirQualityId: null,  
            prePolicyAirQualityName: ""
          });

          store.commit("analysis/updatePostPolicyAirQuality", 
          { 
            postPolicyAirQualityId: null,  
            postPolicyAirQualityName: ""
          });

          selectedItemFriendlyName.value = friendlyName
        }
      }
    );

    onBeforeMount(() => {
      (async () => {
        console.log("loadPollutantsOptions");
        const response = await loadPollutants().fetch();
        rows.value = response.data.value;
      })();
    });

    onMounted(() => {
      console.log("... " + store.state.analysis.pollutantId);
      if (store.state.analysis.pollutantId != null) {
        selectedItem.value = store.state.analysis.pollutantId;
        selectedItemName.value = store.state.analysis.pollutantName;
        console.log("- selectedItem: " + selectedItem.value);
        console.log("- selectedItemName: " + selectedItemName.value);
      }
    })();

    return {
      rows,
      selectedItem,
      selectedItemName,
      selectedItemFriendlyName
    };
  },
});
</script>

<style lang="scss" scoped>
.pollutant-options {
  width: 250px;
}
</style>
