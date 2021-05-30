package gov.epa.bencloud.server.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import spark.Request;

public class RoutesBase {

	
	protected List<String> getPostParametersNames(Request req) {
	
		Map<String, String[]> params = req.raw().getParameterMap();
		
		List<String> names = new ArrayList<String>();
		
		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			names.add(entry.getKey());
		}
		
		return names;
	}

	protected String getPostParameterValue(Request req, String name) {
		
		String value = null;
		
		Map<String, String[]> params = req.raw().getParameterMap();
		
		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			if (entry.getKey().equals(name)) {
				value = entry.getValue()[0];
			}
		}
		
		return value;
	}

	protected Map<String, String[]> getPostParameterAsMap(Request req) {

		return req.raw().getParameterMap();
	}
}
