<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <div class="row">
        <div class="col">Review Air Quality</div>
      </div>
      <div class="row">
        <div class="col">
          <Suspense>
            <Pollutants updateState></Pollutants>
          </Suspense>
        </div>

        <div class="col" v-if="currentPollutantId && isAdmin">
          <q-toggle
            size="lg"
            v-model="showAll"
            color="blue"
            label="See AQ surfaces of all users"
          />
        </div>

        <div class="col" v-if="currentPollutantId">
          <AirQualityAdd
            :pollutantId=selectedPollutantId 
            :pollutantFriendlyName=selectedPollutantFriendlyName
          >
            ></AirQualityAdd
          >
        </div>
      </div>

      <div class="q-pa-md">
        <AirQualityLayers></AirQualityLayers>
      </div>

      <div class="q-pa-md">
        <AirQualityTabs></AirQualityTabs>
      </div>
    </div>
  </q-page>
</template>

<script>
import { defineComponent } from "vue";
import { ref, reactive } from "vue";
import { watch, onBeforeMount } from "vue";

import AirQualityTabs from "../../../../components/datacenter/airquality/AirQualityTabs.vue";

import AirQualityLayers from "../../../../components/datacenter/airquality/AirQualityLayers.vue";
import Pollutants from "../../../../components/common/Pollutants.vue";
import AirQualityAdd from "../../../../components/common/AirQualityAdd.vue";
import { useStore } from "vuex";
import { isAdmin } from "../../../../boot/auth.js";

export const showAll = ref(false);

export default defineComponent({
  model: ref(null),
  name: "ReviewAirQuality",
  components: {
    AirQualityTabs,
    AirQualityLayers,
    AirQualityAdd,
    Pollutants,
  },
  setup(props, context) {
    const store = useStore();
    const currentPollutantId = ref(0);
    const selectedPollutantId = reactive(ref(0));
    const selectedPollutantFriendlyName = ref("OOPS");

    watch(
      () => store.state.airquality.pollutantId,
      (pollutantId, prevPollutantId) => {
        currentPollutantId.value = pollutantId;
        console.log("------- currentPollutantId: " + currentPollutantId.value);
        selectedPollutantId.value = store.state.airquality.pollutantId
        selectedPollutantFriendlyName.value = store.state.airquality.pollutantFriendlyName
        console.log("------- pollutantId: " + store.state.airquality.pollutantId);
        console.log("------- pollutantId: " + pollutantId);
        console.log("------- pollutantFriendlyName: " + store.state.airquality.pollutantFriendlyName);
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
      onChangePollutantValue,
      showAll,
      isAdmin
    };
  },
});
</script>

<style lang="scss" scoped>
.pollutant-options {
  width: 250px;
}
</style>
