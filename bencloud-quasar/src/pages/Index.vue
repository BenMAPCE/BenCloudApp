<template>
  <q-page>

    <div class="q-pa-md q-gutter-sm">
      <p class="description">
        You can start a new analysis, or select a template to start your analysis
      </p>
    </div>

    <div class="row">
      <div class="col-2">
    <div class="q-pa-md q-gutter-sm">
      <q-btn
        color="secondary"
        icon-right="mdi-home"
        push
        @click="$router.replace('analysis')"
        label="New Analysis"
      />
    </div>
</div>
      <div class="col-2">

      <q-select v-model="model" :options="options" label="Select a template" class="template-select">
        <template v-slot:prepend>
          <q-icon name="mdi-file-cabinet" />
        </template>
      </q-select>

      <q-btn
        color="secondary"
        icon-right="mdi-home"
        push
        @click="$router.replace('analysis')"
        label="Analysis From Template"
      />
          </div>

    </div>


  </q-page>
</template>

<script>
import { defineComponent, ref } from "vue";
import AppTopNavigation from "../components/AppTopNavigation.vue";

export default defineComponent({
  name: "PageIndex",
  components: {},

  setup() {
    return {
      model: ref(null),

      options: [
        "Template 01",
        "Template 02",
        "Template 03",
        "Template 04",
        "Template 05",
      ],
    };
  },
});
</script>

<style scoped>
.description {
  font-size: 18px;
  font-weight: 400;
}

.template-select {
  padding-bottom: 25px;;
}
</style>