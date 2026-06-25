<template>
    <div class="upload-grid">
      <q-dialog class="upload-grid-dialog" ref="dialog" @hide="onDialogHide" persistent>
        <q-card class="upload-card">
          <q-form @submit="onSubmit" class="q-gutter-md">
            <div class="row">
              <div class="col-12">
                <q-uploader
                  label="Upload your zipped shape file"
                  accept=".zip"
                  :max-file-size="20000000"
                  square
                  flat
                  @added="file_selected"
                  @removed="file_removed"
                  @rejected="file_rejected"
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
                <template v-slot:list="scope">
                  <div class="q-uploader__list scroll">
                    <div v-if="scope.files.length === 0" class="q-uploader__no-files">
                      {{ this.errorMessage || '*Please upload your grid as a ZIP folder containing the following: .shp, .shx, .dbf, and .prj files.' }}
                    </div>
                    <q-uploader-file v-for="file in scope.files" :key="file.name" :file="file" />
                  </div>
                </template>
              </q-uploader>
              </div>
            </div>
  
            <div class="row">
              <div class="col-12 grid-name">
                Add Grid Definition
              </div>
            </div>
  
            <div class="row">
              <div class="col-12">
                <q-input
                  filled
                  dense
                  v-model="name"
                  label="*Descriptive Name"
                  hint=""
                  lazy-rules
                  :rules="[(val) => (val && val.length > 0) || 'Please enter a name']"
                />
              </div>
            </div>

            <div v-if="isAdmin" class="q-px-md q-pb-sm">
              <q-toggle
                v-model="shareScope"
                :true-value="1"
                :false-value="0"
                label="Share with all users"
              />
            </div>

            <div class="row justify-center">
              <q-card-actions>
                <q-btn color="primary" label="Upload" @click="onSubmit" />
                <q-btn color="primary" label="Cancel" @click="onCancelClick" />
              </q-card-actions>
            </div>
          </q-form>
  
          <!-- <q-card-section class="error-card" v-if="this.errorMessage != ''">
            {{ this.errorMessage }}
          </q-card-section> -->
        </q-card>
      </q-dialog>
    </div>
  </template>
  
  <script>
  import GridDefinitionUploadErrorsDialog from "./GridDefinitionUploadErrorsDialog.vue";
  import GridDefinitionUploadSuccessDialog from "./GridDefinitionUploadSuccessDialog.vue";
  import { useQuasar } from "quasar";
  import { useStore } from "vuex";
  import { isAdmin } from "src/boot/auth.js";
  
  export var layerName = null;
  
  export default {
    data: () => ({
      selected_file: "",
      check_if_document_upload: false,
      errorMessage: "",
      name: "",
      filename:"",
      uploadDate: "",
      dashData: [],
      shareScope: 0,
    }),

    setup() {
      return { isAdmin };
    },

    emits: [
      // REQUIRED
      "ok",
      "hide",
    ],
  
    
  
    methods: {
      // following method is REQUIRED
      // (don't change its name --> "show")
      show() {
        this.$refs.dialog.show();
      },
  
      // following method is REQUIRED
      // (don't change its name --> "hide")
      hide() {
        this.$refs.dialog.hide();
      },
  
      onDialogHide() {
        // required to be emitted
        // when QDialog emits "hide" event
        this.$emit("hide");
      },
  
      onUploadClick() {
        console.log("onUploadClick");
        console.log(this.name);
        console.log("gridValue: " + this.gridValue);
  
        // on OK, it is REQUIRED to
        // emit "ok" event (with optional payload)
        // before hiding the QDialog
        this.$emit("ok");
        // or with payload: this.$emit('ok', { ... })
  
        // then hiding dialog
        this.hide();
      },
  
      onOKClick() {
        // on OK, it is REQUIRED to
        // emit "ok" event (with optional payload)
        // before hiding the QDialog
        this.$emit("ok");
        // or with payload: this.$emit('ok', { ... })
  
        // then hiding dialog
        this.hide();
      },
  
      onSubmit() {
        var hasErrors = false;
        this.errorMessage = "";
  
        console.log(this.selected_file);
  
        if (this.name === "") {
          this.errorMessage =
            this.errorMessage + (hasErrors ? ", " : "") + "Name is required";
          hasErrors = true;
        }

  
        if (this.selected_file === "") {
          this.errorMessage =
            this.errorMessage + (hasErrors ? ", " : "") + "File is required";
          hasErrors = true;
        }
     
        if (hasErrors) {
          return;
        }
  
        //get upload time in local time and "yyyy-MM-dd'T'HH:mm:ss" format. Without calculating timezone offset, the date time will be in UTC.
        var tzoffset = (new Date()).getTimezoneOffset() * 60000
        var localISOTime = (new Date(Date.now() - tzoffset)).toISOString();
  
        const url = process.env.API_SERVER + "/api/grid-definitions";
        const fileData = new FormData();
        fileData.append("file", this.selected_file);
        fileData.append("name", this.name);
        fileData.append("filename", this.selected_file.name);
        console.log(fileData);
        fileData.append("uploadDate",localISOTime)
        fileData.append("shareScope", this.shareScope);
        var self = this;

        this.$q.loading.show({
          message: "Uploading Grid Definition. Please wait...",
          boxClass: "bg-grey-2 text-grey-9",
          spinnerColor: "primary",
        });
  
        var self = this;
        this.$axios
          .post(url, fileData, {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          })
          .then((response) => {
            //data.value = response.data;
  
            console.log(response.status);
            console.log(response.statusText);
            console.log(response.data.success);
            console.log(response.data.messages);
  
            if (response.data.success === false) {
              console.log("BAD NEWS");
              if (response.data.messages.length > 0) {
                console.log("Show Errors");
  
                this.$q
                  .dialog({
                    component: GridDefinitionUploadErrorsDialog,
                    parent: this,
                    persistent: true,
                    componentProps: {
                      errorList: response.data.messages,
                      fileName: this.selected_file.name,
                    },
                  })
                  .onOk(() => {
                    console.log("OK");
                    this.$refs.dialog.hide()
                  })
                  .onCancel(() => {
                  })
                  .onDismiss(() => {
                 });
              }
            } else {
              this.$q
                .dialog({
                  component: GridDefinitionUploadSuccessDialog,
                  parent: this,
                  persistent: true,
                  componentProps: {
                    fileName: this.selected_file.name,
                    parentDialog: this.$refs.dialog,
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
  
            self.$q.loading.hide();
  
            var oldValue =  this.$store.state.grids.gridForceReloadValue
            console.log("oldValue: " + oldValue);
            var newValue = oldValue + 1;
            console.log("newValue: " + newValue);
            layerName = this.name;
            this.$store.commit("grids/updateGridForceReloadValue", newValue)
  
            //self.hide();
            //self.$emit("ok");
  
            return response.status;
          })
          .catch(function (error) {
            if (error.response) {
              // The request was made and the server responded with a status code
              // that falls out of the range of 2xx
              console.log(error.response.data);
              self.errorMessage = error.response.data;
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
            self.$q.loading.hide();
          })
          .finally(function () 
            { 
              layerName = null;
            }
          );
      },
  
      file_selected: function (file) {
        this.errorMessage = ""; // Clear any previous error messages
        this.selected_file = file[0];
        this.check_if_document_upload = true;
      },
  
      file_removed: function (file) {
        this.selected_file = "";
        this.check_if_document_upload = false;
      },
  
      file_rejected: function (rejectedFiles) {
        rejectedFiles.forEach(rejection=>{
          if(rejection.failedPropValidation === 'max-file-size'){
            this.$q.notify({
              type: 'negative',
              position:'top',
              message:'File size exceeds 20MB limit!'
            });
            this.errorMessage = "File " + rejection.file.name + " exceeds 20MB limit!";
            //console.log('Size limit reach.');
          }
          else{
            this.$q.notify({
              type: 'negative',
              position:'top',
              message:'Invalid file format!'
            });
            this.errorMessage = "Invalid file format. Please upload a ZIP file containing .shp, .shx, .dbf, and .prj files.";
          }
        })
        
        
      },
  
      onCancelClick() {
        this.shareScope = 0;
        this.hide();
      },
    },
  };
  </script>
  
  <style lang="scss" scoped>
  .upload-grid {
    .q-field {
      padding-left: 10px;
      padding-right: 10px;
    }
  
    .grid-name {
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
  