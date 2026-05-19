<template>
  <div class="hif-results-map">

    <!-- Controls bar -->
    <div class="map-controls row q-pa-md q-gutter-md items-end">
      <div class="col-auto">
        <div class="control-label">Health Effect</div>
        <q-select
          v-model="selectedEndpoint"
          :options="endpointOptions"
          option-value="key"
          option-label="label"
          emit-value
          map-options
          dense
          outlined
          square
          style="min-width: 300px"
          :loading="loadingResults"
        />
      </div>

      <div class="col-auto">
        <div class="control-label">Display Metric</div>
        <q-select
          v-model="selectedMetric"
          :options="metricOptions"
          option-value="value"
          option-label="label"
          emit-value
          map-options
          dense
          outlined
          square
          style="min-width: 220px"
        />
      </div>

      <div class="col-auto">
        <div class="control-label">Spatial Scale</div>
        <q-select
          v-model="selectedGridId"
          :options="gridOptions"
          option-value="id"
          option-label="name"
          emit-value
          map-options
          dense
          outlined
          square
          style="min-width: 200px"
          :loading="loadingResults"
        />
      </div>

      <div class="col-auto">
        <div class="control-label">Color Scale</div>
        <q-select
          v-model="selectedColorScheme"
          :options="colorSchemeOptions"
          option-value="value"
          option-label="label"
          emit-value
          map-options
          dense
          outlined
          square
          style="min-width: 160px"
        />
      </div>

      <div class="col-auto">
        <q-btn
          color="primary"
          label="Apply"
          :loading="renderingMap"
          :disable="!selectedEndpoint || loadingResults"
          @click="applySelection"
        />
      </div>

      <div class="col-grow" />

      <div class="col-auto row q-gutter-md">
        <q-toggle v-model="showStateOverlay" label="State boundaries" dense />
        <q-toggle v-model="showCountyOverlay" label="County boundaries" dense />
      </div>
    </div>

    <!-- Error notice -->
    <div v-if="loadError" class="no-data-message q-pa-lg text-center text-negative">
      {{ loadError }}
    </div>

    <!-- No results notice -->
    <div v-else-if="!loadingResults && endpointOptions.length === 0" class="no-data-message q-pa-lg text-center text-grey-7">
      No health impact results available for this task.
    </div>

    <!-- Map + legend row -->
    <div v-else-if="!loadingResults || endpointOptions.length > 0" class="map-and-legend row q-px-md q-pb-md">

      <!-- Map -->
      <div class="col map-wrapper">
        <div v-if="renderingMap || loadingResults" class="map-loading-overlay">
          <q-linear-progress indeterminate color="primary" class="map-progress-bar" />
          <div class="map-loading-label">{{ loadingStatus }}</div>
        </div>
        <div ref="mapContainer" class="map-container"></div>

        <!-- Hover tooltip -->
        <div
          v-if="hoveredCell"
          class="map-tooltip"
          :style="{ top: tooltipY + 'px', left: tooltipX + 'px' }"
        >
          <div class="tooltip-title">{{ hoveredCell.endpoint }}</div>
          <div class="tooltip-row">
            <span class="tooltip-key">Change in Incidence:</span>
            <span class="tooltip-val">{{ fmt(hoveredCell.point_estimate) }}</span>
          </div>
          <div class="tooltip-row">
            <span class="tooltip-key">Population:</span>
            <span class="tooltip-val">{{ fmt(hoveredCell.population) }}</span>
          </div>
          <div class="tooltip-row">
            <span class="tooltip-key">Baseline Incidence:</span>
            <span class="tooltip-val">{{ fmt(hoveredCell.baseline) }}</span>
          </div>
          <div class="tooltip-row">
            <span class="tooltip-key">Change in AQ:</span>
            <span class="tooltip-val">{{ fmt(hoveredCell.delta_aq) }}</span>
          </div>
        </div>
      </div>

      <!-- Legend panel -->
      <div class="col-auto legend-panel q-ml-md">
        <div class="legend-title">{{ currentMetricLabel }}</div>

        <div v-if="legendStops.length" class="legend-gradient-wrap">
          <div class="legend-gradient" :style="legendGradientStyle"></div>
          <div class="legend-labels">
            <span>{{ fmtLegend(legendMax) }}</span>
            <span>{{ fmtLegend((legendMax + legendMin) / 2) }}</span>
            <span>{{ fmtLegend(legendMin) }}</span>
          </div>
        </div>

        <div class="legend-null-row q-mt-sm">
          <span class="legend-null-swatch"></span>
          <span class="legend-null-label">No data</span>
        </div>

        <q-separator class="q-my-md" />

        <div class="legend-stats" v-if="statsReady">
          <div class="stat-row"><span class="stat-label">Min:</span><span>{{ fmtLegend(legendMin) }}</span></div>
          <div class="stat-row"><span class="stat-label">Max:</span><span>{{ fmtLegend(legendMax) }}</span></div>
          <div class="stat-row"><span class="stat-label">Total:</span><span>{{ fmtLegend(legendTotal) }}</span></div>
          <div class="stat-row"><span class="stat-label">Cells:</span><span>{{ legendCount.toLocaleString() }}</span></div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import 'ol/ol.css';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import XYZ from 'ol/source/XYZ';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import { fromLonLat } from 'ol/proj';
