<template>
  <div class="q-pa-md col-12">
    <q-table
      :rows="rows"
      :columns="columns"
      :rows-per-page-options="[0]"
      v-model:pagination="pagination"
      :loading="loading"
      :filter="filter"
      binary-state-sort
      :fullscreen="fullscreen"
      :visible-columns="visibleColumns"
      class="valuation-table"
    >
      <template v-slot:top-right>
        <q-input borderless dense debounce="300" v-model="filter" placeholder="Search" style="margin-right: 15px;">
          <template v-slot:append>
            <q-icon name="mdi-magnify" />
          </template>
        </q-input>
        <q-btn
          dense
          flat
          :icon="fullscreen ? 'mdi-fullscreen-exit' : 'mdi-fullscreen'"
          @click="fullscreen = !fullscreen"
        />
      </template>

      <template v-slot:body-cell-edit="props">
        <q-td :props="props" class="edit-column">
          <q-btn
            dense
            flat
            round
            color="blue"
            field="edit"
            icon="mdi-pencil"
            @click="editValueOfEffects(props.row)"
          ></q-btn>
        </q-td>
      </template>

      <template v-slot:body-cell-valuation="props">
        <q-td :props="props" class="valuation-column">
          <div v-html="props.row.valuation"></div>
        </q-td>
      </template>
    </q-table>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";
import { useQuasar } from "quasar";

import { loadHealthImpactFunctionGroups, buildHealthImpactFunctionGroups } from "../../composables/analysis/health-impact-function-groups";
import {
  getValuationFunctionsForEndpointGroupId,
  loadValuationFunctions,
} from "../../composables/analysis/valuation-functions";
import ValueOfEffectsEditForm from "./ValueOfEffectsEditForm.vue";
import analysis from "src/store/analysis";

