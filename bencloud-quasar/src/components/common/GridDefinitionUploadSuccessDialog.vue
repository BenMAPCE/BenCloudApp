<template>
  <q-dialog ref="dialog" persistent @hide="onDialogHide">
    <q-card id="success-list-card" class="success-list-card">
      <q-card-section>
        <div class="success-dialog">Your grid definition {{ this.fileName }} has been submitted for processing. This process may take a couple minutes to complete. Your new grid will display in the list on this page after processing is complete.</div>
      </q-card-section>

      <q-card-actions class="button-row">
        <q-btn
          flat
          label="OK"
          id="ok-button"
          class="action-button"
          color="primary"
          @click="onOKClick"
        />
        <q-btn
          flat
          label="View Your Tasks"
          id="view-tasks-button"
          class="action-button"
          color="primary"
          @click="onViewTasksClick"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>
export default {
  props: {
    fileName: {
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

    onViewTasksClick() {
      // Navigate to manage-tasks route
      this.$router.push('/datacenter/manage-tasks');
      this.parentDialog.hide();
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
.action-button {
  background-color: var(--q-primary) !important;
  color: white !important;
  margin: 0 8px;
}

.button-row {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 16px;
}

.success-dialog {
  max-width: 100%;
  text-align: center;
}

.success-list-card {
  max-width: 500px; // Reduced width of the dialog
  width: 90%; // Ensures some responsiveness
  margin: auto;
}
</style>