<template>
    <div class="q-pa-sm q-gutter-sm">
        <q-dialog ref="dialog" persistent @hide="onDialogHide">
            <q-card>
            <q-form>
                <q-card-section>
                    <div class="text-h6">Edit Template {{ this.templateName }}</div>
                </q-card-section>
                <q-card-section>
                <q-input v-model="this.newName" label="Template Name: "></q-input>
                </q-card-section>
                <q-card-actions>
                    <q-card-actions>
                        <q-btn color="primary" label="OK" @click="onOKClick()" />
                    </q-card-actions>

                    <q-card-actions>
                        <q-btn color="primary" label="Cancel" @click="onCancelClick()" />
                    </q-card-actions>
                </q-card-actions>
            </q-form>
            <q-card-section class="error-card" v-if="this.errorMessage != ''">
                {{ this.errorMessage }}

            </q-card-section>                
            </q-card>

        </q-dialog>
    </div>
</template>

<script>
export default {
  data: () => ({
    errorMessage: "",
    newName:""
  }),

 props: {
    templateId: {
      type: Number,
      default: 0,
    },
    templateName: {
      type: String,
      default: "",
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
      //this.hide()
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
  setup(props, context){
    
  }
};
</script>