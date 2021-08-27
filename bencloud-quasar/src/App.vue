<template>
  <router-view />
</template>
<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'App',
  created () {
    return Promise.all([
      // p = this.$store.dispatch('bencloud/getPollutantId'),
    ])
  },
})


</script>
