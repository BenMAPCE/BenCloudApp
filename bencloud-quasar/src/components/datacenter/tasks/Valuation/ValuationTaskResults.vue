<template>
  <div class="valuation-task-results">
    <div class="q-pa-md download-button">
      <q-btn color="primary" label="Download" @click="showDownloadDialog()" />
    </div>

    <div class="q-pa-md">
      <q-table
        :rows="rows"
        :columns="columns"
        row-key="unique_id"
        :rows-per-page-options="[0]"
        v-model:pagination="pagination"
        :loading="loading"
        @request="loadValuationResults"
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
import { getValuationTaskResults } from "../../../../composables/tasks/task-results";
import DownloadTaskResultsDialog from "../DownloadTaskResultsDialog.vue";

export default defineComponent({
  model: ref(null),
  name: "ValuationTaskResults",

  props: ["valuation_task_uuid", "valuation_task_name", "valuation_task_type"],

  setup(props, context) {
    //const task_type = ref("");
    //const task_uuid = ref(null);

    const filter = ref("");
    const loading = ref(false);
    const pagination = ref({
      page: 1,
      rowsPerPage: 0,
      sortBy: "task_health_effect",
      descending: true,
    });
    const $q = useQuasar();

    const rows = ref([]);

    function loadValuationResults() {
      loading.value = true;

      (async () => {
        const response = await getValuationTaskResults(props.valuation_task_uuid).fetch();

        // create a unique if for the table key - will later come from the back end
        var valuationResults = JSON.parse(JSON.stringify(unref(response.data)));
        for (var i = 0; i < valuationResults.length; i++){
          valuationResults[i].unique_id = i;
        }

        rows.value = valuationResults;
        loading.value = false;
      })();
    }

    function showDownloadDialog() {
      $q.dialog({
        component: DownloadTaskResultsDialog,
        parent: this,
        persistent: true,
        componentProps: {
          task_uuid: props.valuation_task_uuid,
          task_name: props.valuation_task_name,
          task_type: props.valuation_task_type,
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
      visibleColumns.value = ["endpoint", "name", "study", "ages", "point_estimate"];
      loadValuationResults(props.task_uuid);
    });

    return {
      rows,
      columns,
      filter,
      loading,
      pagination,
      visibleColumns,
      loadValuationResults,
      showDownloadDialog,
    };
  },
});

const visibleColumns = ref([
  "endpoint",
  "name",
  "study",
  //"qualifier",
  "ages",
  "point_estimate",
  //"standard_deviation",
  //"race",
  //"ethnicity",
  //"gender",
  //"metric",
  //"seasonal_metric",
  //"metric_statistic",
]);

const columns = [
  {
    name: "endpoint",
    label: "Health Effect",
    align: "left",
    field: (row) => row.endpoint,
    format: (val) => `${val}`,
    align: "left",
    sortable: true,
  },
  {
    name: "name",
    label: "Name",
    field: (row) => row.name,
    align: "left",
    sortable: true,
  },
  {
    name: "study",
    label: "Study",
    field: (row) => row.author + " (" + row.year + ")",
    align: "left",
    sortable: true,
  },
  {
    name: "qualifier",
    label: "Qualifier",
    field: (row) => row.qualifier,
    align: "left",
    style:
      "inline-size: 150px; min-width: 400px; max-width: 400px; overflow-wrap: break-word; word-break: break-word; white-space: pre-wrap;",
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
    name: "point_estimate",
    label: "Valuation Point Estimate",
    field: (row) =>
      row.point_estimate.toLocaleString("en-US", { maximumFractionDigits: 4 }),
    sort: (a, b, rowA, rowB) => parseFloat(rowA.point_estimate) - parseFloat(rowB.point_estimate),
    sortable: true,
  },
  {
    name: "standard_deviation",
    label: "Standard Deviation",
    field: (row) =>
      row.standard_deviation.toLocaleString("en-US", { maximumFractionDigits: 4 }),
    sort: (a, b, rowA, rowB) => parseFloat(rowA.standard_deviation) - parseFloat(rowB.standard_deviation),
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
    name: "seasonal_metric",
    label: "Seasonal Metric",
    field: "seasonal_metric",
    sortable: true,
  },
  {
    name: "metric_statistic",
    label: "Annual Statistic",
    field: "metric_statistic",
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
.valuation-task-results {
  .download-button {
    text-align: right;
  }
}
</style>
