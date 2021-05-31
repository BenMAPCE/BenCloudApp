package gov.epa.bencloud.server.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import spark.Request;
import spark.Response;

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

	protected String[] getPostParameterValues(Request req, String name) {
		
		String[] values = null;
		
		Map<String, String[]> params = req.raw().getParameterMap();
		
		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			if (entry.getKey().equals(name)) {
				values = entry.getValue();
			}
		}
		
		return values;
	}

	protected Map<String, String[]> getPostParametersAsMap(Request req) {

		return req.raw().getParameterMap();
	}
	
	public static String getOrSetOrExtendCookie(Request req, Response res) {

		String userIdentifier = req.cookie("bcoUserIdentifier");
		if (null == userIdentifier) {
			UUID uuid = UUID.randomUUID();
			res.cookie("bcoUserIdentifier", uuid.toString(), 60 * 60 * 24 * 90);
		} else {
			res.cookie("bcoUserIdentifier", userIdentifier, 60 * 60 * 24 * 90);	
		}
		
		return req.cookie("bcoUserIdentifier");
	}

}
