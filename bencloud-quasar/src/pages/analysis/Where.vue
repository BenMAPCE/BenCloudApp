<template>
    <p class="location-header">Where do you want to perform your analysis?</p>
    <Location></Location>
</template>

<script>
import { defineComponent } from 'vue';
import Location from "../../components/analysis/Location.vue";

export default defineComponent({
  name: 'Where',
  components: {
    Location
  }
})
</script>

<style scoped>
  .location-header {
    font-size: 16px;
    font-weight: 500
  }
</style>