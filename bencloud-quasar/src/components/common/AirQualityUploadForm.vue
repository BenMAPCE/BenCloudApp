<template>
  <div class="upload-air-quality">
    <q-dialog class="upload-air-quality-dialog" ref="dialog" @hide="onDialogHide" persistent>
      <q-card class="upload-card">
        <q-form @submit="onSubmit" class="q-gutter-md">
          <div class="row">
            <div class="col-12">
              <q-uploader
                label="Upload your CSV"
                accept=".csv"
                :max-file-size="20000000"
                square
                flat
                @added="files_selected"
                @removed="files_removed"
                @rejected="file_rejected"
                bordered
                hide-upload-btn
                multiple
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
            <div class="col-12 air-quality-name">
              Add {{ pollutantFriendlyName }} Air Quality Layer
            </div>
          </div>

          <div class="row">
            <div class="col-12">
              <q-input
                filled
                dense
                v-model="groupName"
                :label="groupNameLabel"
                hint=""
                lazy-rules
              />
            </div>
          </div>

          <div class="row">
            <div class="col-12">
              <div v-if="isBatchUpload" class="batch-name">
                Layer name of each surface will match the file name of each uploaded CSV.
              </div>
              <q-input
                v-else
                filled
                dense
                v-model="name"
                label="*Layer Name"
                hint=""
                lazy-rules
                :rules="[(val) => (val && val.length > 0) || 'Please enter a name']"
              />
            </div>
          </div>

          <div class="row">
            <div class="col-12">
              <q-input
                filled
                dense
                v-model="aqYear"
                label="*Year"
                hint=""
                lazy-rules
                :rules="[val => (val > 1900 && val < 3000) || 'Please enter a valid year']"
              />
            </div>
          </div>

          <div class="row">
            <div class="col-12">
              <q-input
                filled
                dense
                v-model="source"
                label="*Source"
                hint="Citation, web page, publishing organization, etc." 
                lazy-rules
                :rules="[(val) => (val && val.length > 0) || 'Please enter a source for this data']"               
              />
            </div>
          </div>

          <div class="row">
            <div class="col-12">
              <q-select 
                square
                dense
                outlined 
                v-model="dataType" 
                :options="['Photochemical AQ Model', 'Land Use Regression Model', 'Satellite', 'Sensor', 'Hybrid Model']" 
                label="*Data type"
                lazy-rules
                :rules="[(val) => (val && val.length > 0) || 'Please select a data type for this data']" 
              />
              
            </div>
          </div>

          <div class="row">
            <div class="col-12">
              <q-input
                filled
                dense
                v-model="description"
                label="Description"
                :hint="descriptionHint"                
              />
            </div>
          </div>

          

          <div class="row">
            <div class="col-12">
              <GridDefinitions @changeGridValue="onChangeGridValue"></GridDefinitions>
            </div>
          </div>

          <input type="hidden" name="pollutantId" :value="pollutantId" />

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
              <q-btn color="primary" :label="`Upload ${selected_files.length>0?selected_files.length:''} Surface${isBatchUpload?`s`:''}`" @click="onSubmit" />
              <q-btn color="primary" label="Cancel" @click="onCancelClick" />
            </q-card-actions>
          </div>
        </q-form>

        <q-card-section class="error-card" v-if="this.errorMessage != ''">
          {{ this.errorMessage }}
        </q-card-section>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>
import GridDefinitions from "../datacenter/airquality/GridDefinitions.vue";
import AirQualityUploadResponseDialog from "./AirQualityUploadResponseDialog.vue";

import { useQuasar } from "quasar";
import { useStore } from "vuex";
import { isAdmin } from "src/boot/auth.js";

export var layerName = null;

