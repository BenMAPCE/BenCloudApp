<template>
  <q-page>

    <ProgressStepper></ProgressStepper>

  </q-page>
</template>

<script>
import { defineComponent } from 'vue';
import ProgressStepper from 'components/ProgressStepper.vue'

export default defineComponent({
  name: 'HelpPage',
    components: {
        ProgressStepper
  }
})


</script>