<template>
  <div style="display: flex;">
    <div class="q-pa-md discount-rate">
      <q-input
        filled
        dense
        v-model="discountRate"
        label="Discount Rate"
        hint=""
        lazy-rules
        :rules="[val => (val >= 0 && val < 1) || 'Please enter a value between 0 and 1']"
      />
    </div>
    <div class="q-pa-md">
      <q-checkbox v-model="useDecliningDR" label="Use Declining DR"/>
    </div>
  </div>
  
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "InflationYearSelection",

  async setup(props, context) {
    const store = useStore();
    const useDecliningDR = ref(false);
    const discountRate = ref(0); 

    watch(
      () => discountRate.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          store.commit("analysis/updateDiscountRate", currentSelectedItem);
        }
      }
    );
    watch(
      () => useDecliningDR.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          store.commit("analysis/updateUseDecliningDR", currentSelectedItem);
        }
      }
    );

    onBeforeMount(() => {
      
    });

    onMounted(() => {
      if (store.state.analysis.discountRate !=null){
        discountRate.value = store.state.analysis.discountRate;
      }
      else{
        discountRate.value = 0;
      }
      if (store.state.analysis.useDecliningDR !=null){
        useDecliningDR.value = store.state.analysis.useDecliningDR;
      }
      else{
        useDecliningDR.value = false;
      }
    })();

    return {
      useDecliningDR,
      discountRate,
    };
  },
});
</script>

<style lang="scss" scoped>
.discount-rate {
  width: 300px;
}
</style>
