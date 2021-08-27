<template>
  <q-item
    clickable
    tag="a"
    :href="link"
    manual-focus
  >
    <q-item-section
      class="link-icon"
      v-if="icon"
      side
    >
    <q-icon :name="icon" size="20px" />
    </q-item-section>

    <q-item-section>
      <q-item-label>{{ title }}</q-item-label>
      <q-item-label caption>
        {{ caption }}
      </q-item-label>
    </q-item-section>
  </q-item>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'AppNavLinks',
  props: {
    title: {
      type: String,
      required: true
    },

    caption: {
      type: String,
      default: ''
    },

    link: {
      type: String,
      default: '#'
    },

    icon: {
      type: String,
      default: ''
    }
  }
})
</script>

<style lang="scss" scoped>

.link-icon {
  padding-right: 5px;
}

</style>