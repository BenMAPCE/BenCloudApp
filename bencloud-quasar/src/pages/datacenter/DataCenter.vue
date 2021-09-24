<template>
  <q-page>
    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="manage-tasks">
        <q-card-section> View your active and complete tasks. </q-card-section>
        <q-card-section>
          <q-btn
            color="primary"
            icon-right="mdi-grid"
            push
            @click="$router.replace('/datacenter/manage-tasks')"
            label="Manage Tasks"
          />
        </q-card-section>
      </q-card>
    </div>
    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="my-card">
        <q-card-section> Review Air Quality Layers </q-card-section>
        <q-card-section>
          <q-btn
            color="secondary"
            icon-right="mdi-air-filter"
            push
            @click="$router.replace('/datacenter/review-air-quality')"
            label="Review Air Quality Layers"
          />
        </q-card-section>
      </q-card>

      <q-card class="my-card">
        <q-card-section> Upload your own Air Quality Layers </q-card-section>
        <q-card-section>
          <q-btn
            color="secondary"
            icon-right="mdi-cloud-upload-outline"
            push
            @click="$router.replace('/datacenter/upload-air-quality')"
            label="Upload Air Quality Layers"
          />
        </q-card-section>
      </q-card>
    </div>
    <div class="q-pa-md q-gutter-sm">
      <p class="description"></p>
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
