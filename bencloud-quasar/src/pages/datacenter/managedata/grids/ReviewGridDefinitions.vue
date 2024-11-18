<template>
    <q-page>
      <div class="col title-message">
        Review Grid Definitions
      </div>
      <div class="q-pa-md q-gutter-sm">
        <div class="row">
          <div class="col"></div>
        </div>
        <div class="row">
          <div class="col">
            <Suspense>
            </Suspense>
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
            <GridDefinitionAdd
            >
              ></GridDefinitionAdd
            >
          </div>
        </div>
  
        <div class="q-pa-md">
          <GridDefinitions></GridDefinitions>
        </div>
        
        <!-- <div class="q-pa-md">
          <GridTabs></GridTabs>
        </div> -->

        <div class= "q-pa-md">
          <GridMap></GridMap>
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
  import GridMap from "../../../../components/datacenter/grids/GridMap.vue";
  import GridDefinitionAdd from "src/components/common/GridDefinitionAdd.vue";

  
  import { useStore } from "vuex";
  import { isAdmin } from "../../../../boot/auth.js";
  
  export const showAll = ref(false);
  
  export default defineComponent({
    model: ref(null),
    name: "ReviewGrids",
    components: {
      
      GridDefinitions,
      GridDefinitionAdd,
      GridMap
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


  </style>
  