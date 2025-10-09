<template>
  <q-select
    square
    dense
    outlined
    v-model="groupValue"
    :options="options"
    option-value="id"
    option-label="name"
    class="hif-group-options"
    emit
    map-options
    label="Health Impact Function Group"
  />
</template>

<script>
import { defineComponent } from "vue";
import { ref, provide } from "vue";
import { useStore } from "vuex";
import { loadHifGroups } from "../../composables/common/hifGroups";

import { watch, onBeforeMount } from "vue";

export default defineComponent({
  model: ref(null),
  name: "HifGroups",

  props: {
    updateState: {
      type: Boolean,
      default: false,
    },
    fromUploadForm: {
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
    const groupValue = ref(null);
    const options = ref([]);

    watch(
      () => groupValue.value,
      (groupValue, prevGroupValue) => {
        if (groupValue != prevGroupValue && !props.fromUploadForm) {
          changeGroupValue(groupValue);
        }
      }
    );

    //update group dropdown when a new group is added
    watch(
      () => store.state.hif.hifGroupForceReloadValue,
      (groupValue, prevGroupValue) => {
        if (groupValue != prevGroupValue) {
          (async () => {
            const response = await loadHifGroups().fetch();
            options.value = JSON.parse(JSON.stringify(response.data.value));
          })();
        }
      }
    );

    function changeGroupValue(value) {
      store.commit("hif/updateHifGroupId", value.id);
      store.commit("hif/updateHifGroupName", value.name);
      emit("changeGroupValue", value);
    }

    onBeforeMount(() => {
      if(!props.fromUploadForm)
        store.commit("hif/updateHifGroupId", 0);

      (async () => {
        const response = await loadHifGroups().fetch();
        console.log(response);
        options.value = JSON.parse(JSON.stringify(response.data.value));
      })();
    });

    return {
      options,
      groupValue,
    };
  },
});
</script>
