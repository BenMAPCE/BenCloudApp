<template>
    <q-btn no-caps push color="primary" ref="btn" @click="alert">
      ADD GRID DEFINITION</q-btn
    >
  </template>
  
  <script>
  import { defineComponent, ref, reactive } from "vue";
  import { useQuasar, date } from "quasar";
  import GridDefinitionUploadForm from "./GridDefinitionUploadForm.vue";
  import { onMounted, watch } from "vue";
  import { useStore } from "vuex";
  
  export default defineComponent({
    model: ref(null),
    name: "GridDefinitionAdd",
    components: {},
  

  
    setup() {
      const $q = useQuasar();
      const store = useStore();
  
      function alert() {
        $q.dialog({
          component: GridDefinitionUploadForm,
          parent: this,
          persistent: true,
        })
          .onOk(() => {
            console.log("Upload Incidence OK");
            store.commit("grids/updateGridAddedDate", new Date());
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