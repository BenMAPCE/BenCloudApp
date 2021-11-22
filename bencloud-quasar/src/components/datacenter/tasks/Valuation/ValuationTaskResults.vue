<template>
  <div class="q-pa-md">
    <q-table
      :rows="rows"
      :columns="columns"
      row-key="name"
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
</template>

<script>
import { defineComponent } from "vue";
import { ref, unref, watch, onBeforeMount, onUpdated, onMounted } from "vue";
import { useQuasar, date } from "quasar";
import { getValuationTaskResults } from "../../../../composables/tasks/task-results";

export default defineComponent({
  model: ref(null),
  name: "ValuationTaskResults",

  props: ["task_uuid"],

  setup(props, context) {

    const task_type = ref("");
    const task_uuid = ref(null);

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
        const response = await getValuationTaskResults(props.task_uuid).fetch();
        console.log(unref(response.data))
        console.log(JSON.parse(JSON.stringify(unref(response.data))));
        rows.value = JSON.parse(JSON.stringify(unref(response.data)))
        //console.log(rows.value)
        loading.value = false;
      })();
    }

    onMounted(() => {
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
    style: "inline-size: 150px; min-width: 400px; max-width: 400px; overflow-wrap: break-word; word-break: break-word; white-space: pre-wrap;",
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
    field: (row) => row.point_estimate.toFixed(4),
    sortable: true,
  },{
    name: "standard_deviation",
    label: "Standard Deviation",
    field: (row) => row.standard_deviation.toFixed(4),
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
];
</script>

<style lang="scss"></style>
