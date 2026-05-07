<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <div class="row">
        <div class="col"> Review Health Impact Functions</div>
      </div>
      <div class="row">
        <div class="col">
          <div style="padding-bottom: 15px;">
            <Suspense>
              <Pollutants updateState isHif></Pollutants>
            </Suspense>
          </div>
          <div>
            <Suspense>
              <HifGroups updateState></HifGroups>
            </Suspense>
          </div>
          
        </div>

      <div class="col" v-if="currentPollutantId && currentHifGroupId && isAdmin">
        <q-toggle
          size="lg"
          v-model="showAll"
          color="blue"
          label="See health impact functions of all users"
        />
      </div>

      <div class="col">
          <HifAdd
            :pollutantId=selectedPollutantId 
            :pollutantFriendlyName=selectedPollutantFriendlyName
          >
            ></HifAdd
          >
        </div>
      <div class="col" v-if="currentPollutantId && currentHifGroupId && currentHifGroupId > 6">
          <q-btn no-caps push color="primary" ref="btn" @click="deleteHifGroup(selectedHifGroupName)">
          DELETE HEALTH IMPACT FUNCTION GROUP</q-btn>
      </div>
    </div>

    <div class="q-pa-md">
        <HifDatasets></HifDatasets>
      </div>
      

  </div>
  </q-page>
</template>

<script>
import { defineComponent } from "vue";
import { ref, reactive } from "vue";
import { watch, onBeforeMount } from "vue";
import axios from "axios";

import Pollutants from "../../../../components/common/Pollutants.vue";
import HifGroups from "../../../../components/common/HifGroups.vue";
import HifAdd from "../../../../components/common/HifAdd.vue";
import HifDatasets from "../../../../components/datacenter/HIF/HifDatasets.vue";

import { useStore } from "vuex";
import { isAdmin } from "../../../../boot/auth.js";

export const showAll = ref(false);

export default defineComponent({
  model: ref(null),
  name: "ReviewHif",
  components: {
    HifAdd,
    HifDatasets,
    Pollutants,
    HifGroups
},
setup(props, context) {
  const store = useStore();
    const currentPollutantId = ref(0);
    const selectedPollutantId = reactive(ref(0));
    const selectedPollutantFriendlyName = ref("OOPS");

    const currentHifGroupId = ref(0);
    const selectedHifGroupId = reactive(ref(0));
    const selectedHifGroupName = ref("OOPS");


    watch(
      () => store.state.hif.pollutantId,
      (pollutantId, prevPollutantId) => {
        currentPollutantId.value = pollutantId;
        console.log("------- currentPollutantId: " + currentPollutantId.value);
        selectedPollutantId.value = store.state.hif.pollutantId
        selectedPollutantFriendlyName.value = store.state.hif.pollutantFriendlyName
        console.log("------- pollutantId: " + store.state.hif.pollutantId);
        console.log("------- pollutantId: " + pollutantId);
        console.log("------- pollutantFriendlyName: " + store.state.hif.pollutantFriendlyName);
      }
    );

    watch(
      () => store.state.hif.hifGroupId,
      (hifGroupId, prevHifGroupId) => {
        currentHifGroupId.value = hifGroupId;
        console.log("------- currentHifGroupId: " + currentHifGroupId.value);
        selectedHifGroupId.value = store.state.hif.hifGroupId
        selectedHifGroupName.value = store.state.hif.hifGroupName
        console.log("------- hifGroupId: " + store.state.hif.hifGroupId);
        console.log("------- hifGroupId: " + hifGroupId);
        console.log("------- hifGroupFriendlyName: " + store.state.hif.hifGroupName);
      }
    );

    function onChangePollutantValue(value) {
      console.log("!!!!!!!!!!!!!!!!!!");
    }

    function deleteHifGroup(groupName) {
      // Prompt user to confirm HIF Group deletion
      if(currentHifGroupShareScope.value == 1 && !confirm("This HIF Group is shared with all users. Are you sure you wish to permanently delete the HIF Group: " + groupName + "?")){
        return;
      }
      if(confirm((currentHifGroupShareScope.value == 1 ? "This HIF Group is shared with all users. Are you sure?" : "Are you sure you wish to permanently delete the HIF Group: " + groupName + "?"))){
        // Delete HIF Group, reload if successful, alert the user if unsuccessful       
        axios
          .delete(process.env.API_SERVER + "/api/health-impact-function-groups/" + selectedHifGroupId.value, 
            {validateStatus: function (status) {
              return status < 500;
            }}
          )
          .then((response) => {
            if(response.status === 204) {
              console.log("Successfully deleted HIF Group: " + groupName);
              var oldValue =  store.state.hif.hifGroupForceReloadValue
              var newValue = oldValue + 1;
              store.commit("hif/updateHifGroupId", 0);
              store.commit("hif/updateHifGroupName", "");
              store.commit("hif/updateHifGroupForceReloadValue", newValue)

            } else if(response.status === 403){
              console.log("Forbidden action on HIF Group: " + groupName);
              this.$q.notify({
                group: false, // required to be updateable
                type: 'negative',
                timeout: 6000, 
                color: "red",
                spinner: false, // we reset the spinner setting so the icon can be displayed
                position: "top",
                message: response.data.message,
              });
              this.$emit('ok')
            } else {
              this.$q.notify({
                group: false, // required to be updateable
                type: 'negative',
                timeout: 6000, 
                color: "red",
                spinner: false, // we reset the spinner setting so the icon can be displayed
                position: "top",
                message: "Unknown error: " + response.status,
              });
              this.$emit('ok')
            }
          })
      }
    }

  onBeforeMount(() => {
    store.commit("hif/updatePollutantId", 0);
    store.commit("hif/updatePollutantFriendlyName", "");
  });

  return {
    currentPollutantId,
    selectedPollutantId,
    selectedPollutantFriendlyName,
    currentHifGroupId,
    selectedHifGroupId,
    selectedHifGroupName,
    showAll,
    onChangePollutantValue,
    deleteHifGroup,
    isAdmin
  };
},
});
</script>

<style lang="scss" scoped>
.pollutant-options {
  width: 250px;
}

.hif-group-options {
  width: 250px;
}
</style>
