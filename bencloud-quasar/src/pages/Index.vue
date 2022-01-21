<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <p class="description">
        You can start a new analysis, or select a template to start your analysis
      </p>
    </div>

    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="start-tasks">
        <q-card-section> Start a new analysis </q-card-section>
        <q-card-section>
          <q-btn
            color="secondary"
            icon-right="mdi-home"
            push
            @click="$router.replace('analysis')"
            label="New Analysis"
          />
        </q-card-section>
      </q-card>

      <q-card class="manage-tasks">
        <q-card-section> Start a new analysis from a template </q-card-section>
        <q-card-section>
          <q-select
            v-model="model"
            :options="options"
            option-value="id"
            option-label="name"
            map-options
            label="Select a template"
            class="template-select"
          >
            <template v-slot:prepend>
              <q-icon name="mdi-file-cabinet" />
            </template>
          </q-select>

          <q-btn
            color="secondary"
            icon-right="mdi-home"
            push
            @click="loadSelectedTemplate()"
            label="Analysis From Template"
          />
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script>
import { defineComponent, ref, onMounted } from "vue";
import AppTopNavigation from "../components/navigation/AppTopNavigation.vue";
import { getTemplates, loadTemplate } from "../composables/templates/templates";
import { useStore } from "vuex";

export default defineComponent({
  name: "PageIndex",
  components: {},

  setup(props, context) {
    const store = useStore();
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

    function loadSelectedTemplate() {

      (async () => {
        loadTemplate(model, store);

        //console.log("waiting for healthImpactFunctions");
        //console.log("-----")
        //console.log(store.state.analysis.healthImpactFunctions)
        //console.log("-----")
        while (store.state.analysis.healthImpactFunctions.length === 0) {
          //console.log("...");
          await new Promise((resolve) => setTimeout(resolve, 1000));
        }
        //this.$router.replace("/analysis/review");
        this.$router.replace('analysis')
      })();

      //var parameters = model.value.parameters;
      //console.log(parameters)
      //console.log(parameters.air_quality_data)
      //store.commit("analysis/updatePollutantId", parameters.pollutantId);

      //  this.$router.replace("/analysis/review");

      //this.$router.replace('analysis')
    }

    return {
      model,
      options,
      loadSelectedTemplate,
    };
  },
});
</script>

<style scoped>
.description {
  font-size: 18px;
  font-weight: 400;
}

.template-select {
  padding-bottom: 25px;
}
</style>
