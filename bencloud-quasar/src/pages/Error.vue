<template>
  <q-page>
    <div class="description q-pa-md q-gutter-sm">
        <p>Welcome to BenMAP</p>
        <p>BenMAP is currently down. Please try again later.</p>
    </div>
  </q-page>
</template>

<script>
import { defineComponent, ref, onMounted } from "vue";
import { getTemplates, loadTemplate } from "../composables/templates/templates";

export default defineComponent({
  name: "PageIndex",
  components: {},

  setup(props, context) {
    const options = ref([]);
    const model = ref(null);
    onMounted(() => {
      (async () => {
        console.log("loadTemplates");
        const response = await getTemplates().fetch();
        console.log(JSON.parse(JSON.stringify(response.data.value)));
        options.value = JSON.parse(JSON.stringify(response.data.value));
      })();
    });


    return {
      model,
      options
    };
  },
});
</script>

<style scoped>
.description {
    font-size: 18px;
    font-weight: 400;
  }
</style>
