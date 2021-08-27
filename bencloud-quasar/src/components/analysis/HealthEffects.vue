<template>
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
      <div class="text-h6">
        Estimate a standard set of health effects
        <span>
          <q-icon name="mdi-information" />
          <q-tooltip class="bg-yellow text-black" anchor="center right" self="center right" :offset="[100, 0]">
            HELP!
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
import { defineComponent } from "vue";
import { ref } from "vue";
import HealthEffectsStandard from "./HealthEffectsStandard.vue";

export default defineComponent({
  model: ref(null),
  name: "HealthEffects",

  components: {
    HealthEffectsStandard,
  },

  setup(props, context) {
    return {
      tab: ref("standard"),
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
</style>
