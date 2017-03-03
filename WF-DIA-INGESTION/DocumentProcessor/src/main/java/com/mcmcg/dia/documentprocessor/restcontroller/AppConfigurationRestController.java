package com.mcmcg.dia.documentprocessor.restcontroller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.documentprocessor.common.EventCode;
import com.mcmcg.dia.documentprocessor.dao.ConfigurationParameterDAO;
import com.mcmcg.dia.documentprocessor.entity.ConfigurationParameter;
import com.mcmcg.dia.documentprocessor.exception.PersistenceException;
import com.mcmcg.dia.iwfm.domain.Response;

/**
 * @author Jose Aleman
 *
 */
@RestController
@RequestMapping(value = "/app")
public class AppConfigurationRestController extends BaseRestController {
	private static final Logger LOG = Logger.getLogger(AppConfigurationRestController.class);

	@Value("${app.version}")
	private String appVersion;

	@Value("${app.name}")
	private String appName;

	@Value("${maven.build.timestamp}")
	private String mavenBuildTimestamp;

	@Autowired
	private ConfigurationParameterDAO configurationParameterDAO;

	public AppConfigurationRestController() {
	}

	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> ping() throws IOException {

		String message = appName + " Version " + appVersion + " is up and running. Released on " + mavenBuildTimestamp;
		Response<Object> response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, true);

		LOG.info(response.toString());

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

	}

	/**
	 * 
	 * @param level
	 * @return
	 */
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

	@RequestMapping(value = "/parameters", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> getParameters() throws IOException, PersistenceException {

		List<ConfigurationParameter> list = configurationParameterDAO.findAll();

		Response<Object> response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Parameters ", list);

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

	}
	
	@RequestMapping(value = "/parameters/{param}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> getParameter(@PathVariable("param") String parameter) {

		Response<Object> response = null;
		try {
			ConfigurationParameter result = configurationParameterDAO.findOne("documentprocessor." + parameter);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Get Parameter " + parameter, result);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/parameters/{param}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateParameter(@PathVariable("param") String parameter,
			@RequestParam("newValue") String newValue, @RequestParam("updatedBy") String updatedBy) throws PersistenceException {

		List<ConfigurationParameter> list = configurationParameterDAO
				.updateParameter("documentprocessor." + parameter, newValue, updatedBy);

		Response<Object> response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Parameters ", list);

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
