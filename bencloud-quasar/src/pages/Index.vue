<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <p class="description">
        You can start a new analysis, or select a template to start your analysis
      </p>
    </div>

    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="start-tasks">
        <q-card-section> Start a new analysis </q-card-section>
        <q-card-section>
          <q-btn
            color="secondary"
            icon-right="mdi-home"
            push
            @click="$router.replace('analysis')"
            label="New Analysis"
          />
        </q-card-section>
      </q-card>

      <q-card class="manage-tasks">
        <q-card-section> Start a new analysis from a template </q-card-section>
        <q-card-section>
          <q-select
            v-model="model"
            :options="options"
            label="Select a template"
            class="template-select"
          >
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
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script>
import { defineComponent, ref } from "vue";
import AppTopNavigation from "../components/navigation/AppTopNavigation.vue";

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
  padding-bottom: 25px;
}
</style>
