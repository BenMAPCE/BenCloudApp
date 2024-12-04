<template>
  <div>
    <div id="map" ref="mapContainer" class="map-container"></div>
  </div>
</template>

<script>
import Map from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import XYZ from "ol/source/XYZ";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import { fromLonLat } from "ol/proj";
import GeoJSON from "ol/format/GeoJSON";
import { Style, Stroke, Fill, Text } from "ol/style";
import Control from "ol/control/Control";
import { defaults as defaultControls } from "ol/control";

export default {
  name: "LayerControlMapWithZoomFilter",
  data() {
    return {
      mapContainer: null,
      map: null,
      layers: {
        state: null,
        county: null,
        nation: null,
        grid12km: null,
      },
      defaultView: {
        center: fromLonLat([-98.5795, 39.8283]),
        zoom: 4.3,
      },
    };
  },
  computed: {
    visibleLayers() {
      return this.$store.state.grids.visibleLayers;
    },
  },
  watch: {
  visibleLayers: {
    handler(newVal) {
      // Iterate through all defined layers
      Object.keys(this.layers).forEach((key) => {
        if (this.layers[key]) {
          // Set visibility based on Vuex state
          this.layers[key].setVisible(newVal.includes(key));
        }
      });
    },
    immediate: true, // Ensure it runs immediately after the component is mounted
    deep: true, // Track changes in the array
  },
},
  methods: {
    initializeMap() {
      const stateStyle = (feature) => {
        const stateName = feature.get("state_usps");
        return new Style({
          stroke: new Stroke({
            color: "#434343",
            width: 0.5,
            lineJoin: "bevel",
          }),
          fill: new Fill({
            color: "rgba(0, 0, 0, 0)",
          }),
          text: new Text({
            font: "13px Arial",
            text: stateName || "",
            fill: new Fill({
              color: "#767676",
            }),
            offsetY: -10,
          }),
        });
      };

      const countyStyle = (feature) => {
        const countyName = feature.get("name");
        return new Style({
          stroke: new Stroke({
            color: "#a5a5a5",
            width: 0.5,
            lineJoin: "bevel",
          }),
          fill: new Fill({
            color: "rgba(0, 0, 0, 0)",
          }),
          text: new Text({
            font: "13px Arial",
            text: countyName || "",
            fill: new Fill({
              color: "#323232",
            }),
            offsetY: -10,
          }),
        });
      };

      const nationStyle = new Style({
        stroke: new Stroke({
          color: "#232323",
          width: 2,
          lineJoin: "bevel",
        }),
        fill: new Fill({
          color: "rgba(0, 0, 0, 0)",
        }),
      });

      const grid12kmStyle = new Style({
        stroke: new Stroke({
          color: "#000000",
          width: 0.1,
        }),
        fill: new Fill({
          color: "rgba(0, 0, 0, 0)",
        }),
      });

      this.layers.state = new VectorLayer({
        source: new VectorSource({
          url: "http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap:us_state_usps&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326",
          format: new GeoJSON({ featureProjection: "EPSG:3857" }),
        }),
        style: stateStyle,
      });
      this.layers.state.setVisible(false);


      this.layers.county = new VectorLayer({
        source: new VectorSource({
          url: "http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap:us_county&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326",
          format: new GeoJSON({ featureProjection: "EPSG:3857" }),
        }),
        style: countyStyle,
      });
      this.layers.county.setVisible(false);


      this.layers.nation = new VectorLayer({
        source: new VectorSource({
          url: "http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap:us_nation&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326",
          format: new GeoJSON({ featureProjection: "EPSG:3857" }),
        }),
        style: nationStyle,
      });
      this.layers.nation.setVisible(false);


      this.layers.grid12km = new VectorLayer({
        source: new VectorSource({
          url: "http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap:us_cmaq_12km_nation&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326",
          format: new GeoJSON({ featureProjection: "EPSG:3857" }),
        }),
        style: grid12kmStyle,
      });
      this.layers.grid12km.setVisible(false);


      const baseLayer = new TileLayer({
        source: new XYZ({
          urls: [
            "https://server.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer/tile/{z}/{y}/{x}",
          ],
        }),
      });

      this.map = new Map({
        target: this.mapContainer,
        layers: [
          baseLayer,
          this.layers.state,
          this.layers.county,
          this.layers.nation,
          this.layers.grid12km,
        ],
        view: new View({
          center: this.defaultView.center,
          zoom: this.defaultView.zoom,
        }),
        controls: defaultControls({ rotate: false }),
      });

      const resetControl = new Control({
        element: document.createElement("button"),
      });
      resetControl.element.innerHTML = "⤾";
      resetControl.element.title = "Reset Map";
      resetControl.element.className = "reset-control ol-unselectable ol-control";

      resetControl.element.addEventListener("click", () => {
        this.map.getView().setCenter(this.defaultView.center);
        this.map.getView().setZoom(this.defaultView.zoom);
      });

      this.map.addControl(resetControl);
    },
  },
  mounted() {
    this.mapContainer = this.$refs.mapContainer;
    this.initializeMap();
  },
};
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 58vh;
  border: 1px solid #ccc;
}
.reset-control {
  background: white;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 5px 10px;
  cursor: pointer;
  font-size: 1rem;
}
.reset-control:hover {
  background: #f0f0f0;
}
</style>
