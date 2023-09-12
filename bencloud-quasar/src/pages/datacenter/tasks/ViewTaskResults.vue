<template>
  <div class="task-results-page">
    <div class="link-to-magage-tasks">
      <router-link to="/datacenter/manage-tasks">Back to Manage Tasks</router-link>
    </div>
    <div class="info task-pollutant">
      <p class="label">Pollutant:</p>
      <p>{{ pollutant_name }}</p>
    </div>
    <div class="info batch-task-name">
      <p class="label">Batch Task:</p>
      <p>{{ batch_task_name }}</p>
    </div>
    <div class="info task-pre-policy">
      <p class="label">Pre-policy: </p>
      <p>{{ task_pre_policy }}</p>  
    </div>
    <div class="info task-completed-date">
      <p class="label">Completed: </p>
      <p>{{ task_completed_date }}</p>  
    </div>

    <div class="info selection"> 
      <p class="label">Viewing results for:</p>
      <div class="aq-post-policy-selection q-pl-md q-pt-sm q-pb-sm q-pr-md">
        <q-select
          square
          dense
          outlined
          v-model="selected_scenario_name"
          :options="scenario_names"
          emit-value
          label="Post Policy Scenario Name"
        />
      </div>

      <div class="aq-post-policy-selection q-pt-sm q-pb-sm">
        <q-select
          square
          dense
          outlined
          v-model="selected_scenario_year"
          :options="scenario_years"
          emit-value
          label="Post Policy Scenario Year"
        />
      </div>
    </div>
    
    <div class="task-results">
      <TaskResultsTabs v-bind:task_uuid_with_type="task_uuid_with_type" v-bind:task_name="task_name" 
        v-bind:valuation_task_uuid_with_type="valuation_task_uuid_with_type" v-bind:valuation_task_name="valuation_task_name"
        v-bind:batch_task_id = "batch_task_id"
        :key="componentKey"></TaskResultsTabs>
    </div>

  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, unref, reactive, onBeforeMount } from "vue";
import { useRoute } from "vue-router";
import axios from 'axios';

import TaskResultsTabs from "../../../components/datacenter/tasks/TaskResultsTabs.vue";
import { getCompletedTasks } from "../../../composables/tasks/completed-tasks";

export default defineComponent({
  model: ref(null),
  name: "ViewTaskResults",
  components: {
    TaskResultsTabs,
  },

  setup() {
    const route = useRoute();
    const task_uuid_with_type = ref("");
    const valuation_task_uuid_with_type = ref("");
    const batch_task_id = route.params.batch_task_id;
    const pollutant_name = ref("");
    const task_pre_policy = ref("");
    const batch_task_name = ref("NO TASK NAME")
    const task_name = ref("");
    const valuation_task_name = ref("");
    const task_completed_date = ref(null);
    const scenario_names = ref([]);
    const scenario_years = ref([]);
    const selected_scenario_name = ref([]);
    const selected_scenario_year = ref([]);
    const batch_info = ref();
    const componentKey = ref(0);

    const rows = ref([]);

    watch(
      () => selected_scenario_name.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " | " + prevSelectedItem);
        scenario_years.value = [];
        for(var i = 0; i < batch_info.value.tasks.length; i++) {
          if(batch_info.value.tasks[i].aq_scenario_name == currentSelectedItem) {
            scenario_years.value.push(batch_info.value.tasks[i].pop_year)
          }
        }
        selected_scenario_year.value = scenario_years.value[0]
      }
    )

    watch(
      () => selected_scenario_year.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " | " + prevSelectedItem);
        for(var i = 0; i < batch_info.value.tasks.length; i++) {
          var currentTask = batch_info.value.tasks[i];
          if(currentTask.task_type === "HIF" && currentTask.aq_scenario_name === selected_scenario_name.value && currentTask.pop_year === currentSelectedItem) {
            valuation_task_uuid_with_type.value = "";
            valuation_task_name.value = "";
            task_uuid_with_type.value = "H-" + currentTask.task_uuid;
            task_name.value = currentTask.task_name;
            for(var j = 0; j < batch_info.value.tasks.length; j++) {
              {
                var currentTaskValuation = batch_info.value.tasks[j];
                if(currentTaskValuation.task_type === "Valuation" && currentTaskValuation.task_parent_uuid === currentTask.task_uuid) {
                  valuation_task_uuid_with_type.value = "V-" + currentTaskValuation.task_uuid
                  valuation_task_name.value = currentTaskValuation.task_name;
                }
              }
            }
            continue
          }
          else if(currentTask.task_type === "Exposure" && currentTask.aq_scenario_name === selected_scenario_name.value && currentTask.pop_year === currentSelectedItem) {
            valuation_task_uuid_with_type.value = "";
            valuation_task_name.value = "";
            task_uuid_with_type.value = "E-" + currentTask.task_uuid;
            task_name.value = currentTask.task_name;
          }
        }
        componentKey.value += 1;
      }
    )

    onBeforeMount(() => {
      (async () => {

        // console.log("--------------------------------")
        // console.log("ViewTaskResults: " + task_uuid_with_type)
        // console.log("--------------------------------")


        const response = await getCompletedTasks().fetch();
        //console.log(unref(response.data).data)
        rows.value = unref(response.data).data;
        var tasks = JSON.parse(JSON.stringify(rows.value));
        for (var i = 0; i < tasks.length; i++) {
          if(tasks[i].batch_task_id == batch_task_id) {
            batch_task_name.value = tasks[i].batch_task_name
            task_completed_date.value = tasks[i].batch_completed_date
            break
          }
        }

        try {
          const result = await axios
            .get(process.env.API_SERVER + "/api/batch-tasks/" + batch_task_id + "/scenarios")
            .then((response) => {
              task_pre_policy.value = response.data.aq_baseline_name
              pollutant_name.value = response.data.pollutant_name
              batch_info.value = response.data
              var scenariosList = response.data.scenarios
              for(var i = 0; i < scenariosList.length; i++) {
                scenario_names.value.push(scenariosList[i].scenario_name)
              }
            })
            .catch(error => {
              console.log("error" + error);
            })
        } catch (ex) {
            console.log(ex)
        } finally {
            selected_scenario_name.value = scenario_names.value[0];
            // return {response, error, data, loading }
        }


//        console.log(tasks);

      })();
    });

    return {
      task_uuid_with_type,
      task_name,
      valuation_task_uuid_with_type,
      valuation_task_name,
      task_completed_date,
      batch_task_id,
      batch_task_name,
      pollutant_name,
      task_pre_policy,
      scenario_names,
      scenario_years,
      selected_scenario_name,
      selected_scenario_year,
      componentKey,
    };
  },
});
</script>

<style lang="scss" scoped>
.task-results-page {
  padding: 10px;
  .info {
    padding-left: 20px;
    font-size: 18px;
    display: flex;
    height: 30px;
  }
  .label {
    width: 125px;
    font-weight: bold;
  }
  .selection {
    padding-top: 30px;
    display:flex;
    align-items: center;
    padding-bottom: 20px;
  }
  .selection>.label {
    width: 200px;
  }
  .aq-post-policy-selection {
    min-width: 180px;
  }
  .link-to-magage-tasks {
    padding-left: 20px;
    padding-top: 10px;
    padding-bottom: 10px;
  }
  .task-results {
    padding-top: 25px;
  }
}

</style>
