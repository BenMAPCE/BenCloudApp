<template>
  <q-page>
    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="manage-tasks task-card">
        <q-card-section> View or upload air quality data. </q-card-section>
        <q-card-actions vertical>
          <q-btn
            color="primary"
            icon-right="mdi-air-filter"
            push
            @click="$router.replace('/datacenter/review-air-quality')"
            label="Review Air Quality"
          />
        </q-card-actions>
      </q-card>
      <q-card class="my-card task-card">
        <q-card-section> View or upload incidence data. </q-card-section>
        <q-card-actions vertical>
          <q-btn
            color="secondary"
            icon-right="mdi-medical-bag"
            push
            @click="$router.replace('/datacenter/review-incidence')"
            label="Review Incidence"
          />
        </q-card-actions>
      </q-card>
      <q-card class="my-card task-card">
        <q-card-section> View or upload grid definitions. </q-card-section>
        <q-card-actions vertical>
          <q-btn
            color="accent"
            icon-right="mdi-grid"
            push
            @click="$router.replace('/datacenter/review-grids')"
            label="Review Grid Definitions"
          />
        </q-card-actions>
      </q-card>
    </div>
  </q-page>
</template>

<script>
import { defineComponent } from "vue";

export default defineComponent({
  name: "DataCenter",
  components: {},
});
</script>

<style scoped>
  .task-card {
    width: 280px;
  }
</style>
