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

</template>

<script>
import { defineComponent } from "vue";
import { ref, onBeforeMount } from "vue";
import { useStore } from "vuex";

import { buildHifTask } from "../../composables/hif-task";

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

    const healthEffectsIds = ref([])
    const healthEffectsNames = ref("")

    const healthImpactFunctions = store.state.analysis.healthImpactFunctions

    onBeforeMount(() => {
      (async () => {
        console.log("ready to submit");
        
        var heItems = JSON.parse(JSON.stringify(store.state.analysis.healthEffects))
        var heItemIds = ""
        var healthEffectsNamesList = ""

        for (var i = 0; i < heItems.length; i++) {
            console.log(heItems[i].healthEffectId)
            heItemIds = heItemIds + heItems[i].healthEffectId + ","
            healthEffectsNamesList = healthEffectsNamesList + heItems[i].healthEffectName + ", "
        }
        
        healthEffectsIds.value = heItemIds.substring(0, heItemIds.length - 1);
        console.log(healthEffectsIds.value)
        //console.log(healthEffectsNamesList)
        healthEffectsNames.value = healthEffectsNamesList.substring(0, healthEffectsNamesList.length - 2)
        console.log(healthEffectsNames)

        console.log(store.state.analysis.healthImpactFunctions)

        var xyz = await buildHifTask().fetch();
        console.log("-------------------------------------")
        console.log(xyz)
        console.log("-------------------------------------")

        
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
      healthImpactFunctions
    }
  }
  
});
</script>