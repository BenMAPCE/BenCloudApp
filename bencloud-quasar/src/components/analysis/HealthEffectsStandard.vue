
<template>
  <div class="q-pa-md">
    <q-option-group :options="rows" type="checkbox" v-model="selectedItems" />
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";
import { loadHealthEffects } from "../../composables/analysis/health-effects";
import { convertHealthEffects } from "../../composables/analysis/health-effects";

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

          var heItems = []
          var heItem = {}
          for (var i = 0; i < currentSelectedItems.length; i++) {
            console.log(currentSelectedItems)
            var name = rows.value.find((opt) => opt.value === currentSelectedItems[i]).label;
            console.log(name)
            heItem = { healthEffectId: currentSelectedItems[i], healthEffectName: name }
            heItems.push(heItem)
              //var name = currentSelectedItems[i].find((opt) => opt.value === currentSelectedItems[i]).label;
          }
          console.log(heItems)
  
          store.commit("analysis/updateHealthEffects", heItems);
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

        var heItems = store.state.analysis.healthEffects
        var heItemIds = []
        for (var i = 0; i < heItems.length; i++) {
            console.log(heItems[i].healthEffectId)
            heItemIds.push(heItems[i].healthEffectId)
        }

        selectedItems.value = heItemIds

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
