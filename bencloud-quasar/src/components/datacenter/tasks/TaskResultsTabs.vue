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
    <div class="tabs q-pl-sm" v-if="task_type != 'E'">
      <q-tab name="results" class="hif-results" label="HIF Results" @click="changeTabs('results')" />
      <q-tab name="valuation-results" class="valuation-results" label="Valuation Results" @click="changeTabs('valuation-results')" />
    </div>
    <div class="q-pl-sm" v-if="task_type == 'E'">
      <q-tab name="results" class="exposure-results" label="Exposure Results" @click="changeTabs('results')" />
    </div>
  </q-tabs>

  <q-tab-panels v-model="tab" animated v-if="task_type != 'E'" keep-alive>
    <q-tab-panel name="results"> 
      <div v-if="task_type === 'H'">
        <HIFTaskResults  v-bind:task_uuid="task_uuid" v-bind:task_name="task_name" v-bind:task_type="task_type" v-bind:batch_task_id="batch_task_id" :key="componentKey"></HIFTaskResults>
      </div>
    </q-tab-panel>

    <q-tab-panel name="valuation-results">
      <div v-if="valuation_task_type === 'V'">
        <ValuationTaskResults  v-bind:valuation_task_uuid="valuation_task_uuid" v-bind:valuation_task_name="valuation_task_name" v-bind:valuation_task_type="valuation_task_type" v-bind:batch_task_id="batch_task_id" :key="componentKey"></ValuationTaskResults>
      </div>
      <div v-if="valuation_task_type === ''">
        No valuation results for the selected post-policy
      </div>
    </q-tab-panel>
  </q-tab-panels>

  <q-tab-panels v-model="tab" animated v-if="task_type == 'E'">
    <q-tab-panel name="results"> 
      <div v-if="task_type === 'E'">
        <ExposureTaskResults  v-bind:task_uuid="task_uuid" v-bind:task_name="task_name" v-bind:task_type="task_type" v-bind:batch_task_id="batch_task_id" :key="componentKey"></ExposureTaskResults>
      </div>
    </q-tab-panel>
  </q-tab-panels>
</template>

<script>
import { ref, onBeforeMount } from "vue";
import { defineComponent } from "vue";
import { useStore } from "vuex";

import HIFTaskResults from "./HIF/HIFTaskResults.vue";
import ValuationTaskResults from "./Valuation/ValuationTaskResults.vue";
import ExposureTaskResults from "./Exposure/ExposureTaskResults.vue";

export default defineComponent({
  model: ref(null),
  name: "TaskResultsTab",
  props: ["task_uuid_with_type", "task_name", "valuation_task_uuid_with_type", "valuation_task_name", "batch_task_id"],

  components: {
    HIFTaskResults,
    ValuationTaskResults,
    ExposureTaskResults
  },
  setup(props) {
    const store = useStore();

    const task_type = ref("");
    const task_name = ref("");
    const task_uuid = ref(null);
    const valuation_task_type = ref("");
    const valuation_task_name = ref("");
    const valuation_task_uuid = ref(null);
    const componentKey = ref(0);
    const batch_task_id = ref(0);
    const currentTab = ref(store.state.datacenter.resultsTab);

    onBeforeMount(() => {

        console.log("TaskResultsTab");
        task_type.value = props.task_uuid_with_type.substring(0,1)
        if(task_type.value === "E") {
          store.commit("datacenter/updateResultsTab", "results");
          currentTab.value = "results";
        }
        task_uuid.value = props.task_uuid_with_type.substring(2)
        batch_task_id.value = props.batch_task_id
        task_name.value = props.task_name
        valuation_task_type.value = props.valuation_task_uuid_with_type.substring(0,1)
        valuation_task_uuid.value = props.valuation_task_uuid_with_type.substring(2)
        valuation_task_name.value = props.valuation_task_name
        componentKey.value += 1;
    
    })

    function changeTabs(tab) {
      store.commit("datacenter/updateResultsTab", tab);
      currentTab.value = tab;
    }

    return {
      tab: currentTab,
      task_uuid,
      task_type,
      componentKey,
      valuation_task_uuid,
      valuation_task_type,
      changeTabs,
    };

  },
});
</script>

<style lang="scss" scoped>
.tabs {
  display: flex;
  align-items: left;
  align-self: left;
}

.active-tasks-wrapper,
.completed-tasks-wrapper {
  flex: 0 0 auto;
  align-items: left;
  align-self: left;
  padding: 10px 20px 20px 20px;
}

.refresh-message {
  padding: 25px 20px 10px 20px;
  font-size: 18px;
}
</style>
