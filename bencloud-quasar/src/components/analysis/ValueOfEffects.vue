<template>
  <q-table
    :rows="rows"
    :columns="columns"
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
        <div v-html="props.row.valuation" >
        </div>
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
import {
  getValuationFunctionsForEndpointGroupId,
  loadValuationFunctions,
} from "../../composables/valuation-functions";
import ValueOfEffectsEditForm from "./ValueOfEffectsEditForm.vue";

export default defineComponent({
  model: ref(null),
  name: "ValueOfEffects",

  async setup(props, context) {

    const valuations = ref("")
    const trial = ref("")
    const $q = useQuasar();

    const store = useStore();
    const rows = ref([]);
    const valuationFunctions = ref([]);
    const filter = ref("");
    const loading = ref(false);
    const pagination = ref({
      sortBy: "",
      descending: false,
      page: 1,
      rowsPerPage: 10,
      rowsNumber: 0,
    });
    const selectedItem = ref(store.state.analysis.incidenceId);

    const visibleColumns = [
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
        name: "endpoint_group_id",
        align: "left",
        label: "Group",
        field: "endpoint_group_id",
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
          store.commit("analysis/updateIncidence", currentSelectedItem, name);
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

      $q.dialog({
        component: ValueOfEffectsEditForm,
        parent: this,
        persistent: true,
        componentProps: {
          row: row,
          valuationFunctions: valuationFunctionsForEndpointGroupId,
        },
      })
        .onOk((valuationFunctionsSelected) => {
          console.log("OK");
          console.log(row)
          console.log(valuationFunctionsSelected);
          var records = JSON.parse(JSON.stringify(valuationFunctionsSelected));
          var valuations = "";
          console.log(records.length)
          for (var i = 0; i <records.length; i++) {
            console.log(records[i].qualifier)
            valuations = valuations + "<p>" + records[i].qualifier + '</p>';
          }
          console.log(row)
          row.valuation = valuations;
          console.log(row.valuation)
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
        rows.value = buildHealthImpactFunctionGroups(response.data.value);
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
