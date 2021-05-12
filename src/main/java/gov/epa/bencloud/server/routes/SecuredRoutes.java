package gov.epa.bencloud.server.routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import spark.Service;

public class SecuredRoutes {

	private static final Logger log = LoggerFactory.getLogger(SecuredRoutes.class);
	private Service service = null;

	public SecuredRoutes(Service service, Configuration freeMarkerConfiguration){
		this.service = service;
		addRoutes(freeMarkerConfiguration);
	}

	private void addRoutes(Configuration freeMarkerConfiguration) {

	}
}
