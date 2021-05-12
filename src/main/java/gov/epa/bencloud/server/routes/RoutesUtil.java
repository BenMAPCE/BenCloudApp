package gov.epa.bencloud.server.routes;

import java.util.UUID;

import spark.Request;
import spark.Response;

public class RoutesUtil {

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
