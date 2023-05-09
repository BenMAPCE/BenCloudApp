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
          <div class="title">Population Groups</div>
          <div>{{ this.populationGroups }}</div>
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
      <q-form @submit="saveTemplate()" class="q-gutter-md">
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

import { createTemplate, saveTemplate } from "../../composables/templates/templates";
// import { buildHifTaskJSON, submitHifTask } from "../../composables/exposure/hif-task";
// import { buildValuationTaskJSON } from "../../composables/exposure/valuation-task";
// import TaskSubmittedDialog from "./TaskSubmittedDialog.vue";
import SubmissionErrorDialog from "./SubmissionErrorDialog.vue";

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
    const populationGroups = store.state.exposure.populationGroupNames.toString().replaceAll(",", ", ");

    const taskName = ref("");
    const templateName = ref("");
    const errorMessage = ref("");

    var hifTaskId = null;
    var valuationFunctionCount = null;
    var totalTaskCount = null;

    watch(
      () => hifTaskId,
      (currentHifTaskId, prevHifTaskId) => {
        if (currentHifTaskId != prevHifTaskId) {
          // console.log("yes... " + currentHifTaskId);
        }
      }
    );

    function submitTemplate() {

      $q.dialog({
        component: SubmissionErrorDialog,
        parent: this,
        persistent: true,
        componentProps: {},
      })
        .onOk(() => {
          //taskName.value = ""
        })

      // var template = createTemplate(taskName.value, store);

      // const templateNotification = $q.notify({
      //   group: false, // required to be updatable
      //   timeout: 0, // we want to be in control when it gets dismissed
      //   spinner: true,
      //   position: "top",
      //   message: "Saving Template...",
      // });

      // (async () => {
      //   const response = await saveTemplate(
      //     templateName.value,
      //     "v1",
      //     template,
      //     store
      //   ).fetch();

      //   templateName.value = "";

      //   if(!!response.data.value.message) {
      //     // if there is an issue, display the error message
      //     templateNotification({
      //       spinner: false, // we reset the spinner setting so the icon can be displayed
      //       message: response.data.value.message,
      //       color: "red",
      //       timeout: 4000, // we will timeout it in 4 seconds
      //     });
      //   } else {
      //     // if the template was saved
      //     templateNotification({
      //       spinner: false, // we reset the spinner setting so the icon can be displayed
      //       message: "Template Saved!",
      //       color: "green",
      //       timeout: 2000, // we will timeout it in 2 seconds
      //     });
      //   }
      // })();
    }

    //console.log("----- healthImpactFunctions -----");
    //console.log(healthImpactFunctions);

    function submitTask() {

      $q.dialog({
        component: SubmissionErrorDialog,
        parent: this,
        persistent: true,
        componentProps: {},
      })
        .onOk(() => {
          //taskName.value = ""
        })


    //   var heItems = JSON.parse(JSON.stringify(healthEffects));
    //   var heItemIds = "";
    //   var healthEffectsNamesList = "";

    //   for (var i = 0; i < heItems.length; i++) {
    //     // console.log(heItems[i].healthEffectId);
    //     heItemIds = heItemIds + heItems[i].healthEffectId + ",";
    //     healthEffectsNamesList =
    //       healthEffectsNamesList + heItems[i].healthEffectName + ", ";
    //   }

    //   healthEffectsIds.value = heItemIds.substring(0, heItemIds.length - 1);
    //   healthEffectsNames.value = healthEffectsNamesList.substring(
    //     0,
    //     healthEffectsNamesList.length - 2
    //   );

    //   var hifTaskJSON = JSON.parse(JSON.stringify(store.state.exposure.batchTaskObject));
    //   hifTaskJSON['name'] = taskName.value;
    //   hifTaskJSON['gridDefinitionId'] = store.state.exposure.aggregationScale;
    //   store.commit("exposure/updateBatchTaskObject", hifTaskJSON);
    //   console.log("----- Batch task configuration -----")
    //   console.log(hifTaskJSON);

    //   // var template = createTemplate(taskName.value, store);
    //   // console.log("----- template -----");
    //   // console.log(template);
    //   // console.log("--------------------");

    //   hifTaskId = submitHifTask(hifTaskJSON, store).fetch();

    //   $q.dialog({
    //     component: TaskSubmittedDialog,
    //     parent: this,
    //     persistent: true,
    //     componentProps: {
    //       taskName: taskName,
    //     },
    //   })
    //     .onOk(() => {
    //       //taskName.value = ""
    //       this.$router.replace("/datacenter/manage-tasks");
    //     })
    //     .onCancel(() => {
    //       // Sounds backwards, but clicking on the 'OK' button is actually a Cancel since we don't
    //       // want the user to go anywhere (we're cancelling out of the dialog)
    //       // Clear the task name field
    //       taskName.value = "";
    //     });
    }

    onMounted(() => {
      // (async () => {
      //   //var heItems = JSON.parse(JSON.stringify(store.state.exposure.healthEffects));
      //   var heItemIds = "";
      //   var healthEffectsNamesList = "";

      //   console.log("----------");
      //   //.log(heItems);
      //   console.log("----------");

      //   for (var i = 0; i < heItems.length; i++) {
      //     //console.log(heItems[i].healthEffectId);
      //     heItemIds = heItemIds + heItems[i].healthEffectId + ",";
      //     healthEffectsNamesList =
      //       healthEffectsNamesList + heItems[i].healthEffectName + ", ";
      //   }

      //   healthEffectsNames.value = healthEffectsNamesList.substring(
      //     0,
      //     healthEffectsNamesList.length - 2
      //   );
      // })();
    });

    return {
      pollutantId,
      pollutantFriendlyName,
      prePolicyAirQualityId,
      prePolicyAirQualityName,
      postPolicyAirQualityId,
      postPolicyAirQualityName,
      populationGroups,
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
