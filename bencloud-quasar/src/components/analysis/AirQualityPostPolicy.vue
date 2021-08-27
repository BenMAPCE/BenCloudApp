<template>

        <q-scroll-area class="aq-post-policy-scroll-area" visible: true>
          <div class="q-py-xs">
            <q-option-group
              v-model="selectedItem"
              :options="rows"
              color="primary"
            ></q-option-group>
          </div>
          
        </q-scroll-area>
    {{ selectedItem }}
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount } from "vue";
import { useStore } from "vuex";
import { loadAirQualityLayers } from '../../composables/air-quality';
import { convertAirQualityLayers } from '../../composables/air-quality';

export default defineComponent({
  model: ref(null),
  name: "AirQualityPostPolicy",

async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItem  = ref(0);

    watch(
      () => selectedItem.value,
      (selectedItem, prevSelectedItem) => {
        console.log("watch: " + selectedItem + " |" + prevSelectedItem)
        if (selectedItem != prevSelectedItem) {
          console.log("selectedItem: " + selectedItem)
          store.commit("analysis/updatePostPolicyAirQuality", selectedItem);

        }
      });

  onBeforeMount(() => {
        (async () => {
          const response = await loadAirQualityLayers().fetch();
          rows.value = convertAirQualityLayers(response.data.value);

        if (store.state.analysis.postPolicyAirQuality != null) {
          selectedItem.value = store.state.analysis.postPolicyAirQuality;
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

.aq-post-policy-scroll-area {
  border: 1px solid black;
  height: 200px; 
  max-width: 300px;
}

</style>