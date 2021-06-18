package gov.epa.bencloud.api.model;

import java.util.ArrayList;
import java.util.List;

public class ValuationTaskConfig {
	public String name;
	public Integer hifResultDatasetId = null;
	public Integer variableDatasetId = null;
	public List<ValuationConfig> valuationFunctions = new ArrayList<ValuationConfig>();

}
