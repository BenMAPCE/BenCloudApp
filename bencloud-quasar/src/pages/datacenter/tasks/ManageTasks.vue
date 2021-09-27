<template>
  <ManageTasksTabs></ManageTasksTabs>
</template>

<script>
import { defineComponent } from "vue";
import { ref } from "vue";

import ManageTasksTabs from "../../../components/datacenter/tasks/ManageTasksTabs.vue";

//import AirQualityLayers from "./AirQualityLayers.vue";
//import Pollutants from "./Pollutants.vue";
//import AirQualityAdd from "./AirQualityAdd.vue";


export default defineComponent({
  model: ref(null),
  name: "ManageTasks",
  components: {
    ManageTasksTabs,
//    AirQualityLayers,
//    AirQualityAdd,
//    Pollutants,
  },
});


</script>

<style lang="scss" scoped>
.pollutant-options {
  width: 250px;
}
</style>
