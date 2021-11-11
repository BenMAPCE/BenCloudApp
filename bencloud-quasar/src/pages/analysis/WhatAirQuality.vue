<template>
<div class="row add-air-quality-layer">
    <div class="col-8">
        Don't see the desired air quality dataset below? &nbsp; &nbsp;
        <AirQualityAdd :pollutantId=selectedPollutantId :pollutantFriendlyName=selectedPollutantFriendlyName>
        </AirQualityAdd>
    </div>
</div>

<div class="row">
    <div class="col-6">
        <div>
            <p>Step 1. Select your pre-policy air quality data</p>
            <Suspense>
                <AirQualityPrePolicy></AirQualityPrePolicy>
            </Suspense>
        </div>
        <div>
            <AirQualityPrePolicyMetricSelection></AirQualityPrePolicyMetricSelection>
        </div>
    </div>
    <div class="col-6">
        <div>
            <p>Step 2. Select your post-policy air quality data</p>
            <Suspense>
                <AirQualityPostPolicy></AirQualityPostPolicy>
            </Suspense>
        </div>
        <div>
            <div>
                <AirQualityPostPolicyMetricSelection></AirQualityPostPolicyMetricSelection>
            </div>

        </div>
    </div>
</div>
</template>

<script>
import {
    defineComponent,
    onBeforeMount,
    watch,
    ref
} from "vue";
import {
    useStore
} from "vuex";
import AirQualityPrePolicy from "../../components/analysis/AirQualityPrePolicy.vue";
import AirQualityPostPolicy from "../../components/analysis/AirQualityPostPolicy.vue";
import AirQualityAdd from "../../components/common/AirQualityAdd.vue"
import AirQualityPrePolicyMetricSelection from '../../components/analysis/AirQualityPrePolicyMetricSelection.vue'
import AirQualityPostPolicyMetricSelection from '../../components/analysis/AirQualityPostPolicyMetricSelection.vue'

export default defineComponent({
    name: "WhatAirQuality",
    components: {
        AirQualityAdd,
        AirQualityPrePolicy,
        AirQualityPostPolicy,
        AirQualityPrePolicyMetricSelection,
        AirQualityPostPolicyMetricSelection
    },
    setup() {

        const store = useStore();

        const selectedPollutantId = ref(0)
        const selectedPollutantFriendlyName = ref("OOPS")

        onBeforeMount(() => {

            selectedPollutantId.value = store.state.analysis.pollutantId
            selectedPollutantFriendlyName.value = store.state.analysis.pollutantFriendlyName

            console.log(selectedPollutantId.value)
            console.log(selectedPollutantFriendlyName.value)

        })

        function onChangeAirQualityPrePolicy(value) {
            console.log(" --- changeAirQualityPrePolicy: " + value);
        }

        return {
            group: ref(null),
            pollutantId: ref(0),
            pollutantFriendlyName: ref(""),
            onChangeAirQualityPrePolicy,
            selectedPollutantId,
            selectedPollutantFriendlyName
        };
    },
});
</script>

<style scoped>
.aq-pre-policy-scroll-area {
    border: 1px solid black;
    height: 200px;
    width: 100%;
}

.row.add-air-quality-layer {
  padding-bottom: 25px;
}

</style>
