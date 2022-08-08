<template>
  <q-page>
    <div class="description q-pa-md q-gutter-sm">
        <p>Welcome to BenMAP</p>
        <p>To request access to this tool:</p>
        <ol>
            <li>Visit <a :href='waaUrl' target=_blank> EPA's Web Application Access page</a></li>
            <li>Click Community Access / Request Web Community Access</li>
            <li>Choose BenMAP User from the list and click Submit</li>
            <li>When asked for your personal information, please use Neal Fann (<a href="mailto:fann.neal@epa.gov">fann.neal@epa.gov</a>) as the sponsor</li>
        </ol>
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
    const waaUrl = ref("DDDDD");    

    onMounted(() => {
      (async () => {
        console.log("loadTemplates");
        waaUrl.value = process.env.WAAUrl;
        const response = await getTemplates().fetch();
        console.log(JSON.parse(JSON.stringify(response.data.value)));
        options.value = JSON.parse(JSON.stringify(response.data.value));
      })();
    });


    return {
      model,
      options,
      waaUrl
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
