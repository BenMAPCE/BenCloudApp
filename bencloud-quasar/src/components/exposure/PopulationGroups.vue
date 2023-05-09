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
  name: "PopulationGroups",

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

          var heItems = [];
          var heItem = {};
          for (var i = 0; i < currentSelectedItems.length; i++) {
            console.log("____________");
            console.log(currentSelectedItems);
            var name = rows.value.find((opt) => opt.value === currentSelectedItems[i])
              .label;
            console.log(name);
            heItem = { healthEffectId: currentSelectedItems[i], healthEffectName: name };
            heItems.push(heItem);
            //var name = currentSelectedItems[i].find((opt) => opt.value === currentSelectedItems[i]).label;
          }
          console.log(heItems);

          store.commit("exposure/updatePopulationGroups", heItems);
        }
      }
    );

    onMounted(() => {});

    onBeforeMount(() => {
      (async () => {
        //const response = await loadHealthEffects().fetch();
        //console.log(response.data.value);
        //rows.value = convertHealthEffects(response.data.value);
        rows.value = convertHealthEffects([
          {id: 1, name: "Race"}, 
          {id: 2, name: "Ethnicity"},
          {id: 3, name: "Linguistically Isolated"},
          {id: 4, name: "Income < 2x Poverty Line"},
        ]);

        var popGroups = store.state.exposure.populationGroups;

        if (popGroups) {
          var selectedPopGroupIds = [];
          for (var i = 0; i < popGroups.length; i++) {
            selectedPopGroupIds.push(popGroups[i].healthEffectId);
          }
          selectedItems.value = selectedPopGroupIds;
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
