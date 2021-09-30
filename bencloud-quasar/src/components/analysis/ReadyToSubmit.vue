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
          <div>{{ this.prePolicyAirQualityName }}</div>
        </div>
        <div class="row odd choices">
          <div class="title">Post-Policy</div>
          <div>{{ this.postPolicyAirQualityName }}</div>
        </div>
        <div class="row even choices">
          <div class="title">Population Dataset</div>
          <div>{{ this.populationDatasetName }}</div>
        </div>
        <div class="row odd choices">
          <div class="title">Year</div>
          <div>{{ this.populationYear }}</div>
        </div>
        <div class="row even choices">
          <div class="title">Incidence</div>
          <div>{{ this.incidenceName }}</div>
        </div>
        <div class="row odd choices">
          <div class="title">Health Effects</div>
          <div>{{ this.healthEffectsNames }}</div>
        </div>
        <div class="row even choices">
          <div class="title">Health Impact Functions</div>
          <div>{{ this.healthImpactFunctions.length }}</div>
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
          @click="saveTemplate()"
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

import { buildHifTaskJSON, submitHifTask } from "../../composables/analysis/hif-task";
import { buildValuationTaskJSON } from "../../composables/analysis/valuation-task";
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
    const populationYear = store.state.analysis.populationYear;
    const valuationsForHealthImpactFunctionGroups =
      store.state.analysis.valuationsForHealthImpactFunctionGroups;

    const healthEffects = store.state.analysis.healthEffects;

    const healthEffectsIds = ref([]);
    const healthEffectsNames = ref("");

    const healthImpactFunctions = store.state.analysis.healthImpactFunctions;

    const taskName = ref("");
    const templateName = ref("");
    const errorMessage = ref("");

    var hifTaskId = null;

    watch(
      () => hifTaskId,
      (currentHifTaskId, prevHifTaskId) => {
        if (currentHifTaskId != prevHifTaskId) {
          console.log("yes... " + currentHifTaskId);
        }
      }
    );

    function saveTemplate() {
      var templateName = "";
    }

    function submitTask() {
      var heItems = JSON.parse(JSON.stringify(healthEffects));
      var heItemIds = "";
      var healthEffectsNamesList = "";

      for (var i = 0; i < heItems.length; i++) {
        console.log(heItems[i].healthEffectId);
        heItemIds = heItemIds + heItems[i].healthEffectId + ",";
        healthEffectsNamesList =
          healthEffectsNamesList + heItems[i].healthEffectName + ", ";
      }

      healthEffectsIds.value = heItemIds.substring(0, heItemIds.length - 1);
      healthEffectsNames.value = healthEffectsNamesList.substring(
        0,
        healthEffectsNamesList.length - 2
      );

      var hifTaskJSON = buildHifTaskJSON(taskName.value, store);
      console.log(JSON.stringify(hifTaskJSON));

      hifTaskId = submitHifTask(hifTaskJSON, store).fetch();

      $q.dialog({
        component: TaskSubmittedDialog,
        parent: this,
        persistent: true,
        componentProps: {
          taskName: taskName,
        },
      })
        .onOk(() => {
          console.log("OK");
          //taskName.value = ""
        })
        .onCancel(() => {
          // console.log('Cancel')
        })
        .onDismiss(() => {
          console.log("go to view tasks");
          this.$router.replace("/datacenter/manage-tasks");
          // console.log('I am triggered on both OK and Cancel')
        });
    }

    onMounted(() => {
      (async () => {
        var heItems = JSON.parse(JSON.stringify(store.state.analysis.healthEffects));
        var heItemIds = "";
        var healthEffectsNamesList = "";

        for (var i = 0; i < heItems.length; i++) {
          console.log(heItems[i].healthEffectId);
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
      populationYear,
      healthEffectsIds,
      healthEffectsNames,
      healthImpactFunctions,
      valuationsForHealthImpactFunctionGroups,
      submitTask,
      saveTemplate,
      taskName,
      templateName,
      errorMessage,
    };
  },
});
</script>

<style lang="scss">
.save-template-button,
.submit-task-button {
  margin-left: 25px;
  padding-top: 2px;
}

.enter-template-row {
  margin-top: 10px;
}
.choices-card {
  width: 500px;
  margin-bottom: 50px;
}

.choices {
  max-width: 600px;
  padding: 5px;
}

.title {
  width: 200px;
}

.value {
  width: 250px;
}

.even {
  background-color: #eee;
}
</style>