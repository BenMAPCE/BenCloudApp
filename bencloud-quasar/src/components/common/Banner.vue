<template>
  <q-banner v-if="showBanner" rounded dense inline-actions :class="['text-white', 'banner', typeClass]">
    {{ message }}
    <template v-slot:action>
      <q-btn flat color="white" :label="isPreview?'Dismiss Preview':'Dismiss'" @click="onBannerDismissClicked"></q-btn>
    </template>
  </q-banner>
</template>

<script>
import { defineComponent, onBeforeMount, onMounted, onBeforeUnmount, ref, computed } from 'vue'
import axios from "axios";
import { useQuasar } from 'quasar'

export default defineComponent({
  name: 'Banner',

  setup () {
    const $q = useQuasar();

    const message = ref("");
    const showBanner = ref(false);
    const isPreview = ref(false);

    const type = ref(1);
    const typeClasses = [
      "bg-primary", // Info
      "bg-orange",  // Warning
      "bg-red",     // Error
    ]

    const cookieName = 'banner';
    var dismissedDate = undefined;
    var modifiedDate = undefined;
    var enabled = false;

    onBeforeMount(() => {
      readCookie();
      loadBannerData();
    })

    onMounted(() => {
      window.addEventListener('banner-updated', loadBannerData);
      window.addEventListener('banner-preview', previewBanner);
    });

    onBeforeUnmount(() => {
      window.removeEventListener('banner-updated', loadBannerData);
      window.removeEventListener('banner-preview', previewBanner);
    });

    const typeClass = computed(() => {
      if (Number.isInteger(type.value) && type.value >= 1 && type.value <= typeClasses.length)
      {
        return typeClasses[type.value-1];
      }
      console.warn("Invalid banner type: " + type.value)
      return typeClasses[0];
    });

    function setShowBanner() {
      // If either value has not been set yet.
      if (dismissedDate === undefined || modifiedDate === undefined) {
        showBanner.value = false;
        return;
      }

      // If either value was set null (cookie missing will return null for example)
      // Separate check because new Date(null) returns a valid date
      if (dismissedDate === null || modifiedDate === null) {
        showBanner.value = enabled;
        return;
      }

      const modified  = new Date(modifiedDate);
      const dismissed = new Date(dismissedDate);

      // If either value was badly formatted and did not parse
      if (isNaN(modified.getTime()) || isNaN(dismissed.getTime()) ) {
        showBanner.value = enabled;
        return;
      }

      showBanner.value = (modified > dismissed) && enabled;
    }

    function readCookie() {
      dismissedDate = $q.cookies.get(cookieName);
      setShowBanner();
    }

    function loadBannerData() {
      (async () => {
        try {
          const result = await axios
            .get(process.env.API_SERVER + "/api/banner")
            .then((response) => {
              message.value = response.data.message;
              type.value = response.data.type;
              enabled = response.data.enabled;
              // Replace ' ' with 'T' for ISO 8601 standard used when we convert to Date later
              modifiedDate = response.data.modified_date.replace(' ', 'T');
              isPreview.value = false;
            })
        } catch (ex) {
          console.log(ex)
        } finally {
          setShowBanner();
        }
      })();
    }

    const onBannerDismissClicked = () => {
      if (isPreview.value) {
        loadBannerData();
        return;
      }

      $q.cookies.set(cookieName, modifiedDate, {
        expires: 1000,
        path: '/',
        sameSite: 'Lax',
      });
      dismissedDate = modifiedDate;
      setShowBanner();
    };

    function previewBanner(event) {
      isPreview.value = true;
      message.value = event.detail.message;
      type.value = event.detail.type;
      showBanner.value = true;
    }

    return {
      message,
      typeClass,
      showBanner,
      isPreview,
      onBannerDismissClicked
    }
  }
});
</script>

<style lang="scss" scoped>
  .banner {
    margin: 10px;
  }
</style>