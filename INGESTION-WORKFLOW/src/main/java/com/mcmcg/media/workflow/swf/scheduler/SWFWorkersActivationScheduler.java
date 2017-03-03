package com.mcmcg.media.workflow.swf.scheduler;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.service.BaseService.Methods;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.ingestion.IngestionWorkflowManager;
import com.mcmcg.media.workflow.swf.IngestionWorkflowHost;

/**
 * 
 * @author wporras
 *
 */
@Component("swfWorkersActivationScheduler")
public class SWFWorkersActivationScheduler{
	
	private static final Logger LOG = Logger.getLogger(SWFWorkersActivationScheduler.class);
	@Autowired
	private IngestionWorkflowManager ingestionWorkflowManager;
	
	@Resource
	Map<String, Object> workerMap;
	
	public SWFWorkersActivationScheduler() {
		
	}

	@Scheduled(cron = "${workers.cron}")
	public void processDocumentsScheduled() {
		try {
			
			setThreadCountParameter();
			
			setReprocessAttemptsParameter();

		} catch (Throwable e) {
			
			LOG.error(e.getMessage(), e);
			
		}
	}

	/**
	 * 
	 */
	private void setThreadCountParameter() {

		int count = retrieveParameterIntValue(IngestionWorkflowManager.GET_PARAM_THREADCOUNT, IngestionWorkflowHost.ACTIVITIES_POOL_SIZE);
		
		if (IngestionWorkflowHost.maxPoolSize != count){
			IngestionWorkflowHost.taskExecutor.setCorePoolSize(count);
			IngestionWorkflowHost.taskExecutor.setMaxPoolSize(count);
			IngestionWorkflowHost.maxPoolSize = count;
			
			LOG.info("ThreadCount REFRESHED ------ " + count);
		}
	}

	/**
	 * 
	 */
	private void setReprocessAttemptsParameter() {

		int attempts = retrieveParameterIntValue(IngestionWorkflowManager.GET_PARAM_REPROCESS_ATTEMPTS, IngestionWorkflowHost.retryAttempts);
		
		if (IngestionWorkflowHost.retryAttempts != attempts){
			IngestionWorkflowHost.retryAttempts = attempts;
			LOG.info("Reprocess Attempts REFRESHED ------ " + attempts);
		}
		
	}
	
	/**
	 * 
	 * @param parameter
	 * @param defaultValue
	 * @return
	 */
	public int retrieveParameterIntValue(String parameter, Integer defaultValue){
		int parameterInt = defaultValue;
		

		try {
			
			Response<Object> response = ingestionWorkflowManager.execute(parameter, Methods.GET.toString());
			
			if (response.getData() != null){
				parameterInt = Integer.parseInt(response.getData().toString());
			}
			
		} catch (Throwable e) {

			LOG.warn(parameter + " =>" + e.getMessage(), e);
			
		}
		
		return parameterInt;
	}
	
}