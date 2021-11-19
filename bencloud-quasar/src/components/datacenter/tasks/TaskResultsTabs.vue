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
      <div>
        <TaskResults></TaskResults>
      </div>
    </q-tab-panel>

    <q-tab-panel name="map">
      <div>Coming Soon</div>
    </q-tab-panel>
  </q-tab-panels>
</template>

<script>
import { ref } from "vue";
import { defineComponent } from "vue";

import TaskResults from "./TaskResults.vue";

export default defineComponent({
  model: ref(null),
  name: "TaskResultsTab",
  props: ["task_uuid"],

  components: {
    TaskResults
  },
  setup() {
    return {
      tab: ref("national-results"),
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
