<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-toolbar-title>
          BenCLOUD - Environmental Benefits &amp; Mapping Tool
        </q-toolbar-title>

        <div>v{{ 0.01 }} beta ({{ environment }})</div>
      </q-toolbar>

      <transition name="hmenu" class="menu-bar">
        <slot name="h-links">
          <q-list dense padding class="h-nav">
            <AppNavLinks v-for="link in appNavLinks" :key="link.title" v-bind="link" />
          </q-list>
        </slot>
      </transition>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { defineComponent, onBeforeMount, ref } from "vue";
import AppNavLinks from "components/AppNavLinks.vue";

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
  {
    title: "Settings",
    caption: "",
    icon: "mdi-cog",
    link: "/#/settings",
  },
  {
    title: "Language",
    caption: "",
    icon: "mdi-web",
    link: "/#/language",
  },
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
    link: "/#/feedback",
  },
];

export default defineComponent({
  name: "MainLayout",

  components: {
    AppNavLinks,
  },

  setup() {
    const environment = ref("DDDDD");

    onBeforeMount(() => {
      environment.value = process.env.ENV_TYPE;
    })();

    return {
      appNavLinks: linksList,
      environment,
    };
  },
});
</script>

<style lang="scss">
.menu-bar {
  display: flex;
  flex-direction: row;
  height: 40px;
  background-color: black;
  color: white;

.q-icon {
    color: #DDD;
  }
}

  
</style>
