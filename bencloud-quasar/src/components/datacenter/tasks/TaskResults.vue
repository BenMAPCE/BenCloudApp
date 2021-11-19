<template>
  <div class="q-pa-md">
    
     <q-table
      :rows="rows"
      :columns="columns"
      row-key="name"
      :rows-per-page-options="[0]"
      v-model:pagination="pagination"
      :loading="loading"
      :filter="filter"
      @request="getResults"
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
import { ref, watch, onBeforeMount, onUpdated, onMounted } from "vue";
import { useQuasar, date } from "quasar";

export default defineComponent({
  model: ref(null),
  name: "TaskResults",

  setup(props, context) {
    //const rows = ref([]);
    const filter = ref("");
    const loading = ref(false);
    const pagination = ref({
      page: 1,
      rowsPerPage: 0,
      sortBy: "task_health_effect",
      descending: true,
    });
    const $q = useQuasar();

    function getResults() {}

    onMounted(() => {});

    return {
      rows,
      columns,
      filter,
      loading,
      pagination,
      visibleColumns,
      getResults,
    };
  },
});

const rows = [
  {
    task_uuid: "101010101",
    task_health_effect: "Mortallity",
    task_ages: "0-99",
    task_study: "study 1",
    task_completed_date: new Date(),
    task_change_in_aq: 24,
    task_change_in_incidence: 4.0,
    task_population_exposed: 87000,
    task_baseline_incidence: 140000,
    task_xyz: "XYZ",
  },
  {
    task_uuid: "202020202",
    task_health_effect: "Mortallity",
    task_ages: "50-64",
    task_study: "study 2",
    task_completed_date: new Date(),
    task_change_in_aq: 33.2,
    task_change_in_incidence: 5.0,
    task_population_exposed: 78000,
    task_baseline_incidence: 150000,
    task_xyz: "ZYX",
  },
];

const visibleColumns = ref([
  //  "task_uuid",
  "task_health_effect",
  "task_ages",
  "task_study",
  "task_change_in_aq",
  "task_population_exposed",
  "task_baseline_incidence",
  "task_change_in_incidence",
  //"task_xyz",
])

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
    name: "task_health_effect",
    label: "Health Effect",
    align: "left",
    field: (row) => row.task_health_effect,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "task_ages",
    label: "Ages",
    align: "left",
    field: (row) => row.task_ages,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "task_study",
    label: "Study",
    field: "task_study",
    sortable: true,
  },
  {
    name: "task_change_in_aq",
    label: "Change in AQ (ppb)",
    field: "task_change_in_aq",
    sortable: true,
  },
  {
    name: "task_change_in_incidence",
    label: "Change in Incidence (Cases)",
    field: "task_change_in_incidence",
    sortable: true,
  },
  {
    name: "task_population_exposed",
    label: "Population Exposed",
    field: "task_population_exposed",
    sortable: true,
  },
  {
    name: "task_baseline_incidence",
    label: "Baseline Incidence",
    field: "task_baseline_incidence",
    sortable: true,
  },
  {
    name: "task_xyz",
    label: "TASK XYZ",
    field: "task_xyz",
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

</style>
