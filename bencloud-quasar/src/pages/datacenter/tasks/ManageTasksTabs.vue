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
    <q-tab name="active" label="Active Tasks" />
    <q-tab name="pending" label="Completed Tasks" />
  </q-tabs>

  <q-tab-panels 
    v-model="tab" 
    animated
    >
    <q-tab-panel name="active">
      <div class="text-h6">Active Tasks</div>
      Active View
      <ActiveTasksTab></ActiveTasksTab>
    </q-tab-panel>

    <q-tab-panel name="pending">
      <div class="text-h6">Completed Tasks Go Here</div>
      Completed View
      <CompletedTasksTab></CompletedTasksTab>
    </q-tab-panel>
  </q-tab-panels>
</template>

<script>
import { ref } from "vue";
import { defineComponent } from "vue";

import CompletedTasksTab from "./CompletedTasksTab.vue";
import ActiveTasksTab from "./ActiveTasksTab.vue";

export default defineComponent({
  model: ref(null),
  name: "ManageTasksTabs",
  components: {
    CompletedTasksTab,
    ActiveTasksTab
  },
  computed: {
//    airQualityLayerId() {
//      return this.$store.state.airquality.airQualityLayerId;
//    },
  },
  setup() {
    return {
      tab: ref("pending"),
    };
  },
});
</script>
