<template>
  <div class="description-change-form">
    <q-dialog class="description-change-dialog" ref="dialog" @hide="onDialogHide" persistent>
      <q-card class="change-card">
        <q-card-section>
          <div class="text-h6">Edit Grid Description</div>
        </q-card-section>

        <q-form @submit="onSubmit" class="q-gutter-md">
          <q-card-section>
            <div class="row">
              <div class="col-12">
                <q-input
                  filled
                  dense
                  v-model="newDescription"
                  label="Descriptive Name"
                  lazy-rules
                  :rules="[(val) => (val && val.length > 0) || 'Please enter a descriptive name']"
                />
              </div>
            </div>
          </q-card-section>

          <q-card-actions align="right">
            <q-btn color="secondary" label="Cancel" @click="onCancelClick" />
            <q-btn color="primary" label="Submit" type="submit" />
          </q-card-actions>
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
  name: 'GridDescriptionChangeForm',
  props: {
    currentName: {
      type: String,
      required: true,
      default: ''
    }
  },
  data() {
    return {
      newDescription: "",
      errorMessage: "",
    };
  },
  created() {
    this.newDescription = this.currentName;
  },
  watch: {
    currentName: {
      immediate: true,
      handler(newVal) {
        this.newDescription = newVal;
      }
    }
  },
  emits: ["ok", "hide"],
  methods: {
    show() {
      this.newDescription = this.currentName;
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

      if (!this.newDescription || this.newDescription.trim() === "") {
        this.errorMessage = "Descriptive name is required.";
        return;
      }

      if (this.newDescription === this.currentName) {
        this.hide();
        return;
      }

      this.$emit("ok", { newDescription: this.newDescription });
      this.hide();
    },
    onCancelClick() {
      this.hide();
    },
  },
};
</script>

<style scoped>
.description-change-form {
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