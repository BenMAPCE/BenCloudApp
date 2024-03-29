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
          <div class="title">Exposure Function Group</div>
          <div>{{ this.exposureFunctionGroups }}</div>
        </div>
        <div class="row even choices">
          <div class="title">Reference Population</div>
          <div>Total US Population</div>
        </div>
        <!-- <div class="row even choices">
          <div class="title">Exposure Metric</div>
          <div>{{  }}</div
        </div>
        <div class="row odd choices">
          <div class="title">Exposures Measures</div>
          <div>{{  }}</div>
        </div> -->
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
import { ref, onBeforeMount, onMounted, watch } from "vue";
import { useStore } from "vuex";
import { useQuasar } from "quasar";

import { createExposureTemplate, saveTemplate } from "../../composables/templates/templates";
import { buildExposureBatchTask, submitExposureTask } from "../../composables/exposure/exposure-task";
import TaskSubmittedDialog from "./TaskSubmittedDialog.vue";

export default defineComponent({
  model: ref(null),
  name: "ReadyToSubmit",

  setup(props) {
    const store = useStore();

    const $q = useQuasar();

    const pollutantValue = ref(null);
    const options = ref([]);

    const pollutantId = store.state.exposure.pollutantId;
    const pollutantFriendlyName = store.state.exposure.pollutantFriendlyName;
    const prePolicyAirQualityId = store.state.exposure.prePolicyAirQualityId;
    const prePolicyAirQualityName = store.state.exposure.prePolicyAirQualityName;
    const postPolicyAirQualityId = store.state.exposure.postPolicyAirQualityId;
    const postPolicyAirQualityName = store.state.exposure.postPolicyAirQualityName;

    const populationDatasetId = store.state.exposure.populationDatasetId;
    const populationDatasetName = store.state.exposure.populationDatasetName;
    const exposureFunctionGroups = store.state.exposure.exposureFunctionGroupName;

    const taskName = ref("");
    const templateName = ref("");
    const errorMessage = ref("");

    var batchTaskId = null;

    watch(
      () => batchTaskId,
      (currentBatchTaskId, prevBatchTaskId) => {
        if (currentBatchTaskId != prevBatchTaskId) {
          // console.log("yes... " + currentBatchTaskId);
        }
      }
    );

    function submitTemplate() {

      var template = createExposureTemplate(taskName.value, store);

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
          "Exposure Analysis",
          template,
          store,
          templateNotification
        ).fetch();

        templateName.value = "";
      })();
    }


    function submitTask() {

      var batchTaskJSON = JSON.parse(JSON.stringify(store.state.exposure.batchTaskObject));
      batchTaskJSON['name'] = taskName.value;
      store.commit("analysis/updateBatchTaskObject", batchTaskJSON);
      console.log("----- Batch task configuration -----")
      console.log(batchTaskJSON);

      (async () => {
        const response = await submitExposureTask(batchTaskJSON, store).fetch();
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

    onBeforeMount(() => {

      (async () => {
        const response = await buildExposureBatchTask(store).fetch(store);
      })();

    })();

    return {
      pollutantId,
      pollutantFriendlyName,
      prePolicyAirQualityId,
      prePolicyAirQualityName,
      postPolicyAirQualityId,
      postPolicyAirQualityName,
      exposureFunctionGroups,
      populationDatasetId,
      populationDatasetName,
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

.back-to-exposure-button {
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
