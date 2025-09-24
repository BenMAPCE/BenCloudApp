<template>
  <div class="upload-valuation">
    <q-dialog class="upload-valuation-dialog" ref="dialog" @hide="onDialogHide" persistent>
      <q-card class="upload-card">
        <q-form @submit="onSubmit()" class="q-gutter-md">
          <div class="row">
            <div class="col-12">
              <q-uploader
                label="Upload your CSV"
                accept=".csv"
                :max-file-size="1000000000"
                square
                flat
                @added="file_selected"
                @removed="file_removed"
                @rejected = "file_rejected"
                bordered
                hide-upload-btn
              >
                <template v-slot:header="scope">
                  <div class="row no-wrap items-center q-pa-sm q-gutter-xs">
                    <q-spinner v-if="scope.isUploading" class="q-uploader__spinner" />
                    <div class="col">
                      <div class="q-uploader__title">Upload your files</div>
                      <div class="q-uploader__subtitle">{{ scope.uploadSizeLabel }} / {{ scope.uploadProgressLabel }}</div>
                    </div>
                    <q-btn v-if="scope.canAddFiles" type="a" icon="mdi-paperclip" @click="scope.pickFiles" square flat>
                      <q-uploader-add-trigger />
                    </q-btn>
                  </div>
                </template>
              </q-uploader>
            </div>
          </div>

          <div class="row">
            <div class="col-12 valuation-name">
              Add Valuation Functions
            </div>
          </div>

          <q-select
            square
            dense
            outlined
            v-model="typeValue"
            :options="typeOptions"
            class="upload-type-options"
            emit
            map-options
            label="Upload Type"
          />

          <div v-if="typeValue != null">
            
            <div v-if="typeValue == 'Append to an existing health effect category'" class="col-12">
              <Suspense>
                <HealthEffectGroups v-model="healthEffectGroupName" updateState getAll></HealthEffectGroups>
              </Suspense>
            </div>
            

            <div v-if="typeValue == 'Add a new health effect category'" class="row">
              <div class="col-12">
                <q-input
                  filled
                  dense
                  v-model="healthEffectGroupName"
                  label="*Health Effect Category Name"
                  hint=""
                  lazy-rules
                  :rules="[(val) => (val && val.length > 0) || 'Please enter a name']"
                />
              </div>
            </div>

            <div v-if="typeValue == 'Add a new health effect category'" class="row">
              <div class="col-12">
                <q-input
                  filled
                  dense
                  v-model="description"
                  label="*Description"
                  hint=""
                  lazy-rules
                  :rules="[(val) => (val && val.length > 0) || 'Please enter a description']"
                />
              </div>
            </div>
          </div>

          <div class="row justify-center">
            <q-card-actions>
              <q-btn color="primary" label="Upload" @click="onSubmit" />
              <q-btn color="primary" label="Cancel" @click="onCancelClick" />
            </q-card-actions>
          </div>
        </q-form>

        <q-card-section class="error-card" v-if="errorMessage != ''">
          {{ errorMessage }}
        </q-card-section>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>
import ValuationUploadErrorsDialog from "./ValuationUploadErrorsDialog.vue";
import ValuationUploadSuccessDialog from "./ValuationUploadSuccessDialog.vue";
import HealthEffectGroups from "./HealthEffectGroups.vue";

import { Notify, Loading, Dialog } from "quasar";
import { ref, watch } from "vue";
import { useStore } from "vuex";
import axios from "axios";

