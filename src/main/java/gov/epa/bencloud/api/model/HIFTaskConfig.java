package gov.epa.bencloud.api.model;

import java.util.ArrayList;
import java.util.List;

public class HIFTaskConfig {
	public String name;
	public Integer aqBaselineId = 0;
	public Integer aqScenarioId = 0;
	public Integer popId = 0;
	public Integer popYear = 0;
	public Boolean preserveLegacyBehavior = false;
	public List<HIFConfig> hifs = new ArrayList<HIFConfig>();

}
