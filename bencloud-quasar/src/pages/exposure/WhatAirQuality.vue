<template>
<div class="row add-air-quality-layer">
    <div class="col-8">
        Don't see the desired air quality dataset below? &nbsp; &nbsp;
        <AirQualityAdd :pollutantId=selectedPollutantId :pollutantFriendlyName=selectedPollutantFriendlyName>
        </AirQualityAdd>
    </div>
</div>

<div v-if="stepHasError && atStep.value == 2">
    Please select a pre and post Air Quality dataset
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
    reactive,
    inject,
    ref
} from "vue";
import {
    useStore
} from "vuex";
import AirQualityPrePolicy from "../../components/exposure/AirQualityPrePolicy.vue";
import AirQualityPostPolicy from "../../components/exposure/AirQualityPostPolicy.vue";
import AirQualityAdd from "../../components/common/AirQualityAdd.vue"
import AirQualityPrePolicyMetricSelection from '../../components/exposure/AirQualityPrePolicyMetricSelection.vue'
import AirQualityPostPolicyMetricSelection from '../../components/exposure/AirQualityPostPolicyMetricSelection.vue'

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

        const stepHasError = reactive(inject("stepHasError"));
        const atStep = reactive(inject("atStep"));

        onBeforeMount(() => {

            selectedPollutantId.value = store.state.exposure.pollutantId
            selectedPollutantFriendlyName.value = store.state.exposure.pollutantFriendlyName

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
            selectedPollutantFriendlyName,
            stepHasError,
            atStep

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
