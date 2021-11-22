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
    <q-tab name="national-results" class="national-results" label="National Results" />
    <q-tab name="map" class="map" label="Map" />
  </q-tabs>

  <q-tab-panels v-model="tab" animated>
    <q-tab-panel name="national-results"> 
      <div v-if="task_type === 'H'">
        <HIFTaskResults  v-bind:task_uuid="task_uuid"></HIFTaskResults>
      </div>
      <div v-if="task_type === 'V'">
        <ValuationTaskResults  v-bind:task_uuid="task_uuid"></ValuationTaskResults>
      </div>
    </q-tab-panel>

    <q-tab-panel name="map">
      <div>Coming Soon</div>
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
  props: ["task_uuid_with_type"],

  components: {
    HIFTaskResults,
    ValuationTaskResults
  },
  setup(props) {

    const task_type = ref("");
    const task_uuid = ref(null)

    onBeforeMount(() => {

        console.log("TaskResultsTab");
        // console.log("--------------------------------")
        // console.log(props.task_uuid_with_type)
        // console.log("--------------------------------")
        task_type.value = props.task_uuid_with_type.substring(0,1)
        task_uuid.value = props.task_uuid_with_type.substring(2)
        // console.log("--------------------------------")
        // console.log("task_type: " + task_type.value)
        // console.log("task_uuid: " + task_uuid.value)
        // console.log("--------------------------------")
    
    })

    return {
      tab: ref("national-results"),
      task_uuid,
      task_type
    };

  },
});
</script>

<style lang="scss" scoped>
.q-tab.national-results {
  flex: 0 0 auto;
  align-items: left;
  align-self: left;
}
.q-tab.map {
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
