<template>
    <q-page>
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
        
        <div class="q-pa-md">
          <GridTabs></GridTabs>
        </div>
  
    </div>
    </q-page>
  </template>
  
  <script>
  import { defineComponent } from "vue";
  import { ref, reactive } from "vue";
  import { watch, onBeforeMount } from "vue";
  
  import GridDefinitionAdd from "../../../../components/common/GridDefinitionAdd.vue";
  import GridDefinitions from "../../../../components/datacenter/grids/GridDefinitions.vue";
  import GridTabs from "src/components/datacenter/grids/GridTabs.vue";
  
  import { useStore } from "vuex";
  import { isAdmin } from "../../../../boot/auth.js";
  
  export const showAll = ref(false);
  
  export default defineComponent({
    model: ref(null),
    name: "ReviewGrids",
    components: {
      GridTabs,
      GridDefinitions,
      GridDefinitionAdd
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

  </style>
  