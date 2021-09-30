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

        <template v-slot:body-cell-downloadz="props">
          <q-td :props="props">
            <q-btn
              round
              flat
              color="grey"
              @click="showOptions(props)"
              icon="mdi-dots-vertical"
            ></q-btn>
          </q-td>
        </template>

        <template v-slot:body-cell-download="props">
           <q-td :props="props">
           <q-btn-dropdown color="primary" label="" dense>
              <q-list>
                <q-item dense clickable v-close-popup @click="onClick(props)">
                  <q-item-section>
                    <q-item-label>View/Export Results</q-item-label>
                  </q-item-section>
                </q-item>

                <q-item dense clickable v-close-popup @click="onClick(props)">
                  <q-item-section>
                    <q-item-label>View Configuration Details</q-item-label>
                  </q-item-section>
                </q-item>

                <q-item dense clickable v-close-popup @click="onClick(props)">
                  <q-item-section>
                    <q-item-label>Use as a template for new analysys</q-item-label>
                  </q-item-section>
                </q-item>
                <q-separator light style="color: red"></q-separator>

                <q-item dense clickable v-close-popup @click="onClick(props)">
                  <q-item-section>
                    <q-item-label dense>Delete</q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </q-btn-dropdown>
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
import { ref, unref, onMounted, onBeforeMount, watch, watchEffect } from "vue";
import axios from "axios";
import { useStore } from "vuex";
import { useQuasar } from "quasar";

import { getCompletedTasks } from "../../../composables/tasks/completed-tasks";

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

    const optionSelected = ref(null);
    const fab1 = ref(true);
    const fab2 = ref(true);

    const stringOptions = [
      "View/Export Results",
      "View Configuration Details",
      "Use as a template for new analysis",
      "Delete",
    ];
    //    let myFilter = unref(filter);

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
      loading.value = true;

      (async () => {
        const response = await getCompletedTasks().fetch();
        rows.value = unref(response.data).data;
        loading.value = false;
      })();
    });

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
      fab1,
      fab2,
      showOptions,
      onValueChange,
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

<style lang="scss">
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