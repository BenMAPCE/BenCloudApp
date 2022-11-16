<template>
      <div class="col refresh-message">
        Tasks are auto-refreshed every 5 seconds
      </div>

      <div class="col" v-if="isAdmin">
        <q-toggle
          size="lg"
          v-model="showAllTasks"
          color="blue"
          label="See tasks of all users"
        />
      </div>

      <q-page class="tasks">
        <div name="active" class="active-tasks-wrapper">
          <div class="text-h6">Active/Pending Tasks</div>
          <ActiveTasksTab></ActiveTasksTab>
        </div>

        <div name="completed"  class="completed-tasks-wrapper">
          <div class="text-h6">Completed Tasks</div>
          <CompletedTasksTab></CompletedTasksTab>
        </div>
      </q-page>
</template>

<script>
import { ref } from "vue";
import { defineComponent } from "vue";

import CompletedTasksTab from "./CompletedTasksTab.vue";
import ActiveTasksTab from "./ActiveTasksTab.vue";
import { isAdmin } from "../../../boot/auth.js";

export const showAllTasks = ref(false);

export default defineComponent({
  model: ref(null),
  name: "ManageTasksTabs",
  components: {
    CompletedTasksTab,
    ActiveTasksTab
  },
  setup() {
    return {
      tab: ref("active"),
      isAdmin,
      showAllTasks,
    };
  },
});
</script>

<style lang="scss" scoped>

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
