/**
 * 
 */
package com.mcmcg.dia.iwfm.restcontroller;

import java.io.IOException;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.iwfm.dao.ConfigurationParametersDAO;
import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.entity.ConfigurationParameters;
import com.mcmcg.dia.iwfm.util.EventCode;

/**
 * @author Jose Aleman
 *
 */
@RestController
@RequestMapping(value = "/app")
public class AppConfigurationRestController extends BaseRestController{
	
	private static final Logger LOG = Logger.getLogger(AppConfigurationRestController.class);
	
	@Autowired
	private ConfigurationParametersDAO configurationParametersDAO;
	

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
	
	@RequestMapping(value = "/parameters", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> getParameters() throws IOException, PersistenceException {

		List<ConfigurationParameters> list = configurationParametersDAO.findAll();

		Response<Object> response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Parameters ", list);

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/parameters/{param}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateParameter(@PathVariable("param") String parameter, 
			@RequestParam("newValue") String newValue, @RequestParam("user") String user) throws PersistenceException {

		List<ConfigurationParameters> list = configurationParametersDAO.updateParameter("iwfm." + parameter, newValue, user);

		Response<Object> response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Parameters ", list);

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/parameters/{param}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> retrieveParameter(@PathVariable("param") String parameter) throws PersistenceException {

		ConfigurationParameters value = configurationParametersDAO.findOne("iwfm." + parameter);

		Response<Object> response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Parameters ", value.getValue());

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

	}

	/**
	 * 
	 * @author jaleman
	 *
	 */
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
