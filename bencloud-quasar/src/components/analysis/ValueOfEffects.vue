<template>
  <q-table
    :rows="rows"
    :columns="columns"
    :rows-per-page-options="[0]"
    v-model:pagination="pagination"
    :loading="loading"
    :filter="filter"
    binary-state-sort
    :visible-columns="visibleColumns"
  >
    <template v-slot:top-right>
      <q-input borderless dense debounce="300" v-model="filter" placeholder="Search">
        <template v-slot:append>
          <q-icon name="mdi-magnify" />
        </template>
      </q-input>
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
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";
import { useQuasar } from "quasar";

import { loadHealthImpactFunctionGroups } from "../../composables/health-impact-function-groups";
import { buildHealthImpactFunctionGroups } from "../../composables/health-impact-function-groups";
import { updateValuationsForHealthImpactFunctionGroups } from "../../composables/valuations";
import {
  getValuationFunctionsForEndpointGroupId,
  loadValuationFunctions,
} from "../../composables/valuation-functions";
import ValueOfEffectsEditForm from "./ValueOfEffectsEditForm.vue";

export default defineComponent({
  model: ref(null),
  name: "ValueOfEffects",

  async setup(props, context) {
    const valuations = ref("");
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
      "location",
      "group_name",
      "author_year",
      "endpoint_name",
      "age_range",
      "race_ethnicity_gender",
      "incidence_prevalence",
      "valuation",
      "edit",
    ];

    const columns = [
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
        label: "Endpoint Group Id",
        field: "endpoint_group_id",
        sortable: false,
      },
      {
        name: "endpoint_id",
        align: "left",
        label: "Endpoint Id",
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
        label: "Endpoint",
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
      {
        name: "edit",
        align: "center",
        label: "",
        field: "edit",
        sortable: false,
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
          console.log("OK");
          console.log(row);
          console.log(valuationFunctionsSelected);

          var records = JSON.parse(JSON.stringify(valuationFunctionsSelected));
          var valuations = "";
          var valuationIds = [];
          console.log(records.length);
          var valuationDisplay = "";
          for (var i = 0; i < records.length; i++) {
            console.log(records[i].qualifier);
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
          }
          console.log(valuationIds);
          console.log(row);

          var payload = {};
          payload.endpoint_group_id = row.endpoint_group_id;
          payload.health_function_id = row.health_function_id;
          payload.valuation_ids = valuationIds;

          store.commit("analysis/updateValuationsForHealthImpactFunctionGroups", payload);

          //updateValuationsForHealthImpactFunctionGroups(store, row.endpoint_group_id, valuationIds);

          row.valuation = valuations;
          console.log(row.valuation);
        })
        .onCancel(() => {
          // console.log('Cancel')
        })
        .onDismiss(() => {
          // console.log('I am triggered on both OK and Cancel')
        });
    }

    onBeforeMount(() => {
      (async () => {
        console.log("loadHealthImpactFunctionGroups");
        const response = await loadHealthImpactFunctionGroups().fetch();
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
        const valuationsResponse = await loadValuationFunctions().fetch();
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
</style>
