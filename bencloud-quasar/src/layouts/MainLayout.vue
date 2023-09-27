<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <EPAHeader v-bind:showEpaHeaderFooter="showEpaHeaderFooter"></EPAHeader>
      <div class="benmap-header">
        <q-toolbar>
          <q-toolbar-title>
            BenMAP - Benefits Mapping and Analysis Program
          </q-toolbar-title>

          <div>
            <div style="text-align: right;"><q-icon :name="'mdi-account-circle'" size="20px" /> {{username}}</div>
            <div style="text-align: right; color: #909090">UI v{{ 0.4 }} | API v{{ apiVersion }} | DB v{{ dbVersion }} {{ environment === 'Production' ? '' : '(' + environment + ')' }}</div>
          </div>
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
import axios from "axios";

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
    link: "https://www.epa.gov/benmap/benmap-cloud",
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
    const username = ref("");
    const apiVersion = ref("");
    const dbVersion = ref("");

    // Intercept all HTTP responses
    axios.interceptors.response.use(
      (response) => {
        // If the session has timed out, reload the page
        if(!!response && response.status === 302) {
          window.location.reload();
        }
        return response;
      },
      (error) => {
        if(!!error.response) {
          // If the error is due to session timing out, reload the page
          if(error.response.status === 302) {
            console.log("302 error");
            window.location.reload();
            // If there is a message in the error response, display it in a pop up
          } 
          else if(!!error.response.data.message && error.response.status === 401) {
            alert(error.response.data.message);
            // For non-302 errors with no message, display a generic error pop up 
          }
        }     
      }
    )

    onBeforeMount(() => {
      environment.value = process.env.ENV_TYPE;
      if (environment.value === "Development") {
        showEpaHeaderFooter.value = true;
      }
      (async () => {
        try {
          const result = await axios
            .get(process.env.API_SERVER + "/api/user")
            .then((response) => {
              username.value = response.data.displayname;
            })
        } catch (ex) {
          console.log(ex)
        }
        finally { }
      })();

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
    })

    return {
      appNavLinks: linksList,
      environment,
      showEpaHeaderFooter,
      username,
      apiVersion,
      dbVersion
    };
  }
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
