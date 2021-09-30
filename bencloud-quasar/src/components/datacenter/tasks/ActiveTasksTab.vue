<template>
  <q-page-container>
    <q-page class="active-tasks">
      <q-table
        :rows="rows"
        :columns="columns"
        row-key="name"
        :rows-per-page-options="[0]"
        v-model:pagination="pagination"
        :loading="loading"
        :filter="filter"
        @request="getActiveTasks"
        binary-state-sort
        v-model:selected="selected"
        class="active-tasks"
      >
        <template v-slot:top-right>
          <q-btn
            color="primary"
            icon-right="mdi-reload"
            class="reload-button"
            label="Reload"
            no-caps
            @click="getActiveTasks"
          />

          <q-input borderless dense debounce="300" v-model="filter" placeholder="Search">
            <template v-slot:append>
              <q-icon name="mdi-magnify" />
            </template>
          </q-input>
        </template>

        <template v-slot:body-cell-task_status="props">
          <q-td :props="props">
            <div>{{ props.value ? "Active" : "Pending" }}</div>
          </q-td>
        </template>
      </q-table>
    </q-page>
  </q-page-container>
</template>

<script>
import { defineComponent } from "vue";
import { ref, unref, onMounted, onBeforeMount, watch, watchEffect } from "vue";
import axios from "axios";
import { useStore } from "vuex";

import { getActiveTasks } from "../../../composables/tasks/active-tasks";

export default defineComponent({
  model: ref(null),
  name: "ActiveTasksTab",
  computed: {},

  setup(props, context) {
    const store = useStore();

    const rows = ref([]);
    const filter = ref("");
    const loading = ref(false);
    const pagination = ref({
      page: 1,
      rowsPerPage: 0,
    });

    const timer = ref(null);

    let myFilter = unref(filter);

    onMounted(() => {
      loading.value = true;

      (async () => {
        const response = await getActiveTasks().fetch();
        rows.value = unref(response.data).data;
        loading.value = false;
      })();
    });

    onBeforeMount(() => {});

    return {
      columns,
      filter,
      loading,
      pagination,
      rows,
      selected: ref([]),
      getActiveTasks,
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
    name: "task_wait_time",
    label: "Wait Time",
    field: "task_wait_time",
    sortable: true,
  },
  {
    name: "task_started_date",
    label: "Started",
    field: "task_started_date",
    sortable: true,
  },
  {
    name: "task_status",
    label: "Status",
    field: "task_status",
    sortable: true,
  },
  {
    name: "task_active_time",
    label: "Elapsed Time",
    field: "task_active_time",
    sortable: true,
  },
  {
    name: "task_percentage",
    label: "% Complete",
    field: "task_percentage",
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

<style lang="scss">
.active-tasks {
  .reload-button {
    margin-right: 25px;
  }

  .q-table th {
    position: -webkit-sticky;
    position: sticky;
    top: 0px;
    background-color: grey;
    z-index: 2;
    color: white;
  }

  .q-table__middle.scroll {
    max-height: 500px;
  }
}
</style>
