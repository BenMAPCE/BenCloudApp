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
  name: "IncidenceDatasets",

  props: {
    includeLayerName: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    deleteRow(props) {
      // Prompt user to confirm incidence dataset deletion
      if(confirm("Are you sure you wish to permanently delete " + props.row.name + "?")){
        // Delete AQ layer, reload the incidence dataset list if successful, alert the user if unsuccessful       
        axios
          .delete(process.env.API_SERVER + "/api/incidence/" + props.row.id, {
            params: {
              id: props.row.id,
            },
          })
          .then((response) => {
            if(response.status === 204) {
              trackCurrentPage = this.pagination.page;
              console.log("Successfully deleted incidence dataset: " + props.row.name);

              // Reload list
              var oldValue =  this.$store.state.incidence.incidenceForceReloadValue
              console.log("oldValue: " + oldValue);
              var newValue = oldValue - 1;
              console.log("newValue: " + newValue);
              this.$store.commit("incidence/updateIncidenceForceReloadValue", newValue)
            } else {
              alert("An error occurred, incidence dataset was not deleted.")
            }
          });
      }
    },

   
    
    rowClicked(props) {
      this.selected = [];
      this.selected.push(props.row);
      this.$store.commit("incidence/updateIncidenceDatasetId", props.row.id);
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
      () => store.state.incidence.incidenceDatasetAddedDate,
      (incidenceDatasetAddedDate, prevIncidenceDatasetAddedDate) => {
          console.log("--- updated Incidence Dataset")
          onRequest({
            pagination: pagination.value,
            filter: undefined,
         });
    })

    watch(
      () => store.state.incidence.incidenceForceReloadValue,
      (newValue, oldValue) => {
        if(newValue > oldValue) {
          console.log("--- added Incidence Dataset");
        } else if(newValue < oldValue) {
          console.log("--- deleted Incidence Dataset");
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

    // watch(
    //   () => showAll.value,
    //   () => {
    //     console.log("Show all layers: " + showAll.value);
    //     if(showAll.value && !visibleColumns.value.includes("user")) {
    //       visibleColumns.value.push("user");
    //       //visibleColumns.value.push("edit");
    //     }
    //     if(!showAll.value && visibleColumns.value.includes("user")) {
    //       visibleColumns.value.pop("user");
    //       //visibleColumns.value.pop("edit");
    //     }
    //     onRequest({
    //       filter: "",
    //       pagination: pagination.value,
    //       rows: [],
    //     });
    //   }
    // );

    function onRequest(props) {
      console.log("on onRequest()");
        loadIncidenceDatasets(props);

    }

    function loadIncidenceDatasets(props) {
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

    
        axios
          .get(process.env.API_SERVER + "/api/incidence", {
            params: {
              page: page,
              rowsPerPage: ++numLayers,
              sortBy: sortBy,
              descending: descending,
              filter: filter,
              showAll: showAll.value,
            },
          })
          .then((response) => {
            let data = response.data;

            console.log("----- return -----");
            console.log(data);

            store.commit("incidence/updateIncidenceDatasetId", 0);

            let loadPage = 1;
            for(let i = 0; i < data.length; i++) {
              if(data[i].name === layer) {
                loadPage = Math.floor((i/rowsPerPage) + 1);
                break;
              }
            }

            rows.value = [];
            let rowCount = 0;
            for(let i = 0; i < rowsPerPage; i++) {
              if(!!data[(loadPage-1)*rowsPerPage + i]) {
                rows.value[i] = data[(loadPage-1)*rowsPerPage + i];
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
    

    onBeforeMount(() => {
      console.log("includeDatasetName: " + props.includeDatasetName);

      if (props.includeDatasetName) {
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
  "id",
  "name",
  "grid_definition_id",
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
    name: "grid_definition_id",
    align: "left",
    label: "Grid ID",
    field: "grid_definition_id",
    sortable: true,
  },
  {
    name: "years",
    align: "left",
    label: "Years",
    field: "years",
    format: (val) => val.join(", "),
    sortable: true,
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
