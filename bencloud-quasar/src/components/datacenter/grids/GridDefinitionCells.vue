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
    v-if= "gridId != 0"
  >
    <template v-slot:top-right>
      <q-btn
        color="primary"
        icon-right="mdi-grid"
        class="export-to-csv-button"
        label="Export to csv"
        no-caps
        @click="exportGridGeometries"
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
  name: "GridGeometries",
  computed: {
    gridId() {
      return this.$store.state.grids.gridId;
    },

    columnLabelMap() {
      return {
        col: "Column",
        row: "Row",
        geom: "Geometry",
      };
    },
    // Compute columns dynamically based on the fields of the first record
    columns() {
      const firstRecord = this.rows[0] || {};
      return Object.keys(firstRecord).map(field => ({
        name: field,
        align: "left",
        label: this.columnLabelMap[field], // Use mapping or default to field name
        field,
        sortable: true,
      }));
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
      () => store.state.grids.gridId,
      (gridId, prevGridId) => {
        gridId = gridId;
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
      if (store.state.grids.gridId != 0) {
        loadGridGeometries(props);
      }
    }

    function exportGridGeometries() {
      console.log("exportGridGeometries");

      loading.value = true;

      axios
        .get(
          process.env.API_SERVER + "/api/grid-definitions/" +
            store.state.grids.gridId +
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

    function loadGridGeometries(props) {
      console.log(props.pagination);
      const { page, rowsPerPage, sortBy, descending } = props.pagination;
      const filter = props.filter;

      console.log("--------------------------------------------");
      console.log(filter);
      console.log("--------------------------------------------");

      loading.value = true;

      axios
        .get(
          process.env.API_SERVER + "/api/grid-definitions/" +
            store.state.grids.gridId +
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
          let data = response.data;

          console.log("----- return -----");
          console.log(data);

          rows.value = data;

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
      filter,
      loading,
      pagination,
      rows,
      onRequest,
      exportGridGeometries
    };
  },
});

// const columns = [
//   {
//     name: "col",
//     align: "left",
//     label: "Column",
//     field: "col",
//     sortable: true,
//   },
//   {
//     name: "row",
//     align: "left",
//     label: "Row",
//     field: "row",
//     sortable: true,
//   },
//   {
//     name: "geom",
//     align: "left",
//     label: "Geometry",
//     field: "geom",
//     sortable: true,
//   }
  
// ];

const rows = [];
</script>

<style scoped>
.export-to-csv-button {
  margin-right: 25px;
}
</style>
