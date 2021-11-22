<template>
  <div class="task-results-page">
    <div class="link-to-magage-tasks">
      <router-link to="/datacenter/manage-tasks">Back to Manage Tasks</router-link>
    </div>
    <div class="task-name">{{ task_name }}</div>
    <div class="task-completed-date">{{ task_completed_date }}</div>

    <div class="task-results">
      <TaskResultsTabs v-bind:task_uuid_with_type="task_uuid_with_type"></TaskResultsTabs>
    </div>

  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, unref, reactive, onBeforeMount } from "vue";
import { useRoute } from "vue-router";

import TaskResultsTabs from "../../../components/datacenter/tasks/TaskResultsTabs.vue";
import { getCompletedTasks } from "../../../composables/tasks/completed-tasks";

export default defineComponent({
  model: ref(null),
  name: "ViewTaskResults",
  components: {
    TaskResultsTabs,
  },

  setup() {
    const route = useRoute();
    console.log(route.params.task_uuid);
    const task_uuid_with_type = route.params.task_uuid;
    const task_name = ref("NO TASK NAME")
    const task_completed_date = ref(null)
    const task_type = ref("");
    const task_uuid = ref(null)

    const rows = ref([]);

    onBeforeMount(() => {
      (async () => {

        // console.log("--------------------------------")
        // console.log("ViewTaskResults: " + task_uuid_with_type)
        // console.log("--------------------------------")
        task_type.value = task_uuid_with_type.substring(0,1)
        task_uuid.value = task_uuid_with_type.substring(2)
        // console.log("--------------------------------")
        // console.log("task_type: " + task_type.value)
        // console.log("task_uuid: " + task_uuid.value)
        // console.log("--------------------------------")


        const response = await getCompletedTasks().fetch();
        //console.log(unref(response.data).data)
        rows.value = unref(response.data).data;
        var tasks = JSON.parse(JSON.stringify(rows.value));
        for (var i = 0; i < tasks.length; i++) {
          if (tasks[i].task_uuid === task_uuid.value) {
            task_name.value = tasks[i].task_name
            task_completed_date.value = tasks[i].task_completed_date
            break
          }
        }

//        console.log(tasks);

      })();
    });

    return {
      task_uuid_with_type,
      task_name,
      task_completed_date,
    };
  },
});
</script>

<style lang="scss" scoped>
.task-results-page {
  padding: 10px;
  .task-name, .task-completed-date {
    padding-left: 20px;
    font-size: 18px;;
  }
  .link-to-magage-tasks {
    padding-left: 20px;
    padding-top: 10px;
    padding-bottom: 10px;
  }
  .task-results {
    padding-top: 25px;
  }
}

</style>
