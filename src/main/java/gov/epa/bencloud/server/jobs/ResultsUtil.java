package gov.epa.bencloud.server.jobs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.epa.bencloud.server.util.ApplicationUtil;
import spark.Request;
import spark.Response;

public class ResultsUtil {

	private static Logger log = LoggerFactory.getLogger(ResultsUtil.class);

	private static Object getFile(Request request, Response responce, String userIdentifier, String fileName) {

		String scenarioFileDirectory = ApplicationUtil.getProperty("output.directory") + 
				File.separator + userIdentifier;

		File downloadFile = new File(scenarioFileDirectory + File.separator + fileName);
		
		responce.raw().setContentType("application/octet-stream");
		responce.raw().setHeader("Content-Disposition","attachment; filename="+downloadFile.getName());
		try {

			try(OutputStream outputStream = new BufferedOutputStream(responce.raw().getOutputStream());
					BufferedInputStream bufferedInputStream = 
							new BufferedInputStream(new FileInputStream(downloadFile))) {
				byte[] buffer = new byte[1024];
				int len;
				while ((len = bufferedInputStream.read(buffer)) > 0) {
					outputStream.write(buffer,0,len);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return null;
	}

}
