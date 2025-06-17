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
            <p>Step 1. Select your pre-policy air quality scenario</p>
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
            <p>Step 2. Select your post-policy air quality scenario(s)</p>
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
    onBeforeUnmount,
    watch,
    reactive,
    inject,
    ref
} from "vue";
import {
    useStore
} from "vuex";
import { useQuasar } from 'quasar';
import AirQualityPrePolicy from "../../components/analysis/AirQualityPrePolicy.vue";
import AirQualityPostPolicy from "../../components/analysis/AirQualityPostPolicy.vue";
import AirQualityAdd from "../../components/common/AirQualityAdd.vue"
import AirQualityPrePolicyMetricSelection from '../../components/analysis/AirQualityPrePolicyMetricSelection.vue'
import AirQualityPostPolicyMetricSelection from '../../components/analysis/AirQualityPostPolicyMetricSelection.vue'
import { prePolicyAirQualityIdHasValue } from "../../composables/validation/analysis-validations";
import { postPolicyAirQualityIdHasValue } from "../../composables/validation/analysis-validations";

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
        const $q = useQuasar();

        const selectedPollutantId = ref(0)
        const selectedPollutantFriendlyName = ref("OOPS")

        const stepHasError = reactive(inject("stepHasError"));
        const atStep = reactive(inject("atStep"));
        const validationEvent = reactive(inject("validationEvent"));

        var gridDefinitionMismatchNotification = null;

        onBeforeMount(() => {

            selectedPollutantId.value = store.state.analysis.pollutantId
            selectedPollutantFriendlyName.value = store.state.analysis.pollutantFriendlyName

            console.log(selectedPollutantId.value)
            console.log(selectedPollutantFriendlyName.value)

        })

        onBeforeUnmount(() => {
            if (gridDefinitionMismatchNotification !== null){
                gridDefinitionMismatchNotification({timeout: 200});
                gridDefinitionMismatchNotification = null;
            }
        })

        watch(
            () => validationEvent.value,
            (currentValue, newValue) => {
                if (!(prePolicyAirQualityIdHasValue(store) && 
                    postPolicyAirQualityIdHasValue(store)) &&
                    stepHasError.value && atStep.value.value == 3) {
                    $q.notify({
                        type: "negative",
                        message: "Please select a pre and post Air Quality dataset."
                    });
                }
        });

        watch(
            () => store.state.analysis.prePolicyAirQualityId,
            (currentSelectedItem, prevSelectedItem) => {
                console.log(" --- changeAirQualityPrePolicy: " + currentSelectedItem);
                validateGridDefinitionIds();
        });

        watch(
            () => store.state.analysis.postPolicyAirQualityId,
            (currentSelectedItem, prevSelectedItem) => {
                console.log(" --- changeAirQualityPostPolicy: " + currentSelectedItem);
                validateGridDefinitionIds();
        });

        function differentGridDefinitionIdIndices() {
            const indices = [];

            if (store.state.analysis.postPolicyGridDefinitionId == null || store.state.analysis.prePolicyGridDefinitionId == null) {
                return indices;
            }

            for (let i = 0; i < store.state.analysis.postPolicyGridDefinitionId.length; i++) {
                if (store.state.analysis.postPolicyGridDefinitionId[i] !== store.state.analysis.prePolicyGridDefinitionId) {
                    indices.push(i);
                }
            }

            return indices;
        }

        function validateGridDefinitionIds() {
            const indices = differentGridDefinitionIdIndices();
            if (indices.length > 0) {
                const plural = indices.length > 1;
                const names = indices.map((i) => "\"" + store.state.analysis.postPolicyAirQualityName[i].name +
                     "\" uses \"" + store.state.analysis.postPolicyGridDefinitionName[i] + "\"").join(", ");
                var mismatchMessage = "The pre-policy and post-policy air quality surfaces must use "+
                    "the same grid definition. Your pre-policy selection is associated with the \"" + 
                    store.state.analysis.prePolicyGridDefinitionName + "\" grid however the" + (plural?"es":"") +
                    " post-policy selection" + (plural?"s use":" uses a ") +" different grid" + (plural?"s: ":": ") +
                    names + ". Please change your selections."
                console.log(mismatchMessage);
                if (gridDefinitionMismatchNotification !== null){
                    gridDefinitionMismatchNotification({message: mismatchMessage});
                } else {
                    gridDefinitionMismatchNotification = $q.notify({
                        group: false,
                        timeout: 0,
                        multiLine: true,
                        type: "negative",
                        message: mismatchMessage });
                }
            } else {
                if (gridDefinitionMismatchNotification !== null){
                    gridDefinitionMismatchNotification({timeout: 200});
                    gridDefinitionMismatchNotification = null;
                }
            }
        }

        return {
            group: ref(null),
            pollutantId: ref(0),
            pollutantFriendlyName: ref(""),
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
