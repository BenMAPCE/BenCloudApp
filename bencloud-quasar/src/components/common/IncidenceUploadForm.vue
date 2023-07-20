<template>
  <div class="upload-incidence">
    <q-dialog class="upload-incidence-dialog" ref="dialog" @hide="onDialogHide">
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
                @added="file_selected"
                @removed="file_removed"
                bordered
                hide-upload-btn
              />
            </div>
          </div>

          <div class="row">
            <div class="col-12 incidence-name">
              Add Incidence Dataset
            </div>
          </div>

          <div class="row">
            <div class="col-12">
              <q-input
                filled
                dense
                v-model="name"
                label="*File Name"
                hint=""
                lazy-rules
                :rules="[(val) => (val && val.length > 0) || 'Please enter a name']"
              />
            </div>
          </div>

          <!-- <div class="row">
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
          </div> -->

          <!-- <div class="row">
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
          </div> -->


          <!-- <div class="row">
            <div class="col-12">
              <q-input
                filled
                dense
                v-model="description"
                label="Description"
                :hint="descriptionHint"                
              />
            </div>
          </div> -->

          

          <div class="row">
            <div class="col-12">
              <GridDefinitions @changeGridValue="onChangeGridValue"></GridDefinitions>
            </div>
          </div>

          <div class="row justify-center">
            <q-card-actions>
              <q-btn color="primary" label="Upload" @click="onSubmit" />
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
import AirQualityUploadErrorsDialog from "./AirQualityUploadErrorsDialog.vue";
import AirQualityUploadSuccessDialog from "./AirQualityUploadSuccessDialog.vue";

import { useQuasar } from "quasar";
import { useStore } from "vuex";

export var layerName = null;

export default {
  data: () => ({
    selected_file: "",
    check_if_document_upload: false,
    pollutantValue: 0,
    gridValue: 0,
    errorMessage: "",
    name: "",
    // aqYear:"",
    // source:"",
    // dataType:"",
    // description:"",
    fileName:"",
    uploadDate: "",
    dashData: [],
    // descriptionHint:"",
  }),
  
  components: {
    GridDefinitions,
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

      console.log(this.selected_file);

      if (this.name === "") {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Name is required";
        hasErrors = true;
      }

      if (this.gridValue === 0) {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Grid is required";
        hasErrors = true;
      }

      if (this.selected_file === "") {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "File is required";
        hasErrors = true;
      }

      // if (this.aqYear === "") {
      //   this.errorMessage =
      //     this.errorMessage + (hasErrors ? ", " : "") + "Year is required";
      //   hasErrors = true;
      // }
      // else{
      //   const yearRegex = /^\d{2}(\d{2})?$/;
      //   if(!yearRegex.test(this.aqYear)) {
      //     this.errorMessage + (hasErrors ? ", " : "") + "Please enter a valid year";
      //   hasErrors = true;
      //   }
      // }

      // if (this.source === "") {
      //   this.errorMessage =
      //     this.errorMessage + (hasErrors ? ", " : "") + "Source is required";
      //   hasErrors = true;
      // }

   
      if (hasErrors) {
        return;
      }

      //get upload time in local time and "yyyy-MM-dd'T'HH:mm:ss" format. Without calculating timezone offset, the date time will be in UTC.
      var tzoffset = (new Date()).getTimezoneOffset() * 60000
      var localISOTime = (new Date(Date.now() - tzoffset)).toISOString();

      const url = process.env.API_SERVER + "/api/incidence-data";
      const fileData = new FormData();
      fileData.append("file", this.selected_file);
      fileData.append("name", this.name);
      fileData.append("gridId", this.gridValue);
      // fileData.append("year", this.year);
      // fileData.append("source", this.source);
      // fileData.append("dataType", this.dataType);
      // fileData.append("description", this.description);
      fileData.append("fileName", this.selected_file.name);
      console.log(fileData);
      fileData.append("uploadDate",localISOTime)
      var self = this;

      this.$q.loading.show({
        message: "Uploading Incidence Data. Please wait...",
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
                  component: AirQualityUploadErrorsDialog,
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
                component: AirQualityUploadSuccessDialog,
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

          var oldValue =  this.$store.state.incidence.incidenceForceReloadValue
          console.log("oldValue: " + oldValue);
          var newValue = oldValue + 1;
          console.log("newValue: " + newValue);
          layerName = this.name;
          this.$store.commit("incidence/updateIncidenceForceReloadValue", newValue)

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
      this.selected_file = file[0];
      this.check_if_document_upload = true;
    },

    file_removed: function (file) {
      this.selected_file = "";
      this.check_if_document_upload = false;
    },

    onCancelClick() {
      // we just need to hide the dialog
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
.upload-incidence {
  .q-field {
    padding-left: 10px;
    padding-right: 10px;
  }

  .incidence-name {
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
