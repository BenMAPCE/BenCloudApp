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
          <template v-if="col.name === 'epa_hero_url'">
            <a :href="props.row.epa_hero_url" target="_blank">EPA Hero URL</a>
          </template>
          <template v-if="col.name === 'access_url'">
            <a :href="props.row.access_url" target="_blank">Access URL</a>
          </template>
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
import { showAll } from '../../../pages/datacenter/managedata/HIF/ReviewHif.vue';
import { date } from 'quasar'

var trackCurrentPage = null;
var numLayers = null;

export default defineComponent({
  model: ref(null),
  name: "HifDatasets",

  props: {
    hifDatasetName: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    deleteRow(props) {
      // Prompt user to confirm hif dataset deletion
      if(confirm("Are you sure you wish to permanently delete " + props.row.name + "?")){
        // Delete AQ layer, reload the hif dataset list if successful, alert the user if unsuccessful       
        axios
          .delete(process.env.API_SERVER + "/api/health-impact-function/" + props.row.id, 
            {validateStatus: function (status) {
              return status < 500;
            }}
          )
          .then((response) => {
            if(response.status === 204) {
              trackCurrentPage = this.pagination.page;
              console.log("Successfully deleted health impact function: " + props.row.name);

              // Reload list
              var oldValue =  this.$store.state.incidence.incidenceForceReloadValue
              console.log("oldValue: " + oldValue);
              var newValue = oldValue - 1;
              console.log("newValue: " + newValue);
              this.$store.commit("incidence/updateIncidenceForceReloadValue", newValue)
            } else if(response.status === 403){
              console.log("Forbidden action on incidence dataset: " + props.row.name);
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
        loadHealthImpactFunctions(props);

    }

    function loadHealthImpactFunctions(props) {
      console.log(props.pagination);
      if(!!trackCurrentPage) {
        props.pagination.page = trackCurrentPage;
      }
      const { page, rowsPerPage, sortBy, descending } = props.pagination;
      const filter = props.filter;

      //console.log("--------------------------------------------")
      //console.log(filter)
      //console.log("--------------------------------------------")
      loading.value = true;

    
        axios
          .get(process.env.API_SERVER + "/api/health-impact-functions"
          // , {
          //   params: {
          //     page: page,
          //     rowsPerPage: ++numLayers,
          //     sortBy: sortBy,
          //     descending: descending,
          //     filter: filter,
          //     showAll: showAll.value,
          //   },
          // }
        )
          .then((response) => {
            let data = response.data;

            rows.value = data;

            console.log("----- return -----");
            console.log(data);

            store.commit("incidence/updateIncidenceDatasetId", 0);

            // let loadPage = 1;
            // for(let i = 0; i < data.length; i++) {
            //   if(data[i].name === layer) {
            //     loadPage = Math.floor((i/rowsPerPage) + 1);
            //     break;
            //   }
            // }

            // rows.value = [];
            // let rowCount = 0;
            // for(let i = 0; i < rowsPerPage; i++) {
            //   if(!!data[(loadPage-1)*rowsPerPage + i]) {
            //     rows.value[i] = data[(loadPage-1)*rowsPerPage + i];
            //   }
            // }

            // // don't forget to update local pagination object
            // pagination.value.page = loadPage;
            // pagination.value.rowsPerPage = rowsPerPage;
            // pagination.value.sortBy = sortBy;
            // pagination.value.descending = descending;
            // pagination.value.rowsNumber = data.filteredRecordsCount;

            // // ...and turn of loading indicator
            // loading.value = false;
            // trackCurrentPage = null;
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
  "endpoint_group_name",
  "author",
  "race_name",
  "gender_name",
  "ethnicity_name",
  "hero_id",
  "epa_hero_url",
  "access_url",
  "actions",
  "edit"
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
    name: "endpoint_group_name",
    align: "left",
    label: "Endpoint Group",
    field: "endpoint_group_name",
    sortable: true,
  },
  {
    name: "author",
    align: "left",
    label: "Author",
    field: "author",
    sortable: true,
  },
  {
    name: "race_name",
    align: "left",
    label: "Race",
    field: "race_name",
    sortable: true,
  },
  {
    name: "gender_name",
    align: "left",
    label: "Gender",
    field: "gender_name",
    sortable: true,
  },
  {
    name: "ethnicity_name",
    align: "left",
    label: "Ethnicity",
    field: "ethnicity_name",
    sortable: true,
  },
  {
    name: "hero_id",
    align: "left",
    label: "Hero ID",
    field: "hero_id",
    sortable: true,
  },
  {
    name: "epa_hero_url",
    align: "left",
    label: "EPA Hero URL",
    field: "",
  },
  {
    name: "access_url",
    align: "left",
    label: "Access",
    field: "",
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
