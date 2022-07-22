<template>
  <q-dialog ref="dialog" @hide="onDialogHide">
    <q-card class="value-of-effects-edit-card">
      <q-form class="q-gutter-md">
        <div class="q-pa-md">
          <div class="row">
            <div class="col">Your task {{ taskName.value }} has been submitted</div>
          </div>
        </div>


        <div class="row justify-center">
          <q-card-actions>
            <q-btn color="primary" label="OK" @click="onCancelClick()" />
          </q-card-actions>

          <q-card-actions>
            <q-btn color="primary" label="View your tasks" @click="onOKClick()" />
          </q-card-actions>
        </div>

      </q-form>

      <q-card-section class="error-card" v-if="this.errorMessage != ''">
        {{ this.errorMessage }}
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script>
export default {
  data: () => ({
    errorMessage: "",
  }),

 props: {
    taskName: {
      type: Object,
      default: null,
    },
  },

  emits: [
    // REQUIRED
    "ok", "hide"
  ],

  methods: {
    // following method is REQUIRED
    // (don't change its name --> "show")
    show () {
      this.$refs.dialog.show()
    },

    // following method is REQUIRED
    // (don't change its name --> "hide")
    hide () {
      this.$refs.dialog.hide()
    },

    onDialogHide () {
      // required to be emitted
      // when QDialog emits "hide" event
      this.$emit('hide')
    },

    onOKClick () {
      // on OK, it is REQUIRED to
      // emit "ok" event (with optional payload)
      // before hiding the QDialog
      this.$emit('ok')
      // or with payload: this.$emit('ok', { ... })

      // then hiding dialog
      this.hide()
    },

    onCancelClick () {
      // we just need to hide the dialog
      this.hide()
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
