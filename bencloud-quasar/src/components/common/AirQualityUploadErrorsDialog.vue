<template>
  <q-dialog ref="dialog" persistent @hide="onDialogHide">
    <q-card id="error-list-card" class="error-list-card">
      <q-card-section>
        <div class="text-h6">Your upload of {{ this.fileName }} has failed</div>
        <div v-if="this.errorList.length > 1" class="text-h6">Your file has the following errors</div>
        <div v-if="this.errorList.length == 1" class="text-h6">Your file has the following error</div>
      </q-card-section>

      <q-separator />

      <q-card-section style="max-height: 50vh" class="scroll">
        <li v-for="item in errorList" :key="item.message" class="error-list-item">
          <q-item>
            <q-item-section class="icon-column">
              <q-icon class="error-icon" name="mdi-alert-circle" />
            </q-item-section>
            <q-item-section class="type-column">
              {{ item.type }}
            </q-item-section>

            <q-item-section class="message-column">
              {{ item.message }}
            </q-item-section>
          </q-item>
        </li>
      </q-card-section>

      <q-separator />

      <q-card-actions align="right">
        <q-btn
          color="black"
          @click="printErrorList()"
          label="Print"
          id="print-button"
          class="q-ml-sm back-button"
        />
        <q-btn flat label="OK" id="ok-button" class="ok-button" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>
import print from "print-js";

export default {
  data: () => ({
    errorMessage: "",
  }),

  props: {
    errorList: {
      type: Object,
      default: null,
    },
    fileName: {
      type: String,
      default: "",
    },
  },
  components: {},

  emits: [
    // REQUIRED
    "ok",
    "hide",
    "dismiss",
  ],

  methods: {
    // following method is REQUIRED
    // (don't change its name --> "show")
    show() {
      console.log("^^^^^^^^^^^^^^");
      console.log(this.errorList);
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

    onOKClick() {
      console.log("clicked OK");
      // on OK, it is REQUIRED to
      // emit "ok" event (with optional payload)
      // before hiding the QDialog
      this.$emit("ok");
      // or with payload: this.$emit('ok', { ... })

      // then hiding dialog
      this.hide();
    },

    onDismissClick() {
      console.log("clicked View Tasks");
      // required to be emitted
      // when QDialog emits "hide" event
      this.$emit("dismiss");
      this.hide();
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

    printErrorList() {
      console.log("printing...");
      document.getElementById("print-button").style.display = "none";
      document.getElementById("ok-button").style.display = "none";
      printJS({
        printable: "error-list-card",
        type: "html",
        style: "li.error-list-item { list-style: none; } .type-column { text-transform: capitalize; }",
      });
      document.getElementById("print-button").style.display = "flex";
      document.getElementById("ok-button").style.display = "flex";
    },
  },
};
</script>

<style lang="scss" scoped>

.ok-button {
  background-color: var(--q-primary) !important;
    color: white !important;
}
.error-list-card {
  max-width: fit-content;
}

li.error-list-item {
  list-style: none;
}
.error-icon {
  color: red;
}

.icon-column {
  max-width: 15px;
}

.type-column {
  max-width: 40px;
  text-transform: capitalize;
  font-weight: 600;
}

.message-column {
  max-width: 600px;
}
</style>
