/**
 * 
 */
package com.mcmcg.ingestion.restcontroller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.util.EventCode;

/**
 * @author Jose Aleman
 *
 */
@RestController
@RequestMapping(value = "/app")
public class AppConfigurationRestController extends BaseRestController{
	private static final Logger LOG = Logger.getLogger(AppConfigurationRestController.class);
	

	/**
	 * 
	 */
	public AppConfigurationRestController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> ping() {
		
		Response<Object> response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "App is up and running", true);
		
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
		if (StringUtils.equals(level, newLevelString)){
			Logger.getRootLogger().setLevel(newLevel);
			String message = String.format("Current Log Level [%s]  ----  New Log Level [%s]", current, newLevel);
			LOG.info(message);
			
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, new LogLevelWrapper(level));
		}else{
			response = buildResponse(EventCode.BAD_REQUEST.getCode(), "Bad Request", new LogLevelWrapper(level));
		}
		
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

	}
	
	/**
	 * 
	 * @param level
	 * @return
	 */
	@RequestMapping(value = "/metric-log", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> metricLog() {

		((AppenderSkeleton)Logger.getRootLogger().getAppender("MetricsRollingAppender")).setThreshold(Level.toLevel("DEBUG"));
		LOG.debug("-------------------------------------------");
		LOG.debug("-------------------------------------------");
		((AppenderSkeleton)Logger.getRootLogger().getAppender("MetricsRollingAppender")).setThreshold(Level.toLevel("OFF"));
		
		return changeLogLevelTo("DEBUG");

	}

	class LogLevelWrapper {
		private String level;
		
		public LogLevelWrapper(String level){
			this.level = level;
		}
		
		public String getLevel() {
			return this.level;
		}
		
	}

	
	
}
