<template>
  <div class="review-and-submit">
    <div class="row">
      <p>Review And Submit</p>
    </div>
    <div class="row">
      <q-card class="choices-card">
        <div class="row odd choices">
          <div class="title">Pollutant</div>
          <div>{{ this.pollutantFriendlyName }}</div>
        </div>
        <div class="row even choices">
          <div class="title">Pre-Policy</div>
          <div class="prePolicyList">{{ this.prePolicyAirQualityName }}</div>
        </div>
        <div class="row odd choices">
          <div class="title">Post-Policy</div>
          <div> 
            <li class="postPolicyList" v-for="postPolicy in this.postPolicyAirQualityName" :key=postPolicy.name>
              {{ postPolicy.name }} <br> &emsp;
              Population year(s): {{ postPolicy.years.toString().replaceAll(",", ", ") }}
            </li>
          </div>
        </div>
        <div class="row even choices">
          <div class="title">Population Dataset</div>
          <div>{{ this.populationDatasetName }}</div>
        </div>
        <div class="row odd choices">
          <div class="title">Incidence</div>
          <div>{{ this.incidenceName }}</div>
        </div>
        <div class="row even choices">
          <div class="title">Health Effects</div>
          <div>{{ this.healthEffectsNames }}</div>
        </div>
        <div class="row odd choices">
          <div class="title">Health Impact Functions</div>
          <div>{{ this.healthImpactFunctions.length }}</div>
        </div>
        <div class="row even choices">
          <div class="title">Valuation Functions</div>
          <div>{{ this.valuationFunctionCount }}</div>
        </div>
        <div class="row odd choices">
          <div class="title">Total number of tasks</div>
          <div>{{ this.totalTaskCount }}</div>
        </div>
      </q-card>
    </div>
  </div>

  <div class="row">
    <div class="col-3">
      <q-form @submit="submitTask()" class="q-gutter-md">
        <q-input
          outlined
          dense
          v-model="taskName"
          label="Enter Task Name"
          hint=""
          lazy-rules="ondemand"
          :rules="[(val) => (val && val.length > 0) || 'Please enter a Task Name']"
        />
      </q-form>
    </div>
    <div class="col-2">
      <div class="save-template-button">
        <q-btn
          :disabled="taskName.trim() == ''"
          color="primary"
          label="Submit Task"
          @click="submitTask()"
        />
      </div>
    </div>
  </div>
  <div class="row enter-template-row">
    <div class="col-3">
      <q-form @submit="submitTemplate()" class="q-gutter-md">
        <q-input
          outlined
          dense
          v-model="templateName"
          label="Enter Template Name"
          hint=""
          lazy-rules="ondemand"
          :rules="[(val) => (val && val.length > 0) || 'Please enter a Template Name']"
        />
      </q-form>
    </div>
    <div class="col-2">
      <div class="save-template-button">
        <q-btn
          :disabled="templateName.trim() == ''"
          color="primary"
          label="Save Template"
          @click="submitTemplate()"
        />
      </div>
    </div>

    <q-card-section class="error-card" v-if="this.errorMessage != ''">
      {{ this.errorMessage }}
    </q-card-section>
  </div>

</template>

<script>
import { defineComponent } from "vue";
import { ref, onMounted, watch } from "vue";
import { useStore } from "vuex";
import { useQuasar } from "quasar";

import { createHifTemplate, saveTemplate } from "../../composables/templates/templates";
import { submitBatchTask } from "../../composables/analysis/batch-task";
import TaskSubmittedDialog from "./TaskSubmittedDialog.vue";

