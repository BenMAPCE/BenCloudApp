<template>
  <q-dialog ref="dialog" @hide="onDialogHide">
    <q-card class="my-card">
      <q-form @submit="onSubmit" class="q-gutter-md">
        <div class="row justify-center">
          <q-uploader
            label="Upload your CSV"
            color="purple"
            accept=".csv"
            :max-file-size="20000000"
            square
            flat
            @added="file_selected"
            bordered
          />
        </div>

        <q-input
          filled
          dense
          v-model="name"
          label="Name"
          hint=""
          lazy-rules
          :rules="[(val) => (val && val.length > 0) || 'Please enter a name']"
        />

        <Pollutants @changePollutantValue="onChangePollutantValue">
        </Pollutants>

        <GridDefinitions @changeGridValue="onChangeGridValue"></GridDefinitions>

        <div class="row justify-center">
          <q-card-actions>
            <q-btn color="primary" label="Upload" @click="onSubmit" />
            <q-btn color="primary" label="Cancel" @click="onCancelClick" />
          </q-card-actions>
        </div>
      </q-form>

      <q-card-section
      class="error-card"
      v-if="this.errorMessage != ''"
      >
      {{ this.errorMessage }}


      </q-card-section>
    </q-card>

    

  </q-dialog>
</template>

<script>
import Pollutants from "./Pollutants.vue";
import GridDefinitions from "./GridDefinitions.vue";

export default {
  data: () => ({
    selected_file: "",
    check_if_document_upload: false,
    pollutantValue: 0,
    gridValue: 0,
    errorMessage: "",
    name: "",
    dashData: []
  }),

  components: {
    Pollutants,
    GridDefinitions,
  },

  props: {
    text: {
      type: String,
      default: "SSS",
    },
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

      if (this.selected_file === '') {
        this.errorMessage = "No file selected"
        hasErrors = true;
      }
      if (this.name === '') {
        this.errorMessage = this.errorMessage  + ((hasErrors) ? ', ' : '') + "Name is required"
        hasErrors = true;
      }
      if (this.pollutantValue === 0) {
        this.errorMessage = this.errorMessage  + ((hasErrors) ? ', ' : '') + "Pollutant is required"
        hasErrors = true;
      }

      if (this.gridValue === 0) {
        this.errorMessage = this.errorMessage  + ((hasErrors) ? ', ' : '') + "Grid is required"
        hasErrors = true;
      }

      if (hasErrors) {
        return;
      }

      const url = process.env.API_SERVER + "/api/air-quality-data";
      const fileData = new FormData();
      fileData.append("file", this.selected_file);
      fileData.append("name", this.name);
      fileData.append("pollutantId", this.pollutantValue);
      fileData.append("gridId", this.gridValue);
      var self = this;
      this.$axios
        .post(url, fileData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then(function () {
          console.log("SUCCESS!!");
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
        });
    },

    file_selected: function (file) {
      console.log(file);
      this.selected_file = file[0];
      this.check_if_document_upload = true;
    },

    onCancelClick() {
      // we just need to hide the dialog
      this.hide();
    },

    onRejected(rejectedEntries) {
      // Notify plugin needs to be installed
      // https://quasar.dev/quasar-plugins/notify#Installation
      $q.notify({
        type: "negative",
        message: `${rejectedEntries.length} file(s) did not pass validation constraints`,
      });
    },

    onChangePollutantValue(value) {
      this.pollutantValue = value;
    },
    onChangeGridValue(value) {
      this.gridValue = value;
    },
  },
};
</script>
