<template>
  <div>
    <q-spinner v-if="loading" size="50px" color="blue" class="loading-spinner" />
    <div id="map" ref="mapContainer" class="map-container"></div>
  </div>
</template>

<script>
import 'ol/ol.css';
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
import { ref } from "vue";

export default {
  name: "LayerControlMapWithZoomFilter",
  data() {
    return {
      mapContainer: null,
      map: null,
      layers: {
        state: null,
        county2010: null,
        county2020: null,
        nation: null,
        grid12km: null,
      },
      defaultView: {
        center: fromLonLat([-98.5795, 39.8283]),
        zoom: 4.3,
      },
      loading: false,
      loadingLayers: new Set(),
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
        Object.keys(this.layers).forEach((key) => {
          if (this.layers[key]) {
            const isVisible = newVal.includes(key);
            
            // If we're making a layer visible and it wasn't before
            if (isVisible && !this.layers[key].getVisible()) {
              // Track loading for vector layers that need to fetch data
              const source = this.layers[key].getSource();
              if (source instanceof VectorSource && source.getFeatures().length === 0) {
                this.trackLayerLoading(this.layers[key]);
              }
            }
            
            this.layers[key].setVisible(isVisible);
          }
        });
      },
      immediate: true,
      deep: true,
    },
  },
  methods: {
    trackLayerLoading(layer) {
      const source = layer.getSource();
      const layerId = layer.get('id') || Math.random().toString(36).substring(2, 9);
      
      // Add to loading set
      this.loadingLayers.add(layerId);
      this.loading = true;
      console.log(`Layer ${layerId} started loading. Total loading layers: ${this.loadingLayers.size}`);
      
      if (source instanceof VectorSource) {
        const loadStartListener = () => {
          this.loading = true;
          console.log(`Layer ${layerId} load start. Total loading layers: ${this.loadingLayers.size}`);
        };
        
        const loadEndListener = () => {
          this.loadingLayers.delete(layerId);
          this.loading = this.loadingLayers.size > 0;
          console.log(`Layer ${layerId} load end. Total loading layers: ${this.loadingLayers.size}`);
          
          if (this.loadingLayers.size === 0) {
            this.loading = false;
            console.log('All layers loaded.');
          }
          // Clean up event listeners
          source.un('featuresloadstart', loadStartListener);
          source.un('featuresloadend', loadEndListener);
          source.un('featuresloaderror', loadErrorListener);
        };
        
        const loadErrorListener = () => {
          this.loadingLayers.delete(layerId);
          this.loading = this.loadingLayers.size > 0;
          console.log(`Layer ${layerId} load error. Total loading layers: ${this.loadingLayers.size}`);
          
          if (this.loadingLayers.size === 0) {
            this.loading = false;
            console.log('All layers loaded.');
          }
          // Clean up event listeners
          source.un('featuresloadstart', loadStartListener);
          source.un('featuresloadend', loadEndListener);
          source.un('featuresloaderror', loadErrorListener);
        };
        
        source.on('featuresloadstart', loadStartListener);
        source.on('featuresloadend', loadEndListener);
        source.on('featuresloaderror', loadErrorListener);
      }
    },
    
    initializeMap() {
      const stateStyle = (feature) => {
        const zoom = this.map ? this.map.getView().getZoom() : 4;
        const stateName = feature.get("state_usps");
        const showText = zoom > 4;

        return new Style({
          stroke: new Stroke({
            color: "#434343",
            width: 0.8,
            lineJoin: "bevel",
          }),
          fill: new Fill({
            color: "rgba(0, 0, 0, 0)",
          }),
        });
      };

      const countyStyle = () => {
        return new Style({
          stroke: new Stroke({
            color: "#595959",
            width: 0.4,
            lineJoin: "bevel",
          }),
          fill: new Fill({
            color: "rgba(0, 0, 0, 0)",
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
          color: "#3f3f3f",
          width: 0.1,
        }),
        fill: new Fill({
          color: "rgba(0, 0, 0, 0)",
        }),
      });

      // Create layers with identifiers
      this.layers.state = new VectorLayer({
        source: new VectorSource({
          url: "http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap%3Aus_state&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326",
          format: new GeoJSON({ featureProjection: "EPSG:3857" }),
        }),
        style: stateStyle,
        properties: {
          id: 'state-layer'
        }
      });
      this.layers.state.setVisible(false);

      this.layers.county2010 = new VectorLayer({
        source: new VectorSource({
          url: "http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap:us_county&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326",
          format: new GeoJSON({ featureProjection: "EPSG:3857" }),
        }),
        style: countyStyle,
        properties: {
          id: 'county2010-layer'
        }
      });
      this.layers.county2010.setVisible(false);

      this.layers.county2020 = new VectorLayer({
        source: new VectorSource({
          url: "http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap%3Aus_county_2020&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326",
          format: new GeoJSON({ featureProjection: "EPSG:3857" }),
        }),
        style: countyStyle,
        properties: {
          id: 'county2020-layer'
        }
      });
      this.layers.county2020.setVisible(false);

      this.layers.nation = new VectorLayer({
        source: new VectorSource({
          url: "http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap:us_nation&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326",
          format: new GeoJSON({ featureProjection: "EPSG:3857" }),
        }),
        style: nationStyle,
        properties: {
          id: 'nation-layer'
        }
      });
      this.layers.nation.setVisible(false);

      this.layers.grid12km = new VectorLayer({
        source: new VectorSource({
          url: "http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap:us_cmaq_12km_nation&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326",
          format: new GeoJSON({ featureProjection: "EPSG:3857" }),
        }),
        style: grid12kmStyle,
        properties: {
          id: 'grid12km-layer'
        }
      });
      this.layers.grid12km.setVisible(false);

      const baseLayer = new TileLayer({
        source: new XYZ({
          urls: [
            "https://server.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer/tile/{z}/{y}/{x}",
          ],
        }),
        properties: {
          id: 'base-layer'
        }
      });

      const layers = [
        baseLayer,
        this.layers.state,
        this.layers.county2010,
        this.layers.county2020,
        this.layers.nation,
        this.layers.grid12km,
      ];

      this.map = new Map({
        target: this.mapContainer,
        layers: layers,
        view: new View({
          center: this.defaultView.center,
          zoom: this.defaultView.zoom,
        }),
        theme: null,
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
      
      // Map level loading events
      this.map.on('loadstart', () => {
        this.loading = true;
        console.log('Map load start.');
      });
      
      this.map.on('loadend', () => {
        if (this.loadingLayers.size === 0) {
          this.loading = false;
          console.log('Map load end. All layers loaded.');
        }
      });
    },
  },
  mounted() {
    this.mapContainer = this.$refs.mapContainer;
    this.initializeMap();
  },
};
</script>

<style>
.map-container {
  width: 100%;
  height: 58vh;
  border: 1px solid #ccc;
  position: relative;
}

.loading-spinner {
  position: absolute;
  top: 40%;
  left: 45%;
  transform: translate(-50%, -50%);
  z-index: 2000; 
}

.reset-control,
.map-container .ol-zoom button {
  width: 40px;
  height: 40px;
  background: white;
  border: 1px solid #ccc;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.reset-control:hover,
.map-container .ol-zoom button:hover {
  border: 1px solid #000000;
  border-radius: 5px;
  background: #f0f0f0;
}

.map-container .ol-zoom {
  top: .5em;
  left: .5em;
  display: flex;
  flex-direction: column;
  padding: 0;
  background: transparent;
}

.ol-control.reset-control {
  position: absolute;
  top: 4em;
  left: 0.35em;
  margin: 0;
}

</style>