<template>
  <q-page>
    <div class="q-pa-sm q-gutter-sm">
      <p class="description">
        Welcome to BenMAP
      </p>
      <div>
        <p class="prompt"> Start a new analysis</p>
        <div class="home-options">
          <q-btn flat
                color="primary"
                push
                @click="$router.replace('analysis')"
                label="Analyze Health Impacts of Air Pollutants"
              />
        </div>
        <div class="home-options">
          <q-btn flat
              color="primary"
              push
              label="Analyze Exposure to Air Pollutants"
            />
        </div>
      </div>
      <div>
        <div class="home-template" id="template">
          <p class="prompt">Start from a template</p>
          <div v-if="options.length === 0" class="home-options">Set up a new analysis and save your favorite configurations as templates. They'll show up here for you to use as a starting point in the future.</div>
          <div v-if="options.length > 0" id="list" class="home-options">
            <ul>
              <li v-for="option in options" :key="option.id">
                <q-btn flat
                  color="primary"
                  push
                  @click="startAnalysisFromTemplate(option)">
                  <div>{{ option.name }} for {{ option.type }}</div>
                </q-btn>
                <q-btn
                  dense
                  round
                  flat
                  color="grey"
                  @click="editTemplate(option)"
                  icon="mdi-pencil"
                ></q-btn>
                <q-btn
                  dense
                  round
                  flat
                  color="grey"
                  @click="deleteTemplate(option)"
                  icon="mdi-delete"
                ></q-btn>
              </li>
            </ul>
          </div>
          
        </div>
      </div>
    </div>
  </q-page>
</template>  

<script>
import { defineComponent, ref, onMounted, onBeforeMount } from "vue";
import AppTopNavigation from "../components/navigation/AppTopNavigation.vue";
import { getTemplates, loadTemplate } from "../composables/templates/templates";
import { useStore } from "vuex";
import axios from "axios";
import { useQuasar } from "quasar";
import EditTemplateDialog from "./EditTemplateDialog.vue";

export default defineComponent({
  name: "PageIndex",
  components: {},

  setup(props, context) {
    const store = useStore();
    const options = ref([]);
    const $q = useQuasar();

    const template = document.getElementById('template');

    function displayTemplates() {
      (async () => {
        console.log("loadTemplates");
        const response = await getTemplates().fetch();
        for(var i = 0; i < response.data.value.length; i++) {
          options.value.push(response.data.value[i]);
        }
      })();
    }

    function editTemplate(template) {
      
      
      $q.dialog({
        component:EditTemplateDialog,
        parent:this,
        persistent: true,
        componentProps:{
          templateName: template.name
        },
        data:{
          newName:template.name
        }     
      })
      .onOk((newName) => {
          console.log(newName);
          axios
          .put(process.env.API_SERVER + "/api/task-configs/" + template.id,{
            params:{
              id:template.id,
              newName:newName,
            },
          })
          .then((response) => {
            if(response.status === 204) {              
              console.log("Successfully renamed template: " + template.name);
               $q.notify({
                group: false, 
                type: 'positive',
                timeout: 4000, 
                color: "green",
                spinner: false, 
                position: "top",
                message: response.data.message,
              });
              //todo: hide pop-up dialog
              window.location.reload();
            } else {
              console.log("Unable to renamed template: " + template.name);
              $q.notify({
                group: false, // required to be updatable
                type: 'negative',
                timeout: 4000, // we will timeout it in 4 seconds
                color: "red",
                spinner: false, // we reset the spinner setting so the icon can be displayed
                position: "top",
                message: response.data.message,
              });             
              
              //alert("An error occurred, template was not deleted.")
            }
          });

      })
      .onCancel(() => {
        // console.log('Cancel')
      })
      .onDismiss(() => {
        // console.log('I am triggered on both OK and Cancel')
      });
      console.log("Edit template - functionality to be added");

    }

    function deleteTemplate(template) {
      // Prompt user to confirm template deletion
      if(confirm("Are you sure you wish to permanently delete " + template.name + "?")){
        // Delete template, reload the page if successful, alert the user if unsuccessful       
        axios
          .delete(process.env.API_SERVER + "/api/task-configs/" + template.id, {
            params: {
              id: template.id,
            },
          })
          .then((response) => {
            if(response.status === 204) {
              window.location.reload();
              console.log("Successfully deleted template: " + template.name);
            } else {
              alert("An error occurred, template was not deleted.")
            }
          });
      }
    }

    function startAnalysisFromTemplate(template) {
     (async () => {
        loadTemplate(template, store);

        while (store.state.analysis.healthImpactFunctions.length === 0) {
          await new Promise((resolve) => setTimeout(resolve, 1000));
        }

        this.$router.replace('/analysis')
      })();

    }

    function goToReviewAnalysisFromTemplate() {
      (async () => {
        loadTemplate(template, store);

        while (store.state.analysis.healthImpactFunctions.length === 0) {
          await new Promise((resolve) => setTimeout(resolve, 1000));
        }
        store.commit("analysis/updateStepNumber", 7);

        this.$router.replace("/analysis");
      })();

    }

    onMounted(() => {
      displayTemplates();
      store.commit("analysis/updateStepNumber", 1);
    })

    return {
      options,
      startAnalysisFromTemplate,
      editTemplate,
      deleteTemplate 
    };
    
  },
});
</script>

<style scoped>
  .description {
    font-size: 24px;
    font-weight: 400;
  }

  .prompt {
    font-size: 24px;
    color: grey;
    font-weight: 400;
    margin-bottom: 0;
    margin-top: 20px;
  }

  .home-options {
    padding-left: 30px;
    padding-top: 10px;
  }

  .home-options > ul {
    list-style-type: none;
    padding: 0;
    margin-top: 0;
  }
</style>
