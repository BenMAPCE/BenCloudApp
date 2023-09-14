<template>
  <div class="download-task-results-dialog">
    <q-dialog class="export-dialog" ref="dialog" @hide="onDialogHide">
      <q-card class="export-card">
        <q-form @submit="onSubmit" class="q-gutter-md">
          <div class="row">
            <div class="col-12 export-results-header text-h6">Export Results</div>
          </div>

          <div class="row"  v-if="this.include!='all'">
            <div class="col-12">
              <q-input
                filled
                dense
                v-model="name"
                label="Name"
                hint=""
                lazy-rules
                :rules="[(val) => (val && val.length > 0) || 'Please enter a name']"
              />
            </div>
          </div>

          <div class="row">            
            <div class="col-6 group-selections">
              <div>Include</div>
              <q-option-group
                v-model="include"
                :options="includeOptions"
                dense
              />
            </div>
          </div> 

          <div class="row" v-if="this.include=='all'">            
            <div class="col-6 group-selections">
              <div>Result Type(s)</div>
              <q-option-group
                v-model="resultType"
                :options="resultTypeOptions"
                dense
                type="checkbox"
              />
            </div>
          </div>  

          <div class="row">
            <div class="col-6 grid-selections">
              <div>Select Grid Levels</div>
              <q-option-group
                v-model="grid"
                :options="gridOptions"
                dense
                type="checkbox"
              />
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
import { ref, unref, onMounted, onBeforeMount } from "vue";
import { useQuasar } from "quasar";
import { useStore } from "vuex";
import axios from "axios";
import { getGridDefinitions, buildGridDefinitionOptions } from "../../../composables/tasks/grid-definitions";

