<template>
  <ReadyToSubmit></ReadyToSubmit>
</template>

<script>
import { defineComponent } from "vue";
import ReadyToSubmit from "../../components/analysis/ReadyToSubmit.vue";

export default defineComponent({
  name: "ReviewAndSubmit",
  components: {
    ReadyToSubmit,
  },

  setup(props, context) {},
});
</script>
