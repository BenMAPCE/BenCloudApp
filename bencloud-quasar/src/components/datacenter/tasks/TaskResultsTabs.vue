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
    <q-tab name="hif-results" class="hif-results" label="HIF Results" />
    <q-tab name="valuation-results" class="valuation-results" label="Valuation Results" />
  </q-tabs>

  <q-tab-panels v-model="tab" animated>
    <q-tab-panel name="hif-results"> 
      <div v-if="task_type === 'H'">
        <HIFTaskResults  v-bind:task_uuid="task_uuid" v-bind:task_name="task_name" v-bind:task_type="task_type" :key="componentKey"></HIFTaskResults>
      </div>
    </q-tab-panel>

    <q-tab-panel name="valuation-results">
      <div v-if="valuation_task_type === 'V'">
        <ValuationTaskResults  v-bind:valuation_task_uuid="valuation_task_uuid" v-bind:valuation_task_name="valuation_task_name" v-bind:valuation_task_type="valuation_task_type" :key="componentKey"></ValuationTaskResults>
      </div>
      <div v-if="valuation_task_type === ''">
        No valuation results for the selected post-policy
      </div>
    </q-tab-panel>
  </q-tab-panels>
</template>

<script>
import { ref, onBeforeMount } from "vue";
import { defineComponent } from "vue";

import HIFTaskResults from "./HIF/HIFTaskResults.vue";
import ValuationTaskResults from "./Valuation/ValuationTaskResults.vue";

export default defineComponent({
  model: ref(null),
  name: "TaskResultsTab",
  props: ["task_uuid_with_type", "task_name", "valuation_task_uuid_with_type", "valuation_task_name"],

  components: {
    HIFTaskResults,
    ValuationTaskResults
  },
  setup(props) {

    const task_type = ref("");
    const task_name = ref("");
    const task_uuid = ref(null);
    const valuation_task_type = ref("");
    const valuation_task_name = ref("");
    const valuation_task_uuid = ref(null);
    const componentKey = ref(0);

    onBeforeMount(() => {

        console.log("TaskResultsTab");
        // console.log("--------------------------------")
        // console.log(props.task_uuid_with_type)
        // console.log("--------------------------------")
        task_type.value = props.task_uuid_with_type.substring(0,1)
        task_uuid.value = props.task_uuid_with_type.substring(2)
        task_name.value = props.task_name
        // console.log("--------------------------------")
        valuation_task_type.value = props.valuation_task_uuid_with_type.substring(0,1)
        valuation_task_uuid.value = props.valuation_task_uuid_with_type.substring(2)
        valuation_task_name.value = props.valuation_task_name
        componentKey.value += 1;
        // console.log("--------------------------------")
    
    })

    return {
      tab: ref("hif-results"),
      task_uuid,
      task_type,
      componentKey,
      valuation_task_uuid,
      valuation_task_type,
    };

  },
});
</script>

<style lang="scss" scoped>
.q-tab.hif-results {
  flex: 0 0 auto;
  align-items: left;
  align-self: left;
}
.q-tab.valuation-results {
  flex: 0 0 auto;
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
