<template>
  <ReadyToSubmit></ReadyToSubmit>
</template>

<script>
import { defineComponent } from "vue";
import ReadyToSubmit from "../../components/exposure/ReadyToSubmit.vue";

export default defineComponent({
  name: "ReviewAndSubmit",
  components: {
    ReadyToSubmit,
  },

  setup(props, context) {},
});
</script>
