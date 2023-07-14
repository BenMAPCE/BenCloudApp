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

        <div class="col">
          <IncidenceAdd

          >
            ></IncidenceAdd
          >
        </div>
      </div>

      <div class="q-pa-md">
        <IncidenceDatasets></IncidenceDatasets>

      <div class="q-pa-md">
      </div>
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
import { useStore } from "vuex";
import { isAdmin } from "../../../../boot/auth.js";

export const showAll = ref(false);

export default defineComponent({
  model: ref(null),
  name: "ReviewIncidence",
  components: {
    IncidenceAdd,
    IncidenceDatasets,
},
  setup(props, context) {
    const store = useStore();
  },
});
</script>

<style lang="scss" scoped>
.pollutant-options {
  width: 250px;
}
</style>
