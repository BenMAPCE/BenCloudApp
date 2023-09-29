<template>
  <q-page>
    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="manage-tasks task-card">
        <q-card-section> View your active and complete tasks. </q-card-section>
        <q-card-actions vertical>
          <q-btn
            color="primary"
            icon-right="mdi-file-tree"
            push
            @click="$router.replace('/datacenter/manage-tasks')"
            label="Manage Tasks"
          />
        </q-card-actions>
      </q-card>
      <q-card class="my-card task-card">
        <q-card-section> View or upload datasets. </q-card-section>
        <q-card-actions vertical>
          <q-btn
            color="secondary"
            icon-right="mdi-database-outline"
            push
            @click="$router.replace('/datacenter/manage-data')"
            label="Manage Data"
          />
        </q-card-actions>
      </q-card>
      <!-- <q-card class="my-card task-card">
        <q-card-section> Review Air Quality Layers </q-card-section>
        <q-card-actions vertical>
          <q-btn
            color="secondary"
            icon-right="mdi-air-filter"
            push
            @click="$router.replace('/datacenter/review-air-quality')"
            label="Review Air Quality Layers"
          />
        </q-card-actions>
      </q-card> -->
      <!-- <q-card class="my-card task-card">
        <q-card-section> Review Incidence Data </q-card-section>
        <q-card-actions vertical>
          <q-btn
            color="tertiary"
            icon-right="mdi-air-filter"
            push
            @click="$router.replace('/datacenter/review-incidence')"
            label="Review Incidence Data"
          />
        </q-card-actions>
      </q-card> -->

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
