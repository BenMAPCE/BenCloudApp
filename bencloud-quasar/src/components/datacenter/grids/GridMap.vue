<template>
  <div>
    <div class="layer-options">
      <label>
        <input type="checkbox" v-model="layersVisible.state" @change="toggleLayer('state')" /> State
      </label>
      <label>
        <input type="checkbox" v-model="layersVisible.county" @change="toggleLayer('county')" /> County
      </label>
      <label>
        <input type="checkbox" v-model="layersVisible.nation" @change="toggleLayer('nation')" /> Nation
      </label>
      <label>
  <input type="checkbox" v-model="layersVisible.grid12km" @change="toggleLayer('grid12km')" />
  12km Grid
</label>
    </div>

    <div id="map" ref="mapContainer" class="map-container"></div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import Map from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import XYZ from "ol/source/XYZ";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import { fromLonLat } from "ol/proj";
import GeoJSON from "ol/format/GeoJSON";
import { Style, Stroke, Fill, Text } from 'ol/style';
import Control from "ol/control/Control";
import { defaults as defaultControls } from "ol/control";


export default {
  name: "LayerControlMapWithZoomFilter",
  setup() {
    const mapContainer = ref(null);
    const map = ref(null);

    const layersVisible = ref({
      state: true,
      county: false,  
      nation: false,
      grid12km: false, 

    });

    // Layers reference to toggle visibility
    const layers = ref({
      state: null,
      county: null,
      nation: null,
      grid12km: null, 

    });

    const initializeMap = () => {
  
      //default view for reset button and inital render
      const defaultView = {
    center: fromLonLat([-98.5795, 39.8283]),
    zoom: 4.3,
  };


//Set styles for state, county, nation, and 12km
      const stateStyle = (feature) => {
        const stateName = feature.get('state_usps');

        return new Style({

  stroke: new Stroke({
    color: '#434343', 
    width: 0.5, 
    lineJoin: 'bevel', 
  }),
  fill: new Fill({
    color: 'rgba(0, 0, 0, 0)', 
  }),
  text: new Text({
    font: '13px Arial', 
    text: stateName || '', 
    fill: new Fill({
      color: '#767676', 
    }),
    offsetY: -10, // Adjust placement
  }),
});
};


const countyStyle = (feature) => {
  const countyName = feature.get('name');
  return new Style({
  stroke: new Stroke({
    color: '#a5a5a5', 
    width: 0.5, 
    lineJoin: 'bevel', // Line join style
  }),
  fill: new Fill({
    color: 'rgba(0, 0, 0, 0)', 
  }),
  text: new Text({
    font: '13px Arial', 
    text: countyName || '', 
    fill: new Fill({
      color: '#323232', 
    }),
    offsetY: -10, 
  }),
});
};



const nationStyle = new Style({
  stroke: new Stroke({
    color: '#232323', 
    width: 2, 
    lineJoin: 'bevel', 
  }),
  fill: new Fill({
    color: 'rgba(0, 0, 0, 0)', 
  }),
});
const grid12kmStyle = new Style({
  stroke: new Stroke({
    color: '#000000', 
    width: 0.1, // Very thin stroke
  }),
  fill: new Fill({
    color: 'rgba(0, 0, 0, 0)', 
  }),
});

      // Create vector source for State, County, Nation layers
      const stateSource = new VectorSource({
        url: 'http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap:us_state_usps&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326',
        format: new GeoJSON({
          featureProjection: 'EPSG:3857',
        }),
      });

      const countySource = new VectorSource({
        url: 'http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap:us_county&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326',
        format: new GeoJSON({
          featureProjection: 'EPSG:3857',
        }),
      });

      const nationSource = new VectorSource({
        url: 'http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap:us_nation&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326',
        format: new GeoJSON({
          featureProjection: 'EPSG:3857',
        }),
      });
      const grid12kmSource = new VectorSource({
  url: 'http://colo-wtest-1:8080/geoserver/benmap/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=benmap:us_cmaq_12km_nation&maxFeatures=1000000&outputFormat=application/json&srsName=EPSG:4326',
  format: new GeoJSON({
    featureProjection: 'EPSG:3857',
  }),
});

      // Create vector layers for State, County, Nation
      layers.value.state = new VectorLayer({
        source: stateSource,
        style: stateStyle,
      });

      layers.value.county = new VectorLayer({
        source: countySource,
        style: countyStyle,
      });

      layers.value.nation = new VectorLayer({
        source: nationSource,
        style: nationStyle,
      });
      
layers.value.grid12km = new VectorLayer({
  source: grid12kmSource,
  style: grid12kmStyle,
});

      // Base map layer
      const baseLayer = new TileLayer({
        source: new XYZ({
          urls: [
            'https://server.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer/tile/{z}/{y}/{x}',
          ],
        }),
      });

      // Initialize the map with base layer and vector layers
      map.value = new Map({
        target: mapContainer.value,
        layers: [
          baseLayer,
          layers.value.state,
          layers.value.county,
          layers.value.nation,
          layers.value.grid12km
        ],
        view: new View({
          center: defaultView.center,
          zoom: defaultView.zoom,
        }),
        controls: defaultControls({ rotate: false }), //remove the reset orientation button
        
      });
      
      //Add button to reset map to default zoom and position
      const resetControl = new Control({
  element: document.createElement("button"),
});

resetControl.element.innerHTML = "⤾"; // Symbol for reset
resetControl.element.title = "Reset Map";
resetControl.element.className = "reset-control ol-unselectable ol-control";

resetControl.element.addEventListener("click", () => {
  map.value.getView().setCenter(defaultView.center);
  map.value.getView().setZoom(defaultView.zoom);
});

map.value.addControl(resetControl);
      // Set initial visibility based on checkbox state

      layers.value.state.setVisible(layersVisible.value.state);
      layers.value.county.setVisible(layersVisible.value.county);
      layers.value.nation.setVisible(layersVisible.value.nation);
      layers.value.grid12km.setVisible(layersVisible.value.grid12km);


    };

    // Function to toggle visibility of layers based on checkbox state
    const toggleLayer = (layerType) => {
      const layer = layers.value[layerType];
      if (layer) {
        const isVisible = layersVisible.value[layerType];
        layer.setVisible(isVisible);
      }
    };
  

    onMounted(() => {
      initializeMap();
    });

    return {
      mapContainer,
      layersVisible,
      toggleLayer,
    };
  },
};
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 500px;
  margin-top: 20px;
  border: 1px solid #ccc;
}

.layer-options {
  display: flex;
  gap: 10px; 
  margin-bottom: 10px; 
}

label {
  font-size: 1.2rem; 
  display: flex;
  align-items: center;
}

input[type="checkbox"] {
  width: 18px; 
  height: 18px;
  margin-right: 5px; 
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
