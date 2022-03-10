<template>
  <div class="air-quality-pre-policy">
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
import { defineComponent, onActivated } from "vue";
import { ref, watch, onMounted, onBeforeUpdate, onUpdated } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "AirQualityPrePolicyMetricSelection",

  components: {},

  setup(props, context) {
    const store = useStore();
    const selectedItem = ref(0);

    const options = ref([]);
    const rows = ref([]);

    watch(
      () => store.state.analysis.prePolicyAirQualityId,
      (currentSelectedItem, prevSelectedItem) => {
        console.log(
          "watch prePolicyAirQualityId: " + currentSelectedItem + " | " + prevSelectedItem
        );
        loadAirQualityMetricsWhenReady(true);
      }
    );

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " | " + prevSelectedItem);
        if (currentSelectedItem != prevSelectedItem) {
          store.commit("analysis/updatePrePolicyAirQualityMetricId", currentSelectedItem);

          console.log(
            "prePolicyAirQualityId currentSelectedItem: " + currentSelectedItem
          );
          loadMetricDetailsWhenReady(currentSelectedItem);
        } else {
          console.log("*** SAME VALUE");
        }
      }
    );

    function loadAirQualityMetricsWhenReady(stateChange) {
      (async () => {
        console.log("waiting for airQualityLayers");
        while (!store.state.analysis.airQualityLayers)
          // define the condition as you like
          await new Promise((resolve) => setTimeout(resolve, 1000));
        console.log("airQualityLayers is defined");
        loadAirQualityMetrics(stateChange);
      })();
    }

    function loadAirQualityMetrics(stateChange) {
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
        if (airQualityLayers[i].id == store.state.analysis.prePolicyAirQualityId) {
          console.log(airQualityLayers[i].id);
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
                metric_statistics[m].seasonal_metric_id;
            }
          }

         if (stateChange) {
            selectedItem.value = selectedMetrics;
          } else if (store.state.analysis.prePolicyAirQualityMetricId) {
            selectedItem.value = store.state.analysis.prePolicyAirQualityMetricId;
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
        if (airQualityLayers[i].id == store.state.analysis.prePolicyAirQualityId) {
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

              row = {};
              row.input_file_characteristic = "Number of grid cells above LRL";
              row.value = metric_statistics[m].cell_count_above_lrl;
              rows.value.push(row);
            }
          }
        }
      }
    }

    onMounted(() => {
      // var prePolicyAirQualityId = store.state.analysis.prePolicyAirQualityId;

      if (store.state.analysis.prePolicyAirQualityId) {
        console.log(" ### updating prePolicyAirQualityId");

        loadAirQualityMetricsWhenReady(false);

        console.log(options.value);

        selectedItem.value = store.state.analysis.prePolicyAirQualityMetricId;
        console.log("metricId: " + store.state.analysis.prePolicyAirQualityMetricId);
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
.air-quality-pre-policy {
  .metric-characteristics {
    margin-top: 25px;
    max-width: 95%;
  }
  .metric-options {
    max-width: 95%;
    margin-top: 15px;
    .q-field__label {
      padding-bottom: 20px;
    }
  }
}
</style>
