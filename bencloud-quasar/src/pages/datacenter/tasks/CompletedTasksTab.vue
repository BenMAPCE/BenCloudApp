<template>
  C
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
  >
    <template v-slot:body="props">
      <q-tr class="cursor-pointer" :props="props" @click.exact="rowClicked(props)">
        <q-td v-for="col in props.cols" :key="col.name" :props="props">
          {{ col.value }}
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

    <template v-slot:body-cell-actions="props">
      <q-td :props="props">
        <q-btn
          dense
          round
          flat
          color="grey"
          @click="deleteRow(props)"
          icon="mdi-dots-vertical"
        ></q-btn>
      </q-td>
    </template>
  </q-table>
</template>

<script>
import { defineComponent } from "vue";
import { ref, unref, onMounted, watch, watchEffect } from "vue";
import axios from "axios";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "CompletedTasksTab",
  computed: {
    pollutantId() {
      return this.$store.state.airquality.pollutantId;
    },
  },

  methods: {
    deleteRow(props) {
      this.noti();
      // do something
      this.noti = this.$q.notify({
        type: "negative",
        multiline: true,
        message: `I'll delete row data => ${JSON.stringify(props.row)
          .split(",")
          .join(", ")}`,
        timeout: 2000,
      });
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
      rowsPerPage: 3,
      rowsNumber: 0,
    });

    let myFilter = unref(filter);

    watch(
      () => store.state.airquality.pollutantId,
      (pollutantId, prevPollutantId) => {
        pollutantId = pollutantId;
        filter.value = "";
        pagination.value.sortBy = "name";
        pagination.value.descending = false;
        pagination.value.page = 1;
        pagination.value.rowsPerPage = 3;
        pagination.value.rowsNumber = 0;
        console.log("resetting table.....");
        onRequest({
          filter: "",
          pagination: pagination.value,
          rows: [],
        });
      }
    );

    function onRequest(props) {
      console.log("on onRequest()");
      loadAirQualityLayers(props);
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
        .get(store.state.app.apiServerURL + "/api/tasks/completed", {
          params: {
            page: page,
            rowsPerPage: rowsPerPage,
            sortBy: sortBy,
            descending: descending,
            filter: filter,
          },
        })
        .then((response) => {
          let records = response.data.data;
          let data = response.data;

          console.log("----- return -----");
          console.log(records);

          rows.value = records;

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
      selected: ref([]),
      onRequest,
    };
  },
});

const rows = [];

const columns = [
  {
    name: "task_name",
    required: true,
    label: "Task Name",
    align: "left",
    field: (row) => row.task_name,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "task_type",
    align: "left",
    label: "Type",
    field: "task_type",
    sortable: true,
  },
  {
    name: "task_submitted_date",
    label: "Submitted",
    field: "task_submitted_date",
    sortable: true,
  },
  {
    name: "task_started_date",
    label: "Started",
    field: "task_started_date",
    sortable: true,
  },
  {
    name: "task_completed_date",
    label: "Completed",
    field: "task_completed_date",
    sortable: true,
  },
  {
    name: "task_successful",
    label: "Successful",
    field: "task_successful",
    sortable: true,
  },
  {
    name: "task_message",
    label: "Message",
    field: "task_message",
    sortable: true,
  },

  { name: "actions", label: "", field: "", align: "center" },
];
</script>
