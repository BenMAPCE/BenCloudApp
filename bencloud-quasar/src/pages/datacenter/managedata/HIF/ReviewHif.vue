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

      <div class="col" v-if="currentPollutantId">
          <HifAdd
            :pollutantId=selectedPollutantId 
            :pollutantFriendlyName=selectedPollutantFriendlyName
          >
            ></HifAdd
          >
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

  onBeforeMount(() => {});

  return {
    currentPollutantId,
    selectedPollutantId,
    selectedPollutantFriendlyName,
    currentHifGroupId,
    selectedHifGroupId,
    selectedHifGroupName,
    showAll,
    onChangePollutantValue,
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
