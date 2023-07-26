<template>
  <q-table
    :rows="rows"
    :columns="columns"
    v-model:pagination="pagination"
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
        label="Export to csv"
        no-caps
        @click="exportIncidenceCells"
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
import { useStore } from "vuex";

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

    watch(
      () => store.state.incidence.incidenceDatasetId,
      (incidenceDatasetId, prevIncidenceDatasetId) => {
        incidenceDatasetId = incidenceDatasetId;
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
        loadIncidenceCells(props);
      
    }

    function exportIncidenceCells() {
      console.log("exportIncidenceCells");

      loading.value = true;

      axios
        .get(
          process.env.API_SERVER + "/api/incidence/" +
            store.state.incidence.incidenceDatasetId +
            "/contents",
          {
            params: {
              page: 1,
              rowsPerPage: 9999999,
            },
            headers: { Accept: "text/csv", "Content-Type": "text/csv" },
          }
        )
        .then((response) => {
          let records = response.data.records;
          let data = response.data;
          //console.log(data);

          console.log(response);

          //console.log(response.headers['content-disposition']);

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
        });
    }

    function loadIncidenceCells(props) {
      console.log(props.pagination);
      const { page, rowsPerPage, sortBy, descending } = props.pagination;
      const filter = props.filter;

      console.log("--------------------------------------------");
      console.log(filter);
      console.log("--------------------------------------------");

      loading.value = true;

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
      onRequest,
      exportIncidenceCells,
    };
  },
});

const columns = [
  {
    name: "grid_col",
    align: "left",
    label: "Column",
    field: "grid_col",
    sortable: true,
  },
  {
    name: "grid_row",
    align: "left",
    label: "Row",
    field: "grid_row",
    sortable: true,
  },
  { name: "endpoint group", 
    align: "left", 
    label: "Endpoint Group", 
    field: "endpoint_group",
    sortable: true,
  },
  { name: "endpoint", 
    align: "left", 
    label: "Endpoint", 
    field: "endpoint",
    sortable: true,
  },
  { name: "race", 
    align: "left", 
    label: "Race", 
    field: "race",
    sortable: true,
  },
  { name: "gender", 
    align: "left", 
    label: "Gender", 
    field: "gender",
    sortable: true,
  },
  { name: "ethnicity", 
    align: "left", 
    label: "Ethnicity", 
    field: "ethnicity",
    sortable: true,
  },
  {
    name: "start age",
    align: "left",
    label: "Start Age",
    field: "start_age",
    sortable: true,
  },
  {
    name: "end_age",
    align: "left",
    label: "End Age",
    field: "end_age",
    sortable: true,
  },
  {
    name: "type",
    align: "left",
    label: "Prevalence",
    field: "type",
    sortable: true,
  },
  {
    name: "timeframe",
    align: "left",
    label: "Timeframe",
    field: "timeframe",
    sortable: true,
  },
  {
    name: "units",
    align: "left",
    label: "Units",
    field: "units",
    sortable: true,
  },
  {
    name: "value",
    align: "left",
    label: "Value",
    field: "value",
    sortable: true,
  },
  {
    name: "distribution",
    align: "left",
    label: "Distribution",
    field: "distribution",
    sortable: true,
  },
  {
    name: "standard error",
    align: "left",
    label: "Standard Error",
    field: "standard_error",
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
