<template>

  <q-scroll-area class="aq-post-policy-scroll-area" visible: true>
      <q-option-group
        v-model="selectedItems"
        :options="rows"
        color="primary"
        type="checkbox"
      ></q-option-group>
  </q-scroll-area>

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
    const selectedItems = ref([]);

    watch(
      () => selectedItems.value,
      (currentSelectedItems, prevSelectedItems) => {
        console.log("watch: " + currentSelectedItems + " |" + prevSelectedItems)
        if (currentSelectedItems != prevSelectedItems) {
          var names = [];
          rows.value.forEach(element => {
            if(currentSelectedItems.includes(element.value)) {
              var scenario = null;
              if(!!store.state.analysis.postPolicyAirQualityName) {
                scenario = store.state.analysis.postPolicyAirQualityName.find(e => e.name === element.label)
              }
              //if this element is already stored, there may be corresponding years stored
              if(!!scenario) {
                names.push(scenario);
                //if the element is not stored, the years value is empty for now
              } else {
                var scenario = { name: element.label, years:[], popYears: []};
                names.push(scenario);
              }
            }
          })
          store.commit("analysis/updatePostPolicyAirQuality",
          {
            postPolicyAirQualityId: currentSelectedItems,
            postPolicyAirQualityName: names
          });
        }
      });

  // The airQualityLayers property is set when the AirQualityPrePolicy.vue component pulls a new list of layers
  // We monitor the change so we don't have to make a duplicate api call.  Instead we pull the data fro the store.
  watch(
    () => store.state.analysis.airQualityLayers,
    (newData, oldData) => {
      rows.value = convertAirQualityLayers(newData);
      if (store.state.analysis.postPolicyAirQualityId != null) {
        selectedItems.value = store.state.analysis.postPolicyAirQualityId;
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

    return {
      rows,
      selectedItems,
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

</style>