export default defineComponent({
  model: ref(null),
  name: "ValueOfEffects",

  async setup(props, context) {
    const valuations = ref("");
    const fullscreen = ref(false)
    const trial = ref("");
    const $q = useQuasar();

    const store = useStore();
    const rows = ref([]);
    const valuationFunctions = ref([]);
    const filter = ref("");
    const loading = ref(false);

    const pagination = ref({
      page: 1,
      rowsPerPage: 0,
    });

    const selectedItem = ref(store.state.analysis.incidenceId);

    const visibleColumns = [
      //      "health_function_id",
      //      "endpoint_group_id",
      //      "endpoint_id",
      "edit",
      "location",
      "group_name",
      "author_year",
      "endpoint_name",
      "age_range",
      "race_ethnicity_gender",
      "incidence_prevalence",
      "valuation",
    ];

    const columns = [
      {
        name: "edit",
        align: "center",
        label: "",
        field: "edit",
        sortable: false,
      },
      {
        name: "health_function_id",
        align: "left",
        label: "Health Function Id",
        field: "health_function_id",
        sortable: false,
      },
      {
        name: "endpoint_group_id",
        align: "left",
        label: "Health Effect Group Id",
        field: "endpoint_group_id",
        sortable: false,
      },
      {
        name: "endpoint_id",
        align: "left",
        label: "Health Effect Id",
        field: "endpoint_id",
        sortable: false,
      },
      {
        name: "group_name",
        align: "left",
        label: "Group",
        field: "group_name",
        sortable: true,
      },
      {
        name: "author_year",
        align: "left",
        label: "Author / Year",
        field: "author_year",
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
        name: "age_range",
        align: "left",
        label: "Age Range",
        field: "age_range",
        sortable: true,
      },
      {
        name: "race_ethnicity_gender",
        align: "left",
        label: "Race / Ethnicity / Gender",
        field: "race_ethnicity_gender",
        sortable: true,
      },
      {
        name: "location",
        align: "left",
        label: "Location",
        field: "location",
        sortable: true,
        style: "max-width: 250px; white-space: normal;"
      },
      {
        name: "incidence_prevalence",
        align: "left",
        label: "Incidence or Prevalence",
        field: "incidence_prevalence",
        sortable: true,
      },
      {
        name: "valuation",
        align: "left",
        label: "Valuation",
        field: "valuation",
        sortable: true,
      },
    ];
    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          console.log(rows.value.find((opt) => opt.id === currentSelectedItem).name);
          var name = rows.value.find((opt) => opt.id === currentSelectedItem).name;
          store.commit("analysis/updateIncidence", {
            incidenceId: currentSelectedItem,
            incidenceName: name,
          });
        }
      }
    );

    watch(
      () => store.state.analysis.valuationSelection,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          (async () => {
          console.log("loadHealthImpactFunctionGroups");
          const response = await loadHealthImpactFunctionGroups(store).fetch(store);
          rows.value = response.data.value;
          console.log(rows.value);
          rows.value = buildHealthImpactFunctionGroups(
            response.data.value,
            valuationFunctions,
            store
          );
        })();
        }
      }
    );

    function setSelectedValuationFunctions(valuationFunctionsSelected, row) {
      console.log("getSelectedValuationFunctions");
      console.log(valuationFunctionsSelected);
      console.log(row.endpoint_group_id);
    }

    function editItem(row) {
      console.log("editItem");
      console.log(row);
    }

    function editValueOfEffects(row) {
      var valuationFunctionsForEndpointGroupId = getValuationFunctionsForEndpointGroupId(
        valuationFunctions.value,
        row.endpoint_group_id
      );

      console.log("------------------");
      console.log(valuationFunctionsForEndpointGroupId);
      console.log("------------------");

      console.log("*****");
      console.log(row.health_function_id);
      //  console.log(this.getValuationsForHealthFunctionId(row.health_function_id))
      //  var valuationsForHealthFunction = this.getValuationsForHealthFunctionId(row.health_function_id);

      var valuationsForHealthFunction = store.getters[
        "analysis/getValuationsForHealthFunctionId"
      ](row.health_function_id);

      console.log("*****");

      $q.dialog({
        component: ValueOfEffectsEditForm,
        parent: this,
        persistent: true,
        componentProps: {
          row: row,
          valuationFunctionsForEndpointGroupId: valuationFunctionsForEndpointGroupId,
          valuationsSelected: valuationsForHealthFunction,
        },
      })
        .onOk((valuationFunctionsSelected) => {
          if(store.state.analysis.valuationSelection == "Use EPA's current default values") {
            store.commit("analysis/updateValuationSelection", "Select my own value functions");
          }
          console.log(row);
          console.log(valuationFunctionsSelected);

          var records = JSON.parse(JSON.stringify(valuationFunctionsSelected));
          var valuations = "";
          var valuationIds = [];
          var valuationDisplay = "";
          
          var valuationFunctionsArray = [];
          for (var i = 0; i < records.length; i++) {
            valuationDisplay =
              records[i].endpoint_name +
              " | " +
              records[i].start_age +
              " - " +
              records[i].end_age +
              " | " +
              records[i].qualifier;

            valuations = valuations + "<p>" + valuationDisplay + "</p>";
            valuationIds.push(records[i].id);
            var valuationFunction = {
              hifId: row.health_function_id,
              hifInstanceId: null,
              vfId: records[i].id,
              vfRecord: records[i]
            }
            valuationFunctionsArray.push(valuationFunction);
          }

          var batchTaskObject = JSON.parse(JSON.stringify(store.state.analysis.batchTaskObject));
          for(var i = 0; i < batchTaskObject.batchHifGroups.length; i++) {
            if(batchTaskObject.batchHifGroups[i].name === row.group_name) {
              for(var j = 0; j < batchTaskObject.batchHifGroups[i].hifs.length; j++) {
                if(batchTaskObject.batchHifGroups[i].hifs[j].hifId === row.health_function_id) {
                  for(var k = 0; k < valuationFunctionsArray.length; k++) {
                    valuationFunctionsArray[k].hifInstanceId = batchTaskObject.batchHifGroups[i].hifs[j].hifInstanceId;
                  }
                  batchTaskObject.batchHifGroups[i].hifs[j]['valuationFunctions'] = valuationFunctionsArray;
                  break;
                }
              }
              break;
            }
          }

          store.commit("analysis/updateBatchTaskObject", batchTaskObject);
          console.log(store.state.analysis.batchTaskObject);

          var payload = {};
          payload.endpoint_group_id = row.endpoint_group_id;
          payload.health_function_id = row.health_function_id;
          payload.valuation_ids = valuationIds;

          console.log("... updateValuationsForHealthImpactFunctionGroups")
          store.commit("analysis/updateValuationsForHealthImpactFunctionGroups", payload);

          row.valuation = valuations;
        })
        .onCancel(() => {
          // console.log('Cancel')
        })
        .onDismiss(() => {
          // console.log('I am triggered on both OK and Cancel')
        });
    }

    onBeforeMount(() => {

      console.log("!#!#!#!#!#!#!#")
      if (!store.state.analysis.valuationsForHealthImpactFunctionGroups) {
        console.log("NOT YET")
      } else {
        console.log(store.state.analysis.valuationsForHealthImpactFunctionGroups)
      }

      (async () => {
        console.log("loadHealthImpactFunctionGroups");
        const response = await loadHealthImpactFunctionGroups(store).fetch(store);
        rows.value = response.data.value;
        console.log(rows.value);
        rows.value = buildHealthImpactFunctionGroups(
          response.data.value,
          valuationFunctions,
          store
        );
      })();

      (async () => {
        console.log("loadValuationFunctions");
        const valuationsResponse = await loadValuationFunctions(store).fetch(store);
        console.log("....");
        valuationFunctions.value = valuationsResponse.data.value;
        console.log(valuationFunctions.value);
      })();
    });

    onMounted(() => {
      //console.log("... " + store.state.analysis.incidenceId)
      //if (store.state.analysis.incidenceId != null) {
      //  selectedItem.value = store.state.analysis.incidenceId;
      //  console.log("- selectedItem: " + selectedItem.value);
      //}
    })();

    return {
      rows,
      valuationFunctions,
      setSelectedValuationFunctions,
      valuations,
      filter,
      loading,
      pagination,
      columns,
      selectedItem,
      editItem,
      visibleColumns,
      editValueOfEffects,
      fullscreen,
    };
  },
});
</script>

<style lang="scss" scoped>
.incidence-options {
  width: 300px;
}

.edit-column {
  width: 50px;
}

.valuation-column {
  width: 350px;
  white-space: normal;
}

.location-column {
  max-width: 250px;
  white-space: normal;
}
</style>

<style lang="sass">
.valuation-table

  thead tr:first-child th:first-child,
  td:first-child
    position: sticky
    -webkit-position: sticky
    left: 0
    z-index: 1
    background-color: #fff
    opacity: 1

  thead tr:first-child th:last-child,
  td:last-child
    position: sticky
    -webkit-position: sticky 
    right: 0
    z-index: 1
    background-color: #fff
    opacity: 1
</style>