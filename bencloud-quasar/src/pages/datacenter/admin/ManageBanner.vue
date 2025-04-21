<template>
  <q-page>
    <div class="q-pa-sm q-gutter-sm">
      <p class="title">
        Manage Banner
      </p>
      <div class="content">
        <div class="setting">
          <q-toggle
            v-model="isEnabled"
            color="green"
            label="Enabled"
            left-label
          />
          <div class="description">
            If enabled, the banner will display to all users untill dismissed.
          </div>
          <div class="description">
            If disabled, no banner will be shown.
          </div>
        </div>
        <div class="setting">
          <q-select
            square
            dense
            outlined
            v-model="selectedType"
            :options="bannerTypes"
            option-value="value"
            option-label="name"
            class="type-options"
            emit-value
            map-options
            label="Banner Type"
          />
          <div class="description">
            Controls the color of the banner.
          </div>
        </div>
        <div class="setting">
          <q-input
            hide-bottom-space
            outlined
            dense
            v-model="message"
            label="Enter Banner Message"
            lazy-rules="ondemand"
          />
        </div>
        <div class="modified">
          <div>
            Last modified by: {{ modifiedBy }}
          </div>
          <div>
            Last modified at: {{ modifiedDate }}
          </div>
        </div>
        <q-btn
          color="black"
          @click="loadBannerData()"
          label="Undo Changes"
          class="q-ml-sm button"
        />
        <q-btn
          color="secondary"
          @click="previewBanner()"
          label="Preview"
          class="q-ml-sm button"
        />
        <q-btn
          @click="updateBanner()"
          color="primary"
          label="Submit"
          class="q-ml-sm button"
        />
      </div>
    </div>
  </q-page>
</template>  

<script>
import { defineComponent, onBeforeMount, ref } from "vue";
import axios from "axios";

import { convertToUserTimezone } from "src/composables/common/time";

export default defineComponent({
  name: 'BannerSettings',
  setup() {

    const bannerTypes = [
      {
        name: 'Informational',
        value: 1
      }, 
      {
        name: 'Warning',
        value: 2
      },
      {
        name: 'Error',
        value: 3
      }
    ]
    const selectedType = ref(null);
    const message = ref("");
    const isEnabled = ref(null);
    const modifiedBy = ref("");
    const modifiedDate = ref("");

    onBeforeMount(() => {
      loadBannerData();
    })

    function loadBannerData() {
      (async () => {
        try {
          const result = await axios
            .get(process.env.API_SERVER + "/api/banner")
            .then((response) => {
              message.value = response.data.message;
              selectedType.value = response.data.type;
              isEnabled.value = response.data.enabled;
              modifiedBy.value = response.data.modified_by
              modifiedDate.value = convertToUserTimezone(response.data.modified_date);
            })
        } catch (ex) {
          console.log(ex)
        }
      })();
    }

    function updateBanner() {
      if (isEnabled.value && message.value.trim().length === 0) {
        alert("Cannot enable a banner with an empty message.");
        return;
      }
      if (!bannerTypes.some((e)=>e.value === selectedType.value)) {
        alert("Cannot set a banner with an invalid type.");
        return;
      }
      if (typeof isEnabled.value !== 'boolean') {
        alert("Cannot set a banner with an invalid enabled value.");
        return;
      }

      if(confirm("Are you sure you want to update the banner?")) {
        var payload = {
          message: message.value.trim(),
          type: selectedType.value,
          enabled: isEnabled.value,
        };

        (async () => {
          try {
            const result = await axios
              .post(process.env.API_SERVER + "/api/banner", payload, {
                headers: { "Content-Type": "application/x-www-form-urlencoded" }
              })
              .then((response) => {
                loadBannerData();
                window.dispatchEvent(new CustomEvent('banner-updated'));
              })
          } catch (ex) {
            if (ex.response.status===403) {
              this.$q.notify({
                type: 'negative',
                message: 'You do not have permissions to perform this action.',
              });
            }
            console.log(ex)
          }
        })();
      }
    }

    function previewBanner() {
      if (!isEnabled.value) {
        alert("Nothing to preview as the banner is not enabled.");
        return;
      }
      if (!bannerTypes.some((e)=>e.value===selectedType.value)) {
        alert("Cannot preview a banner with an invalid type.");
        return;
      }
      window.dispatchEvent(new CustomEvent('banner-preview', { 
        detail: {
          message: message.value,
          type: selectedType.value,
        }
      }));
    }

    return {
      isEnabled,
      bannerTypes,
      selectedType,
      message,
      modifiedBy,
      modifiedDate,
      loadBannerData,
      previewBanner,
      updateBanner,
    };
  },
})

</script>

<style scoped>
  .title {
    font-size: 32px;
    font-weight: 400;
  }

  .content {
    margin-left: 50px;
    margin-right: 50px;
  }

  .setting {
    margin-bottom: 20px
  }

  .description {
    margin-left: 5px;
    font-size: 12px;
    color: grey;
  }

  .type-options {
    width: 150px;
  }

  .modified {
    margin-bottom: 20px
  }

  .button {
    margin-right: 15px;
  }

</style>