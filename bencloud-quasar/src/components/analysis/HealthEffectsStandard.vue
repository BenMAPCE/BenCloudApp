
<template>
  <div class="q-pa-md">
    <q-option-group :options="rows" type="checkbox" v-model="selectedItems" />
  </div>

  {{ selectedItems }}
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";
import { loadHealthEffects } from "../../composables/health-effects";
import { convertHealthEffects } from "../../composables/health-effects";

export default defineComponent({
  model: ref(null),
  name: "HealthEffectsStandard",

  async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItems = ref([]);

    watch(
      () => selectedItems.value,
      (currentSelectedItems, prevSelectedItems) => {

        console.log("watch: " + currentSelectedItems + " |" + prevSelectedItems);
        if (currentSelectedItems != prevSelectedItems) {
          console.log("selectedItems: " + currentSelectedItems);

          store.commit("analysis/updateHealthEffects", currentSelectedItems);
       }
      }
    );

    onMounted(() => {
    });

    onBeforeMount(() => {
      (async () => {
        const response = await loadHealthEffects().fetch();
        console.log(response.data.value);
        rows.value = convertHealthEffects(response.data.value);

        if (store.state.analysis.healthEffects != null) {
          selectedItems.value = store.state.analysis.healthEffects;
        }
      })();
    });

    return {
      rows,
      selectedItems,
    };
  },
});
</script>

<style lang="scss" scoped>
.aq-pre-policy-scroll-area {
  border: 1px solid black;
  height: 200px;
  max-width: 300px;
}
</style>
