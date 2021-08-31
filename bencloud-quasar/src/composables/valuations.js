import { ref } from "vue";

export const updateValuationsForHealthImpactFunctionGroups = (
  store,
  hifgId,
  valuationIds
) => {
  console.log("--------------------------------------------");
  console.log(hifgId);
  console.log(valuationIds);

  var valuationsForHealthImpactFunctionGroups =
    store.state.analysis.valuationsForHealthImpactFunctionGroups;

  var hifgIdFound = false;

  for (var v = 0; v < valuationsForHealthImpactFunctionGroups.length; v++) {
    if (
      "health_impact_function_id" in valuationsForHealthImpactFunctionGroups[v]
    ) {
      console.log("... found health_impact_function_id");
      console.log(valuationsForHealthImpactFunctionGroups[v].valuationIds);
      // need to update
    }
  }

  if (!hifgIdFound) {
    var vfhifg = {};
    vfhifg.health_impact_function_id = hifgId;
    vfhifg.valuationIds = valuationIds;
    console.log("@@@")
    valuationsForHealthImpactFunctionGroups.push(vfhifg);
  }

  console.log("-----");
  console.log(valuationsForHealthImpactFunctionGroups);
  console.log("-----");

  console.log("--------------------------------------------");
};
