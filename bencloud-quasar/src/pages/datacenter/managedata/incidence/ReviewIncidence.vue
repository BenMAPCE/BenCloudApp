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
          label="See incidence datasets of all users"
        />
      </div>

        <div class="col">
          <IncidenceAdd
          >
            ></IncidenceAdd
          >
        </div>
      </div>

      <div class="q-pa-md">
        <IncidenceDatasets></IncidenceDatasets>
      </div>
      
      <div class="q-pa-md">
        <IncidenceTabs></IncidenceTabs>
      </div>

  </div>
  </q-page>
</template>

<script>
import { defineComponent } from "vue";
import { ref, reactive } from "vue";
import { watch, onBeforeMount } from "vue";

import IncidenceAdd from "../../../../components/common/IncidenceAdd.vue";
import IncidenceDatasets from "../../../../components/datacenter/incidence/IncidenceDatasets.vue";
import IncidenceTabs from "src/components/datacenter/incidence/IncidenceTabs.vue";

import { useStore } from "vuex";
import { isAdmin } from "../../../../boot/auth.js";

export const showAll = ref(false);

export default defineComponent({
  model: ref(null),
  name: "ReviewIncidence",
  components: {
    IncidenceTabs,
    IncidenceDatasets,
    IncidenceAdd
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
