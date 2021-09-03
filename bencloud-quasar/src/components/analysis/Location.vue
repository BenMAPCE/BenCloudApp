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
    <q-tab
      name="individual"
      class="individual"
      label="Search for one or more locations"
    />
  </q-tabs>

  <q-tab-panels v-model="tab" animated>
    <q-tab-panel name="defaults">

      <div class="q-py-xs">
        <q-option-group
          v-model="selectedItem"
          :options="options"
          color="primary"
        ></q-option-group>
      </div>
      
    </q-tab-panel>

    <q-tab-panel name="individual">
      <div class="header-text">Not available at this time.</div>
    </q-tab-panel>
  </q-tab-panels>
</template>
<script>
import { defineComponent } from "vue";
import { ref, watch, onMounted } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "Location",


  setup(props, context) {
    const rows = ref([]);
    const selectedItem = ref(0);
    const store = useStore();

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " |" + prevSelectedItem)
        if (currentSelectedItem != prevSelectedItem) {
          console.log("selectedItem: " + currentSelectedItem)
          //console.log("options: " + options)
          
          //var name = options.find((opt) => opt.value === currentSelectedItem).label;

          store.commit("analysis/updateLocation", 
          { 
            locationId: currentSelectedItem,  
            plocationName: ""
          });
        }
      });

   onMounted(() => {
      console.log("... " + store.state.analysis.locationId);
      if (store.state.analysis.locationId != null) {
        selectedItem.value = store.state.analysis.locationId;
        console.log("- selectedItem: " + selectedItem.value);
      }
    })();

    return {
      tab: ref("defaults"),
      options: [
        { label: 'U.S. National', value: '1' },
        { label: 'Eastern U.S.', value: '2' },
        { label: 'Western U.S.', value: '3'  },
        { label: 'Global (everywhere)', value: '4' }
      ],
      selectedItem,
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
