<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <p class="description"></p>
    </div>

    <div class="q-pa-md q-gutter-sm">
      <div class="row">
        <div class="col-2">
          <q-btn
            color="primary"
            icon-right="mdi-grid"
            push
            @click="$router.replace('/datacenter/manage-tasks')"
            label="Manage Tasks"
          />
        </div>
      </div>
      <div class="row">
        <div class="col-2">
          <q-btn
            color="primary"
            icon-right="mdi-air-filter"
            push
            @click="$router.replace('/datacenter/manage-air-quality')"
            label="Manage Air Quality"
          />
        </div>
      </div>
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
