<template>
  <q-table
    :rows="data"
    :columns="columns"
    row-key="id"
    v-model:selected="selected"
    :visible-columns="visibleColumns"
    class:wider-table
  >
    <template v-slot:body="props">
      <q-tr :props="props">
        <q-td v-for="col in props.cols" :key="col.name" :props="props">
          {{ col.value }}
        </q-td>
      </q-tr>
    </template>
  </q-table>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      data: [],
      selected: [],
      visibleColumns: [
        { name: "name", label: "Name" },
        { name: "id", label: "ID" },
        { name: "grid_definition_id", label: "Grid Definition ID" },
      ],
      columns: [
        { name: "name", required: true, label: "Name", align: "left" },
        { name: "id", label: "ID", align: "left" },
        { name: "grid_definition_id", label: "Grid Definition ID", align: "left" },
      ],
    };
  },
  mounted() {
    axios
      .get(process.env.API_SERVER + "/api/incidence")
      .then((response) => {
        this.data = response.data;
      })
      .catch((error) => {
        console.error(error);
      });
  },
};
</script>
<style>
.wider-table {
  width: 100%; /* Set the desired width here */
  margin-left: 0;
}
</style>