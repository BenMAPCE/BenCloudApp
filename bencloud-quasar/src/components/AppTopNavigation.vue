<template>
<!--
    <div class="app-top-navigation">
        <a href="#" title="Home" icon="mdiHome"><font-awesome-icon :icon="['fas', 'home']" class="home" /> Home</a>
        <a href="#" title="Data Center"><font-awesome-icon :icon="['fas', 'poll']" /> Data Center</a>
        <a href="#" title="Settings"><font-awesome-icon :icon="['fas', 'cog']" /> Settings</a>
        <a href="#" title="Language"><font-awesome-icon :icon="['fas', 'language']" /> Language</a>
        <a href="#" title="Help"><font-awesome-icon :icon="['fas', 'question-circle']" /> Help</a>
    </div>
    <div>
        <hr />
    </div>
-->

    <transition name="hmenu">
        <slot name="h-links">
          <q-list
            v-if="show"
            dense
            padding
            class="h-nav"
          >
            <q-item clickable v-ripple>
              <q-item-section>
                Home
              </q-item-section>
            </q-item>

            <q-item clickable v-ripple>
              <q-item-section>
                Data Center
              </q-item-section>
            </q-item>

            <q-item clickable v-ripple>
              <q-item-section>
                Settings
              </q-item-section>
            </q-item>

            <q-item clickable v-ripple>
              <q-item-section>
                Language
              </q-item-section>
            </q-item>

            <q-item clickable v-ripple>
              <q-item-section>
                Help
              </q-item-section>
            </q-item>
          </q-list>
        </slot>
      </transition>
</template>

<style lang="scss" scoped>

.app-top-navigation {
    margin-top: 10px;
    font-size: 15px;
    text-align: left;
    a {
        padding-right: 20px;
        text-decoration: none;
        color: black;
    }
    a:hover {
        color:blue;
    }
}

.h-nav {
  display: flex;
  flex-direction: row;
  height: 50px;
}

</style>

<script>

const linksList = [
  {
    title: 'Home!',
    caption: 'quasar.dev',
    icon: 'school',
    link: 'https://quasar.dev'
  },
  {
    title: 'Data Center',
    caption: 'quasar.dev',
    icon: 'school',
    link: 'https://quasar.dev'
  },
  {
    title: 'Settings',
    caption: 'quasar.dev',
    icon: 'school',
    link: 'https://quasar.dev'
  },
  {
    title: 'Language',
    caption: 'quasar.dev',
    icon: 'school',
    link: 'https://quasar.dev'
  },
  {
    title: 'Docs',
    caption: 'quasar.dev',
    icon: 'school',
    link: 'https://quasar.dev'
  },
]




export default {
  name: "HorizontalMenu",
  data() {
    return {
      show: true
    };
  },
  props: {
    icon: {
      type: String,
      default: ""
    },
    lorem: {
      type: String,
      default: "A simple card"
    }
  },
  methods: {
    ClickMe() {
      console.log("I am clicked");
    }
  }
};
</script>