<template>
  <q-select
    square
    dense
    outlined
    v-model="pollutantValue"
    :options="options"
    option-value="id"
    option-label="friendly_name"
    class="pollutant-options"
    emit
    map-options
    label="Pollutant"
  />
</template>

<script>
import { defineComponent } from "vue";
import { ref, provide } from "vue";
import { useStore } from "vuex";
import { loadPollutants } from "../../../composables/pollutants";

import { watch, onBeforeMount } from "vue";

export default defineComponent({
  model: ref(null),
  name: "Pollutants",

  props: {
    updateState: {
      type: Boolean,
      default: false,
    },
  },

    // setup (props, context) {
    //     const customChange = (event) => {
    //         context.emit("customChange", event.target.value)
    //     }
    //     return {
    //         customChange
    //     }
    // }

  setup(props, { emit }) {
    const store = useStore();
    const pollutantValue = ref(null);
    const options = ref([]);

    watch(
      () => pollutantValue.value,
      (pollutantValue, prevPollutantValue) => {
        if (pollutantValue != prevPollutantValue) {
          changePollutantValue(pollutantValue);
        }
      }
    );

    function changePollutantValue(value) {
      if (props.updateState) {
        store.commit("airquality/updatePollutantId", value.id);
        store.commit("airquality/updatePollutantFriendlyName", value.friendly_name);
      }
      emit("changePollutantValue", value);
    }

    onBeforeMount(() => {
      if (props.updateState) {
        store.commit("airquality/updatePollutantId", 0);
        store.commit("airquality/updateAirQualityLayerId", 0);
      }

      (async () => {
        const response = await loadPollutants().fetch();
        options.value = JSON.parse(JSON.stringify(response.data.value));
      })();
    });

    return {
      options,
      pollutantValue,
    };
  },
});
</script>
