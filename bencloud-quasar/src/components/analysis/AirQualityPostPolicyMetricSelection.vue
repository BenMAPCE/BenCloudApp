<template>
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
import { defineComponent, onActivated } from "vue";
import { ref, watch, onMounted, onBeforeUpdate, onUpdated } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "AirQualityPostPolicyMetricSelection",

  components: {},

  setup(props, context) {
    const store = useStore();
    const selectedItem = ref(0);

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
        loadAirQualityMetrics();
      }
    );

    watch(
      () => selectedItem.value,
      (currentSelectedItem, prevSelectedItem) => {
        console.log("watch: " + currentSelectedItem + " | " + prevSelectedItem);
        if (currentSelectedItem != prevSelectedItem) {
          store.commit(
            "analysis/updatePostPolicyAirQualityMetricId",
            currentSelectedItem
          );

          console.log(
            "postPolicyAirQualityId currentSelectedItem: " + currentSelectedItem
          );
          loadMetricDetails(currentSelectedItem);
        } else {
          console.log("*** SAME VALUE");
        }
      }
    );

    function loadAirQualityMetrics() {
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
        if (airQualityLayers[i].id == store.state.analysis.postPolicyAirQualityId) {
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
          selectedItem.value = selectedMetrics;
          console.log("*** " + selectedItem.value);

          //console.log(options)
        }
      }
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
        if (airQualityLayers[i].id == store.state.analysis.postPolicyAirQualityId) {
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
              row.input_file_characteristic = "Mean value (ug/m3)";
              row.value = Math.round(metric_statistics[m].mean_value * 100) / 100;

              rows.value.push(row);

              row = {};
              row.input_file_characteristic = "2.5th and 97.5th value (ug/m3)";
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
      var postPolicyAirQualityId = store.state.analysis.postPolicyAirQualityId;
      var postPolicyAirQualityName = store.state.analysis.postPolicyAirQualityName;

      if (null != postPolicyAirQualityId) {
        console.log(" ### updating postPolicyAirQualityId");

        loadAirQualityMetrics();

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
      options,
      rows,
      columns,
      loadMetricDetails,
      // selectedPollutantId,
      // selectedPollutantFriendlyName
    };
  },
});
</script>

<style lang="scss">
.air-quality-post-policy {
  .metric-characteristics {
    margin-top: 25px;
    max-width: 95%;
  }
  .metric-options {
    margin-top: 15px;
    max-width: 95%;
    .q-field__label {
      padding-bottom: 20px;
    }
  }
}
</style>
