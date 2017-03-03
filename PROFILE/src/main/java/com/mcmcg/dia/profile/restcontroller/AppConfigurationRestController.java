package com.mcmcg.dia.profile.restcontroller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.profile.model.domain.Response;
import com.mcmcg.dia.profile.util.EventCode;

/**
 * @author Jose Aleman
 *
 */
@RestController
@RequestMapping(value = "/app")
public class AppConfigurationRestController extends BaseRestController {
	private static final Logger LOG = Logger.getLogger(AppConfigurationRestController.class);

	public AppConfigurationRestController() {
	}

	@Value("${app.version}")
	private String appVersion;
	
	@Value("${app.name}")
	private String appName;

	@Value("${maven.build.timestamp}")
	private String mavenBuildTimestamp;

	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> ping() throws IOException {
		
		String message = appName + " Version " + appVersion + " is up and running. Released on " + mavenBuildTimestamp;

		String data = "IP 127.0.0.1";
		InetAddress host;
		try {
			host = InetAddress.getLocalHost();
			
			data = "Media-Profiles ---> Host:'" + host.getHostName() + "' has address: " + host.getHostAddress();

		} catch (UnknownHostException e) {
			try{
				host = InetAddress.getByName("127.0.0.1");
				data = "Media-Profiles ---> Host:'" + host.getHostName() + "' has address: " + host.getHostAddress();
			} catch (UnknownHostException ex) {
				LOG.warn(ex.getMessage(), ex);
			}
		}

		Response<Object> response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, data);

		LOG.info(response.toString());

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/log", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> changeLogLevelTo(@RequestParam("level") String level) {
		Response<Object> response = null;

		Level current = Logger.getRootLogger().getLevel();
		Level newLevel = Level.toLevel(level);
		String newLevelString = String.format("%s", newLevel);
		if (StringUtils.equals(level, newLevelString)) {
			Logger.getRootLogger().setLevel(newLevel);
			String message = String.format("Current Log Level [%s]  ----  New Log Level [%s]", current, newLevel);
			LOG.info(message);

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, new LogLevelWrapper(level));
		} else {
			response = buildResponse(EventCode.BAD_REQUEST.getCode(), "Bad Request", new LogLevelWrapper(level));
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

	}

	class LogLevelWrapper {
		private String level;

		public LogLevelWrapper(String level) {
			this.level = level;
		}

		public String getLevel() {
			return this.level;
		}

	}

}
