<template>
	  <div class="q-ml-sm" v-html="valuations"></div>
</template>

<script>
import { defineComponent } from "vue";
import { ref } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "Test",

props: {
    updateState: {
      type: Boolean,
      default: false,
    },
    valuations: {
      type: String,
      default: "<strong>NAME</strong>"
    }
  },

  setup(props, { emit }) {
    const store = useStore();
    const pollutantValue = ref(null);
    const options = ref([]);

  }
  
});
</script>
