<template>

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
          <!-- <q-btn
            color="primary"
            icon-right="mdi-reload"
            class="reload-button"
            label="Reload"
            no-caps
            @click="getActiveTasks"
          /> -->

          <q-input borderless dense debounce="300" v-model="filter" placeholder="Search">
            <template v-slot:append>
              <q-icon name="mdi-magnify" />
            </template>
          </q-input>
        </template>


        <template v-slot:body-cell-task_progress_message="props">
          <q-td :props="props">
            <q-linear-progress size="25px" :value="props.row.task_percentage/100" color="accent" v-if="props.row.task_status_message != 'Pending'">
              <div class="absolute-full flex flex-center">
                <q-badge color="white" text-color="accent" :label="props.row.task_percentage" />
              </div>
            </q-linear-progress>

            <ActiveTaskStatus :status = props.row.task_progress_message>

            </ActiveTaskStatus>


          </q-td>
        </template>

        <template v-slot:body-cell-task_status="props">
          <q-td :props="props">
            <div>{{ props.value ? "Active" : "Pending" }}</div>
          </q-td>
        </template>
      </q-table>

</template>

<script>
import { defineComponent } from "vue";
import { ref, unref, onMounted, onBeforeMount, onBeforeUnmount, watch, watchEffect } from "vue";
import axios from "axios";
import { useStore } from "vuex";

import { getActiveTasks } from "../../../composables/tasks/active-tasks";
import ActiveTaskStatus from "./ActiveTaskStatus.vue";

export default defineComponent({
  model: ref(null),
  name: "ActiveTasksTab",
  computed: {},
  components: {
    ActiveTaskStatus
  },
  props: {
    autoRefresh: {
      type: Boolean,
      default: false,
    },
  },

  setup(props, context) {
    const store = useStore();

    const rows = ref([]);
    const filter = ref("");
    const loading = ref(false);
    const pagination = ref({
      page: 1,
      rowsPerPage: 0,
      sortBy: 'task_name',
      descending: false,
    });
    const progress1 = ref(0);

    const timer = ref(null);

    let myFilter = unref(filter);
    let activeTasksRefreshInterval = null;

    function loadActiveTasks() {

      loading.value = true;

      (async () => {
        const response = await getActiveTasks().fetch();
        rows.value = unref(response.data).data;
        console.log(rows.value)
        loading.value = false;
      })();

    }

    function enableAutoRefresh() {
    
      activeTasksRefreshInterval = setInterval(function () {
        loadActiveTasks()   
      }.bind(this), 5000); 

    }

    function disableAutoRefresh() {
      clearInterval(activeTasksRefreshInterval);
    }

    onMounted(() => {
      console.log(props.autoRefresh)
      loadActiveTasks();
      enableAutoRefresh()

    });

    onBeforeMount(() => {});

    onBeforeUnmount(() => {
      //console.log("before unmount")
      disableAutoRefresh()
      //clearInterval(activeTasksRefreshInterval);
    })

    return {
      columns,
      filter,
      loading,
      pagination,
      rows,
      selected: ref([]),
      getActiveTasks,
      loadActiveTasks,
      activeTasksRefreshInterval,
      progress1
    };
  },
});

const rows = [];

const columns = [
  {
    name: "task_name",
    required: true,
    label: "Task",
    align: "left",
    field: (row) => row.task_name,
    format: (val) => `${val}`,
    sortable: true,
  },
  {
    name: "task_status_message",
    label: "Status",
    field: "task_status_message",
    sortable: true,
  },
  {
    name: "task_progress_message",
    label: "Progress",
    field: "task_progress_message",
    sortable: true,
    style: "width: 300px"
  },

  { name: "actions", label: "", field: "", align: "center" },
];
</script>

<style lang="scss" scoped>

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

  .column-task-progress {
    width: 250px;
  }
}
</style>
