<template>
  <q-dialog ref="dialog" @hide="onDialogHide">
    <q-card class="value-of-effects-edit-card">
      <q-form class="q-gutter-md">
        <div class="q-pa-md">
          <div class="row">
            <div class="col">Group</div>
            <div class="col">
              {{ row.group_name }}
            </div>
          </div>

          <div class="row">
            <div class="col">Author / Year</div>
            <div class="col">
              {{ row.author_year }}
            </div>
          </div>

          <div class="row">
            <div class="col">Endpoint</div>
            <div class="col">
              {{ row.endpoint_name }}
            </div>
          </div>

          <div class="row">
            <div class="col">Age Range</div>
            <div class="col">
              {{ row.age_range }}
            </div>
          </div>

          <div class="row">
            <div class="col">Race / Ethnicity / Gender</div>
            <div class="col">
              {{ row.race_ethnicity_gender }}
            </div>
          </div>

          <div class="row">
            <div class="col">Incidence (I) or Prevalence (P)</div>
            <div class="col">
              {{ row.incidence_prevalence }}
            </div>
          </div>
        </div>

        <q-select
          filled
          v-model="valuationFunctionsSelected"
          :options="valuationFunctionsForEndpointGroupId"
          option-value="id"
          option-label="qualifier"
          use-chips
          multiple
          stack-label
          label="Valuations"
        />

        <div class="row justify-center">
          <q-card-actions>
            <q-btn color="primary" label="Cancel" @click="onCancelClick" />
            <q-btn color="primary" label="Save" @click="onSave" />
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
import { ref } from "vue";
import { useStore } from "vuex";

export default {
  data: () => ({
    valuationFunctionsSelected: [],
    errorMessage: "",
  }),

  components: {},

  props: {
    row: {
      type: Object,
      default: null,
    },
    valuationFunctionsForEndpointGroupId: {
      type: Object,
      default: null,
    },
    valuationsSelected: {
      type: Object,
      default: null,
    },
  },

  emits: [
    // REQUIRED
    "ok",
    "hide",
    "selectedValuationsFunctions",
  ],

  methods: {
    // following method is REQUIRED
    // (don't change its name --> "show")
    show() {
      console.log("^^^^^^^^^^^^^^");
      var selectedValidations = [];

      if (this.valuationsSelected != undefined && this.valuationsSelected.valuation_ids != undefined) {
        for (var vf = 0; vf < this.valuationFunctionsForEndpointGroupId.length; vf++) {
          for (var vs = 0; vs < this.valuationsSelected.valuation_ids.length; vs++) {
            if (
              this.valuationFunctionsForEndpointGroupId[vf].id == this.valuationsSelected.valuation_ids[vs]
            ) {
              selectedValidations.push(this.valuationFunctionsForEndpointGroupId[vf]);
            }
          }
        }
      }

      this.valuationFunctionsSelected = selectedValidations;

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

    onSave() {
      var hasErrors = false;
      // then hiding dialog
      console.log("--- onSave()");
      console.log(this.row);
      console.log(this.valuationFunctionsSelected);
      this.$emit("ok", this.valuationFunctionsSelected, this.row.value);
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
.value-of-effects-edit-card {
  width: 500px;
}
</style>
