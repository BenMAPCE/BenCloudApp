<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <div class="row">
        <div class="col"> Review Valuation Functions</div>
      </div>
      <div class="row">
        <div class="col">
            <Suspense>
              <HealthEffectGroups updateState></HealthEffectGroups>
            </Suspense>
          
        </div>

      <div class="col" v-if="currentHealthEffectGroupId && isAdmin">
        <q-toggle
          size="lg"
          v-model="showAll"
          color="blue"
          label="See valuation functions of all users"
        />
      </div>

      <div class="col">
          <ValuationAdd></ValuationAdd>
        </div>
    </div>

    <div class="q-pa-md">
        <ValuationDatasets></ValuationDatasets>
      </div>
      

  </div>
  </q-page>
</template>

<script>
import { defineComponent } from "vue";
import { ref, reactive } from "vue";
import { watch, onBeforeMount } from "vue";

import HealthEffectGroups from "../../../../components/common/HealthEffectGroups.vue";
import ValuationAdd from "../../../../components/common/ValuationAdd.vue";
import ValuationDatasets from "../../../../components/datacenter/valuation/ValuationDatasets.vue";

import { useStore } from "vuex";
import { isAdmin } from "../../../../boot/auth.js";

export const showAll = ref(false);

export default defineComponent({
  model: ref(null),
  name: "ReviewValuation",
  components: {
    ValuationAdd,
    ValuationDatasets,
    HealthEffectGroups
},
setup(props, context) {
  const store = useStore();

    const currentHealthEffectGroupId = ref(0);
    const selectedHealthEffectGroupId = reactive(ref(0));
    const selectedHealthEffectGroupName = ref("OOPS");

    watch(
      () => store.state.valuation.healthEffectGroupId,
      (healthEffectGroupId, prevHealthEffectGroupId) => {
        currentHealthEffectGroupId.value = healthEffectGroupId;
        console.log("------- currentHealthEffectGroupId: " + currentHealthEffectGroupId.value);
        selectedHealthEffectGroupId.value = store.state.valuation.healthEffectGroupId
        selectedHealthEffectGroupName.value = store.state.valuation.healthEffectGroupName
        console.log("------- healthEffectGroupId: " + store.state.valuation.healthEffectGroupId);
        console.log("------- healthEffectGroupId: " + healthEffectGroupId);
        console.log("------- healthEffectGroupFriendlyName: " + store.state.valuation.healthEffectGroupName);
      }
    );

  onBeforeMount(() => {});

  return {
    currentHealthEffectGroupId,
    selectedHealthEffectGroupId,
    selectedHealthEffectGroupName,
    showAll,
    isAdmin
  };
},
});
</script>

<style lang="scss" scoped>

.health-effect-group-options {
  width: 250px;
}
</style>
