<template>
  <q-page-container>
    <q-page class="completed-tasks">
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
      >
        <template v-slot:top-right>
          <q-btn
            color="primary"
            icon-right="mdi-reload"
            class="reload-button"
            label="Reload"
            no-caps
            @click="getCompletedTasks"
          />

          <q-input borderless dense debounce="300" v-model="filter" placeholder="Search">
            <template v-slot:append>
              <q-icon name="mdi-magnify" />
            </template>
          </q-input>
        </template>

        <template v-slot:body-cell-download="props">
          <q-td :props="props">
            <q-btn
              round
              flat
              color="grey"
              @click="exportTaskResults(props)"
              icon="mdi-table-arrow-down"
            ></q-btn>
          </q-td>
        </template>

        <template v-slot:body-cell-task_successful="props">
          <q-td :props="props">
            <div>
              <q-badge color="green" :label="props.value ? 'Yes' : 'No'" />
            </div>
          </q-td>
        </template>
      </q-table>
    </q-page>
  </q-page-container>
</template>

<script>
import { defineComponent } from "vue";
import { ref, unref, onMounted, watch, watchEffect } from "vue";
import axios from "axios";
import { useStore } from "vuex";
import { useQuasar } from "quasar";

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
    });

    let myFilter = unref(filter);

    function getCompletedTasks() {
      //console.log("--------------------------------------------")
      //console.log(filter)
      //console.log("--------------------------------------------")

      loading.value = true;

      axios.get(process.env.API_SERVER + "/api/tasks/completed", {}).then((response) => {
        let records = response.data.data;
        let data = response.data;

        console.log("----- return -----");
        console.log(records);

        rows.value = records;

        // ...and turn of loading indicator
        loading.value = false;
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
      getCompletedTasks();
      $q.loading.hide();
    });

    return {
      columns,
      filter,
      loading,
      pagination,
      rows,
      selected: ref([]),
      visibleColumns,
      getCompletedTasks,
      exportTaskResults,
    };
  },
});

const rows = [];

const visibleColumns = [
  // "task_uuid",
  "task_name",
  "task_type",
  //      "task_submitted_date",
  //      "task_started_date",
  //      "task_completed_date",
  "task_elapsed_time",
  "task_successful",
  "task_message",
  "download",
];

const columns = [
  {
    name: "task_uuid",
    label: "Task UUID",
    align: "left",
    field: (row) => row.task_uuid,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "task_name",
    required: true,
    label: "Task Name",
    align: "left",
    field: (row) => row.task_name,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "task_type",
    align: "left",
    label: "Type",
    field: "task_type",
    sortable: true,
  },
  {
    name: "task_submitted_date",
    label: "Submitted",
    field: "task_submitted_date",
    sortable: true,
  },
  {
    name: "task_started_date",
    label: "Started",
    field: "task_started_date",
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
    label: "Elapsed Time",
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
    name: "task_message",
    label: "Message",
    field: "task_message",
    sortable: true,
  },

  { name: "download", label: "Download", field: "", align: "center" },
];
</script>

<style lang="scss">
.completed-tasks {
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
}
</style>