import GeoJSON from 'ol/format/GeoJSON';
import { Style, Stroke, Fill } from 'ol/style';
import { defaults as defaultControls } from 'ol/control';

import { defineComponent, ref, shallowRef, computed, watch, onMounted, onUnmounted, nextTick } from 'vue';
import axios from 'axios';

const METRIC_OPTIONS = [
  { label: 'Change in Incidence (Cases)', value: 'point_estimate' },
  { label: 'Population Exposed',          value: 'population'     },
  { label: 'Baseline Incidence',          value: 'baseline'       },
  { label: 'Change in Air Quality',       value: 'delta_aq'       },
];

const COLOR_SCHEMES = [
  { label: 'Blue → Red',    value: 'blue-red'   },
  { label: 'White → Blue',  value: 'white-blue' },
  { label: 'Yellow → Red',  value: 'yellow-red' },
];

// Simple linear interpolation between two hex colors
function lerpColor(hexA, hexB, t) {
  const parse = h => [parseInt(h.slice(1,3),16), parseInt(h.slice(3,5),16), parseInt(h.slice(5,7),16)];
  const [r1,g1,b1] = parse(hexA);
  const [r2,g2,b2] = parse(hexB);
  const r = Math.round(r1 + (r2-r1)*t);
  const g = Math.round(g1 + (g2-g1)*t);
  const b = Math.round(b1 + (b2-b1)*t);
  return `rgb(${r},${g},${b})`;
}

const SCHEME_STOPS = {
  'blue-red':   ['#2166ac', '#f7f7f7', '#d6604d'],
  'white-blue': ['#f7fbff', '#6baed6', '#08306b'],
  'yellow-red': ['#ffffb2', '#fd8d3c', '#bd0026'],
};

function valueToColor(value, min, max, scheme) {
  if (value === null || value === undefined || isNaN(value)) return 'rgba(200,200,200,0.4)';
  const [lo, mid, hi] = SCHEME_STOPS[scheme] || SCHEME_STOPS['blue-red'];
  const t = max === min ? 0.5 : (value - min) / (max - min);
  const color = t < 0.5 ? lerpColor(lo, mid, t * 2) : lerpColor(mid, hi, (t - 0.5) * 2);
  return color;
}

