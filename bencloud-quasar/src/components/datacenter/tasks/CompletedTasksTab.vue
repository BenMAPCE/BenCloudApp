<template>

    <q-table
      :rows="rows"
      :columns="columns"
      row-key="name"
      :rows-per-page-options="[0]"
      v-model:pagination="pagination"
      :loading="loading"
      :filter="filter"
      @request="getCompletedTasks"
      binary-state-sort
      v-model:selected="selected"
      :visible-columns="visibleColumns"
      class="completed-tasks"
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
            key="task_completed_date"
            :props="props"
          >
          {{ props.row.batch_completed_date }}
          </q-td>
          <q-td
            key="task_elapsed_time"
            :props="props"
          >
          {{ props.row.batch_execution_time }}
          </q-td>
          <q-td
            key="task_successful"
            :props="props"
            >
            <div v-if="props.row.batch_task_successful">
              <q-badge color="green" label="Yes" />
            </div>
            <div v-if="!props.row.batch_task_successful">
              <q-badge color="red" label="No" />
            </div>
          </q-td>
          <q-td
            key="user"
            :props="props"
            >
              {{ props.row.batch_task_user_id }}
          </q-td>
          <q-td
            key="task_message"
            :props="props"
          >
          </q-td>
          <q-td 
            key="download"  
            :props="props"
            >
              <q-btn-dropdown color="primary" label="" dense>
                <q-list>
                  <q-item dense clickable v-close-popup @click="onClickViewExport(props)">
                    <q-item-section>
                      <q-item-label>View/Export Results</q-item-label>
                    </q-item-section>
                  </q-item>

                  <!-- <q-item dense clickable v-close-popup @click="onClick(props)">
                    <q-item-section>
                      <q-item-label>View Configuration Details</q-item-label>
                    </q-item-section>
                  </q-item>

                  <q-item dense clickable v-close-popup @click="onClick(props)">
                    <q-item-section>
                      <q-item-label>Use as a template for new analysys</q-item-label>
                    </q-item-section>
                  </q-item> -->

                  <q-separator light style="color: red"></q-separator>

                  <q-item dense clickable v-close-popup @click="onClickPromptDelete(props)">
                    <q-item-section>
                      <q-item-label dense>Delete</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-btn-dropdown>
          </q-td>
        </q-tr>
        <!-- We show the batch task details if "props.expand" is true -->
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
            key="task_completed_date"
            :props="props"
          >
            {{ row.task_completed_date }}
          </q-td>
          <q-td
            key="task_elapsed_time"
            :props="props"
          >
            {{ row.task_elapsed_time }}
          </q-td>
          <q-td
            key="task_successful"
            :props="props"
          >
          <div v-if="row.task_successful">
            <q-badge color="green" label="Yes" />
          </div>
          <div v-if="!row.task_successful">
            <q-badge color="red" label="No" />
          </div>
          </q-td>
          <q-td
            key="user"
            :props="props"
          >
            {{ row.task_user_id }}
          </q-td>
          <q-td
            key="task_message"
            :props="props"
          >
            {{ row.task_message }}
          </q-td>
          <q-td 
            key="download"  
            :props="props"
          >
            <q-btn-dropdown color="primary" label="" dense>
              <q-list>
                <q-item dense clickable v-close-popup @click="onClickViewExport(row)">
                  <q-item-section>
                    <q-item-label>View/Export Results</q-item-label>
                  </q-item-section>
                </q-item>

                <!-- <q-item dense clickable v-close-popup @click="onClick(props)">
                  <q-item-section>
                    <q-item-label>View Configuration Details</q-item-label>
                  </q-item-section>
                </q-item>

                <q-item dense clickable v-close-popup @click="onClick(props)">
                  <q-item-section>
                    <q-item-label>Use as a template for new analysys</q-item-label>
                  </q-item-section>
                </q-item> -->

                <q-separator light style="color: red"></q-separator>

                <q-item dense clickable v-close-popup @click="onClickPromptDelete(props)">
                  <q-item-section>
                    <q-item-label dense>Delete</q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </q-btn-dropdown>
          </q-td>
        </q-tr>
      </template>

      <template v-slot:top-right>
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
import { useQuasar, date } from "quasar";

import { getCompletedTasks } from "../../../composables/tasks/completed-tasks";
import { showAllTasks } from "../tasks/ManageTasksTabs.vue";

