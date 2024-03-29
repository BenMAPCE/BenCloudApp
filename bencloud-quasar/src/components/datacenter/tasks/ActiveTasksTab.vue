<template>

    <q-table
      :rows="rows"
      :columns="columns"
      row-key="batch_task_name"
      :rows-per-page-options="[0]"
      v-model:pagination="pagination"
      :loading="loading"
      :filter="filter"
      @request="getActiveTasks"
      binary-state-sort
      v-model:selected="selected"
      class="active-tasks"
      :visible-columns="visibleColumns"
    >
      <template v-slot:body="props">
        <q-tr :props="props">
          <q-td
            key="expand"
            name="expand"
            :props="props"
            auto-width
          >
            <q-btn
              flat
              round
              color="primary"
              :icon="props.expand ? 'mdi-minus-circle' : 'mdi-plus-circle'"
              @click="props.expand = !props.expand"
            />
          </q-td>
          <q-td
            key="task_name"
            :props="props"
          >
            {{ props.row.batch_task_name }}
          </q-td>
          <q-td
            key="task_status_message"
            :props="props"
          >
            {{ props.row.batch_started_date }}
          </q-td>
          <q-td
            key="user"
            :props="props"
          >
            {{ props.row.batch_task_user_id }}
          </q-td>
          <q-td
            key="task_progress_message"
            :props="props"
          >
            {{ props.row.batch_task_progress }}
          </q-td>
          <q-td
            key="actions"
            :props="props"
          >
            <q-btn color="primary" label="Cancel" 
                @click="onClickCancelBatchTask(props)"
              />
          </q-td>
        </q-tr>
        <!-- We show the details if "props.expand" is true -->
        <q-tr v-for="row in props.row.tasks" :key="row.task_name"
          v-show="props.expand"
          :props="props"
        >
          <q-td
            key="expand"
            :props="props"
          >
          </q-td>
          <q-td
            key="task_name"
            :props="props"
          >
            {{ row.task_name }}
          </q-td>
          <q-td
            key="task_status_message"
            :props="props"
          >
            {{ row.task_status_message ? row.task_status_message : "Pending" }}
          </q-td>
          <q-td
            key="user"
            :props="props"
          >
            {{ props.row.task_user_id }}
          </q-td>
          <q-td
            key="task_progress_message"
            :props="props"
          >
            <q-linear-progress size="25px" :value="row.task_percentage/100" color="accent" v-if="row.task_status_message != 'Pending' && row.task_progress_message != 'Complete'">
              <div class="absolute-full flex flex-center">
                <q-badge color="white" text-color="accent" :label="row.task_percentage + ' %'" />
              </div>
            </q-linear-progress>

            <ActiveTaskStatus :status = row.task_progress_message>

            </ActiveTaskStatus>

          </q-td>
          <q-td
            key="actions"
            :props="props"
          >
            <!-- <q-btn color="white" text-color="black" label="Cancel" v-if="row.task_status_message == 'Pending'" :size="'sm'"
                @click="onClickCancelOneTask(row)"
              /> -->
          </q-td>
        </q-tr>
      </template>
        
        <template v-slot:top-right>
          <!-- <q-btn
            color="primary"
            icon-right="mdi-reload"
            class="reload-button"
            label="Reload"
            no-caps
            @click="getActiveTasks"
          /> -->

          <q-input borderless dense debounce="300" v-model="filter" placeholder="Search">
            <template v-slot:append>
              <q-icon name="mdi-magnify" />
            </template>
          </q-input>
        </template>
      </q-table>

</template>

<script>
import { defineComponent } from "vue";
import { ref, unref, onMounted, onBeforeMount, onBeforeUnmount, watch, watchEffect } from "vue";
import axios from "axios";
import { useStore } from "vuex";

import { getActiveTasks } from "../../../composables/tasks/active-tasks";
import ActiveTaskStatus from "./ActiveTaskStatus.vue";
import { showAllTasks } from "../tasks/ManageTasksTabs.vue";

