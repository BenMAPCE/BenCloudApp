package gov.epa.bencloud.server.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class FreeMarkerRenderUtil {

	public static Configuration configureFreemarker(String templatePath) {
		
		Configuration freeMarkerConfiguration = new Configuration(Configuration.VERSION_2_3_31);
		
		try {
			FileTemplateLoader templateLoader = 
					new FileTemplateLoader(new File(templatePath));

			freeMarkerConfiguration.setDefaultEncoding("UTF-8");
			freeMarkerConfiguration.setTemplateExceptionHandler(
					TemplateExceptionHandler.RETHROW_HANDLER);

			freeMarkerConfiguration.setTemplateLoader(templateLoader);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return freeMarkerConfiguration;
		
	}
	
	public static String render(
			Configuration freeMarkerConfiguration, 
			Map<String, Object> model, 
			String templatePath) {
	    return new FreeMarkerEngine(freeMarkerConfiguration)
	    		.render(new ModelAndView(model, templatePath));
	}

	public static void logPath(
			HttpServletRequest httpServletRequest, 
			HttpServletResponse httpServletResponse) {
	}

}
