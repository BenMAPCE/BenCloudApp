<template>
  <q-btn push color="primary" ref="btn" @click="alert">
    Add an Air Quality Layer</q-btn
  >
</template>

<script>
import { defineComponent, ref } from "vue";
import { useQuasar } from "quasar";
import AirQualityUploadForm from "./AQUploadForm.vue";

export default defineComponent({
  model: ref(null),
  name: "AirQualityAdd",
  components: {},

  setup() {
    const $q = useQuasar();

    function alert() {
      $q.dialog({
        component: AirQualityUploadForm,
        parent: this,
        persistent: true,
        componentProps: {
          text: "something",
          // ...more..props...
        },
      })
        .onOk(() => {
          // console.log('OK')
        })
        .onCancel(() => {
          // console.log('Cancel')
        })
        .onDismiss(() => {
          // console.log('I am triggered on both OK and Cancel')
        });
    }

    return {
      alert,
    };
  },
});
</script>

<style lang="scss" scoped></style>