export default defineComponent({
  model: ref(null),
  name: "ActiveTasksTab",
  computed: {},
  components: {
    ActiveTaskStatus
  },
  props: {
    autoRefresh: {
      type: Boolean,
      default: false,
    },
  },

  setup(props, context) {
    const store = useStore();

    const rows = ref([]);
    const filter = ref("");
    const loading = ref(false);
    const pagination = ref({
      page: 1,
      rowsPerPage: 0,
      sortBy: 'task_name',
      descending: false,
    });
    const progress1 = ref(0);

    const timer = ref(null);

    let myFilter = unref(filter);
    let activeTasksRefreshInterval = null;

    watch(
      () => showAllTasks.value,
      () => {
        if(showAllTasks.value && !visibleColumns.value.includes("user")) {
          visibleColumns.value.push("user");
        }
        if(!showAllTasks.value && visibleColumns.value.includes("user")) {
          visibleColumns.value.pop("user");
        }
        loadActiveTasks();
      }
    );

    function loadActiveTasks() {

      loading.value = true;

      (async () => {
        const response = await getActiveTasks().fetch();
        // console.log(response);

        rows.value = [];
        for (var i = 0; i < (unref(response.data).data).length; i++) {
          rows.value.push(unref(response.data).data[i]);
        }

        loading.value = false;
      })();

    }

    function enableAutoRefresh() {
    
      activeTasksRefreshInterval = setInterval(function () {
        loadActiveTasks()   
      }.bind(this), 5000); 

    }

    function disableAutoRefresh() {
      clearInterval(activeTasksRefreshInterval);
    }

    onMounted(() => {
      console.log(props.autoRefresh)
      loadActiveTasks();
      enableAutoRefresh()

    });

    onBeforeUnmount(() => {
      //console.log("before unmount")
      disableAutoRefresh()
      //clearInterval(activeTasksRefreshInterval);
    })

    function onClickCancelBatchTask(props){
      // Prompt user to confirm canceling a batch task
      console.log(props)
      if(confirm("Are you sure you wish to cancel task " + props.row.batch_task_name + "?")){
        cancelBatch(props);
      }
    }

    function cancelBatch(props){
      console.log( "Canceling " + process.env.API_SERVER + "/api/batch-tasks/" + props.row.batch_task_id);   
      // Delete (batch) task, reload the task list if successful, alert the user if unsuccessful
      axios
      .put(process.env.API_SERVER + "/api/batch-tasks/" + props.row.batch_task_id, 
        {validateStatus: function (status) {
          return status < 500;
        }}
      )
      .then((response) => {
        if(response.status === 204) {
          console.log("Successfully canceled task: " + props.row.batch_task_id);
          loadActiveTasks();
        } else if(response.status == 403) {
          console.log("Forbidden action on task: " + props.row.batch_task_id);
          this.$q.notify({
            group: false, // required to be updateable
            type: 'negative',
            timeout: 6000, 
            color: "red",
            spinner: false, // we reset the spinner setting so the icon can be displayed
            position: "top",
            message: response.data.message,
          });
          this.$emit('ok')
        } else if(response.status == 404) {
          console.log("Task not found: " + props.row.batch_task_id);
          this.$q.notify({
            group: false, // required to be updateable
            type: 'negative',
            timeout: 6000, 
            color: "red",
            spinner: false, // we reset the spinner setting so the icon can be displayed
            position: "top",
            message: response.data.message,
          });
          this.$emit('ok')
        } else {
          console.log("An error occurred: " + props.row.batch_task_id);
          this.$q.notify({
            group: false, // required to be updateable
            type: 'negative',
            timeout: 6000, 
            color: "red",
            spinner: false, // we reset the spinner setting so the icon can be displayed
            position: "top",
            message: "An error occurred cancelling task: " + props.row.batch_task_id,
          });
          this.$emit('ok')
        }
        return response;
      });
    }

    function onClickCancelOneTask(row){
      // Prompt user to confirm canceling one (sub)task
      console.log(row)
      if(confirm("Are you sure you wish to cancel task " + row.task_name + "?")){
        cancelOne(row);
      }
    }

    function cancelOne(row){
      console.log( "Canceling " + process.env.API_SERVER + "/api/tasks/" + row.task_uuid);   
      // Delete (batch) task, reload the task list if successful, alert the user if unsuccessful
      axios
      .put(process.env.API_SERVER + "/api/tasks/" + row.task_uuid)
      .then((response) => {
        if(response.status === 204) {
          console.log("Successfully canceled task: " + row.task_uuid);
          loadActiveTasks();
        } else {
          alert("An error occurred during canceling batch task.")
        }
        return response;
      });
    }

    return {
      columns,
      filter,
      loading,
      pagination,
      rows,
      selected: ref([]),
      getActiveTasks,
      loadActiveTasks,
      onClickCancelOneTask,
      onClickCancelBatchTask,
      activeTasksRefreshInterval,
      progress1,
      visibleColumns
    };
  },
});

const rows = [];

const visibleColumns = ref([
  "expand",
  "task_name",
  "task_status_message",
  "task_progress_message",
  "actions"
]);

const columns = [
  {
    name: "expand",
    label: "",
    field: "expand",
    sortable: false,
  },
  {
    name: "task_name",
    required: true,
    label: "Task",
    align: "left",
    field: (row) => row.task_name,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "task_status_message",
    label: "Status",
    field: "task_status_message",
    align: "left",
    sortable: true,
  },
  {
    name: "user",
    label: "User",
    field: (row) => row.task_user_id,
    format: (val) => `${val}`,
    align: "left",
    sortable: true,
  },
  {
    name: "task_progress_message",
    label: "Progress",
    field: "task_progress_message",
    sortable: true,
    style: "width: 300px"
  },
  { 
    name: "actions", 
    label: "Action",
    field: "",
    align: "center" 
  },
];
</script>

<style lang="scss" scoped>

.active-tasks {
  .reload-button {
    margin-right: 25px;
  }

  .q-table th {
    position: -webkit-sticky;
    position: sticky;
    top: 0px;
    background-color: grey;
    z-index: 2;
    color: white;
  }

  .q-table__middle.scroll {
    max-height: 500px;
  }

  .column-task-progress {
    width: 250px;
  }
}
</style>
