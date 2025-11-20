<template>
  <q-dialog ref="dialog" persistent @hide="onDialogHide">
    <q-card id="error-list-card" class="error-list-card">
      <q-card-section>
        <div class="text-h6">Your upload of {{ fileNames.join(", ") }} has failed</div>
        <div class="text-h6">
          Your {{ fileNames.length === 1 ? 'file' : 'files' }} 
          {{ fileNames.length === 1 ? 'has' : 'have' }} the following 
          {{ processedErrorList.filter(item => item.isError).length === 1 ? 'error' : 'errors' }}
        </div>
      </q-card-section>

      <q-separator />

      <q-card-section style="max-height: 50vh" class="scroll">
        <li v-for="(item, index) in processedErrorList" :key="index" class="error-list-item">
          <q-item :clickable="item.needsExpansion" @click="item.needsExpansion ? toggleExpand(index) : null">
            <q-item-section class="icon-column" v-if="item.isError">
              <q-icon class="error-icon" name="mdi-alert-circle" />
            </q-item-section>
            <q-item-section class="type-column">
              {{ item.type }}
            </q-item-section>
            <q-item-section class="message-column">
              <div>
                <div :class="{ 'message-truncated': !item.expanded }">
                  {{ item.message }}
                </div>
                <div class="text-caption text-primary q-mt-xs view-more-text" v-if="item.needsExpansion">
                  {{ item.expanded ? '(view less)' : '(view more)' }}
                </div>
              </div>
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
    processedErrorList: [],
  }),

  props: {
    errorList: {
      type: Object,
      default: null,
    },
    fileNames: {
      type: Array,
      default: () => [],
    },
  },
  components: {},

  emits: [
    // REQUIRED
    "ok",
    "hide",
    "dismiss",
  ],

  watch: {
    errorList: {
      handler(newVal) {
        this.processErrorList(newVal);
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    processErrorList(errors) {
      this.processedErrorList = errors.map(error => ({
        ...error,
        expanded: false,
        needsExpansion: this.checkIfNeedsExpansion(error.message),
        isError: error.type.toLowerCase() === 'error'
      }));
    },

    checkIfNeedsExpansion(message) {
      const lines = message.split('\n');
      return lines.length > 3 || message.length > 150;
    },

    toggleExpand(index) {
      if (this.processedErrorList[index].needsExpansion) {
        this.processedErrorList[index].expanded = !this.processedErrorList[index].expanded;
      }
    },

    // following method is REQUIRED
    // (don't change its name --> "show")
    show() {
      console.log("^^^^^^^^^^^^^^");
      console.log(this.errorList);
      console.log(this.fileNames);
      this.processErrorList(this.errorList);
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
        style: `
          li.error-list-item { 
            list-style: none;
          } 
          .type-column { 
            text-transform: capitalize; 
          }
          .message-truncated {
            overflow: hidden;
          }
          .view-more-text {
            display: none !important;
          }
          .q-focus-helper {
            display: none !important;
          }
        `,
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

.message-truncated {
  display: -webkit-box;
  line-clamp: 3;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  white-space: pre-wrap;
}

.view-more-text {
  cursor: pointer;
  user-select: none;
}
</style>