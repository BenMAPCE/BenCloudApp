<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <EPAAccordion v-bind:showEpaHeaderFooter="showEpaHeaderFooter"></EPAAccordion>
      <EPAHeader v-bind:showEpaHeaderFooter="showEpaHeaderFooter"></EPAHeader>
      <div class="benmap-header">
        <q-toolbar>
          <q-toolbar-title>
            BenMAP - Benefits Mapping and Analysis Program
          </q-toolbar-title>

          <div>UI v{{ '0.4.2' }} | API v{{ apiVersion }} | DB v{{ dbVersion }} {{ environment === 'Production' ? '' : '(' + environment + ')' }}</div>
        </q-toolbar>
      </div>    </q-header>

    <q-page-container class="page-view">
      <router-view />
    </q-page-container>

    <EPAFooter v-bind:showEpaHeaderFooter="showEpaHeaderFooter"></EPAFooter>
  </q-layout>
</template>

<script>
import { defineComponent, onBeforeMount, ref } from "vue";
import EPAHeader from "src/components/epa/EPAHeader.vue";
import EPAFooter from "src/components/epa/EPAFooter.vue";
import EPAAccordion from "src/components/epa/EPAAccordion.vue";

export default defineComponent({
  name: "MainLayoutPublic",

  components: {
    EPAHeader,
    EPAFooter,
    EPAAccordion,
  },

  setup() {
    const environment = ref("DDDDD");
    const showEpaHeaderFooter = ref(true);
    const apiVersion = ref("");
    const dbVersion = ref("");

    onBeforeMount(() => {
      environment.value = process.env.ENV_TYPE;
      if (environment.value === "Development") {
        showEpaHeaderFooter.value = true;
      }
      (async () => {
        try {
          const result = await axios
            .get(process.env.API_SERVER + "/api/version")
            .then((response) => {
              apiVersion.value = response.data.apiVersion;
              dbVersion.value = response.data.dbVersion;
            })
        } catch (ex) {
          console.log(ex)
        }
        finally { }
      })();
    })();

    return {
      environment,
      showEpaHeaderFooter,
      apiVersion,
      dbVersion
    };
  },
});
</script>

<style lang="scss" scoped>

.page-view {
  min-height: 1000px;
}
.benmap-header {
  background-color: black;
}
.menu-bar {
  display: flex;
  flex-direction: row;
  height: 40px;
  background-color: black;
  color: white;

  .q-icon {
    color: #ddd;
  }
  .q-item {
    color: white;
  }
}
</style>
