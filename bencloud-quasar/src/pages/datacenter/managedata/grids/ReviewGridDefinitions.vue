<template>
  <q-page>
    <div class="col title-message">
      Review Grid Definitions
    </div>
    <div class="q-pa-md q-gutter-sm split-layout">
      <div class="left-pane">
        <div class="row q-mb-md">
          <div class="col">
            <Suspense></Suspense>
          </div>
          <div class="col" v-if="isAdmin">
            <q-toggle
              size="lg"
              v-model="showAll"
              color="blue"
              label="See Grid Definitions of all users"
            />
          </div>
          <div class="col">
            <GridDefinitionAdd />
          </div>
        </div>
        <div class="q-pa-md">

          <GridDefinitions />
        </div>
      </div>


      <div class="right-pane">
        <div class="q-pa-md">
          <GridMap />
        </div>
      </div>
    </div>
  </q-page>
</template>

  
  <script>
  import { defineComponent } from "vue";


  import { ref, reactive } from "vue";
  import { watch, onBeforeMount } from "vue";
  
  // import GridDefinitionAdd from "../../../../components/common/GridDefinitionAdd.vue";
  import GridDefinitions from "../../../../components/datacenter/grids/GridDefinitions.vue";
  import GridTabs from "src/components/datacenter/grids/GridTabs.vue";
  import GridDefinitionAdd from "src/components/common/GridDefinitionAdd.vue";
  import GridMap from "src/components/datacenter/grids/GridMap.vue";

  
  import { useStore } from "vuex";
  import { isAdmin } from "../../../../boot/auth.js";
  
  export const showAll = ref(false);
  
  export default defineComponent({
    model: ref(null),
    name: "ReviewGrids",
    components: {
      GridDefinitions,
      GridDefinitionAdd,
      GridMap,
  },
    setup(props, context) {
      const store = useStore();

      
      onBeforeMount(() => {});
      return {
        showAll,
        isAdmin
      };
    },
  });
 
  
  </script>
  
  <style lang="scss" scoped>
  .title-message {
    padding: 25px 20px 10px 20px;
    font-size: 18px;
  }
  .split-layout {
  display: flex;
  height: auto;
}


.left-pane {
  width: 40%; 
  padding: 10px;
  border-right: 1px solid #ddd;
}

.right-pane {
  width: 60%; 
  padding: 10px;
  position: relative;
}

@media (max-width: 940px) {
  .split-layout{
    flex-direction: column;
  }
  .left-pane, .right-pane {
    width: 100%; 
    border-right: none; 
  }

}


  </style>
  
