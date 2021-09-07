<template>
  <q-select
    square
    dense
    outlined
    v-model="gridValue"
    :options="options"
    option-value="id"
    option-label="name"
    class="grid-options"
    emit-value
    map-options
    label="Grid"
  />
</template>

<script>
import { defineComponent } from "vue";
import { ref } from "vue";
import axios from "axios";

export default defineComponent({
  model: ref(null),
  name: "GridDefinitions",

  watch: {
    gridValue(newValue, oldValue) {
      if (newValue != oldValue) {
        this.gridValue = newValue
        this.$emit('changeGridValue', newValue)
      }
    },
  },

  data() {
    return {
      options: [],
      gridValue: ref(null),
      value: "",
      noti: () => {},
    };
  },

  mounted() {
 
    axios
      .get(process.env.API_SERVER + "/api/grid-definitions")
      .then((response) => {
        this.options = response.data;
        console.log(response.data);
      });
  },
});
</script>
