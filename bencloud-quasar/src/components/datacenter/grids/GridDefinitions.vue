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
    v-model:selected="selected"
    :visible-columns="visibleColumns"
  >
    
    <template v-slot:body="props">
      <q-tr class="cursor-pointer" :props="props" @click.exact="rowClicked(props)">
        <q-td v-for="col in props.cols" :key="col.name" :props="props">
          <template v-if="col.name === 'actions' && props.row.share_scope != 1">
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
          <template v-if="col.name === 'edit' && props.row.share_scope != 1">
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
          <template v-else-if="col.name === 'toggle'">
        <q-toggle
          size="lg"
          v-model="props.row.visible"
          color="blue"
          @update:model-value="toggleLayerVisibility(props.row)"
        />
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
import { showAll } from '../../../pages/datacenter/managedata/grids/ReviewGridDefinitions.vue';
import { date } from 'quasar'

var trackCurrentPage = null;
var numLayers = null;

export default defineComponent({
  model: ref(null),
  name: "gridDefinitions",

  props: {
    includeGridName: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    async editRow(props) {
  try {
    const FileNameChangeForm = await import('../../common/FileNameChangeForm.vue');

    // Open the dialog and properly wait for the user's response
    const result = await this.$q.dialog({
      component: FileNameChangeForm.default,
      parent: this,
      props: {
        currentName: props.row.name,
      },
    }).onOk(async (submittedName) => {
      try {
        if (!submittedName.filename || submittedName.filename.trim() === "") {
          throw new Error("Grid name cannot be empty.");
        }

        const newName = submittedName.filename.trim();
        const templateData = new FormData();
        templateData.append("newName", newName);

        // Make the API call to update the grid name after confirmation
        const response = await axios.put(
          `${process.env.API_SERVER}/api/grid-definitions/${props.row.id}`,
          templateData
        );

        if (response && response.status === 200) {
          this.$q.notify({ type: "positive", message: "Grid name updated successfully!" });
          props.row.name = newName;
        } else {
          throw new Error(response?.message || "Failed to update grid name.");
        }
      } catch (apiError) {
        console.error("API Error:", apiError.message);
        this.$q.notify({ type: "negative", message: "Update failed, please try again." });
      }
    });

  } catch (error) {
    console.error("Error during dialog interaction:", error.message);
    this.$q.notify({ type: "negative", message: error.message });
  }
},

    deleteRow(props) {
      // Prompt user to confirm drid definition deletion
      if(confirm("Are you sure you wish to permanently delete " + props.row.name + "?")){
        // Delete grid, reload the grid list if successful, alert the user if unsuccessful       
        axios
          .delete(process.env.API_SERVER + "/api/grid-definitions/" + props.row.id, 
            {validateStatus: function (status) {
              return status < 500;
            }}
          )
          .then((response) => {
            if(response.status === 204) {
              trackCurrentPage = this.pagination.page;
              console.log("Successfully deleted grid-definition: " + props.row.name);

              // Reload list
              var oldValue =  this.$store.state.grids.gridForceReloadValue
              console.log("oldValue: " + oldValue);
              var newValue = oldValue - 1;
              console.log("newValue: " + newValue);
              this.$store.commit("grids/updateGridReloadValue", newValue)
            } else if(response.status === 403){
              console.log("Forbidden action on grid definition: " + props.row.name);
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
          });
      }
    },

  toggleLayerVisibility(row) {
    const layerName = this.mapIdToLayerName(row.id);
    if (!layerName) {
      console.error(`Layer name not found for row ID: ${row.id}`);
      return;
    }


    if (row.visible) {
      this.$store.commit('grids/addVisibleLayer', layerName);
    } else {
      this.$store.commit('grids/removeVisibleLayer', layerName);
    }
    console.log(this.$store.state.grids.visibleLayers);
  },
  
  // Map ID to layer name based on gridmap
  mapIdToLayerName(id) {
    const gridmap = {
      28: "grid12km",
      18: "county",
      19: "state",
      20: "nation",
    };
    return gridmap[id] || null;
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
      () => store.state.grids.gridAddedDate,
      (gridAddedDate, prevGridDefinitionAddedDate) => {
          console.log("--- updated Grid Definition")
          onRequest({
            pagination: pagination.value,
            filter: undefined,
         });
    })

    watch(
      () => store.state.grids.gridForceReloadValue,
      (newValue, oldValue) => {
        if(newValue > oldValue) {
          console.log("--- added Grid Definition");
        } else if(newValue < oldValue) {
          console.log("--- deleted Grid Definition");
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
      () => showAll.value,
      () => {
        console.log("Show all grids: " + showAll.value);
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
        loadGridDefinitions(props);

    }

    function loadGridDefinitions(props) {
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
          .get(process.env.API_SERVER + "/api/grid-definitions-info", {
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

            store.commit("grids/updateGridId", 0);

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
      console.log("includeGridName: " + props.includeGridName);

      if (props.includeGridName) {
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
  "col_count",
  "row_count",
  "toggle",
  "edit",
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
    name: "col_count",
    required: true,
    label: "Columns",
    align: "left",
    field: (row) => row.col_count,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "row_count",
    required: true,
    label: "Rows",
    align: "left",
    field: (row) => row.row_count,
    format: (val) => `${val}`,
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
  {
    name: "toggle",
    label: "Layer Visibility",
    align: "center",
    field: "",
    sortable: false,
  }
];
</script>
