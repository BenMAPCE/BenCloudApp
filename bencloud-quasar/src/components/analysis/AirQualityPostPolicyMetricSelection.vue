<template>
  <div class="aq-post-policy-selection q-pt-sm q-pb-sm">
    <q-select
      square
      dense
      outlined
      v-model="selectedScenario"
      :options="scenarios"
      emit-value
      v-if="scenarios.length != 0"
      label="Post Policy Scenario"
    />
  </div>
  <div class="air-quality-post-policy">
    <div class="aaa">
      <q-select
        :class="{ 'metric-options': 1 === 1 }"
        v-model="selectedItem"
        filled
        :options="options"
        emit-value
        v-if="options.length != 0"
        map-options
        label="Metric, Seasonal Metric"
      />
    </div>
    <q-table
      :class="{ 'metric-characteristics': 1 === 1 }"
      :rows="rows"
      dense
      :columns="columns"
      :rows-per-page-options="[0]"
      v-if="rows.length != 0"
      :hide-pagination="true"
    >
    </q-table>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onMounted } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "AirQualityPostPolicyMetricSelection",

  components: {},

  setup(props, context) {
    const store = useStore();
    const selectedItem = ref();
    const scenarios = ref([]);
    const selectedScenario = ref(store.state.analysis.postPolicyAirQualitySelection);
    console.log(store.state.analysis.postPolicyAirQualitySelection);

    const options = ref([]);
    const rows = ref([]);

    watch(
      () => store.state.analysis.postPolicyAirQualityId,
      (currentSelectedItem, prevSelectedItem) => {
        console.log(
          "watch postPolicyAirQualityId: " +
            currentSelectedItem +
            " | " +
            prevSelectedItem
        );
        if(selectedScenario.value != null && !store.state.analysis.postPolicyAirQualityName.includes(selectedScenario.value)) {
          selectedScenario.value = null;
        }
        loadPostPolicyScenarios(true);
      }
    );

    watch(
      () => selectedScenario.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " | " + prevSelectedItem);
        if(selectedScenario.value === null) {
          selectedItem.value = null;
        }
        if(currentSelectedItem != prevSelectedItem) {
            store.commit(
              "analysis/updatePostPolicyAirQualitySelection",
              currentSelectedItem
            )
          loadAirQualityMetricsWhenReady(true);
        }
      }
    )

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " | " + prevSelectedItem);
        // remove the air quality layer id from the metrics
        if(currentSelectedItem === null || currentSelectedItem.length === 0) {
          options.value = [];
          rows.value = [];
        } else {
          var metrics = currentSelectedItem.split("-");
          if (currentSelectedItem != prevSelectedItem) {
            store.commit(
              "analysis/updatePostPolicyAirQualityMetricId",
              metrics[0] + "-" + metrics[1]
            );
            loadMetricDetailsWhenReady(currentSelectedItem);
          } else {
            console.log("*** SAME VALUE");
          }
        }
      }
    );

    function loadPostPolicyScenarios(stateChange) {
      scenarios.value = [];
      store.state.analysis.postPolicyAirQualityName.forEach(element => {
        scenarios.value.push(element.name);
      })
    }

    function loadAirQualityMetricsWhenReady(stateChange) {
      if(selectedScenario.value != null) {
        loadAirQualityMetrics(stateChange);
      }
    }

    function loadAirQualityMetrics(stateChange) {
      if (!store.state.analysis.airQualityLayers) {
        console.log("-- POST airQualityLayers is null");
        setTimeout(() => {
          console.log("waiting...");
        }, 5000);
      }

      var airQualityLayers = JSON.parse(
        JSON.stringify(store.state.analysis.airQualityLayers.records)
      );
      console.log("-----");
      console.log(airQualityLayers);
      console.log("-----");

      selectedItem.value = "99999-99999";

      console.log("selectedItem.value = " + selectedItem.value);
      options.value = [];
      var selectedMetrics = "";
      for (var i = 0; i < airQualityLayers.length; i++) {
        if (selectedScenario.value === airQualityLayers[i].name) {
          var metric_statistics = airQualityLayers[i].metric_statistics;
          for (var m = 0; m < metric_statistics.length; m++) {
            console.log(
              metric_statistics[m].metric_name +
                ", " +
                metric_statistics[m].seasonal_metric_name
            );

            var option = {};

            option = {};
            option.value =
              metric_statistics[m].metric_id +
              "-" +
              metric_statistics[m].seasonal_metric_id;
            option.label =
              metric_statistics[m].metric_name +
              ", " +
              metric_statistics[m].seasonal_metric_name;
            console.log(option);

            options.value.push(option);
            if (m === 0) {
              selectedMetrics =
                metric_statistics[m].metric_id +
                "-" +
                metric_statistics[m].seasonal_metric_id + 
                "-" + 
                // add air quality layer id to metrics to ensure that the file characteristics table is updated
                airQualityLayers[i].id;
            }
          }

          if (stateChange) {
            selectedItem.value = selectedMetrics;
          } else if (store.state.analysis.postPolicyAirQualityMetricId) {
            selectedItem.value = store.state.analysis.postPolicyAirQualityMetricId;
          }

          console.log("*** " + selectedItem.value);

          //console.log(options)
        }
      }
    }

    function loadMetricDetailsWhenReady(currentSelectedItem) {
      (async () => {

        console.log("waiting for airQualityLayers");
        while (!store.state.analysis.airQualityLayers)
          // define the condition as you like
          await new Promise((resolve) => setTimeout(resolve, 1000));
        console.log("airQualityLayers is defined");
        if (store.state.analysis.postPolicyAirQualityMetricId) {
          selectedItem.value = store.state.analysis.postPolicyAirQualityMetricId;
        }
        loadMetricDetails(currentSelectedItem);
      })();
    }

    function loadMetricDetails(metric_statistic_ids) {
      console.log("in loadMetricDetails");
      if (metric_statistic_ids === 0) {
        console.log("... ignore");
        return;
      }

      rows.value = [];

      var airQualityLayers = JSON.parse(
        JSON.stringify(store.state.analysis.airQualityLayers.records)
      );

      var metric_ids = metric_statistic_ids.split("-");

      for (var i = 0; i < airQualityLayers.length; i++) {
        if (selectedScenario.value == airQualityLayers[i].name) {
          console.log(airQualityLayers[i].id);

          var metric_statistics = airQualityLayers[i].metric_statistics;

          for (var m = 0; m < metric_statistics.length; m++) {
            console.log(parseInt(metric_ids[0]));
            console.log(parseInt(metric_ids[1]));

            if (
              metric_statistics[m].metric_id === parseInt(metric_ids[0]) &&
              metric_statistics[m].seasonal_metric_id === parseInt(metric_ids[1])
            ) {
              var row = {};

              row.input_file_characteristic = "AQ year";
              row.value = airQualityLayers[i].aq_year;
              rows.value.push(row);

              row = {};

              row.input_file_characteristic = "Source";
              row.value = airQualityLayers[i].source;
              rows.value.push(row);

              row = {};

              row.input_file_characteristic = "Data type";
              row.value = airQualityLayers[i].data_type;
              rows.value.push(row);

              row = {};

              row.input_file_characteristic = "Description";
              row.value = airQualityLayers[i].description;
              rows.value.push(row);

              row = {};

              row.input_file_characteristic = "filename";
              row.value = airQualityLayers[i].filename;
              rows.value.push(row);

              row = {};

              row.input_file_characteristic = "Number of grid cells";
              row.value = metric_statistics[m].cell_count;
              rows.value.push(row);

              row = {};
              row.input_file_characteristic = "Mean value";
              row.value = Math.round(metric_statistics[m].mean_value * 100) / 100;

              rows.value.push(row);

              row = {};
              row.input_file_characteristic = "2.5th and 97.5th value";
              row.value =
                Math.round(metric_statistics[m].pct_2_5 * 100) / 100 +
                " - " +
                Math.round(metric_statistics[m].pct_97_5 * 100) / 100;
              rows.value.push(row);
            }
          }
        }
      }
    }

    onMounted(() => {
      //var postPolicyAirQualityId = store.state.analysis.postPolicyAirQualityId;

      if (store.state.analysis.postPolicyAirQualityId) {
        console.log(" ### updating postPolicyAirQualityId");

        store.state.analysis.postPolicyAirQualityName.forEach(element => {
          scenarios.value.push(element.name);
        })

        loadAirQualityMetricsWhenReady(false);

        console.log(options.value);

        selectedItem.value = store.state.analysis.postPolicyAirQualityMetricId;
        console.log("metricId: " + store.state.analysis.postPolicyAirQualityMetricId);
      }
    });

    const columns = [
      {
        name: "input_file_characteristic",
        align: "left",
        label: "Input file characteristic",
        field: "input_file_characteristic",
        sortable: false,
      },
      {
        name: "value",
        align: "left",
        label: "Value",
        field: "value",
        sortable: false,
      },
    ];

    return {
      selectedItem,
      selectedScenario,
      scenarios,
      options,
      rows,
      columns,
      loadMetricDetails,
      loadAirQualityMetricsWhenReady,
      loadMetricDetailsWhenReady,
      // selectedPollutantId,
      // selectedPollutantFriendlyName
    };
  },
});
</script>

<style lang="scss" scoped>

.aq-post-policy-selection {
  max-width: 95%;
}
.air-quality-post-policy {
  .metric-characteristics {
    margin-top: 25px;
    max-width: 95%;
  }
  .metric-options {
    margin-top: 7px;
    max-width: 95%;
    .q-field__label {
      padding-bottom: 20px;
    }
  }
}
</style>