export default defineComponent({
  name: 'HIFResultsMap',

  props: {
    task_uuid:    { type: String,  required: true },
    task_pop_id:  { type: String,  default: '' },
  },

  setup(props) {
    const mapContainer  = ref(null);
    const map           = shallowRef(null);
    const resultsLayer  = shallowRef(null);
    const stateLayer    = shallowRef(null);
    const countyLayer   = shallowRef(null);

    const loadingResults  = ref(false);
    const renderingMap    = ref(false);
    const loadingStatus   = ref('');
    const loadError       = ref('');

    const gridOptions     = ref([]);   // [{ id, name, table_name }] from /api/grid-definitions
    const selectedGridId  = ref(null); // pre-set to baseline grid; user may change

    const selectedGridTableName = computed(() => {
      const g = gridOptions.value.find(o => o.id === selectedGridId.value);
      return g?.table_name ?? '';
    });

    const allResults        = ref([]);  // flat array from API
    const endpointOptions   = ref([]);
    const selectedEndpoint  = ref(null);
    const cachedCellData    = ref([]);   // last-fetched grid-cell rows
    const cachedEndpoint    = ref(null); // endpoint key when cachedCellData was fetched
    const cachedGridId      = ref(null); // grid id when cachedCellData was fetched
    const selectedMetric    = ref('point_estimate');
    const selectedColorScheme = ref('blue-red');
    const metricOptions     = METRIC_OPTIONS;
    const colorSchemeOptions = COLOR_SCHEMES;

    const showStateOverlay  = ref(true);
    const showCountyOverlay = ref(false);

    const hoveredCell = ref(null);
    const tooltipX    = ref(0);
    const tooltipY    = ref(0);

    const legendMin   = ref(0);
    const legendMax   = ref(1);
    const legendTotal = ref(0);
    const legendCount = ref(0);
    const legendStops = ref([]);
    const statsReady  = ref(false);

    const currentMetricLabel = computed(() =>
      METRIC_OPTIONS.find(o => o.value === selectedMetric.value)?.label ?? ''
    );

    const legendGradientStyle = computed(() => {
      const [lo, mid, hi] = SCHEME_STOPS[selectedColorScheme.value] || SCHEME_STOPS['blue-red'];
      return { background: `linear-gradient(to bottom, ${hi}, ${mid}, ${lo})` };
    });

    // ── Load results, grid info, and available grids on mount ────────────────
    async function loadResults() {
      if (!props.task_uuid) return;
      loadingResults.value = true;
      loadingStatus.value  = 'Loading health data…';
      loadError.value = '';
      try {
        const [resultsRes, gridInfoRes, gridDefsRes] = await Promise.all([
          axios.get(
            `${process.env.API_SERVER}/api/health-impact-result-datasets/${props.task_uuid}/contents`,
            { params: { gridId: 0, page: 1, rowsPerPage: 9999999 } }
          ),
          axios.get(
            `${process.env.API_SERVER}/api/health-impact-result-datasets/${props.task_uuid}/grid-info`
          ),
          axios.get(`${process.env.API_SERVER}/api/grid-definitions`),
        ]);

        allResults.value = Array.isArray(resultsRes.data) ? resultsRes.data : [];

        const baselineGridId = gridInfoRes.data?.grid_id ?? null;
        gridOptions.value = Array.isArray(gridDefsRes.data)
          ? gridDefsRes.data.map(g => ({ id: g.id, name: g.name, table_name: g.table_name }))
          : [];
        selectedGridId.value = baselineGridId;

        buildEndpointOptions();
      } catch (e) {
        console.error('HIFResultsMap: failed to load results', e);
        loadError.value = `Failed to load results: ${e?.response?.status ?? ''} ${e?.message ?? e}`;
      } finally {
        loadingResults.value = false;
      }
    }

    function buildEndpointOptions() {
      const seen = {};
      for (const row of allResults.value) {
        const key = `${row.endpoint}||${row.start_age}||${row.end_age}||${row.author}||${row.year}`;
        if (!seen[key]) {
          seen[key] = {
            key,
            label: `${row.endpoint} (${row.start_age}–${row.end_age} yrs, ${row.author} ${row.year})`,
          };
        }
      }
      endpointOptions.value = Object.values(seen);
      if (endpointOptions.value.length) {
        selectedEndpoint.value = endpointOptions.value[0].key;
      }
    }

    // ── Fetch per-grid-cell results for the selected endpoint and grid ───────
    async function loadGridCellResults() {
      if (!selectedEndpoint.value) return [];
      const [endpoint, startAge, endAge, author, year] = selectedEndpoint.value.split('||');
      try {
        const res = await axios.get(
          `${process.env.API_SERVER}/api/health-impact-result-datasets/${props.task_uuid}/contents`,
          {
            params: {
              page: 1,
              rowsPerPage: 9999999,
              gridId: selectedGridId.value ?? 0,
              hifEndpoint: endpoint,
              startAge,
              endAge,
              author,
              year,
            },
          }
        );
        return Array.isArray(res.data) ? res.data : [];
      } catch (e) {
        console.error('HIFResultsMap: failed to load grid-cell results', e);
        return [];
      }
    }

    // ── Apply selection: refetch only when endpoint or grid changed ──────────
    async function applySelection() {
      renderingMap.value = true;
      loadingStatus.value = 'Loading health data…';
      try {
        const needsFetch = selectedEndpoint.value !== cachedEndpoint.value ||
                           selectedGridId.value    !== cachedGridId.value;
        if (needsFetch) {
          cachedCellData.value  = await loadGridCellResults();
          cachedEndpoint.value  = selectedEndpoint.value;
          cachedGridId.value    = selectedGridId.value;
        }
        loadingStatus.value = 'Loading map features…';
        // rebuildResultsLayer takes ownership of renderingMap and clears it
        // after the WFS features load and the map finishes rendering.
        const released = rebuildResultsLayer(cachedCellData.value);
        if (!released) renderingMap.value = false;
      } catch {
        renderingMap.value = false;
      }
    }

    // ── Build / replace the choropleth VectorLayer ────────────────────────────
    // Returns true when it takes ownership of renderingMap (async WFS path),
    // false when it returns early so the caller can clear the flag.
    function rebuildResultsLayer(cellData) {
      if (!map.value) return false;

      if (resultsLayer.value) {
        map.value.removeLayer(resultsLayer.value);
        resultsLayer.value = null;
      }

      if (!cellData.length) {
        statsReady.value = false;
        legendStops.value = [];
        return false;
      }

      const metric = selectedMetric.value;
      const values = cellData.map(r => parseFloat(r[metric])).filter(v => !isNaN(v));
      const min = Math.min(...values);
      const max = Math.max(...values);

      legendMin.value   = min;
      legendMax.value   = max;
      legendTotal.value = values.reduce((a, b) => a + b, 0);
      legendCount.value = values.length;
      legendStops.value = [min, max];
      statsReady.value  = true;

      // API returns column field as "col" or "column" depending on grid type.
      const firstRow = cellData[0];
      const colField = 'col' in firstRow ? 'col' : 'column';

      const cellMap = {};
      for (const r of cellData) {
        cellMap[`${r[colField]}_${r.row}`] = r;
      }

      // table_name from the API is schema-qualified ("grids.us_cmaq_12km").
      // GeoServer publishes layers without the schema prefix, so strip it.
      const geoServerBaseUrl = process.env.GEOSERVER_BASE_URL;
      const workspaceName    = process.env.GEOSERVER_WORKSPACE_NAME;
      const scheme           = selectedColorScheme.value;
      const rawTableName     = selectedGridTableName.value;
      const layerName        = rawTableName.includes('.') ? rawTableName.split('.').pop() : rawTableName;

      if (!layerName) return false;

      const source = new VectorSource({
        url: `${geoServerBaseUrl}/${workspaceName}/ows?service=WFS&version=1.0.0` +
             `&request=GetFeature&typeName=${workspaceName}:${layerName}` +
             `&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326`,
        format: new GeoJSON({ featureProjection: 'EPSG:3857' }),
      });

      // Keep the progress bar up through the full pipeline:
      // WFS download → OL parse → style calls → canvas paint → done.
      source.once('featuresloadend', () => {
        loadingStatus.value = 'Rendering…';
        map.value?.once('rendercomplete', () => {
          renderingMap.value = false;
        });
      });
      source.once('featuresloaderror', () => { renderingMap.value = false; });

      resultsLayer.value = new VectorLayer({
        source,
        style: (feature) => {
          const col = feature.get('col');
          const row = feature.get('row');
          const cell = cellMap[`${col}_${row}`];
          const val  = cell ? parseFloat(cell[metric]) : NaN;
          return new Style({
            fill:   new Fill({ color: valueToColor(val, min, max, scheme) }),
            stroke: new Stroke({ color: 'rgba(0,0,0,0.08)', width: 0.3 }),
          });
        },
      });

      // Insert below boundary overlays
      const layers = map.value.getLayers().getArray();
      const insertIndex = layers.findIndex(l => l === stateLayer.value || l === countyLayer.value);
      if (insertIndex >= 0) {
        map.value.getLayers().insertAt(insertIndex, resultsLayer.value);
      } else {
        map.value.addLayer(resultsLayer.value);
      }

      // Pointer move → tooltip
      map.value.on('pointermove', (evt) => {
        const pixel = map.value.getEventPixel(evt.originalEvent);
        const feature = map.value.forEachFeatureAtPixel(
          pixel,
          f => f,
          { layerFilter: l => l === resultsLayer.value }
        );
        if (feature) {
          const col  = feature.get('col');
          const row  = feature.get('row');
          const cell = cellMap[`${col}_${row}`];
          if (cell) {
            hoveredCell.value = cell;
            const rect = mapContainer.value.getBoundingClientRect();
            tooltipX.value = evt.originalEvent.clientX - rect.left + 12;
            tooltipY.value = evt.originalEvent.clientY - rect.top  + 12;
          }
        } else {
          hoveredCell.value = null;
        }
      });

      return true; // spinner ownership transferred to featuresloadend/rendercomplete
    }

    // ── Map initialisation ────────────────────────────────────────────────────
    function initMap() {
      const geoServerBaseUrl = process.env.GEOSERVER_BASE_URL;
      const workspaceName    = process.env.GEOSERVER_WORKSPACE_NAME;

      const baseLayer = new TileLayer({
        source: new XYZ({
          urls: ['https://server.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer/tile/{z}/{y}/{x}'],
        }),
      });

      stateLayer.value = new VectorLayer({
        source: new VectorSource({
          url: `${geoServerBaseUrl}/${workspaceName}/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=${workspaceName}:us_state&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326`,
          format: new GeoJSON({ featureProjection: 'EPSG:3857' }),
        }),
        style: new Style({
          stroke: new Stroke({ color: '#434343', width: 1 }),
          fill:   new Fill({ color: 'rgba(0,0,0,0)' }),
        }),
        visible: showStateOverlay.value,
      });

      countyLayer.value = new VectorLayer({
        source: new VectorSource({
          url: `${geoServerBaseUrl}/${workspaceName}/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=${workspaceName}:us_county&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326`,
          format: new GeoJSON({ featureProjection: 'EPSG:3857' }),
        }),
        style: new Style({
          stroke: new Stroke({ color: '#595959', width: 0.4 }),
          fill:   new Fill({ color: 'rgba(0,0,0,0)' }),
        }),
        visible: showCountyOverlay.value,
      });

      map.value = new Map({
        target: mapContainer.value,
        layers: [baseLayer, stateLayer.value, countyLayer.value],
        view: new View({
          center: fromLonLat([-98.5795, 39.8283]),
          zoom: 4.3,
        }),
        controls: defaultControls({ rotate: false }),
        theme: null,
      });
    }

    // ── Watchers ──────────────────────────────────────────────────────────────
    watch(showStateOverlay,  v => stateLayer.value?.setVisible(v));
    watch(showCountyOverlay, v => countyLayer.value?.setVisible(v));

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    onMounted(async () => {
      await loadResults();
      await nextTick();
      initMap();
      if (selectedEndpoint.value) applySelection();
    });

    onUnmounted(() => {
      map.value?.setTarget(undefined);
    });

    // ── Helpers ───────────────────────────────────────────────────────────────
    function fmt(v) {
      if (v === null || v === undefined) return '—';
      return parseFloat(v).toLocaleString('en-US', { maximumFractionDigits: 2 });
    }
    function fmtLegend(v) {
      if (v === null || v === undefined || isNaN(v)) return '—';
      const abs = Math.abs(v);
      if (abs >= 1e6)  return (v / 1e6).toLocaleString('en-US', { maximumFractionDigits: 2 }) + 'M';
      if (abs >= 1e3)  return (v / 1e3).toLocaleString('en-US', { maximumFractionDigits: 2 }) + 'K';
      return v.toLocaleString('en-US', { maximumFractionDigits: 2 });
    }

    return {
      mapContainer,
      loadingResults, renderingMap, loadingStatus, loadError,
      gridOptions, selectedGridId,
      endpointOptions, selectedEndpoint,
      selectedMetric, metricOptions,
      selectedColorScheme, colorSchemeOptions,
      showStateOverlay, showCountyOverlay,
      hoveredCell, tooltipX, tooltipY,
      legendMin, legendMax, legendTotal, legendCount,
      legendStops, legendGradientStyle, statsReady,
      currentMetricLabel,
      applySelection, fmt, fmtLegend,
    };
  },
});
</script>

