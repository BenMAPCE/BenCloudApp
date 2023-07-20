<template>
  <q-dialog ref="dialog" persistent @hide="onDialogHide">
    <q-card id="success-list-card" class="success-list-card">
      <q-card-section>
        <div class="success-dialog">Your upload of {{ this.filename }} was successful</div>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn
          flat
          label="OK"
          id="ok-button"
          class="ok-button"
          color="primary"
          @click="onOKClick"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>
export default {
  props: {
    filename: {
      type: String,
      default: "",
    },
    parentDialog: {
      type: Object,
      default: null,
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
      this.parentDialog.hide();

      // on OK, it is REQUIRED to
      // emit "ok" event (with optional payload)
      // before hiding the QDialog
      this.$emit("ok");
      // or with payload: this.$emit('ok', { ... })

      // then hiding dialog
      this.hide();
    },

    onDismissClick() {
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
    },
  },
};
</script>

<style lang="scss" scoped>
.ok-button {
  background-color: var(--q-primary) !important;
  color: white !important;
}
.success-dialog {
  max-width: 100%;
}
.success-list-card {
  max-width: 100%;
}

</style>
