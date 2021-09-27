<template>
  <q-page>

    <ProgressStepper></ProgressStepper>

  </q-page>
</template>

<script>
import { defineComponent } from 'vue';
import ProgressStepper from 'src/components/navigation/ProgressStepper.vue'

export default defineComponent({
  name: 'HelpPage',
    components: {
        ProgressStepper
  }
})


</script>