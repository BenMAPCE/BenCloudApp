<template>
  <ul class="status-messages">
    <li v-for="item in status" :key="item.message">
      <q-icon v-if="item.status == 'complete'" name="mdi-check" />
      <q-icon v-if="item.status == 'active'" name="mdi-autorenew" />
      {{ item.message }}
    </li>
  </ul>

</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onUpdated, onMounted } from "vue";

export default defineComponent({
  model: ref(null),
  name: "ActiveTaskStatus",

  props: {
    status: {
      type: Array,
      default: ref([]),
    },
  },

  setup(props, context) {
    const statusMessages = ref("")

    onUpdated(() => {
        // console.log(props.status);
        statusMessages.value = "SSSS"
    })

    onMounted(() => {
      
        //console.log(props.status);


      })

    return {
        statusMessages,
      };
    },
});
</script>

<style lang="scss" scoped>

.status-messages {
  list-style: none;
  text-align: left;
  padding-left: 0px;
}

</style>
