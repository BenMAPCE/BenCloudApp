<template>
  <q-btn no-caps push color="primary" ref="btn" @click="alert">
    ADD {{ pollutantFriendlyName.includes("μm") ? pollutantFriendlyName.toUpperCase().replace("ΜM","μm") : pollutantFriendlyName.toUpperCase()}} AIR QUALITY LAYER</q-btn
  >
</template>

<script>
import { defineComponent, ref, reactive } from "vue";
import { useQuasar, date } from "quasar";
import AirQualityUploadForm from "./AirQualityUploadForm.vue";
import { onMounted, watch } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "AirQualityAdd",
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
        component: AirQualityUploadForm,
        parent: this,
        persistent: true,
        componentProps: {
          pollutantFriendlyName: props.pollutantFriendlyName,
          pollutantId: props.pollutantId,
        },
      })
        .onOk(() => {
          console.log("Upload AQ OK");
          store.commit("airquality/updateAirQualityLayerAddedDate", new Date());
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