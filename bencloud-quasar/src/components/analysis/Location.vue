<template>
  <q-tabs
    v-model="tab"
    dense
    class="text-grey"
    active-color="primary"
    indicator-color="primary"
    align="justify"
    narrow-indicator
  >
    <q-tab name="defaults" class="defaults" label="Select from a list of defaults" />
<!--    <q-tab-->
<!--      name="individual"-->
<!--      class="individual"-->
<!--      label="Search for one or more locations"-->
<!--    />-->
  </q-tabs>

  <q-tab-panels v-model="tab" animated>
    <q-tab-panel name="defaults">
      <div class="q-py-xs">
        <q-option-group
          v-model="limitToGridYN"
          :options="optionsLimitYN"
          color="primary"
        ></q-option-group>
      </div>
      
      <div class="q-py-xs q-ml-md inline-block" v-if="limitToGridYN === '1'">
        <div class="text-caption text-grey q-mb-xs">
          Choose a grid that defines the boundaries of that area
        </div>
        <div class="row">
          <div class="col-12">
            <GridDefinitions 
            v-model= "limitToGridId"
            @changeGridValue="val=> limitToGridId = val"
            ></GridDefinitions>
          </div>
        </div>
        <div class="text-caption text-grey q-mb-xs">
          Not seeing your grid in the list? <router-link to="/datacenter/review-grids">Upload it now</router-link> and return here after it is processed.
        </div>
      </div>

    </q-tab-panel>

<!--    <q-tab-panel name="individual">-->
<!--      <div class="header-text">Not available at this time.</div>-->
<!--    </q-tab-panel>-->
  </q-tab-panels>
</template>
<script>
import { defineComponent } from "vue";
import { ref, watch, onMounted} from "vue";
import { useStore } from "vuex";
import GridDefinitions from "./GridDefinitions.vue";

export default defineComponent({
  model: ref(null),
  name: "Location",
  components: {
    GridDefinitions,
  },

  setup(props, context) {
    const rows = ref([]);
    const limitToGridId = ref(null);
    const store = useStore();
    const limitToGridYN = ref('0');

    watch(
      [
        () => limitToGridId.value, 
        () => limitToGridYN.value
      ],      
      (
        [currentlimitToGridId, currentlimitToGridYN], 
        [prevlimitToGridId, prevlimitToGridYN]
      ) => {
        console.log("watch: current | previous limitToGridId" + currentlimitToGridId + " |" + prevlimitToGridId);
        if (currentlimitToGridId != prevlimitToGridId || currentlimitToGridYN != prevlimitToGridYN) {
          console.log("limitToGridId: " + currentlimitToGridId + "|" + currentlimitToGridYN)
          if(currentlimitToGridYN=="0"){
            limitToGridId.value=null;
          }
          store.commit("analysis/updateLocation",
          {
            limitToGridId: currentlimitToGridId,
            locationName: ""
          });

        }
      });

   onMounted(() => {
    console.log("limitToGridId value in state: " + store.state.analysis.limitToGridId);
    //console.log("... " + store.state.analysis.limitToGridId + "|" + store.state.analysis.limitToGridId);
      if (store.state.analysis.limitToGridId != null) {
        limitToGridId.value = Number(store.state.analysis.limitToGridId);
        limitToGridYN.value = "1";
        console.log('Dropdown initialized with limitToGridId:', limitToGridId.value)
      }
    })();

    return {
      tab: ref("defaults"),
      optionsLimitYN: [
        { label: 'Everywhere I have air quality data', value: '0' },
        { label: 'Limit my analysis to a pre-defined area', value: '1'}
      ],
      limitToGridId,
      limitToGridYN,
      rows
    };
  },
});
</script>

<style lang="scss" scoped>
.aq-pre-policy-scroll-area {
  border: 1px solid black;
  height: 200px;
  max-width: 300px;
}

.defaults,
.individual {
  flex: 0 0 auto;
  align-items: left;
  align-self: left;
}

.header-text {
  font-size: 16px;
  font-weight: 500;
}
</style>
