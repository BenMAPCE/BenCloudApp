<template>
  <q-page>
   <router-link to="/datacenter/manage-air-quality">Manage Air Quality</router-link> | 
   <router-link to="/datacenter/manage-tasks">Manage Tasks</router-link>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'DataCenter',
   components: {
  },
})
</script>
