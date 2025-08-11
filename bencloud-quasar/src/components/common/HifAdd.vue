<template>
  <q-btn no-caps push color="primary" ref="btn" @click="alert">
    ADD HIF DATASET</q-btn
  >
</template>

<script>
import { defineComponent, ref, reactive } from "vue";
import { useQuasar, date } from "quasar";
import HifUploadForm from "./HifUploadForm.vue";
import { onMounted, watch } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "HifAdd",
  components: {},

  setup() {
    const $q = useQuasar();
    const store = useStore();

    function alert() {
      $q.dialog({
        component: HifUploadForm,
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