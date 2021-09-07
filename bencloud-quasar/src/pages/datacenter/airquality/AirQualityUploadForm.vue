<template>
  <q-dialog ref="dialog" @hide="onDialogHide">
    <q-card class="q-dialog-plugin">
      <!--
        ...content
        ... use q-card-section for it?
      -->
      <div class="q-pa-md" style="max-width: 300px">
        <q-uploader url= process.env.API_SERVER +
        "/api/air-quality-data" dark label="Restricted to CSV files" multiple
        accept="text/csv" @rejected="onRejected" />
      </div>

      <!-- buttons example -->
      <q-card-actions align="right">
        <q-btn color="primary" label="Done" @click="onCancelClick" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>

import { useStore } from "vuex";

export default {
  props: {
    // ...your custom props
    text: {
      type: String,
      default: "SSS",
    },
  },

  setup(props, context) {
    const store = useStore();
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

    onOKClick() {
      // on OK, it is REQUIRED to
      // emit "ok" event (with optional payload)
      // before hiding the QDialog
      this.$emit("ok");
      // or with payload: this.$emit('ok', { ... })

      // then hiding dialog
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
  },
};
</script>

<style>

</style>