export default {
  components: {
    HealthEffectGroups
  },


  emits: [
    // REQUIRED
    "ok",
    "hide",
  ],
  setup(props, { emit }) {
    const store = useStore();
    const typeValue = ref(null);
    const typeOptions = ref(["Add a new health effect category", "Append to an existing health effect category"]);
    const healthEffectGroupName = ref("");
    const dialog = ref(null);

    var selected_file = "";
    var check_if_document_upload = false;
    const errorMessage = ref("");
    const description = ref("");


    watch(
      () => typeValue.value,
      (typeValue, prevTypeValue) => {
        if (typeValue != prevTypeValue) {
          healthEffectGroupName.value = "";
          description.value = "";
          errorMessage.value = "";
        }
      }
    );

    const show = () => {
      dialog.value.show();
    }

    // following method is REQUIRED
    // (don't change its name --> "hide")
    const hide = () => {
      dialog.value.hide();
    }

    const onDialogHide = () => {
      // required to be emitted
      // when QDialog emits "hide" event
      emit("hide");
    }

    const onUploadClick = () => {
      console.log("onUploadClick");

      // on OK, it is REQUIRED to
      // emit "ok" event (with optional payload)
      // before hiding the QDialog
      emit("ok");
      // or with payload: emit('ok', { ... })

      // then hiding dialog
      dialog.value.hide();
    }

    const onOKClick = () => {
      // on OK, it is REQUIRED to
      // emit "ok" event (with optional payload)
      // before hiding the QDialog
      emit("ok");
      // or with payload: emit('ok', { ... })

      // then hiding dialog
      dialog.value.hide();
    }

    const file_selected = (file) => {
      selected_file = file[0];
      check_if_document_upload = true;
    }

    const file_removed = (file) => {
      selected_file = "";
      check_if_document_upload = false;
    }

    const file_rejected = (rejectedFiles) =>{
      rejectedFiles.forEach(rejection=>{
          if(rejection.failedPropValidation === 'max-file-size'){
            Notify.create({
              type: 'negative',
              position:'top',
              message:'File size exceeds 1G limit!'
            });
            errorMessage.value = "File " + rejection.file.name + " exceeds 1G limit!";
            //console.log('size limit reach.');
          }
          else{
            Notify.create({
              type: 'negative',
              position:'top',
              message:'Invalid file format.'
            });
            errorMessage.value = "Invalid file format. Please check your csv file.";
            //console.log('Invalid file format.');
          }
        })
    }

    const onCancelClick = () => {
      // we just need to hide the dialog
      dialog.value.hide();
    }

    const onSubmit = () => {
      var hasErrors = false;
      var newCategory = false;
      errorMessage.value = "";

      if (healthEffectGroupName.value === "") {
        errorMessage.value =
          errorMessage.value + (hasErrors ? ", " : "") + "Health effect category name is required";
        hasErrors = true;
      }
      
      if(typeValue.value == 'Append to an existing health effect category') {
        healthEffectGroupName.value = healthEffectGroupName.value.name;
      }

      if (typeValue.value == 'Add a new health effect category') {
        newCategory = true;
        if(description.value === "") {
          errorMessage.value =
            errorMessage.value + (hasErrors ? ", " : "") + "Description is required";
          hasErrors = true;
        }
      }

      if (selected_file === "") {
        errorMessage.value =
          errorMessage.value + (hasErrors ? ", " : "") + "File is required";
        hasErrors = true;
      }

   
      if (hasErrors) {
        return;
      }

      //get upload time in local time and "yyyy-MM-dd'T'HH:mm:ss" format. Without calculating timezone offset, the date time will be in UTC.
      var tzoffset = (new Date()).getTimezoneOffset() * 60000
      var localISOTime = (new Date(Date.now() - tzoffset)).toISOString();

      const url = process.env.API_SERVER + "/api/valuation-function-data";
      const fileData = new FormData();
      fileData.append("file", selected_file);
      fileData.append("healthEffectGroupName", healthEffectGroupName.value);
      fileData.append("description", description.value);
      fileData.append("filename", selected_file.name);
      fileData.append("newCategory", newCategory);
      console.log(fileData);
      fileData.append("uploadDate",localISOTime)
      var self = this;

      Loading.show({
        message: "Uploading valuation function data. Please wait...",
        boxClass: "bg-grey-2 text-grey-9",
        spinnerColor: "primary",
      });

      var self = this;
      axios
        .post(url, fileData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => {
          console.log(response.status);
          console.log(response.statusText);
          console.log(response.data.success);
          console.log(response.data.messages);

          if (response.data.success === false) {
            console.log("BAD NEWS");
            if (response.data.messages.length > 0) {
              console.log("Show Errors");

              Dialog.create({
                  component: ValuationUploadErrorsDialog,
                  parent: this,
                  persistent: true,
                  componentProps: {
                    errorList: response.data.messages,
                    fileName: selected_file.name,
                  },
                })
                .onOk(() => {
                  console.log("OK");
                  dialog.value.hide()
                })
                .onCancel(() => {
                })
                .onDismiss(() => {
               });
            }
          } else {
            Dialog.create({
                component: ValuationUploadSuccessDialog,
                parent: this,
                persistent: true,
                componentProps: {
                  healthEffectGroupName: healthEffectGroupName.value,
                  parentDialog: dialog.value,
                },
              })
              .onOk(() => {
                console.log("OK");
              })
              .onCancel(() => {
              })
              .onDismiss(() => {
              });
          }

          Loading.hide();

          var oldValue =  store.state.valuation.valuationForceReloadValue
          console.log("oldValue: " + oldValue);
          var newValue = oldValue + 1;
          console.log("newValue: " + newValue);
          store.commit("valuation/updateValuationForceReloadValue", newValue)

          var oldValue =  store.state.valuation.healthEffectForceReloadValue
          console.log("oldValue: " + oldValue);
          var newValue = oldValue + 1;
          console.log("newValue: " + newValue);
          store.commit("valuation/updateHealthEffectForceReloadValue", newValue)

          //self.hide();
          //self.$emit("ok");

          return response.status;
        })
        .catch(function (error) {
          if (error.response) {
            // The request was made and the server responded with a status code
            // that falls out of the range of 2xx
            console.log(error.response.data);
            self.errorMessage.value = error.response.data;
            console.log(error.response.status);
            console.log(error.response.headers);
          } else if (error.request) {
            // The request was made but no response was received
            // `error.request` is an instance of XMLHttpRequest in the browser and an instance of
            // http.ClientRequest in node.js
            console.log(error.request);
          } else {
            // Something happened in setting up the request that triggered an Error
            console.log("Error", error.message);
          }
          console.log("FAILURE!!");
          Loading.hide();
        });
    }

    return {
      typeOptions,
      typeValue,
      healthEffectGroupName,
      dialog,
      errorMessage,
      description,
      show,
      hide,
      onDialogHide,
      onUploadClick,
      onCancelClick,
      file_removed,
      file_rejected,
      file_selected,
      onOKClick,
      onSubmit,
    };
  },

};
</script>

<style lang="scss" scoped>
.upload-valuation {
  .q-field {
    padding-left: 10px;
    padding-right: 10px;
  }

  .valuation-name {
    padding-left: 15px;
  }
  .upload-card {
    width: 500px;
  }

  .q-uploader {
    width: 100%;
    a.q-btn-item:first-child {
      display: none;
    }
  }
}
</style>
