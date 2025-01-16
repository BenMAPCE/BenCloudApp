<template>
  <q-page>
    <div class="q-pa-md">
      <h1 class="text-h4 q-mb-lg">What would you like to do?</h1>
      
      <div class="row q-col-gutter-md">
        <!-- Start New Analysis Section -->
        <div class="col-12 col-md-6">
          <q-card class="analysis-card">
            <q-card-section>
              <h2 class="text-h5 q-mb-md text-grey-8">Start a new analysis</h2>
              
              <div class="q-gutter-y-md">
                <q-item 
                  clickable
                  v-ripple
                  @click="$router.replace('analysis')"
                  class="analysis-item"
                >
                  
                  <q-item-section>
                    Analyze Health Impacts of Air Pollutants
                  </q-item-section>
                </q-item>

                <q-item 
                  clickable
                  v-ripple
                  @click="$router.replace('exposure')"
                  class="analysis-item"
                >
                  
                  <q-item-section>
                    Analyze Exposure to Air Pollutants
                  </q-item-section>
                </q-item>
              </div>
            </q-card-section>
          </q-card>
        </div>

        <!-- Templates Section -->
        <div class="col-12 col-md-6">
          <q-card class="template-card">
            <q-card-section>
              <h2 class="text-h5 q-mb-md text-grey-8">Start from a template</h2>
              
              <div v-if="options.length === 0" class="text-grey-7">
                Set up a new analysis and save your favorite configurations as templates. They'll show up here for you to use as a starting point in the future.
              </div>
              
              <div v-else class="q-gutter-y-sm">
                <q-item
                  v-for="option in options"
                  :key="option.id"
                  class="template-item"
                >
                  <q-item-section clickable @click="startAnalysisFromTemplate(option)">
                    <q-item-label>{{ option.name }}</q-item-label>
                    <q-item-label caption>{{ option.type }}</q-item-label>
                  </q-item-section>

                  <q-item-section side>
                    <div class="row items-center">
                      <q-btn
                        flat
                        round
                        dense
                        icon="mdi-pencil"
                        color="grey-7"
                        @click="editTemplate(option)"
                      />
                      <q-btn
                        flat
                        round
                        dense
                        icon="mdi-delete"
                        color="grey-7"
                        @click="deleteTemplate(option)"
                      />
                    </div>
                  </q-item-section>
                </q-item>
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script>
import { defineComponent, ref, onMounted } from "vue";
import { useStore } from "vuex";
import { useQuasar } from "quasar";
import { getTemplates, loadHifTemplate, loadExposureTemplate } from "../composables/templates/templates";
import EditTemplateDialog from "./EditTemplateDialog.vue";

export default defineComponent({
  name: "PageIndex",

  setup() {
    const store = useStore();
    const options = ref([]);
    const $q = useQuasar();

    function displayTemplates() {
      getTemplates()
        .fetch()
        .then((response) => {
          options.value = response.data.value;
        });
    }

    function editTemplate(template) {
      $q.dialog({
        component: EditTemplateDialog,
        componentProps: {
          templateId: template.id,
          templateName: template.name,
        }
      })
      .onOk(() => {
        displayTemplates();
      });
    }

    function deleteTemplate(template) {
      $q.dialog({
        title: 'Confirm Deletion',
        message: `Are you sure you want to delete "${template.name}"?`,
        cancel: true,
        persistent: true
      }).onOk(() => {
        axios
          .delete(`${process.env.API_SERVER}/api/task-configs/${template.id}`)
          .then((response) => {
            if (response.status === 204) {
              displayTemplates();
              $q.notify({
                type: 'positive',
                message: 'Template deleted successfully'
              });
            }
          })
          .catch((error) => {
            $q.notify({
              type: 'negative',
              message: error.response?.data?.message || 'Failed to delete template'
            });
          });
      });
    }

    async function startAnalysisFromTemplate(template) {
      if (template.type === "Health Impact Analysis") {
        await loadHifTemplate(template, store);
        this.$router.replace('/analysis');
      } else if (template.type === "Exposure Analysis") {
        await loadExposureTemplate(template, store);
        this.$router.replace('/exposure');
      }
    }

    onMounted(() => {
      displayTemplates();
      store.commit("analysis/updateStepNumber", 1);
    });

    return {
      options,
      startAnalysisFromTemplate,
      editTemplate,
      deleteTemplate
    };
  }
});
</script>

<style scoped>
.analysis-card,
.template-card {
  height: 100%;
}

.analysis-item {
  border-radius: 8px;
  transition: background-color 0.3s;
}

.analysis-item:hover {
  background: rgba(0, 0, 0, 0.05);
}

.template-item {
  border-radius: 8px;
  background: white;
}

.template-item:hover {
  background: rgba(0, 0, 0, 0.05);
}

@media (max-width: 903px) {
  .template-item {
    flex-direction: column;
  }
}
</style>