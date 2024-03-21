<template>
  <q-table
    :rows="rows"
    :columns="columns"
    row-key="name"
    v-model:pagination="pagination"
    :rows-per-page-options="[5, 10, 25, 50]"
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
          <template v-if="col.name === 'actions' & props.row.share_scope != 1">
            <q-btn
              dense
              round
              flat
              color="grey"
              @click.stop="deleteRow(props)"
              icon="mdi-delete"
            ></q-btn>
          </template>
          <template v-else>
            {{col.value}}
          </template>
          <template v-if="col.name === 'edit' & props.row.share_scope != 1">
            <q-btn
              dense
              round
              flat
              color="grey"
              @click.stop="editRow(props)"
              icon="mdi-pencil"
            ></q-btn>
          </template>
          <template v-if="col.name === 'user' && props.row.share_scope != 1 && !!props.row.user_id">
            {{props.row.user_id}}
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
import { layerName } from '../../common/AirQualityUploadForm.vue';
import { showAll } from '../../../pages/datacenter/managedata/airquality/ReviewAirQuality.vue';
import { date } from 'quasar'

var trackCurrentPage = null;
var numLayers = null;

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
          .delete(process.env.API_SERVER + "/api/air-quality-data/" + props.row.id, 
            {validateStatus: function (status) {
              return status < 500;
            }}
          )
          .then((response) => {
            if(response.status === 204) {
              trackCurrentPage = this.pagination.page;
              console.log("Successfully deleted AQ layer: " + props.row.name);

              // Reload list
              var oldValue =  this.$store.state.airquality.airQualityForceReloadValue
              console.log("oldValue: " + oldValue);
              var newValue = oldValue - 1;
              console.log("newValue: " + newValue);
              this.$store.commit("airquality/updateAirQualityForceReloadValue", newValue)
            } else if(response.status === 403){
              console.log("Forbidden action on AQ layer: " + props.row.name);
              this.$q.notify({
                group: false, // required to be updateable
                type: 'negative',
                timeout: 6000, 
                color: "red",
                spinner: false, // we reset the spinner setting so the icon can be displayed
                position: "top",
                message: response.data.message,
              });
              this.$emit('ok')
            } else {
              this.$q.notify({
                group: false, // required to be updateable
                type: 'negative',
                timeout: 6000, 
                color: "red",
                spinner: false, // we reset the spinner setting so the icon can be displayed
                position: "top",
                message: "Unknown error: " + response.status,
              });
              this.$emit('ok')
            }
          })
      }
    },

    editRow(props) {

      //Pop up form to include edit name and share_scope fields
      //only admin can edit share_scope
      //non-admin can only edit suraces that are their own and not shared (share_scope == 0)

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
      rowsPerPage: 25,
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
        if(newValue > oldValue) {
          console.log("--- added Air Quality Layer");
        } else if(newValue < oldValue) {
          console.log("--- deleted Air Quality Layer");
        }
        filter.value = "";
        pagination.value.sortBy = "name";
        pagination.value.descending = pagination.value.descending;
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
        pagination.value.rowsNumber = 0;
        console.log("resetting table.....");
        onRequest({
          filter: "",
          pagination: pagination.value,
          rows: [],
        });
    })

    watch(
      () => showAll.value,
      () => {
        console.log("Show all layers: " + showAll.value);
        if(showAll.value && !visibleColumns.value.includes("user")) {
          visibleColumns.value.push("user");
          //visibleColumns.value.push("edit");
        }
        if(!showAll.value && visibleColumns.value.includes("user")) {
          visibleColumns.value.pop("user");
          //visibleColumns.value.pop("edit");
        }
        onRequest({
          filter: "",
          pagination: pagination.value,
          rows: [],
        });
      }
    );

    function onRequest(props) {
      console.log("on onRequest()");
      if (store.state.airquality.pollutantId != 0) {
        loadAirQualityLayers(props);
      }
    }

    function loadAirQualityLayers(props) {
      console.log(props.pagination);
      if(!!trackCurrentPage) {
        props.pagination.page = trackCurrentPage;
      }
      let layer = layerName;
      const { page, rowsPerPage, sortBy, descending } = props.pagination;
      const filter = props.filter;

      //console.log("--------------------------------------------")
      //console.log(filter)
      //console.log("--------------------------------------------")
      loading.value = true;

      if(layer === null) {
        axios
          .get(process.env.API_SERVER + "/api/air-quality-data", {
            params: {
              page: page,
              rowsPerPage: rowsPerPage,
              sortBy: sortBy,
              descending: descending,
              filter: filter,
              pollutantId: store.state.airquality.pollutantId,
              showAll: showAll.value,
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
            numLayers = data.filteredRecordsCount;

            // ...and turn of loading indicator
            loading.value = false;
            trackCurrentPage = null;
          });
      } else {
        axios
          .get(process.env.API_SERVER + "/api/air-quality-data", {
            params: {
              page: page,
              rowsPerPage: ++numLayers,
              sortBy: sortBy,
              descending: descending,
              filter: filter,
              pollutantId: store.state.airquality.pollutantId,
              showAll: showAll.value,
            },
          })
          .then((response) => {
            let records = response.data.records;
            let data = response.data;

            console.log("----- return -----");
            console.log(records);

            store.commit("airquality/updateAirQualityLayerId", 0);

            let loadPage = 1;
            for(let i = 0; i < records.length; i++) {
              if(records[i].name === layer) {
                loadPage = Math.floor((i/rowsPerPage) + 1);
                break;
              }
            }

            rows.value = [];
            let rowCount = 0;
            for(let i = 0; i < rowsPerPage; i++) {
              if(!!records[(loadPage-1)*rowsPerPage + i]) {
                rows.value[i] = records[(loadPage-1)*rowsPerPage + i];
              }
            }

            // don't forget to update local pagination object
            pagination.value.page = loadPage;
            pagination.value.rowsPerPage = rowsPerPage;
            pagination.value.sortBy = sortBy;
            pagination.value.descending = descending;
            pagination.value.rowsNumber = data.filteredRecordsCount;

            // ...and turn of loading indicator
            loading.value = false;
            trackCurrentPage = null;
          });
      }
    }

    onBeforeMount(() => {
      console.log("includeLayerName: " + props.includeLayerName);

      if (props.includeLayerName) {
        visibleColumns.value.push("id");
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

const visibleColumns = ref([
  "name",
  "grid_definition_name",
  "aq_year",
  "source",
  "data_type",
  "description",
  "filename",
  "upload_date",
  //"cell_count",
  //"mean_value",
  "actions"
]);

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
    name: "aq_year",
    align: "left",
    label: "Year",
    field: "aq_year",
    sortable: true,
  },
  {
    name: "source",
    align: "left",
    label: "Source",
    field: "source",
    sortable: true,
  },
  {
    name: "data_type",
    align: "left",
    label: "Data Type",
    field: "data_type",
    sortable: true,
  },
  {
    name: "description",
    align: "left",
    label: "Description",
    field: "description",
    sortable: true,
  },
  {
    name: "filename",
    align: "left",
    label: "Filename",
    field: "filename",
    sortable: true,
  },
  {
    name: "upload_date",
    align: "left",
    label: "Upload Date",
    field: "upload_date",
    format: val => date.formatDate(val, 'YYYY-MM-DD HH:mm:ss'),
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
    name: "user", 
    label: "User", 
    field: "", 
    align: "left" 
  },
  { 
    name: "actions", 
    label: "", 
    field: "", 
    align: "left" 
  },
  { 
    name: "edit", 
    label: "", 
    field: "", 
    align: "left" 
  },
];
</script>