export default defineComponent({
  model: ref(null),
  name: "ReadyToSubmit",

  setup(props) {
    const store = useStore();

    const $q = useQuasar();

    const pollutantValue = ref(null);
    const options = ref([]);

    const pollutantId = store.state.analysis.pollutantId;
    const pollutantFriendlyName = store.state.analysis.pollutantFriendlyName;
    const prePolicyAirQualityId = store.state.analysis.prePolicyAirQualityId;
    const prePolicyAirQualityName = store.state.analysis.prePolicyAirQualityName;
    const postPolicyAirQualityId = store.state.analysis.postPolicyAirQualityId;
    const postPolicyAirQualityName = store.state.analysis.postPolicyAirQualityName;

    const incidenceId = store.state.analysis.incidenceId;
    const incidenceName = store.state.analysis.incidenceName;
    const populationDatasetId = store.state.analysis.populationDatasetId;
    const populationDatasetName = store.state.analysis.populationDatasetName;
    const valuationsForHealthImpactFunctionGroups =
      store.state.analysis.valuationsForHealthImpactFunctionGroups;

    const healthEffects = store.state.analysis.healthEffects;

    const healthEffectsIds = ref([]);
    const healthEffectsNames = ref("");

    const healthImpactFunctions = store.state.analysis.healthImpactFunctions;

    const taskName = ref("");
    const templateName = ref("");
    const errorMessage = ref("");

    var batchTaskId = null;
    var valuationFunctionCount = null;
    var totalTaskCount = null;

    valuationsForHealthImpactFunctionGroups.forEach(e => {
      valuationFunctionCount += e.valuation_ids.length;
    })

    postPolicyAirQualityName.forEach(e => {
      totalTaskCount += e.years.length;
    })

    if(valuationFunctionCount > 0) {
      totalTaskCount *= 2;
    }

    watch(
      () => batchTaskId,
      (currentBatchTaskId, prevBatchTaskId) => {
        if (currentBatchTaskId != prevBatchTaskId) {
          // console.log("yes... " + currentBatchTaskId);
        }
      }
    );

    function submitTemplate() {

      var template = createHifTemplate(taskName.value, store);

      const templateNotification = $q.notify({
        group: false, // required to be updateable
        timeout: 0, // we want to be in control when it gets dismissed
        spinner: true,
        position: "top",
        message: "Saving Template...",
      });

      (async () => {
        const response = await saveTemplate(
          templateName.value,
          "Health Impact Analysis",
          template,
          store,
          templateNotification
        ).fetch();

        templateName.value = "";
      })();
    }

    //console.log("----- healthImpactFunctions -----");
    //console.log(healthImpactFunctions);

    function submitTask() {
      var heItems = JSON.parse(JSON.stringify(healthEffects));
      var heItemIds = "";
      var healthEffectsNamesList = "";

      for (var i = 0; i < heItems.length; i++) {
        // console.log(heItems[i].healthEffectId);
        heItemIds = heItemIds + heItems[i].healthEffectId + ",";
        healthEffectsNamesList =
          healthEffectsNamesList + heItems[i].healthEffectName + ", ";
      }

      healthEffectsIds.value = heItemIds.substring(0, heItemIds.length - 1);
      healthEffectsNames.value = healthEffectsNamesList.substring(
        0,
        healthEffectsNamesList.length - 2
      );

      var batchTaskJSON = JSON.parse(JSON.stringify(store.state.analysis.batchTaskObject));
      batchTaskJSON['name'] = taskName.value;
      batchTaskJSON['gridDefinitionId'] = store.state.analysis.aggregationScale;
      store.commit("analysis/updateBatchTaskObject", batchTaskJSON);
      console.log("----- Batch task configuration -----")
      console.log(batchTaskJSON);

      // var template = createTemplate(taskName.value, store);
      // console.log("----- template -----");
      // console.log(template);
      // console.log("--------------------");

      (async () => {
        const response = await submitBatchTask(batchTaskJSON, store).fetch();
        if(response.data.value.message=="Task was submitted"){
          $q.dialog({
              component: TaskSubmittedDialog,
              parent: this,
              persistent: true,
              componentProps: {
                taskName: taskName,
              },
            })
            .onOk(() => {
              //taskName.value = ""
              this.$router.replace("/datacenter/manage-tasks");
            })
            .onCancel(() => {
              // Sounds backwards, but clicking on the 'OK' button is actually a Cancel since we don't
              // want the user to go anywhere (we're cancelling out of the dialog)
              // Clear the task name field
              taskName.value = "";
            });
          }
          else{
            //Usually when reached the maximum of # task scenarios allowed per user.
            alert(response.data.value.message);
          }
      })();        
    }

    onMounted(() => {
      (async () => {
        var heItems = JSON.parse(JSON.stringify(store.state.analysis.healthEffects));
        var heItemIds = "";
        var healthEffectsNamesList = "";

        console.log("----------");
        console.log(heItems);
        console.log("----------");

        for (var i = 0; i < heItems.length; i++) {
          //console.log(heItems[i].healthEffectId);
          heItemIds = heItemIds + heItems[i].healthEffectId + ",";
          healthEffectsNamesList =
            healthEffectsNamesList + heItems[i].healthEffectName + ", ";
        }

        healthEffectsNames.value = healthEffectsNamesList.substring(
          0,
          healthEffectsNamesList.length - 2
        );
      })();
    });

    return {
      pollutantId,
      pollutantFriendlyName,
      prePolicyAirQualityId,
      prePolicyAirQualityName,
      postPolicyAirQualityId,
      postPolicyAirQualityName,
      incidenceId,
      incidenceName,
      populationDatasetId,
      populationDatasetName,
      healthEffectsIds,
      healthEffectsNames,
      healthImpactFunctions,
      valuationsForHealthImpactFunctionGroups,
      valuationFunctionCount,
      totalTaskCount,
      submitTask,
      submitTemplate,
      saveTemplate,
      taskName,
      templateName,
      errorMessage,
    };
  },
});
</script>

<style lang="scss" scoped>
.save-template-button,
.submit-task-button {
  margin-left: 25px;
  padding-top: 2px;
}

.back-to-analysis-button {
  margin-left: 0px;
  padding-top: 0px;
  padding-bottom: 25px;
}

.enter-template-row {
  margin-top: 10px;
}
.choices-card {
  width: 700px;
  margin-bottom: 50px;
}

.choices {
  max-width: 700px;
  padding: 5px;
}

.title {
  width: 200px;
  font-weight: bold;
}

.value {
  width: 450px;
}

.even {
  background-color: #eee;
}

.postPolicyList {
  list-style-type: none;
  overflow-wrap: break-word;
}

.prePolicyList {
  list-style-type: none;
  overflow-wrap: break-word;
  float: right;
}

</style>
