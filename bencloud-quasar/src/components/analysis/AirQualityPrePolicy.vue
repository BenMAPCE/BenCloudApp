<template>

        <q-scroll-area class="aq-pre-policy-scroll-area" visible: true>
          <div class="q-py-xs">
            <q-option-group
              v-model="selectedItem"
              :options="rows"
              color="primary"
            ></q-option-group>
          </div>
          
        </q-scroll-area>
</template>

<script>

import { defineComponent } from "vue";
import { ref, watch, onBeforeMount } from "vue";
import { useStore } from "vuex";
import { loadAirQualityLayers } from '../../composables/air-quality';
import { convertAirQualityLayers } from '../../composables/air-quality';

export default defineComponent({
  model: ref(null),
  name: "AirQualityPrePolicy",

async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItem  = ref(0);

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " | " + prevSelectedItem)
        if (currentSelectedItem != prevSelectedItem) {
          console.log("currentSelectedItem: " + currentSelectedItem)
          var name = rows.value.find((opt) => opt.value === currentSelectedItem).label;

          store.commit("analysis/updatePrePolicyAirQuality", 
          { 
            prePolicyAirQualityId: currentSelectedItem,  
            prePolicyAirQualityName: name
          });
        }
      });

  onBeforeMount(() => {
        (async () => {
          const response = await loadAirQualityLayers().fetch();
          rows.value = convertAirQualityLayers(response.data.value);

        if (store.state.analysis.prePolicyAirQualityId != null) {
          selectedItem.value = store.state.analysis.prePolicyAirQualityId;
        }

        })()
    })

    return {
      rows,
      selectedItem,
    };
}
});

</script>

<style lang="scss" scoped>

.aq-pre-policy-scroll-area {
  border: 1px solid black;
  height: 200px; 
  max-width: 300px;
}

</style>