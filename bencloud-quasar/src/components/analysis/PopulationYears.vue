<template>
  <div class="q-pa-md">
    <li class="scenarios" v-for="scenario in scenarios" :key=scenario.name :id="scenario.name">
      <div class="label">{{ scenario.name }}</div>
      <div class="select">
        <q-select
        square
        dense
        outlined
        v-model="scenario.years"
        multiple
        :options="rows"
        class="population-year-options"
        :id="scenario.index"
        :disable="applyAll == true && scenario.index != 0 ? true : false"
        emit-value
        map-options
        label="Year(s)"
        />
      </div>
      <div v-if="scenario.index === 0 && scenarios.length > 1" class="checkbox">
        <q-checkbox v-model="applyAll" label="Apply to all scenarios"/>
      </div>
    </li>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { ref, watch, onMounted, onUpdated, onBeforeMount } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "PopulationYears",

  setup(props, context) {
    const store = useStore();
    const rows = ref([]);
    const scenarios = ref([]);
    const applyAll = ref();

    watch(
      () => store.state.analysis.populationYears,
      (currentSelectedItem, prevSelectedItem) => {
        var years = [];
        for (let val of currentSelectedItem) {
          years.push({ label: val, value: val });
        }
        rows.value = years;
      }
    );

    watch(
      () => applyAll.value,
      (currentSelectedItem, prevSelectedItem) => {
        if (currentSelectedItem != prevSelectedItem) {
          store.commit("analysis/updateApplyYearsToAll", currentSelectedItem);
        }
      }
    );

    onBeforeMount(() => {
      applyAll.value = store.state.analysis.applyYearsToAll;
      scenarios.value = [];
      for(let i = 0; i < store.state.analysis.postPolicyAirQualityName.length; i++) {
        scenarios.value.push({index: i, name: store.state.analysis.postPolicyAirQualityName[i].name, years: store.state.analysis.postPolicyAirQualityName[i].years});
      }
    });

    onUpdated(() => {
      applyAll.value = store.state.analysis.applyYearsToAll;
      var items = document.getElementsByClassName("scenarios");
      if(applyAll.value == true) {
        scenarios.value.forEach(scenario => {
          scenario.years = scenarios.value[0].years;
        })
        for(let i = 1; i < items.length; i++) {
          var element = document.getElementById(i);
        }
      }
      if(items.length) {
        let yearsArr = [];
        for (let i = 0; i < items.length; i++) {
          scenarios.value[i].years.sort();
          let popYears = [];
          for(let j = 0; j < scenarios.value[i].years.length; j++) {
            popYears.push({year: scenarios.value[i].years[j], hifs: null})
          }
          yearsArr.push({name: scenarios.value[i].name, years: scenarios.value[i].years, popYears: popYears})
        }
        store.commit("analysis/updatePostPolicyAirQualityName", yearsArr);
        console.log(store.state.analysis.postPolicyAirQualityName);
      }
    })();


    return {
      rows,
      scenarios,
      applyAll
    };
  },

});
</script>

<style lang="scss" scoped>
.scenarios {
  display: flex;
  align-items: center;
  list-style: none;
  padding-bottom: 10px;
}
.population-year-options {
  width: 300px;
  margin-right: 30px;
}
.label {
  margin-right: 30px;
  width: 300px;
  overflow-wrap: break-word;
}

</style>
