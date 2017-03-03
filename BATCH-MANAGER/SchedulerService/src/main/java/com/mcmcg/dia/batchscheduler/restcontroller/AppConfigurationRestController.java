package com.mcmcg.dia.batchscheduler.restcontroller;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchmanager.util.EventCode;

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

	@Value("${application.version}")
	private String applicationVersion;

	@Value("${maven.build.timestamp}")
	private String mavenBuildTimestamp;
	
	@Autowired
	private String EC2;
	
	@Autowired
	private String privateIp;

	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> ping() throws IOException {

		String message = "Media Ingestion State App v" + applicationVersion + ". Released on " + mavenBuildTimestamp
				+ " is up and running on " + EC2 + " " + privateIp;

		Response<Object> response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, true);

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
