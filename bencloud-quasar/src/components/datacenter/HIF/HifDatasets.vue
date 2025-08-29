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
    v-if="pollutantId != 0 && hifGroupId != 0"
    v-model:selected="selected"
    :visible-columns="visibleColumns"
  >
    
    <template v-slot:body="props">
      <q-tr class="cursor-pointer" :props="props" @click.exact="rowClicked(props)">
        <q-td v-for="col in props.cols" :key="col.name" :props="props">
          <template v-if="col.name === 'hero_id'">
            <a :href="props.row.epa_hero_url" target="_blank">{{ props.row.hero_id }}</a>
          </template>
          <template v-if="col.name === 'author'">
            <a :href="props.row.access_url" target="_blank">{{ props.row.author_year }}</a>
          </template>
          <template v-if="col.name === 'actions' && props.row.share_scope != 1">
            <q-btn
              dense
              round
              flat
              color="grey"
              @click.stop="archiveRow(props)"
              icon="mdi-archive"
            ></q-btn>
          </template>
          <template v-else>
            {{col.value}}
          </template>
          <template v-if="col.name === 'user' && props.row.share_scope != 1 && !!props.row.user_id">
            {{props.row.user_id}}
          </template>
        </q-td>
      </q-tr>
    </template>  
    
    <template v-slot:top>
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

      <q-space></q-space>

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
  computed: {
    pollutantId() {
      return this.$store.state.hif.pollutantId;
    },
    hifGroupId() {
      return this.$store.state.hif.hifGroupId;
    },
  },

  props: {
    hifDatasetName: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    archiveRow(props) {
      // Prompt user to confirm hif archive
      if(confirm("Are you sure you wish to archive " + props.row.author_year + "?")){
        // Archive the health impact function  
        axios
          .post(process.env.API_SERVER + "/api/health-impact-function/" + props.row.id, 
            {validateStatus: function (status) {
              return status < 500;
            }}
          )
          .then((response) => {
            if(response.status === 200) {
              trackCurrentPage = this.pagination.page;
              console.log("Successfully archived health impact function: " + props.row.author_year);

              // Reload list
              var oldValue =  this.$store.state.hif.hifForceReloadValue;
              console.log("oldValue: " + oldValue);
              var newValue = oldValue - 1;
              console.log("newValue: " + newValue);
              this.$store.commit("hif/updateHifForceReloadValue", newValue)
            } else if(response.status === 403){
              console.log("Forbidden action on HIF: " + props.row.author_year);
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
      sortBy: "endpoint_name",
      descending: false,
      page: 1,
      rowsPerPage: 25,
      rowsNumber: 0,
    });

    let myFilter = unref(filter);

    watch(
      () => store.state.hif.pollutantId,
      (pollutantId, prevPollutantId) => {
        console.log("--- changed pollutant")
        pollutantId = pollutantId;
        filter.value = "";
        pagination.value.sortBy = "endpoint_name";
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
      () => store.state.hif.hifGroupId,
      (hifGroupId, prevPollutantId) => {
        console.log("--- changed HIF Group")
        hifGroupId = hifGroupId;
        filter.value = "";
        pagination.value.sortBy = "endpoint_name";
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
      () => store.state.hif.hifLayerAddedDate,
      (hifLayerAddedDate, prevHifLayerAddedDate) => {
          console.log("--- updated Health Impact Function")
          onRequest({
            pagination: pagination.value,
            filter: undefined,
         });
    })

    watch(
      () => store.state.hif.hifForceReloadValue,
      (newValue, oldValue) => {
        if(newValue > oldValue) {
          console.log("--- added HIF");
        } else if(newValue < oldValue) {
          console.log("--- archived HIF");
        }
        filter.value = "";
        pagination.value.sortBy = "endpoint_name";
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
        }
        if(!showAll.value && visibleColumns.value.includes("user")) {
          visibleColumns.value.pop("user");
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
      if (store.state.hif.pollutantId != 0 && store.state.hif.hifGroupId) {
        loadHealthImpactFunctions(props);
      }
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
          , {
            params: {
              page: page,
              rowsPerPage: rowsPerPage,
              sortBy: sortBy,
              descending: descending,
              filter: filter,
              pollutantId: store.state.hif.pollutantId,
              hifGroupId: store.state.hif.hifGroupId,
              showAll: showAll.value,
            },
          }
        )
          .then((response) => {
            let records = response.data.records;
            let data = response.data;

            records.forEach(function(hif){
              hif.age_range = hif.start_age + " - " + hif.end_age;
              hif.author_year = hif.author + " / " + hif.function_year;
              hif.race_ethnicity_gender = hif.race_name + " / " + hif.ethnicity_name + " / " + hif.gender_name;
            })

            rows.value = records;

            console.log("----- return -----");
            console.log(records);

            store.commit("incidence/updateIncidenceDatasetId", 0);


            // // don't forget to update local pagination object
            pagination.value.page = page;
            pagination.value.rowsPerPage = rowsPerPage;
            pagination.value.sortBy = sortBy;
            pagination.value.descending = descending;
            pagination.value.rowsNumber = data.filteredRecordsCount;

            // // ...and turn of loading indicator
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
  // "id",
  // "pollutant",  
  "endpoint_name",
  "endpoint_group_name",
  "author",
  "age_range",
  "race_ethnicity_gender",
  "metric",
  "location",
  // "qualifier",
  // "reference",
  // "function_text",
  // "baseline_function_text",
  // "beta",
  "hero_id",
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
    name: "pollutant",
    align: "left",
    label: "Pollutant",
    field: "pollutant",
    sortable: true,
  },
  {
    name: "endpoint_name",
    align: "left",
    label: "Health Effect",
    field: "endpoint_name",
    sortable: true,
  },
  {
    name: "endpoint_group_name",
    align: "left",
    label: "Health Effect Category",
    field: "endpoint_group_name",
    sortable: true,
  },
  {
    name: "author",
    align: "left",
    label: "Author / Year",
    field: "",
    sortable: true,
  },
  {
    name: "age_range",
    align: "left",
    label: "Age Range",
    field: "age_range",
    sortable: false,
  },
  {
    name: "timing",
    align: "left",
    label: "Timing",
    field: "timing",
    sortable: true,
  },
  {
    name: "race_ethnicity_gender",
    align: "left",
    label: "Race / Ethnicity / Gender",
    field: "race_ethnicity_gender",
    sortable: false,
  },
  {
    name: "metric",
    align: "left",
    label: "Metric",
    field: "metric",
    sortable: true,
  },
  {
    name: "location",
    align: "left",
    label: "Location",
    field: "location",
    sortable: true,
  },
  {
    name: "qualifier",
    align: "left",
    label: "Risk Model Details",
    field: "qualifier",
    sortable: true,
  },
  {
    name: "reference",
    align: "left",
    label: "Reference",
    field: "reference",
    sortable: true,
  },
  {
    name: "function_text",
    align: "left",
    label: "Function",
    field: "function_text",
    sortable: true,
  },
  {
    name: "baseline_function_text",
    align: "left",
    label: "Baseline Function",
    field: "baseline_function_text",
    sortable: true,
  },
  {
    name: "beta",
    align: "left",
    label: "Beta",
    field: "beta",
    sortable: true,
  },
  {
    name: "dist_beta",
    align: "left",
    label: "Distribution Beta",
    field: "dist_beta",
    sortable: true,
  },
  {
    name: "p1_beta",
    align: "left",
    label: "Standard Error",
    field: "p1_beta",
    sortable: true,
  },
  {
    name: "p2_beta",
    align: "left",
    label: "Parameter 2 Beta",
    field: "p2_beta",
    sortable: true,
  },
  {
    name: "val_a",
    align: "left",
    label: "A",
    field: "val_a",
    sortable: true,
  },
  {
    name: "name_a",
    align: "left",
    label: "Name A",
    field: "name_a",
    sortable: true,
  },
  {
    name: "val_b",
    align: "left",
    label: "B",
    field: "val_b",
    sortable: true,
  },
  {
    name: "name_b",
    align: "left",
    label: "Name B",
    field: "name_b",
    sortable: true,
  },
  {
    name: "val_c",
    align: "left",
    label: "C",
    field: "val_c",
    sortable: true,
  },
  {
    name: "name_c",
    align: "left",
    label: "Name C",
    field: "name_c",
    sortable: true,
  },
  {
    name: "hero_id",
    align: "left",
    label: "HERO ID",
    field: "",
    sortable: true,
  },
  {
    name: "user",
    align: "left",
    label: "User",
    field: "",
    sortable: true,
  },
  { 
    name: "actions", 
    label: "Archive", 
    field: "", 
    align: "left" 
  },
];
</script>
