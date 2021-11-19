<template>

  <div v-if="stepHasError && atStep.value == 5">
    Please select at least one Health Effect
  </div>

  <q-tabs
    v-model="tab"
    dense
    class="text-grey"
    active-color="primary"
    indicator-color="primary"
    align="justify"
    narrow-indicator
  >
    <q-tab
      name="standard"
      class="standard"
      label="Estimate a standard set of health effects"
    />
    <q-tab name="individual" class="individual" label="Estimate individual effects" />
  </q-tabs>

  <q-tab-panels v-model="tab" animated>
    <q-tab-panel name="standard">
      <div class="header-text">
        Estimate a standard set of health effects
        <span>
          <q-icon name="mdi-information" />
          <q-tooltip
            class="bg-grey-11 text-black tooltip"
            anchor="center right"
            self="center right"
            :offset="[310, 0]"
            max-width = 300px
          >
            {{ heTooltip }}
          </q-tooltip>
        </span>
      </div>
      <Suspense>
        <HealthEffectsStandard></HealthEffectsStandard>
      </Suspense>
    </q-tab-panel>

    <q-tab-panel name="individual">
      <div class="text-h6">Estimate individual effects</div>
    </q-tab-panel>
  </q-tab-panels>
</template>
<script>
import { defineComponent, ref, reactive, inject } from "vue";
import HealthEffectsStandard from "./HealthEffectsStandard.vue";

export default defineComponent({
  model: ref(null),
  name: "HealthEffects",

  components: {
    HealthEffectsStandard,
  },

  setup(props, context) {

    const stepHasError = reactive(inject("stepHasError"));
    const atStep = reactive(inject("atStep"));

    return {
      tab: ref("standard"),
      heTooltip: ref("In this mode, you can select health impact functions grouped by the impact type (mortality vs. morbidity) and time scale for impacts. If you wish to select studies either individually or by health endpoint, click on Estimate Individual Effects above."),
      stepHasError,
      atStep
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

.standard,
.individual {
  flex: 0 0 auto;
  align-items: left;
  align-self: left;
}

.header-text {
  font-size: 16px;
  font-weight: 500;
}

.tooltip {
  width: 300px;
  max-width: 300px;
}

</style>
