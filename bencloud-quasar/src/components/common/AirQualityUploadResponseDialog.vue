<template>
  <q-dialog ref="dialog" persistent @hide="onDialogHide">
    <q-card id="message-list-card" class="message-list-card">
      <q-card-section>
        <div>
          <div class="text-h6">
            Your upload {{ success ? 'was successful' : 'has failed' }} for the following {{ fileNames.length === 1 ? 'file' : 'files' }}:
          </div>
          <ul>
            <li class="text-h6" v-for="(name, index) in fileNames" :key="index">{{ name }}</li>
          </ul>
        </div>
        <div class="text-h6" v-if="processedMessageList.length > 0">
          Your {{ fileNames.length === 1 ? 'file has' : 'files have' }} the following 
          {{ warningCount > 0 ? (warningCount === 1 ? 'warning' : 'warnings') : '' }}
          {{ warningCount > 0 && errorCount > 0 ? 'and' : '' }} 
          {{ errorCount > 0 ? (errorCount === 1 ? 'error' : 'errors') : '' }}
        </div>
      </q-card-section>

      <q-separator />

      <q-card-section style="max-height: 50vh" class="scroll" v-if="processedMessageList.length > 0">
        <li v-for="(item, index) in processedMessageList" :key="index" class="message-list-item">
          <q-item :clickable="item.needsExpansion" @click="item.needsExpansion ? toggleExpand(index) : null">
            <q-item-section class="icon-column" v-if="item.isWarning">
              <q-icon class="warning-icon" name="mdi-alert-circle" />
            </q-item-section>
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
          @click="printMessageList()"
          label="Print"
          id="print-button"
          class="q-ml-sm back-button"
        />
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
  data: () => ({
    processedMessageList: [],
  }),

  props: {
    success: {
      type: Boolean,
      default: false,
    },
    messageList: {
      type: Object,
      default: null,
    },
    fileNames: {
      type: Array,
      default: () => [],
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

  watch: {
    messageList: {
      handler(newVal) {
        this.processMessageList(newVal);
      },
      immediate: true,
      deep: true
    }
  },

  computed: {
    warningCount() {
      return this.processedMessageList.filter(item => item.isWarning).length;
    },
    errorCount() {
      return this.processedMessageList.filter(item => item.isError).length;
    },
  },

  methods: {
    processMessageList(errors) {
      this.processedMessageList = errors.map(error => ({
        ...error,
        expanded: false,
        needsExpansion: this.checkIfNeedsExpansion(error.message),
        isError: error.type.toLowerCase() === 'error',
        isWarning: error.type.toLowerCase() === 'warning',
      }));
    },

    checkIfNeedsExpansion(message) {
      const lines = message.split('\n');
      return lines.length > 3 || message.length > 150;
    },

    toggleExpand(index) {
      if (this.processedMessageList[index].needsExpansion) {
        this.processedMessageList[index].expanded = !this.processedMessageList[index].expanded;
      }
    },

    // following method is REQUIRED
    // (don't change its name --> "show")
    show() {
      this.processMessageList(this.messageList);
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
      console.log(this.parentDialog);
      if (this.success && this.parentDialog) {
        this.parentDialog.hide();
      }

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
      if (this.success) {
        return;
      }
      // Notify plugin needs to be installed
      // https://quasar.dev/quasar-plugins/notify#Installation
      $q.notify({
        type: "negative",
        message: `${rejectedEntries.length} file(s) did not pass validation constraints`,
      });
    },

    printMessageList() {
      console.log("printing...");
      document.getElementById("print-button").style.display = "none";
      document.getElementById("ok-button").style.display = "none";
      printJS({
        printable: "message-list-card",
        type: "html",
        style: `
          li.message-list-item { 
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

.message-list-card {
  max-width: fit-content;
}

li.message-list-item {
  list-style: none;
}

.warning-icon {
  color: orange;
}

.error-icon {
  color: red;
}

.icon-column {
  max-width: 15px;
}

.type-column {
  max-width: 60px;
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