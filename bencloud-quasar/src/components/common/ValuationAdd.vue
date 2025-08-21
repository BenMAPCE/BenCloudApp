<template>
  <q-btn no-caps push color="primary" ref="btn" @click="alert">
    ADD VALUATION FUNCTION</q-btn
  >
</template>

<script>
import { defineComponent, ref, reactive } from "vue";
import { useQuasar, date } from "quasar";
import ValuationUploadForm from "./ValuationUploadForm.vue";
import { onMounted, watch } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "ValuationAdd",
  components: {},

  props: {
    pollutantId: {
      type: Number,
      default: 0,
    },
    pollutantFriendlyName: {
      type: String,
      default: "None",
    },

  },

  setup(props) {
    const $q = useQuasar();
    const store = useStore();

    function alert() {
      $q.dialog({
        component: ValuationUploadForm,
        parent: this,
        persistent: true,
      })
        .onOk(() => {
          console.log("Upload Valuation OK");
          store.commit("hif/updateHifAddedDate", new Date());
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