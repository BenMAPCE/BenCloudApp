<template>
  <q-table
    :rows="rows"
    :columns="columns"
    v-model:pagination="pagination"
    :rows-per-page-options="[5, 10, 25, 50]"
    :loading="loading"
    :filter="filter"
    @request="onRequest"
    binary-state-sort
    v-if="incidenceDatasetId != 0"
  >
    <template v-slot:top-right>
      <q-btn
        color="primary"
        icon-right="mdi-grid"
        class="export-to-csv-button"
        label="EXPORT TO CSV"
        no-caps
        @click="showDownloadDialog"
      />

      <q-input
        borderless
        dense
        debounce="300"
        v-model="filter"
        placeholder="Search"
      >
        <template v-slot:append>
          <q-icon name="mdi-magnify" />
        </template>
      </q-input>
    </template>
  </q-table>
</template>

<script>
import { defineComponent } from "vue";
import { ref, unref, onMounted, watch, watchEffect } from "vue";
import axios from "axios";
import { useQuasar } from "quasar";
import { useStore } from "vuex";
import DownloadIncidenceDatasetDialog from "./DownloadIncidenceDatasetDialog.vue"

var currentIncidenceDatasetId;

export default defineComponent({
  model: ref(null),
  name: "IncidenceCells",
  computed: {
    incidenceDatasetId() {
      return this.$store.state.incidence.incidenceDatasetId;
    },
  },

  methods: {
    exportToCSV(props) {},
  },

  data() {
    return {
      options: [],
      value: "",
      noti: () => {},
    };
  },

  setup(props, context) {
    const rows = ref([]);
    const filter = ref("");
    const loading = ref(false);
    const pagination = ref({
      sortBy: "",
      descending: false,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0,
    });

    let myFilter = unref(filter);

    const store = useStore();
    const $q = useQuasar();

  watch(
    () => store.state.incidence.incidenceDatasetId,
    (incidenceDatasetId, prevIncidenceDatasetId) => {
      incidenceDatasetId = incidenceDatasetId;
      currentIncidenceDatasetId = incidenceDatasetId;
      filter.value = "";
      pagination.value.sortBy = "";
      pagination.value.descending = false;
      pagination.value.page = 1;
      pagination.value.rowsPerPage = 10;
      pagination.value.rowsNumber = 0;
      console.log("resetting table.....");
      onRequest({
        filter: "",
        pagination: pagination.value,
        rows: [],
      });
    }
  );

    function onRequest(props) {
      console.log("on onRequest()");
      if (store.state.incidence.incidenceDatasetId != 0)
        loadIncidenceCells(props);
      
    }

    function loadIncidenceCells(props) {
      console.log(props.pagination);
      const { page, rowsPerPage, sortBy, descending } = props.pagination;
      const filter = props.filter;

      console.log("--------------------------------------------");
      console.log(filter);
      console.log("--------------------------------------------");

      loading.value = true;
      console.log(store.state.incidence.incidenceDatasetId);

      axios
        .get(
          process.env.API_SERVER + "/api/incidence/" +
            store.state.incidence.incidenceDatasetId +
            "/contents",
          {
            params: {
              page: page,
              rowsPerPage: rowsPerPage,
              sortBy: sortBy,
              descending: descending,
              filter: filter,
            },
          }
        )
        .then((response) => {
          let records = response.data.records;
          let data = response.data;

          console.log("----- return -----");
          console.log(records);

          rows.value = records;

          // don't forget to update local pagination object
          pagination.value.page = page;
          pagination.value.rowsPerPage = rowsPerPage;
          pagination.value.sortBy = sortBy;
          pagination.value.descending = descending;
          pagination.value.rowsNumber = data.filteredRecordsCount;

          // ...and turn of loading indicator
          loading.value = false;
        });
    }

    function showDownloadDialog() {
      $q.dialog({
        component: DownloadIncidenceDatasetDialog,
        parent: this,
        persistent: true,
        componentProps: {
          year: 0,
          incidence_dataset_id: currentIncidenceDatasetId,
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
      // get initial data from server (1st page)
      onRequest({
        pagination: pagination.value,
        filter: undefined,
      });
    });

    return {
      columns,
      filter,
      loading,
      pagination,
      rows,
      showDownloadDialog,
      onRequest,
    };
  },
});

const columns = [
  {
    name: "grid_col",
    align: "left",
    label: "Column",
    field: "Column",
    sortable: true,
  },
  {
    name: "grid_row",
    align: "left",
    label: "Row",
    field: "Row",
    sortable: true,
  },
  {
    name: "year",
    align: "left",
    label: "Year",
    field: "Year",
    sortable: true,
  },
  { name: "endpoint group", 
    align: "left", 
    label: "Health Effect Group", 
    field: "Health Effect Group",
    sortable: true,
  },
  { name: "endpoint", 
    align: "left", 
    label: "Health Effect", 
    field: "Health Effect",
    sortable: true,
  },
  { name: "race", 
    align: "left", 
    label: "Race", 
    field: "Race",
    sortable: true,
  },
  { name: "gender", 
    align: "left", 
    label: "Gender", 
    field: "Gender",
    sortable: true,
  },
  { name: "ethnicity", 
    align: "left", 
    label: "Ethnicity", 
    field: "Ethnicity",
    sortable: true,
  },
  {
    name: "start age",
    align: "left",
    label: "Start Age",
    field: "Start Age",
    sortable: true,
  },
  {
    name: "end_age",
    align: "left",
    label: "End Age",
    field: "End Age",
    sortable: true,
  },
  {
    name: "type",
    align: "left",
    label: "Type",
    field: "Type",
    sortable: true,
  },
  {
    name: "timeframe",
    align: "left",
    label: "Timeframe",
    field: "Timeframe",
    sortable: true,
  },
  {
    name: "units",
    align: "left",
    label: "Units",
    field: "Units",
    sortable: true,
  },
  {
    name: "value",
    align: "left",
    label: "Value",
    field: "Value",
    sortable: true,
  },
  {
    name: "distribution",
    align: "left",
    label: "Distribution",
    field: "Distribution",
    sortable: true,
  },
  {
    name: "standard error",
    align: "left",
    label: "Standard Error",
    field: "Standard Error",
    sortable: true,
  },
  
];

const rows = [];
</script>

<style scoped>
.export-to-csv-button {
  margin-right: 25px;
}
</style>
