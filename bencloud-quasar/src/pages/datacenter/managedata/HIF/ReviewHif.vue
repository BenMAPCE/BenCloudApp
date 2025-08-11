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
          label="See health impact functions of all users"
        />
      </div>

      <div class="col">
          <HifAdd
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
    HifDatasets
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
