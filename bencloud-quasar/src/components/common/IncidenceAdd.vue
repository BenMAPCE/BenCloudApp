<template>
  <q-btn no-caps push color="primary" ref="btn" @click="alert">
    ADD INCIDENCE DATASET</q-btn
  >
</template>

<script>
import { defineComponent, ref, reactive } from "vue";
import { useQuasar, date } from "quasar";
import IncidenceUploadForm from "./IncidenceUploadForm.vue";
import { onMounted, watch } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "IncidenceAdd",
  components: {},

  setup() {
    const $q = useQuasar();
    const store = useStore();

    function alert() {
      $q.dialog({
        component: IncidenceUploadForm,
        parent: this,
        persistent: true,
      })
        .onOk(() => {
          console.log("Upload Incidence OK");
          store.commit("incidence/updateIncidenceAddedDate", new Date());
        })
        .onCancel(() => {
          // console.log('Cancel')
        })
        .onDismiss(() => {
          // console.log('I am triggered on both OK and Cancel')
        });
    }
  

    onMounted(() => {
    });

    return {
      alert
    };
  },
});
</script>

<style lang="scss" scoped></style>