<style lang="scss" scoped>
.hif-results-map {
  position: relative;
}

.control-label {
  font-size: 12px;
  font-weight: 600;
  color: #555;
  margin-bottom: 4px;
}

.map-wrapper {
  position: relative;
}

.map-container {
  width: 100%;
  height: 58vh;
  border: 1px solid #ccc;
}

.map-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 2000;
  pointer-events: none;
}

.map-progress-bar {
  height: 4px;
}

.map-loading-label {
  font-size: 11px;
  color: #555;
  padding: 3px 6px;
  background: rgba(255, 255, 255, 0.85);
  display: inline-block;
  border-radius: 0 0 4px 0;
}

.map-tooltip {
  position: absolute;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 8px 10px;
  pointer-events: none;
  z-index: 1000;
  min-width: 220px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  font-size: 12px;

  .tooltip-title {
    font-weight: 700;
    margin-bottom: 6px;
    font-size: 13px;
    border-bottom: 1px solid #eee;
    padding-bottom: 4px;
  }

  .tooltip-row {
    display: flex;
    justify-content: space-between;
    margin-top: 3px;
    gap: 12px;
  }

  .tooltip-key { color: #666; }
  .tooltip-val { font-weight: 600; }
}

.legend-panel {
  width: 140px;
  flex-shrink: 0;
  padding-top: 4px;

  .legend-title {
    font-size: 12px;
    font-weight: 700;
    color: #333;
    margin-bottom: 8px;
    word-break: break-word;
  }

  .legend-gradient-wrap {
    display: flex;
    gap: 6px;
    align-items: stretch;
    height: 180px;
  }

  .legend-gradient {
    width: 20px;
    border: 1px solid #ccc;
    border-radius: 2px;
    flex-shrink: 0;
  }

  .legend-labels {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    font-size: 11px;
    color: #444;
  }

  .legend-null-row {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #666;
  }

  .legend-null-swatch {
    width: 20px;
    height: 14px;
    background: rgba(200, 200, 200, 0.4);
    border: 1px solid #ccc;
    border-radius: 2px;
    flex-shrink: 0;
  }

  .legend-stats {
    font-size: 12px;
    .stat-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 3px;
    }
    .stat-label { color: #666; }
  }
}

.no-data-message {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}
</style>
