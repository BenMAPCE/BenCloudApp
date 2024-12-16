<template>
    <div class="filename-change-form">
      <q-dialog class="filename-change-dialog" ref="dialog" @hide="onDialogHide" persistent>
        <q-card class="change-card">
          <q-form @submit="onSubmit" class="q-gutter-md">
            <div class="row">
              <div class="col-12">
                <q-input
                  filled
                  dense
                  v-model="newFilename"
                  label="*New Filename"
                  lazy-rules
                  :rules="[(val) => (val && val.length > 0) || 'Please enter a filename']"
                />
              </div>
            </div>
  
            <div class="row justify-center">
              <q-card-actions>
                <q-btn color="primary" label="Submit" type="submit" />
                <q-btn color="secondary" label="Cancel" @click="onCancelClick" />
              </q-card-actions>
            </div>
          </q-form>
  
          <q-card-section class="error-card" v-if="errorMessage">
            {{ errorMessage }}
          </q-card-section>
        </q-card>
      </q-dialog>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        newFilename: "",
        errorMessage: "",
      };
    },
    emits: ["ok", "hide"],
    methods: {
      show() {
        this.$refs.dialog.show();
      },
      hide() {
        this.$refs.dialog.hide();
      },
      onDialogHide() {
        this.$emit("hide");
      },
      onSubmit() {
        this.errorMessage = "";
  
        if (!this.newFilename) {
          this.errorMessage = "Filename is required.";
          return;
        }
  
        this.$emit("ok", { filename: this.newFilename });
        this.hide();
      },
      onCancelClick() {
        this.hide();
      },
    },
  };
  </script>
  
  <style scoped>
  .filename-change-form {
    .change-card {
      width: 400px;
    }
  
    .error-card {
      color: red;
      text-align: center;
      margin-top: 10px;
    }
  }
  </style>

