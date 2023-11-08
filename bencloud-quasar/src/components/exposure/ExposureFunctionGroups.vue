<template>
  <div class="q-pa-md">
    <q-select
      square
      dense
      outlined
      v-model="selectedItems"
      :options="rows"
      option-value="id"
      option-label="name"
      class="exposure-function-group-options"
      emit-value
      map-options
      label="Exposure Function Groups"
    />
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";
import axios from 'axios';

export default defineComponent({
  model: ref(null),
  name: "ExposureFunctionGroups",

  async setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const selectedItems = ref(store.state.exposure.exposureFunctionGroupId);

    watch(
      () => selectedItems.value,
      (currentSelectedItems, prevSelectedItems) => {
        console.log("watch: " + currentSelectedItems + " |" + prevSelectedItems);
        if (currentSelectedItems != prevSelectedItems) {
          var efItems = [];
          var efItem = {};
          var group = rows.value.find((opt) => opt.id === currentSelectedItems)
          efItem = { exposureGroupId: group.id, exposureGroupName: group.name };
          store.commit("exposure/updateExposureFunctionGroup", efItem);
        }
      }
    );

    onBeforeMount(() => {
      (async () => {
        try {
          const result = await axios
            .get(process.env.API_SERVER + "/api/exposure-function-groups")
            .then((response) => {
              rows.value = [];
              console.log(response.data)
              for(var i = 0; i < response.data.length; i++) {
                rows.value.push(response.data[i])
              }
            })
            .catch(error => {
              console.log("error" + error);
            })
        } catch (ex) {
            console.log(ex)
        }

        if (store.state.exposure.exposureFunctionGroupId != null) {
          selectedItems.value = store.state.exposure.exposureFunctionGroupId;
        }
      })();
    });

    return {
      rows,
      selectedItems,
    };
  },
});
</script>

<style lang="scss" scoped>
.aq-pre-policy-scroll-area {
  border: 1px solid black;
  height: 200px;
  max-width: 300px;
}
.exposure-function-group-options {
  width: 300px;
}
</style>
