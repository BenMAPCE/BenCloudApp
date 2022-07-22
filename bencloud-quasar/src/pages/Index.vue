<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <p class="description">
        You can start a new analysis, or select a template to start your analysis
      </p>
    </div>

    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="start-tasks task-card">
        <q-card-section> Start a new analysis </q-card-section>
        <q-card-actions vertical>
          <q-btn
            color="secondary"
            icon-right="mdi-home"
            push
            @click="$router.replace('analysis')"
            label="New Analysis"
          />
        </q-card-actions>
      </q-card>

      <q-card class="manage-tasks task-card">
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
        </q-card-section>
        <q-card-actions vertical>
          <q-btn
            color="secondary"
            icon-right="mdi-home"
            push
            :disabled="model === null"
            @click="startAnalysisFromTemplate()"
            label="Analysis From Template"
          />

<!--          <q-btn-->
<!--            color="secondary"-->
<!--            icon-right="mdi-home"-->
<!--            push-->
<!--            :disabled="model === null"-->
<!--            @click="goToReviewAnalysisFromTemplate()"-->
<!--            label="Review From Template"-->
<!--          />-->
        </q-card-actions>
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
        store.commit("analysis/updateStepNumber", 1);
      })();
    });

    function startAnalysisFromTemplate() {
     (async () => {
        loadTemplate(model, store);

        while (store.state.analysis.healthImpactFunctions.length === 0) {
          await new Promise((resolve) => setTimeout(resolve, 1000));
        }

        this.$router.replace('/analysis')
      })();

    }

    function goToReviewAnalysisFromTemplate() {
      (async () => {
        loadTemplate(model, store);

        while (store.state.analysis.healthImpactFunctions.length === 0) {
          await new Promise((resolve) => setTimeout(resolve, 1000));
        }
        store.commit("analysis/updateStepNumber", 7);

        this.$router.replace("/analysis");
      })();

    }

    return {
      model,
      options,
      startAnalysisFromTemplate,
      goToReviewAnalysisFromTemplate
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

  .task-card {
    height: 170px;
    width: 280px;
    display: flex;
    flex-direction: column;
  }

  .task-card .q-card__section {
    flex-grow: 1;
  }
</style>