export default defineComponent({
  model: ref(null),

  name: "CompletedTasksTab",
  computed: {},

  setup(props, context) {
    const store = useStore();
    const $q = useQuasar();

    const rows = ref([]);
    const filter = ref("");
    const loading = ref(false);
    const pagination = ref({
      page: 1,
      rowsPerPage: 0,
      sortBy: 'task_submitted_date',
      descending: true,
    });

    const optionSelected = ref(null);
    const fab1 = ref(true);
    const fab2 = ref(true);

    const stringOptions = [
      "View/Export Results",
      "View Configuration Details",
      "Use as a template for new analysis",
      "Delete",
    ];

    const task_uuid = ref(0)

    let completedTasksRefreshInterval = null;

    function loadCompletedTasks() {

      loading.value = true;

      (async () => {
        const response = await getCompletedTasks().fetch();        
        // console.log(unref(response.data));

        rows.value = [];
        for (var i = 0; i < (unref(response.data).data).length; i++) {
          rows.value.push(unref(response.data).data[i])
        }
        loading.value = false;
      })();

    }
    
    
    watch(
      () => showAllTasks.value,
      () => {
        console.log("Show all tasks: " + showAllTasks.value);
        if(showAllTasks.value && !visibleColumns.value.includes("user")) {
          visibleColumns.value.push("user");
        }
        if(!showAllTasks.value && visibleColumns.value.includes("user")) {
          visibleColumns.value.pop("user");
        }
        console.log(visibleColumns.value);
        loadCompletedTasks();
      }
    );

    function enableAutoRefresh() {
    
      completedTasksRefreshInterval = setInterval(function () {
        loadCompletedTasks()   
      }.bind(this), 5000); 

    }

    function disableAutoRefresh() {
      clearInterval(completedTasksRefreshInterval);
    }

    function onValueChange(props, val) {
      console.log("SELECT value changed: ");
      console.log(optionSelected.value);
      console.log(props);
    }
    function showOptions(props) {
      console.log("showOptions");
      console.log(props);
    }

    function onClick(props, item) {
      console.log("onClick");
      console.log(props);
      console.log(item);
    }

    function onClickViewExport(row) {
      console.log("onClickViewExport");
      var row = JSON.parse(JSON.stringify(row))
      //if (row.task_type === "HIF") {
        var task_type = (row.task_type).substring(0,1)
        this.$router.push({ path: `/datacenter/view-export-task/${task_type}-${row.task_uuid}` })
      //} else {
        
      //}
    }

    function onClickPromptDelete(props) {
      // Prompt user to confirm task deletion
      if(confirm("Are you sure you wish to permanently delete " + props.row.task_name + "?")){
        deleteTask(props);
      }
    }

    function deleteTask(props) {
      console.log( "deleting " + process.env.API_SERVER + "/api/tasks/" + props.row.task_uuid);   
      // Delete task, reload the task list if successful, alert the user if unsuccessful
      axios
      .delete(process.env.API_SERVER + "/api/tasks/" + props.row.task_uuid)
      .then((response) => {
        if(response.status === 204) {
          console.log("Successfully deleted task: " + props.row.task_uuid);
          loadCompletedTasks();
        } else {
          alert("An error occurred, task was not deleted.")
        }
        return response;
      });
    }

    function exportTaskResults(props) {
      console.log("exportTaskResults");

      console.log(props.row.task_uuid);

      loading.value = true;

      $q.loading.show({
        message: "Downloading results. Please wait...",
        boxClass: "bg-grey-2 text-grey-9",
        spinnerColor: "primary",
      });

      axios
        .get(process.env.API_SERVER + "/api/tasks/" + props.row.task_uuid + "/results", {
          params: {
            page: 1,
            rowsPerPage: 9999999,
          },
          headers: { Accept: "text/csv", "Content-Type": "text/csv" },
        })
        .then((response) => {
          let records = response.data.records;
          let data = response.data;
          //console.log(data);

          var fileName = response.headers["content-disposition"]
            .split("filename=")[1]
            .split(";")[0];

          var hiddenElement = document.createElement("a");
          hiddenElement.href = "data:text/csv;charset=utf-8," + encodeURI(data);
          hiddenElement.target = "_blank";
          hiddenElement.download = fileName;
          hiddenElement.click();
          hiddenElement.remove();

          loading.value = false;
          $q.loading.hide();
        });
    }

    onMounted(() => {
      loadCompletedTasks()
      enableAutoRefresh()
    });

    onBeforeUnmount(() => {
      //console.log("before unmount")
      disableAutoRefresh()
    })

    return {
      columns,
      filter,
      loading,
      pagination,
      rows,
      selected: ref([]),
      visibleColumns,
      optionSelected,
      stringOptions,
      getCompletedTasks,
      exportTaskResults,
      onClick,
      onClickViewExport,
      onClickPromptDelete,
      fab1,
      fab2,
      showOptions,
      onValueChange,
      loadCompletedTasks,
      completedTasksRefreshInterval
    };
  },

});

const rows = [];

const visibleColumns = ref([
  "expand",
  //"task_uuid",
  "task_name",
  //"task_type",
  "task_submitted_date",
  //"task_started_date",
  "task_completed_date",
  "task_elapsed_time",
  "task_successful",
  "task_message",
  "download",
]);

const columns = [
  
  {
    name: "expand",
    label: "",
    field: "expand",
    sortable: false,
  },
  {
    name: "task_uuid",
    label: "Task UUID",
    align: "left",
    field: (row) => row.task_uuid,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "task_type",
    label: "Task Type",
    align: "left",
    field: (row) => row.task_type,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "task_name",
    label: "Task Name",
    align: "left",
    field: (row) => row.task_name,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "task_completed_date",
    label: "Completed",
    field: "task_completed_date",
    sortable: true,
  },
  {
    name: "task_elapsed_time",
    label: "Run Time",
    field: "task_elapsed_time",
    sortable: true,
  },
  {
    name: "task_successful",
    label: "Successful",
    field: "task_successful",
    sortable: true,
  },
  {
    name: "user",
    label: "User",
    field: (row) => row.task_user_id,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "task_message",
    label: "Message",
    field: "task_message",
    sortable: true,
  },
  {
    name: "download",
    label: "Action",
    field: "",
    align: "center",
    sortable: false,
    style: "width: 200px",
  },
];
</script>

<style lang="scss" scoped>

.completed-tasks {
  .reload-button {
    margin-right: 25px;
  }

  .options-column {
    width: 200px;
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
}
</style>
