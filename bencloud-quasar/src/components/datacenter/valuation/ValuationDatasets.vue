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
    v-if="healthEffectGroupId != 0"
    v-model:selected="selected"
    :visible-columns="visibleColumns"
  >
    
    <template v-slot:body="props">
      <q-tr class="cursor-pointer" :props="props" @click.exact="rowClicked(props)">
        <q-td v-for="col in props.cols" :key="col.name" :props="props">
          <template v-if="col.name === 'reference'">
            <a :href="props.row.access_url" target="_blank">{{ props.row.reference }}</a>
          </template>
          <template v-if="col.name === 'actions' && (props.row.share_scope != 1 || isAdmin)">
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
import { showAll } from '../../../pages/datacenter/managedata/valuation/ReviewValuation.vue';
import { date } from 'quasar'
import { isAdmin } from '../../../boot/auth.js';

var trackCurrentPage = null;
var numLayers = null;

export default defineComponent({
  model: ref(null),
  name: "ValuationDatasets",
  computed: {
    healthEffectGroupId() {
      return this.$store.state.valuation.healthEffectGroupId;
    },
  },

  props: {
    healthEffectName: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    archiveRow(props) {
      // Prompt user to confirm vf archive
      if(props.row.share_scope == 1 && !confirm("This valuation function is shared with all users. Are you sure you wish to archive valuation function " + props.row.id + "?")){
        return;
      }
      if(confirm((props.row.share_scope == 1 ? "This valuation function is shared with all users. Are you sure?" : "Are you sure you wish to archive valuation function " + props.row.id + "?"))){
        // Archive the valuation function  
        axios
          .post(process.env.API_SERVER + "/api/valuation-function/" + props.row.id, 
            {validateStatus: function (status) {
              return status < 500;
            }}
          )
          .then((response) => {
            if(response.status === 200) {
              trackCurrentPage = this.pagination.page;
              console.log("Successfully archived valuation function: " + props.row.author_year);

              // Reload list
              var oldValue =  this.$store.state.valuation.valuationForceReloadValue;
              console.log("oldValue: " + oldValue);
              var newValue = oldValue - 1;
              console.log("newValue: " + newValue);
              this.$store.commit("valuation/updateValuationForceReloadValue", newValue)
            } else if(response.status === 403){
              console.log("Forbidden action on VF: " + props.row.author_year);
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
      sortBy: "endpoint_group_name",
      descending: false,
      page: 1,
      rowsPerPage: 25,
      rowsNumber: 0,
    });

    let myFilter = unref(filter);

    watch(
      () => store.state.valuation.healthEffectGroupId,
      (healthEffectGroupId, prevHealthEffectGroupId) => {
        console.log("--- changed health effect category")
        healthEffectGroupId = healthEffectGroupId;
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
      () => store.state.valuation.healthEffectGroupAddedDate,
      (healthEffectGroupAddedDate, prevHealthEffectGroupAddedDate) => {
          console.log("--- updated Valuation Function")
          onRequest({
            pagination: pagination.value,
            filter: undefined,
         });
    })

    watch(
      () => store.state.valuation.valuationForceReloadValue,
      (newValue, oldValue) => {
        if(newValue > oldValue) {
          console.log("--- added VF");
        } else if(newValue < oldValue) {
          console.log("--- archived VF");
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
        console.log("Show all functions: " + showAll.value);
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
      if (store.state.valuation.healthEffectGroupId) {
        loadValuationFunctions(props);
      }
    }

    function loadValuationFunctions(props) {
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
          .get(process.env.API_SERVER + "/api/valuation-functions-by-health-effect"
          , {
            params: {
              page: page,
              rowsPerPage: rowsPerPage,
              sortBy: sortBy,
              descending: descending,
              filter: filter,
              healthEffectGroupId: store.state.valuation.healthEffectGroupId,
              showAll: showAll.value,
            },
          }
        )
          .then((response) => {
            let records = response.data.records;
            let data = response.data;

            records.forEach(function(vf){
              vf.age_range = vf.start_age + " - " + vf.end_age;
            })

            rows.value = records;

            console.log("----- return -----");
            console.log(records);


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
      isAdmin,
    };
  },
});

const rows = [];

const visibleColumns = ref([
  // "id",
  "endpoint_name",
  "endpoint_group_name",
  "age_range",
  "qualifier",
  // "reference",
  // "function_text",
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
    name: "age_range",
    align: "left",
    label: "Age Range",
    field: "age_range",
    sortable: false,
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
    field: "",
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
    name: "dist_a",
    align: "left",
    label: "Distribution A",
    field: "dist_a",
    sortable: true,
  },
  {
    name: "p1a",
    align: "left",
    label: "Standard Error",
    field: "p1a",
    sortable: true,
  },
  {
    name: "p2a",
    align: "left",
    label: "Parameter 2 A",
    field: "p2a",
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
  // {
  //   name: "val_c",
  //   align: "left",
  //   label: "C",
  //   field: "val_c",
  //   sortable: true,
  // },
  // {
  //   name: "name_c",
  //   align: "left",
  //   label: "Name C",
  //   field: "name_c",
  //   sortable: true,
  // },
  // {
  //   name: "val_d",
  //   align: "left",
  //   label: "D",
  //   field: "val_d",
  //   sortable: true,
  // },
  // {
  //   name: "name_d",
  //   align: "left",
  //   label: "Name D",
  //   field: "name_d",
  //   sortable: true,
  // },
  {
    name: "epa_standard",
    align: "left",
    label: "EPA Standard",
    field: "epa_standard",
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
