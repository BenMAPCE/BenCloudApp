<template>
  <q-page>
    
    <ExposureProgressStepper></ExposureProgressStepper>

  </q-page>
</template>  

<script>
import { defineComponent } from "vue";
import ExposureProgressStepper from 'src/components/navigation/ExposureProgressStepper.vue'

export default defineComponent({
  name: 'HelpPage',
    components: {
        ExposureProgressStepper
  }
})

</script>