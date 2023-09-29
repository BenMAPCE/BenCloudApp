<template>
    <div class="q-pa-sm q-gutter-sm">
        <q-dialog ref="dialog" @hide="onDialogHide">
            <q-card>
            <q-form @submit.prevent>
                <q-card-section>
                    <div class="text-h6">Edit Template {{ this.templateName }}</div>
                </q-card-section>
                <q-card-section>
                <q-input v-model="newName" label="Template Name: " @keydown.enter="onOKClick()"></q-input>
                </q-card-section>
                <q-card-actions>
                    <q-card-actions>
                        <q-btn color="primary" label="OK" @click="onOKClick()" />
                    </q-card-actions>
                    <q-card-actions>
                        <q-btn color="primary" label="Cancel" @click="onCancelClick()" />
                    </q-card-actions>
                </q-card-actions>
            </q-form>
            <q-card-section class="error-card" v-if="this.errorMessage != ''">
                {{ this.errorMessage }}
            </q-card-section>                
            </q-card>

        </q-dialog>
    </div>
</template>

<script>
import { ref } from "vue";
import { useQuasar } from "quasar";
import axios from 'axios';

export default {
  data: () => ({
    errorMessage: "",
    newName: ""
  }),

 props: {
    templateId: {
      type: Number,
      default: 0,
    },
    templateName: {
      type: String,
      default: "",
    },
  },

  emits: [
    // REQUIRED
    "ok", "hide"
  ],

  setup() {
    const $q = useQuasar();
  },

  methods: {
    // following method is REQUIRED
    // (don't change its name --> "show")
    show () {
      this.$refs.dialog.show();
      this.newName = this.templateName;
    },

    // following method is REQUIRED
    // (don't change its name --> "hide")
    hide () {
      this.$refs.dialog.hide()
    },

    onDialogHide () {
      // required to be emitted
      // when QDialog emits "hide" event
      this.$emit('hide')
    },

    async onOKClick () {
      // on OK, it is REQUIRED to
      // emit "ok" event (with optional payload)
      // before hiding the QDialog

      //If users did not change the template name. do not put. 
      if(this.templateName == this.newName){
        this.$q.notify({
          group: false, 
          type: 'positive',
          timeout: 4000, 
          color: "green",
          spinner: false, 
          position: "top",
          message: "Template name was not changed.",
        });
        this.$emit('ok')
        this.hide();
      }
      else{
        const templateData = new FormData();
        templateData.append("newName", this.newName);

        await axios
          .put(process.env.API_SERVER + "/api/task-configs/" + this.templateId, 
            templateData,
            {validateStatus: function (status) {
              return status < 500; // Default is >=200 and <300. If not set status >300 will return null response.
            }}
            )
          .then((response) => {
            if(response.status === 200) {       
              console.log("Successfully renamed template: " + this.newName);
              //window.location.reload();
              this.$q.notify({
                group: false, 
                type: 'positive',
                timeout: 4000, 
                color: "green",
                spinner: false, 
                position: "top",
                message: response.data.message,
              });
              this.$emit('ok')
              this.hide();
              
            } else if(response.status === 409){
              console.log("Unable to renamed template: " + this.newName);
              this.$q.notify({
                group: false, // required to be updatable
                type: 'negative',
                timeout: 6000, // we will timeout it in 4 seconds
                color: "red",
                spinner: false, // we reset the spinner setting so the icon can be displayed
                position: "top",
                message: response.data.message,
              });
              this.$emit('ok')
            }
            else{
              this.$q.notify({
                group: false, 
                type: 'negative',
                timeout: 4000, 
                color: "red",
                spinner: false, 
                position: "top",
                message: "Something went wrong.",
              }); 
              this.$emit('ok')
            }
            
          })
          .catch(function (error) {
            if(error.response){
              console.log(error.response.data);
              console.log(error.response.status);
              console.log(error.response.headers);
            }
            else{
              console.log('Error', error.message);
            }
            console.log(error.config);

            //this.$emit('ok')
        });       

      }
    },

    onCancelClick () {
      // we just need to hide the dialog
      this.hide()
    },

    onRejected(rejectedEntries) {
      // Notify plugin needs to be installed
      // https://quasar.dev/quasar-plugins/notify#Installation
      $q.notify({
        type: "negative",
        message: `${rejectedEntries.length} file(s) did not pass validation constraints`,
      });
    },
  },
};
</script>