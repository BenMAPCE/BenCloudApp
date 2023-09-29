<template>
  <div class="exposure-task-results">
    <div class="q-pa-md download-button">
      <q-btn color="primary" label="Download" @click="showDownloadDialog()" />
    </div>

    <div class="q-pa-md">
      <q-table
        :rows="rows"
        :columns="columns"
        row-key="name"
        :rows-per-page-options="[0]"
        v-model:pagination="pagination"
        :loading="loading"
        @request="loadExposureResults"
        :filter="filter"
        binary-state-sort
        :visible-columns="visibleColumns"
        class="task-results"
      >
        <template v-slot:top="props">
          <q-space></q-space>

          <q-select
            v-model="visibleColumns"
            multiple
            outlined
            dense
            options-dense
            :display-value="$q.lang.table.columns"
            emit-value
            map-options
            :options="columns"
            option-value="name"
            options-cover
            style="min-width: 150px"
          ></q-select>

          <q-btn
            flat
            round
            dense
            :icon="props.inFullscreen ? 'mdi-fullscreen-exit' : 'mdi-fullscreen'"
            @click="props.toggleFullscreen"
            class="q-ml-md"
          />
        </template>
      </q-table>
    </div>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, unref, watch, onBeforeMount, onUpdated, onMounted } from "vue";
import { useQuasar, date } from "quasar";
import { getExposureTaskResults } from "../../../../composables/tasks/task-results";
import DownloadTaskResultsDialog from "../DownloadTaskResultsDialog.vue";

export default defineComponent({
  model: ref(null),
  name: "ExposureTaskResults",

  props: ["task_uuid", "task_name", "task_type", "batch_task_id"],

  setup(props, context) {
    //const task_type = ref("");
    //const task_uuid = ref(null);

    const filter = ref("");
    const loading = ref(false);
    const pagination = ref({
      page: 1,
      rowsPerPage: 0,
      sortBy: "endpoint",
      descending: true,
    });
    const $q = useQuasar();

    const rows = ref([]);

    function loadExposureResults() {
      loading.value = true;

      (async () => {
        const response = await getExposureTaskResults(props.task_uuid).fetch();
        rows.value = JSON.parse(JSON.stringify(unref(response.data)));
        loading.value = false;
      })();
    }

    function showDownloadDialog() {
      $q.dialog({
        component: DownloadTaskResultsDialog,
        parent: this,
        persistent: true,
        componentProps: {
          task_uuid: props.task_uuid,
          task_name: props.task_name,
          task_type: props.task_type,
          batch_task_id: props.batch_task_id,
        },
      })
        .onOk(() => {
          console.log("Export OK");
        })
        .onCancel(() => {
          // console.log('Cancel')
        })
        .onDismiss(() => {
          // console.log('I am triggered on both OK and Cancel')
        });
    }

    onMounted(() => {
      visibleColumns.value = [
        "population_group",
        "ages",
        "baseline_aq",
        "scenario_aq",
        "delta_aq",
        "delta_aq_percent",
        "population",
      ];

      loadExposureResults(props.task_uuid);
    });

    return {
      rows,
      columns,
      filter,
      loading,
      pagination,
      visibleColumns,
      loadExposureResults,
      showDownloadDialog,
    };
  },
});

const visibleColumns = ref([
  //  "task_uuid",
  "population_group",
  "ages",
  // "race",
  // "ethnicity",
  // "gender",
  "baseline_aq",
  "scenario_aq",
  "delta_aq",
  "delta_aq_percent",
  "population",
  // "percent_of_population",
  // "result"
]);

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
    name: "population_group",
    label: "Population Group",
    align: "left",
    field: (row) => row.population_group,
    format: (val) => `${val}`,
    align: "left",
    sortable: true,
  },
  {
    name: "ages",
    label: "Ages",
    align: "left",
    field: (row) => row.start_age + "-" + row.end_age,
    format: (val) => `${val}`,
    align: "right",
    sortable: true,
  },
  {
    name: "race",
    label: "Race",
    field: "race",
    sortable: true,
  },
  {
    name: "ethnicity",
    label: "Ethnicity",
    field: "ethnicity",
    sortable: true,
  },
  {
    name: "gender",
    label: "Gender",
    field: "gender",
    sortable: true,
  },
  {
    name: "baseline_aq",
    label: "Pre-policy Conc (μm/m3)",
    field: (row) => row.baseline_aq.toLocaleString("en-US", { maximumFractionDigits: 4 }),
    sort: (a, b, rowA, rowB) => parseFloat(rowA.baseline_aq) - parseFloat(rowB.baseline_aq),
    sortable: true,
  },
  {
    name: "scenario_aq",
    label: "Post-policy Conc (μm/m3)",
    field: (row) => row.scenario_aq.toLocaleString("en-US", { maximumFractionDigits: 4 }),
    sort: (a, b, rowA, rowB) => parseFloat(rowA.scenario_aq) - parseFloat(rowB.scenario_aq),
    sortable: true,
  },
  {
    name: "delta_aq",
    label: "Change in AQ (μm/m3)",
    field: (row) => row.delta_aq.toLocaleString("en-US", { maximumFractionDigits: 4 }),
    sort: (a, b, rowA, rowB) => parseFloat(rowA.delta_aq) - parseFloat(rowB.delta_aq),
    sortable: true,
  },
  {
    name: "delta_aq_percent",
    label: "Change in AQ (%)",
    field: (row) => row.delta_aq_percent.toLocaleString("en-US", { maximumFractionDigits: 4 }),
    sort: (a, b, rowA, rowB) => parseFloat(rowA.delta_aq_percent) - parseFloat(rowB.delta_aq_percent),
    sortable: true,
  },
  {
    name: "population",
    label: "Population Exposed",
    field: (row) => row.subgroup_population.toLocaleString("en-US", { maximumFractionDigits: 4 }),
    sort: (a, b, rowA, rowB) => parseFloat(rowA.subgroup_population) - parseFloat(rowB.subgroup_population),
    sortable: true,
  },
  {
    name: "percent_of_population",
    label: "Percent of Group Population",
    field: (row) =>
      row.percent_of_population.toLocaleString("en-US", { maximumFractionDigits: 4 }),
    sort: (a, b, rowA, rowB) => parseFloat(rowA.percent_of_population) - parseFloat(rowB.percent_of_population),
    sortable: true,
  },
  {
    name: "formatted_results_2sf",
    label: "Formatted Results (2 sig. figs.)",
    field: "formatted_results_2sf",
    sortable: false
  },
  {
    name: "formatted_results_3sf",
    label: "Formatted Results (3 sig. figs.)",
    field: "formatted_results_3sf",
    sortable: false
  },
];
</script>

<style lang="scss">
.exposure-task-results {
  .download-button {
    text-align: right;
  }
}
</style>