export default {
  data: () => ({
    errorMessage: "",
    //name: "",
    dashData: [],
  }),

  props: {
    
    batch_task_id:{
      type:Number,
      default: 0,
    },
    task_uuid: {
      type: String,
      default: "",
    },
    analysis_type:{
      type:String,
      default:"hi",
    },
    task_name: {
      type: String,
      default: "",
    },
    task_type: {
      type: String,
      default: "",
    },
  },

  setup(props) {
    const name = ref("");
    const batch_task_id = ref(0);
    const task_uuid = ref("");
    const task_type = ref("");

    const gridOptions = ref([]);
    const grid = ref([]);

    const includeOptions = ref([]);
    const include = ref([]);

    const resultType = ref([]);
    const resultTypeOptions = ref([]);

    const $q = useQuasar();

    if (props.task_type === "H" || props.task_type === "V"){
      resultTypeOptions.value = [
        {
          label: "Health Impact Function Results",
          value: "hif",
        },
        {
          label: "Valuation Function Results",
          value: "vf",
        },
      ];
    } else if (props.task_type === "E"){
      resultTypeOptions.value = [
        {
          label: "Exposure Results",
          value: "exp",
        },
      ];
    }
    

    

    onBeforeMount(() => {

      batch_task_id.value = props.batch_task_id;
      task_uuid.value = props.task_uuid;
      task_type.value = props.task_type;
      name.value = props.task_name; 

      (async () => {
          const response = await getGridDefinitions().fetch();
          gridOptions.value = buildGridDefinitionOptions(unref(response.data))
        })()
    });

    return {
      grid,
      name,
      gridOptions,
      xgridOptions: [
        {
          label: "US Nation",
          value: "usn",
        },
        {
          label: "US State",
          value: "uss",
        },
        {
          label: "US County",
          value: "usc",
        },
        {
          label: "CMAQ 12k Grid",
          value: "12k",
        },
      ],
      resultType,
      resultTypeOptions,
      include: ref('current'),
      includeOptions: [
        {
          label: "Current Scenario",
          value: "current",
        },
        {
          label: "All Scenarios",
          value: "all",
        },
      ],

    };
  },

  emits: [
    // REQUIRED
    "ok",
    "hide",
  ],

  // watch: {
  //   include(newValue, oldValue) {
  //     if (newValue.toLowerCase()=="current") {
  //       this.name = "";
  //     }
  //     else{
  //     }
  //   },
  // },

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
      var hasErrors = false;
      this.errorMessage = "";
      //var task_uuid = this.props.task_uuid;

      console.log("grid: |" + this.grid + "|");
      console.log("grid: |" + this.grid.length + "|");

      console.log("NAME: |" + this.name + "|");
      console.log("UUID: |" + this.task_uuid + "|");
      if (this.include=="all") {
        var include_hif = 0;
        var include_vf = 0;
        var include_exp = 0;

        for (var i = 0; i < this.resultType.length; i++){
          if(this.resultType[i]=="hif"){
            include_hif=1;
          }
          else if(this.resultType[i]=="vf"){
            include_vf=1;
          }
          else if(this.resultType[i]=="exp"){
            include_exp=1;
          }
        }

        if(this.task_type === "H" || this.task_type === "V"){
          include_exp=0;
          if(include_hif==0 && include_vf==0){
            this.errorMessage =
            this.errorMessage + (hasErrors ? ", " : "") + "Please select a result type.";
          hasErrors = true;
          }
          
        }
        else if(this.task_type === "E"){
          include_hif=0;
          include_vf=0;
          if(include_exp==0){
              this.errorMessage =
              this.errorMessage + (hasErrors ? ", " : "") + "Please select a result type";
            hasErrors = true;
          }        
        }        
      }
      else{
        if(this.name === ""){
          this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Name is required";
          hasErrors = true;
        }

      }

      if (this.grid.length === 0) {
        this.errorMessage =
          this.errorMessage + (hasErrors ? ", " : "") + "Grid is required";
        hasErrors = true;
      }

      

      if (hasErrors) {
        return;
      }

      var self = this;

      self.$q.loading.show({
        message: "Downloading results. Please wait...",
        boxClass: "bg-grey-2 text-grey-9",
        spinnerColor: "primary",
      });

      
      // console.log(this.batch_task_id + "|" + include_hif + "|" + include_vf + "|" + include_exp);

      var gridList = "";
      for (var i = 0; i < this.grid.length; i++){
        gridList = gridList + this.grid[i] + ","
      }
      gridList = gridList.substring(0, gridList.length - 1)

      var downloadUrl = "";
      if (this.include=="all"){
        downloadUrl = process.env.API_SERVER +
          "/api/batch-tasks/" + this.batch_task_id +
          "/export?gridId=" + gridList +
          "&includeHealthImpact="+ include_hif +
          "&includeValuation="+ include_vf +
          "&includeExposure="+ include_exp;

      }
      else{
        if (this.task_type === "H") {
          downloadUrl =
            process.env.API_SERVER +
            "/api/health-impact-result-datasets/" +
            this.task_uuid +
            "/export?gridId=" + gridList;
        } else if (this.task_type === "V") {
          downloadUrl =
            process.env.API_SERVER +
            "/api/valuation-result-datasets/" +
            this.task_uuid +
            "/export?gridId=" + gridList;
        } else if(this.task_type === "E") {
          downloadUrl =
            process.env.API_SERVER +
            "/api/exposure-result-datasets/" +
            this.task_uuid +
            "/export?gridId=" + gridList;
        }
      }

      

      axios
        .get(downloadUrl, {
          headers: { Accept: "application/zip", "Content-Type": "application/zip" },
          responseType: "blob",
        })
        .then((response) => {
          var fileName="";
          
          if(this.include=="all"){
            //use filename from resposne when exporting whole batch task
            fileName = response.headers["content-disposition"]
            .split("filename=")[1]
            .split(";")[0];
          }
          else{
            //use user entered name when exporting current scenario
            fileName=this.name + ".zip";
          }
            const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", fileName ); //or any other extension
          document.body.appendChild(link);
          link.click();
        })
        .finally(function () {
          self.$q.loading.hide();
          self.hide();
        });
    },

    onCancelClick() {
      // we just need to hide the dialog
      this.hide();
    },
  },
};
</script>

<style lang="scss" scoped>
.download-task-results-dialog {
  .q-field {
    padding-left: 10px;
    padding-right: 10px;
  }

  .export-results-header {
    padding-top: 10px;
    padding-left: 15px;
  }
  .grid-selections {
    padding-left: 15px;
  }
  .group-selections {
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