export default {
  data: () => ({
    selected_files: [],
    check_if_document_upload: false,
    pollutantValue: 0,
    gridValue: 0,
    errorMessage: "",
    groupName: "",
    name: "",
    aqYear:"",
    source:"",
    dataType:"",
    description:"",
    filenames:[],
    uploadDate: "",
    dashData: [],
    descriptionHint:"",
    shareScope: 0,
    //groupNameLabel: "Group Name",
  }),

  setup() {
    return { isAdmin };
  },

  computed: {
    isBatchUpload() {
      return this.selected_files.length > 1;
    },
    groupNameLabel(){
      return this.isBatchUpload ? "Group Name *" : "Group Name";
    }
  },
  
  components: {
    GridDefinitions,
  },

  props: {
    pollutantId: {
      type: Number,
      default: 0,
    },
    pollutantFriendlyName: {
      type: String,
      default: "None",
    },
  },

  emits: [
    // REQUIRED
    "ok",
    "hide",
  ],

  watch: {
    dataType(newValue, oldValue) {
      if (newValue.toLowerCase()=="hybrid model") {
        this.descriptionHint = "Please enter details about the hybrid model.";
        console.log("newValue.toLowerCase()=='hybrid model'");
      }
      else{
        this.descriptionHint = "";
      }
    },
  },

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
      console.log("pollutantValue: " + this.pollutantValue);
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
      console.log(this.pollutantValue);
      var hasErrors = false;
      this.errorMessage = "";

      console.log(this.selected_files);
      console.log(this.filenames);


      if (this.name === "" && !this.isBatchUpload) {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Name is required";
        hasErrors = true;
      }
      if (this.groupName === "" && this.isBatchUpload) {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Group Name is required for batch upload";
        hasErrors = true;
      }
      if (this.pollutantId === 0) {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Pollutant is required";
        hasErrors = true;
      }

      if (this.gridValue === 0) {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Grid is required";
        hasErrors = true;
      }

      if (this.selected_files.length < 1) {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "At least 1 file is required";
        hasErrors = true;
      }

      if (this.aqYear === "") {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Year is required";
        hasErrors = true;
      }
      else{
        const yearRegex = /^\d{2}(\d{2})?$/;
        if(!yearRegex.test(this.aqYear)) {
          this.errorMessage + (hasErrors ? ", " : "") + "Please enter a valid year";
        hasErrors = true;
        }
      }

      if (this.dataType === "") {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Data type is required";
        hasErrors = true;
      }

      if (this.source === "") {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Source is required";
        hasErrors = true;
      }

      if(this.dataType.toLowerCase() ==="hybrid model"){
        if(this.description === ""){
          this.errorMessage = this.errorMessage + (hasErrors ? ", " : "") + "Description is required when Hybrid model is selected";
        hasErrors = true;
        }
      }

      if (hasErrors) {
        return;
      }

      //get upload time in local time and "yyyy-MM-dd'T'HH:mm:ss" format. Without calculating timezone offset, the date time will be in UTC.
      var tzoffset = (new Date()).getTimezoneOffset() * 60000
      var localISOTime = (new Date(Date.now() - tzoffset)).toISOString();

      const url = process.env.API_SERVER + "/api/air-quality-data";
      const fileData = new FormData();
      for (var i=0; i < this.selected_files.length; i++) {
        fileData.append("file" + i, this.selected_files[i]);
      }
      fileData.append("groupName",this.groupName);
      if (!this.isBatchUpload) {
        fileData.append("userLayerName", this.name);
      }
      fileData.append("pollutantId", this.pollutantId);
      fileData.append("gridId", this.gridValue);
      fileData.append("aqYear", this.aqYear);
      fileData.append("source", this.source);
      fileData.append("dataType", this.dataType);
      fileData.append("description", this.description);
      fileData.append("uploadDate",localISOTime)
      fileData.append("shareScope", this.shareScope);
      var self = this;

      this.$q.loading.show({
        message: "Uploading Air Quality Layer. Please wait...",
        boxClass: "bg-grey-2 text-grey-9",
        spinnerColor: "primary",
      });

      var self = this;
      this.$axios
        .post(url, fileData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
          validateStatus: function (status) {
            return status < 500;
          }
        })
        .then((response) => {
          //data.value = response.data;
          console.log(response.status);
          console.log(response.statusText);
          console.log(response.data.success);
          console.log(response.data.messages);
          
          this.$q
          .dialog({component: AirQualityUploadResponseDialog,
            parent: this,
            persistent: true,
            componentProps: {
              success: response.status === 200 && response.data.success,
              messageList: response.data.messages,
              fileNames: this.filenames,
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
              

          self.$q.loading.hide();

          var oldValue =  this.$store.state.airquality.airQualityForceReloadValue
          console.log("oldValue: " + oldValue);
          var newValue = oldValue + 1;
          console.log("newValue: " + newValue);
          layerName = this.name;
          this.$store.commit("airquality/updateAirQualityForceReloadValue", newValue)

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

    files_selected: function (files) {
      this.selected_files.push(...files);
      console.log(this.selected_files)
      this.filenames = this.selected_files.map(f => f.name);
      this.check_if_document_upload = true;
    },

    files_removed: function (files) {
      const removedFilenames = new Set(files.map(f => f.name));
      this.selected_files = this.selected_files.filter(f => !removedFilenames.has(f.name));
      console.log(this.selected_files)
      this.filenames = this.selected_files.map(f => f.name);
      this.check_if_document_upload = this.selected_files.length > 0;
    },

    file_rejected: function(rejections){
      rejections.forEach(rejection=>{
        console.log(rejection.failedPropValidation);
        var message;
        switch (rejection.failedPropValidation) {
          case 'max-file-size':
            message = 'File ' + rejection.file.name + ' exceeds 20MB limit!';
            break;
          case 'duplicate':
            message = 'File named ' + rejection.file.name + ' already selected.';
            break;
          default:
            message = 'File ' + rejection.file.name + ' is rejected.';
        }
        this.$q.notify({
            type: 'negative',
            position: 'top',
            message: message
          });
      });    
    },

    onCancelClick() {
      this.shareScope = 0;
      this.hide();
    },

    onRejected(rejectedEntries) {
    },

    onChangePollutantValue(value) {
      this.pollutantValue = value;
    },
    onChangeGridValue(value) {
      this.gridValue = value;
    },

    hybridSelected(value){
      if(value.toLowerCase()=="hybrid model")
      {
        this.descriptionHint = "Please enter the description for this hybrid model.";
      }
    }
  },
};
</script>

<style lang="scss" scoped>
.upload-air-quality {
  .q-field {
    padding-left: 10px;
    padding-right: 10px;
  }

  .air-quality-name {
    padding-left: 15px;
  }

  .batch-name {
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
