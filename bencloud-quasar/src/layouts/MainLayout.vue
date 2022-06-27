<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <EPAHeader v-bind:showEpaHeaderFooter="showEpaHeaderFooter"></EPAHeader>
      <div class="benmap-header">
        <q-toolbar>
          <q-toolbar-title>
            BenMAP - Benefits Mapping and Analysis Program
          </q-toolbar-title>

          <div>v{{ 0.01 }} beta ({{ environment }})</div>
        </q-toolbar>
      </div>
      <transition name="hmenu" class="menu-bar">
        <slot name="h-links">
          <q-list dense padding class="h-nav">
            <AppNavLinks v-for="link in appNavLinks" :key="link.title" v-bind="link" />
          </q-list>
        </slot>
      </transition>
    </q-header>

    <q-page-container class="page-view">
      <router-view />
    </q-page-container>

    <EPAFooter v-bind:showEpaHeaderFooter="showEpaHeaderFooter"></EPAFooter>
  </q-layout>
</template>

<script>
import { defineComponent, onBeforeMount, ref } from "vue";
import AppNavLinks from "src/components/navigation/AppNavLinks.vue";
import EPAHeader from "src/components/epa/EPAHeader.vue";
import EPAFooter from "src/components/epa/EPAFooter.vue";

const linksList = [
  {
    title: "Home",
    caption: "",
    icon: "mdi-home",
    link: "/#/",
  },
  {
    title: "Data Center",
    caption: "",
    icon: "mdi-chart-box-outline",
    link: "/#/datacenter",
  },
  // {
  //   title: "Settings",
  //   caption: "",
  //   icon: "mdi-cog",
  //   link: "/#/settings",
  // },
  // {
  //   title: "Language",
  //   caption: "",
  //   icon: "mdi-web",
  //   link: "/#/language",
  // },
  {
    title: "Help",
    caption: "",
    icon: "mdi-help",
    link: "/#/help",
  },
  {
    title: "Feedback",
    caption: "",
    icon: "mdi-comment-text-outline",
    link: "https://www.epa.gov/benmap/forms/contact-us-about-benmap",
  },
];

export default defineComponent({
  name: "MainLayout",

  components: {
    AppNavLinks,
    EPAHeader,
    EPAFooter,
  },

  setup() {
    const environment = ref("DDDDD");
    const showEpaHeaderFooter = ref(true);

    onBeforeMount(() => {
      environment.value = process.env.ENV_TYPE;
      if (environment.value === "Development") {
        showEpaHeaderFooter.value = true;
      }
    })();

    return {
      appNavLinks: linksList,
      environment,
      showEpaHeaderFooter
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
