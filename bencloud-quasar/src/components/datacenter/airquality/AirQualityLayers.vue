<template>
  <q-table
    :rows="rows"
    :columns="columns"
    row-key="name"
    v-model:pagination="pagination"
    :loading="loading"
    :filter="filter"
    @request="onRequest"
    binary-state-sort
    v-if="pollutantId != 0"
    v-model:selected="selected"
    :visible-columns="visibleColumns"
  >
    
    <template v-slot:body="props">
      <q-tr class="cursor-pointer" :props="props" @click.exact="rowClicked(props)">
        <q-td v-for="col in props.cols" :key="col.name" :props="props">
          <template v-if="col.name === 'actions'">
            <q-btn
              dense
              round
              flat
              color="grey"
              @click="deleteRow(props)"
              icon="mdi-delete"
            ></q-btn>
          </template>
          <template v-else>
            {{col.value}}
          </template>
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
import { ref, unref, onMounted, onBeforeMount, watch, watchEffect } from "vue";
import axios from "axios";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "AirQualityLayers",
  computed: {
    pollutantId() {
      return this.$store.state.airquality.pollutantId;
    },
  },

  props: {
    includeLayerName: {
      type: Boolean,
      default: false,
    },
  },

  methods: {
    deleteRow(props) {
      // Prompt user to confirm AQ layer deletion
      if(confirm("Are you sure you wish to permanently delete " + props.row.name + "?")){
        // Delete AQ layer, reload the AQ layer list if successful, alert the user if unsuccessful       
        axios
          .delete(process.env.API_SERVER + "/api/air-quality-data/" + props.row.id, {
            params: {
              id: props.row.id,
            },
          })
          .then((response) => {
            console.log(response);
            if(response.status === 204) {
              console.log("Successfully deleted " + props.row.name);

              // Reload list
              var oldValue =  this.$store.state.airquality.airQualityForceReloadValue
              console.log("oldValue: " + oldValue);
              var newValue = oldValue - 1;
              console.log("newValue: " + newValue);
              this.$store.commit("airquality/updateAirQualityForceReloadValue", newValue)
            } else {
              alert("An error occurred, air quality layer was not deleted.")
            }
          });
      }
    },
    
    rowClicked(props) {
      this.selected = [];
      this.selected.push(props.row);
      this.$store.commit("airquality/updateAirQualityLayerId", props.row.id);
    },
  },

  data() {
    return {
      options: [],
      value: "",
      noti: () => {},
    };
  },

  setup(props, context) {
    const store = useStore();

    const rows = ref([]);
    const filter = ref("");
    const loading = ref(false);
    const pagination = ref({
      sortBy: "name",
      descending: false,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0,
    });

    let myFilter = unref(filter);

   watch(
      () => store.state.airquality.airQualityLayerAddedDate,
      (airQualityLayerAddedDate, prevAirQualityLayerAddedDate) => {
          console.log("--- updated Air Quality Layer")
          onRequest({
            pagination: pagination.value,
            filter: undefined,
         });
    })

  watch(
      () => store.state.airquality.airQualityForceReloadValue,
      (newValue, oldValue) => {
        console.log("--- added Air Quality Layer")
        filter.value = "";
        pagination.value.sortBy = "name";
        pagination.value.descending = false;
        pagination.value.page = 1;
        pagination.value.rowsPerPage = 10;
        pagination.value.rowsNumber = 0;
        onRequest({
            pagination: pagination.value,
            filter: undefined,
         });
      })

    watch(
      () => store.state.airquality.pollutantId,
      (pollutantId, prevPollutantId) => {
        console.log("--- changed Air Quality Layer")
        pollutantId = pollutantId;
        filter.value = "";
        pagination.value.sortBy = "name";
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
    })

    function onRequest(props) {
      console.log("on onRequest()");
      if (store.state.airquality.pollutantId != 0) {
        loadAirQualityLayers(props);
      }
    }

    function loadAirQualityLayers(props) {
      console.log(props.pagination);
      const { page, rowsPerPage, sortBy, descending } = props.pagination;
      const filter = props.filter;

      //console.log("--------------------------------------------")
      //console.log(filter)
      //console.log("--------------------------------------------")

      loading.value = true;

      axios
        .get(process.env.API_SERVER + "/api/air-quality-data", {
          params: {
            page: page,
            rowsPerPage: rowsPerPage,
            sortBy: sortBy,
            descending: descending,
            filter: filter,
            pollutantId: store.state.airquality.pollutantId,
          },
        })
        .then((response) => {
          let records = response.data.records;
          let data = response.data;

          console.log("----- return -----");
          console.log(records);

          rows.value = records;

          store.commit("airquality/updateAirQualityLayerId", 0);

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

    onBeforeMount(() => {

      visibleColumns.value = [];
      visibleColumns.push("name");
      visibleColumns.push("grid_definition_name");
      // visibleColumns.push("cell_count");
      // visibleColumns.push("mean_value");
      visibleColumns.push("actions");

      console.log("includeLayerName: " + props.includeLayerName);

      if (props.includeLayerName) {
        visibleColumns.push("id");
      }

    })

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
      visibleColumns,
      selected: ref([]),
      onRequest,
    };
  },
});

const rows = [];

const visibleColumns = [];

const columns = [
  {
    name: "id",
    label: "ID",
    align: "left",
    field: (row) => row.id,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "name",
    required: true,
    label: "Name",
    align: "left",
    field: (row) => row.name,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "grid_definition_name",
    align: "left",
    label: "Grid",
    field: "grid_definition_name",
    sortable: true,
  },
  {
    name: "cell_count",
    label: "Cell Count",
    field: "metric_statistics.cell_count",
    sortable: true,
  },
  {
    name: "mean_value",
    label: "Mean Value",
    field: "mean_value",
    sortable: true,
  },
  { 
    name: "actions", 
    label: "", 
    field: "", 
    align: "center" 
  },
];
</script>
