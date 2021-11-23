<template>
  <div class="download-task-results-dialog">
    <q-dialog class="export-dialog" ref="dialog" @hide="onDialogHide">
      <q-card class="export-card">
        <q-form @submit="onSubmit" class="q-gutter-md">
          <div class="row">
            <div class="col-12 export-results-header">Export Results</div>
          </div>

          <div class="row">
            <div class="col-12">
              <q-input
                filled
                dense
                v-model="name"
                label="Name"
                hint=""
                lazy-rules
                :rules="[(val) => (val && val.length > 0) || 'Please enter a name']"
              />
            </div>
          </div>

          <div class="row">
            <div class="col-6 grid-selections">
              <div class="select-grid-levels">Select Grid Levels</div>
              <q-option-group
                v-model="gridGroup"
                :options="gridOptions"
                dense
                type="checkbox"
              />
            </div>
            <div class="col-6 export-format">
              <div class="select-export-formats">Select Export Format</div>
              <q-option-group
                v-model="exportFormatGroup"
                :options="exportFormatOptions"
                dense
              />
            </div>
          </div>

          <input type="hidden" name="pollutantId" :value="pollutantId" />

          <div class="row justify-center">
            <q-card-actions>
              <q-btn color="primary" label="Cancel" @click="onCancelClick" />
              <q-btn color="primary" label="Export" @click="onSubmit" />
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
import { ref } from "vue";
import { useQuasar } from "quasar";
import { useStore } from "vuex";

export default {
  data: () => ({
    selected_file: "",
    pollutantValue: 0,
    gridValue: 0,
    errorMessage: "",
    name: "",
    dashData: [],
  }),

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

  setup(props) {
    const gridGroup = ref(["12k"]);
    const exportFormatGroup = ref("zip");

    return {
      gridGroup,

      gridOptions: [
        {
          label: "US Nation",
          value: "usn",
        },
        {
          label: "US State",
          value: "uss",
        },
        {
          label: "US County",
          value: "usc",
        },
        {
          label: "CMAQ 12k Grid",
          value: "12k",
        },
      ],

      exportFormatGroup,

      exportFormatOptions: [
        {
          label: "CSV",
          value: "csv",
        },
        {
          label: "Zip",
          value: "zip",
        },
      ],
    };
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

    onExportClick() {
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
      var hasErrors = false;
      this.errorMessage = "";

      console.log(this.selected_file);

      console.log("grid: |" + this.gridGroup + "|");
      console.log("grid: |" + this.gridGroup.length + "|");

      if (this.name === "") {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Name is required";
        hasErrors = true;
      }

      if (this.gridGroup.length === 0) {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Grid is required";
        hasErrors = true;
      }

      if (hasErrors) {
        return;
      }

      /*
      const url = process.env.API_SERVER + "/api/air-quality-data";
      const fileData = new FormData();
      fileData.append("file", this.selected_file);
      fileData.append("name", this.name);
      fileData.append("pollutantId", this.pollutantId);
      fileData.append("gridId", this.gridValue);
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
        })
        .then(function () {
          console.log("SUCCESS!!");
          self.$q.loading.hide();
          self.hide();
          self.$emit("ok");
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
        .finally(function () {});
    */
      this.hide();
      this.$emit("ok");
    },

    onCancelClick() {
      // we just need to hide the dialog
      this.hide();
    },
  },
};
</script>

<style lang="scss" scoped>
.download-task-results-dialog {
  .q-field {
    padding-left: 10px;
    padding-right: 10px;
  }

  .air-quality-name {
    padding-left: 15px;
  }

  .export-results-header {
    padding-top: 10px;
    padding-left: 15px;
  }
  .grid-selections {
    padding-left: 15px;
  }

  .select-export-formats {
    padding-bottom: 10px;
  }

  .select-grid-levels {
    padding-bottom: 10px;
  }
  .export-card {
    width: 500px;
  }
}
</style>
