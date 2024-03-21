<template>
  <div class="download-incidence-dataset-dialog">
    <q-dialog class="export-dialog" ref="dialog" @hide="onDialogHide">
      <q-card class="export-card">
        <q-form @submit="onSubmit" class="q-gutter-md">
          <div class="row">
            <div class="col-12 export-results-header text-h6">Export Results</div>
          </div>

          <div class="row">
            <div class="col-6 year-selection">
              Include
              <div class="select">
                <q-select
                square
                dense
                outlined
                v-model="year"
                :options="yearOptions"
                class="year-options"
                emit-value
                map-options
                label="Year"
                />
              </div>
            </div>
          </div>

              

          <div class="row justify-center">
            <q-card-actions>
              <q-btn color="primary" label="Cancel" @click="onCancelClick" />
              <q-btn color="primary" label="Export" @click="onSubmit" />
            </q-card-actions>
          </div>
        </q-form>

        <q-card-section class="error-card" v-if="this.errorMessage != ''">
          {{ this.errorMessage }}
        </q-card-section>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>
import { ref, unref, onBeforeMount } from "vue";
import { getIncidenceDatasetYears, buildIncidenceYearOptions } from "../../../composables/analysis/incidence";
import axios from "axios";

export default {
  data: () => ({
    errorMessage: "",
    //name: "",
    dashData: [],
  }),

  props: {
    
    incidence_dataset_id:{
      type:Number,
      default: 0,
    },
    
  },

  setup(props) {
    const incidence_dataset_id = ref(0);

    const yearOptions = ref([]);
    const year = ref("All years");
    const model = ref(null);

    onBeforeMount(() => {

      incidence_dataset_id.value = props.incidence_dataset_id;

      //get all grid definitions to list  
      (async () => {
          const response = await getIncidenceDatasetYears(incidence_dataset_id.value).fetch();
          yearOptions.value = buildIncidenceYearOptions(unref(response.data));
        })()
    });

    return {
      year,
      yearOptions,
      model
    };
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

    onSubmit() {
      
      var selected;
      if(this.year == "All years") {
        selected = 0;
      } else {
        selected = this.year;
      }
      var self = this;
      self.$q.loading.show({
        message: "Downloading incidence data. Please wait...",
        boxClass: "bg-grey-2 text-grey-9",
        spinnerColor: "primary",
      });

        axios
        .get(
          process.env.API_SERVER + "/api/incidence/" +
           this.incidence_dataset_id +
            "/contents",
          {
            params: {
              page: 1,
              rowsPerPage: 9999999,
              year: selected
            },
          headers: { Accept: "application/zip", "Content-Type": "application/zip" },
          responseType: "blob",
        })
        .then((response) => {          
          var fileName = response.headers["content-disposition"]
          .split("filename=")[1]
          .split(";")[0];
            

          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", fileName ); //or any other extension
          document.body.appendChild(link);
          link.click();
        })
        .finally(function () {
          self.$q.loading.hide();
        })
        this.hide();
    },

    onCancelClick() {
      // we just need to hide the dialog
      this.hide();
    },
  },
};
</script>

<style lang="scss" scoped>
.download-incidence-dataset-dialog {
  .q-field {
    padding-left: 10px;
    padding-right: 10px;
  }
  .export-results-header {
    padding-top: 10px;
    padding-left: 15px;
  }
  .year-selection {
    padding-left: 15px;
  }
  .export-card {
    width: 500px;
  }
  .error-card{
    color:red;
  }
}
</style>
