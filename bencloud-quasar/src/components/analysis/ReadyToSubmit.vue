<template>
	  <p>Review And Submit</p>
    <p>----------------</p>
    <p>{{ this.pollutantId}} -  {{ this.pollutantFriendlyName}} </p>
    <p>PrePolicy: {{ this.prePolicyAirQualityId }} -  {{ this.prePolicyAirQualityName }} </p>
    <p>PostPolicy: {{ this.postPolicyAirQualityId }} -  {{ this.postPolicyAirQualityName }} </p>
    <p>Pouplation Dataset: {{ this.populationDatasetId}} -  {{ this.populationDatasetName }} </p>
    <p>Year: {{ this.populationYear}} </p>
    <p>Incidence: {{ this.incidenceId}} -  {{ this.incidenceName }} </p>
    <p>Health Effects: {{ this.healthEffectsNames}} </p>
    <p>Health Effects Ids: {{ this.healthEffectsIds }} </p>
    <p>Health Impact Functions: {{ this.healthImpactFunctions.value }} </p>

   <q-card class="my-card">
      <q-form @submit="submitTask()" class="q-gutter-md">
 
        <q-input
          filled
          dense
          v-model="taskName"
          label="Task Name"
          hint=""
          lazy-rules
          :rules="[(val) => (val && val.length > 0) || 'Please enter a Task Name']"
        />

        <div class="row justify-center">
          <q-card-actions>
            <q-btn :disabled="taskName.trim() == ''" color="primary" label="Submit Task" @click="submitTask()" />
          </q-card-actions>
        </div>
      </q-form>

      <q-card-section
      class="error-card"
      v-if="this.errorMessage != ''"
      >
      {{ this.errorMessage }}
      </q-card-section>
    </q-card>
</template>

<script>
import { defineComponent } from "vue";
import { ref, onBeforeMount, onMounted, watch } from "vue";
import { useStore } from "vuex";

import { buildHifTaskJSON, submitHifTask } from "../../composables/hif-task";
import { buildValuationTaskJSON } from "../../composables/valuation-task";

export default defineComponent({
  model: ref(null),
  name: "ReadyToSubmit",

  setup(props) {
    
    const store = useStore();
    const pollutantValue = ref(null);
    const options = ref([]);

    const pollutantId = store.state.analysis.pollutantId;
    const pollutantFriendlyName = store.state.analysis.pollutantFriendlyName;
    const prePolicyAirQualityId = store.state.analysis.prePolicyAirQualityId
    const prePolicyAirQualityName = store.state.analysis.prePolicyAirQualityName
    const postPolicyAirQualityId = store.state.analysis.postPolicyAirQualityId
    const postPolicyAirQualityName = store.state.analysis.postPolicyAirQualityName

    const incidenceId = store.state.analysis.incidenceId
    const incidenceName = store.state.analysis.incidenceName
    const populationDatasetId = store.state.analysis.populationDatasetId
    const populationDatasetName = store.state.analysis.populationDatasetName
    const populationYear = store.state.analysis.populationYear
    const valuationsForHealthImpactFunctionGroups = store.state.analysis.valuationsForHealthImpactFunctionGroups

    const healthEffects = store.state.analysis.healthEffects

    const healthEffectsIds = ref([])
    const healthEffectsNames = ref("")

    const healthImpactFunctions = store.state.analysis.healthImpactFunctions

    const taskName = ref("");
    const errorMessage = ref("");

    var hifTaskId = null;

    watch(
      () => hifTaskId,
      (currentHifTaskId, prevHifTaskId) => {
        if (currentHifTaskId != prevHifTaskId) {v
          console.log("yes... " + currentHifTaskId)
        }
      }
    );

    function submitTask() {

       var heItems = JSON.parse(JSON.stringify(healthEffects))
        var heItemIds = ""
        var healthEffectsNamesList = ""

        for (var i = 0; i < heItems.length; i++) {
            console.log(heItems[i].healthEffectId)
            heItemIds = heItemIds + heItems[i].healthEffectId + ","
            healthEffectsNamesList = healthEffectsNamesList + heItems[i].healthEffectName + ", "
        }
        
        healthEffectsIds.value = heItemIds.substring(0, heItemIds.length - 1);
        healthEffectsNames.value = healthEffectsNamesList.substring(0, healthEffectsNamesList.length - 2)
 
        var hifTaskJSON = buildHifTaskJSON(taskName.value, store);
        console.log(JSON.stringify(hifTaskJSON));
        hifTaskId = submitHifTask(hifTaskJSON, store).fetch();

    }

    onMounted(() => {
      (async () => {
        // console.log("ready to submit");
        
        // var heItems = JSON.parse(JSON.stringify(store.state.analysis.healthEffects))
        // var heItemIds = ""
        // var healthEffectsNamesList = ""

        // for (var i = 0; i < heItems.length; i++) {
        //     console.log(heItems[i].healthEffectId)
        //     heItemIds = heItemIds + heItems[i].healthEffectId + ","
        //     healthEffectsNamesList = healthEffectsNamesList + heItems[i].healthEffectName + ", "
        // }
        
        // healthEffectsIds.value = heItemIds.substring(0, heItemIds.length - 1);
        // healthEffectsNames.value = healthEffectsNamesList.substring(0, healthEffectsNamesList.length - 2)
 
        // var hifTaskJSON = buildHifTaskJSON("Task 001 HIF");
        // console.log(JSON.stringify(hifTaskJSON));
        // //hifTaskId = await submitHifTask(hifTaskJSON).fetch();
        
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
      submitTask,
      taskName,
      errorMessage
    }
  }
  
});
</script>

<style>
.my-card {
  width: 500px;
}

</style>