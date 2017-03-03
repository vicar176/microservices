/**
 * 
 */
package com.mcmcg.media.workflow.swf.step;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mcmcg.media.workflow.common.Diagnostics;
import com.mcmcg.media.workflow.common.EventCode;
import com.mcmcg.media.workflow.common.StepStatusCode;
import com.mcmcg.media.workflow.common.WorkflowConstants;
import com.mcmcg.media.workflow.common.WorkflowStateCode;
import com.mcmcg.media.workflow.service.BaseService.Methods;
import com.mcmcg.media.workflow.service.domain.IngestionStep;
import com.mcmcg.media.workflow.service.domain.MediaDocument;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.domain.WorkflowState;
import com.mcmcg.media.workflow.service.exception.MediaServiceException;
import com.mcmcg.media.workflow.service.ingestion.AccountMetadataIngestionService;
import com.mcmcg.media.workflow.service.ingestion.IngestionWorkflowManager;
import com.mcmcg.media.workflow.service.ingestion.MetadataIngestionService;
import com.mcmcg.media.workflow.service.ingestionstate.IngestionStepService;
import com.mcmcg.media.workflow.service.ingestionstate.WorkflowStateService;
import com.mcmcg.media.workflow.swf.scheduler.SWFWorkersActivationScheduler;
import com.mcmcg.media.workflow.util.DiagnosticsLogger;

/**
 * @author jaleman
 *
 */
public abstract class BaseStep<T> implements Step<T> {

	private final static Logger LOG = Logger.getLogger(BaseStep.class);
	
	@Autowired
	protected MetadataIngestionService metadataIngestionService;
	
	@Autowired
	protected AccountMetadataIngestionService accountMetadataIngestionService;

	@Autowired
	protected IngestionStepService ingestionStepService;

	@Autowired
	protected WorkflowStateService workflowStateService;
	
	@Autowired 
	protected IngestionWorkflowManager ingestionWorkflowManager;

	@Resource
	Map<WorkflowStateCode, Integer> workflowStateCodeMap;

	@Autowired
	private DiagnosticsLogger diagnosticsLogger;
	
	@Autowired
	SWFWorkersActivationScheduler  swfWorkersActivationScheduler;
	
	public static final String ERROR_504_GATEWAY_TIMEOUT = "504 GATEWAY_TIMEOUT";
	public static final String ERROR_503_SERVICE_UNAVAILABLE = "503 Service Unavailable";
	public static final String SLOWDOWN = "SLOWDOWN";
	public static final String ERROR_500_SERVER_ERROR = "500 Internal Server Error";

	public static final int WAIT_FOR = 5000;

	protected int retryAttemptsLimit = 0;
	/**
	 * 
	 */
	public BaseStep() {
		
		
	}

	@PostConstruct
	public void init(){

		retryAttemptsLimit = swfWorkersActivationScheduler.retrieveParameterIntValue(IngestionWorkflowManager.GET_PARAM_REPROCESS_ATTEMPTS, 5);
		
	}
	
	/****
	 * 
	 * 
	 * ABSTRACT METHODS
	 * 
	 */

	public abstract Response<T> execute(Map<String, Object> contextMap) throws MediaServiceException;

	public abstract WorkflowStateCode getWorkflowStateCode();

	/****
	 * 
	 * 
	 * MAIN PUBLIC METHOD
	 * 
	 */

	/**
	 * 
	 * @param contextMap
	 * @return
	 */
	@Diagnostics
	public Response<T> doExecution(Map<String, Object> contextMap) {

		Response<T> returnValue = null;

		//Begin
		Date start = new Date();
		
		WorkflowState workflowState = retrieveWorkFlowState(contextMap);

		if (checkForceReRunFromWorkflowState(workflowState)) {

			returnValue = reRunStep(contextMap, workflowState);

		} else {

			returnValue = runStep(contextMap, workflowState);
		}

		updateWorkflowState(returnValue, workflowState, getWorkflowStateCode());
	
		//End
		Date end = new Date();
		StringBuilder stepLogBuilder = new StringBuilder();
		stepLogBuilder.append("Step ").append(getName()).append("\t").
					   append("Document Id ").append(workflowState.getDocumentId()).append("\t").
					   append("Start Date ").append(start).append("\t").
					   append("End Date ").append(end);
		diagnosticsLogger.log(stepLogBuilder.toString(), new Object[]{workflowState.getDocumentId()}, "SWF-STEPS", start.getTime(), end.getTime());
		
		return returnValue;
	}

	/****
	 * 
	 * 
	 * PRIVATE METHODS
	 * 
	 */

	/**
	 * @param contextMap
	 * @param returnValue
	 * @param workflowState
	 * @return
	 */
	private Response<T> runStep(Map<String, Object> contextMap, WorkflowState workflowState) {

		Response<T> result = null;

		IngestionStep step = retrievePreviousStepRun(workflowState);

		if (step != null && step.getStatusCode().equals(StepStatusCode.SUCCESS.toString())) {

			result = addPreviousStepToCurrentExecution(step);

		} else {

			result = executeStep(contextMap, workflowState, step);

		}

		return result;
	}

	/**
	 * 
	 * @param contextMap
	 * @param workflowState
	 * @return
	 */
	private Response<T> reRunStep(Map<String, Object> contextMap, WorkflowState workflowState) {
		LOG.info("Rerun step: " + this.getName());
		return executeStep(contextMap, workflowState);
	}

	/**
	 * 
	 * @param contextMap
	 * @param workflowState
	 * @return
	 */
	private Response<T> executeStep(final Map<String, Object> contextMap, final WorkflowState workflowState, IngestionStep... steps) {

		Response<T> returnValue = null;
		StepStatusCode newCode = StepStatusCode.FAILED;
		try {
			LOG.info("Executing step: " + this.getName());
			
			Date start = new Date();
			
			returnValue = callExecuteStep(contextMap, workflowState, returnValue);
			
			if (returnValue != null && !returnValue.getError().getMessage().startsWith(StepStatusCode.FAILED.toString())) {

				String returnedCode = returnValue.getError().getMessage();
				newCode = returnedCode.startsWith(StepStatusCode.SUCCESS.toString()) ? StepStatusCode.SUCCESS
						: StepStatusCode.SKIP;
			}
			
			IngestionStep step = createOrUpdateIngestionStep(contextMap, returnValue.getError(), workflowState, newCode, start, steps);
			
			LOG.info(step);
			
		} catch (Throwable e) {

			LOG.error("Error creating the step --> " + e.getMessage(), e);

			returnValue = null;	
		}

		return returnValue;
	}

	/**
	 * @param contextMap
	 * @param workflowState
	 * @param returnValue
	 * @return
	 */
	protected Response<T> callExecuteStep(final Map<String, Object> contextMap, final WorkflowState workflowState,
			Response<T> returnValue) {
		
		int count = 0;
		boolean needsToRetry = false;

		do{
			try {

				returnValue = execute(contextMap);

			} catch (Exception e) {

				if (StringUtils.contains(e.getMessage().toUpperCase(), ERROR_504_GATEWAY_TIMEOUT.toUpperCase()) || 
						StringUtils.contains(e.getMessage().toUpperCase(), ERROR_500_SERVER_ERROR.toUpperCase()) ||	
						StringUtils.contains(e.getMessage().toUpperCase(), SLOWDOWN.toUpperCase()) ||	
						StringUtils.endsWith(e.getMessage().trim(), "null") ||	
						StringUtils.contains(e.getMessage().toUpperCase(), ERROR_503_SERVICE_UNAVAILABLE.toUpperCase())){
				
					needsToRetry = ++count >= retryAttemptsLimit ? false : true;

					if (needsToRetry){
						waitTime();
						LOG.warn("Retrying: " + workflowState.getDocumentId() +  " => " +  e.getMessage(), e);
					}else{
						LOG.info("Max Retry limit retrieved: " + workflowState.getDocumentId() +  " => " +  e.getMessage(), e);
					}
				}else{
					LOG.info("Expected error ==> " + e.getMessage());
					
				}

			}
			
		}while(needsToRetry);
		
		return returnValue;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	/***
	 * 
	 * PRIVATE METHODS
	 * 
	 */

	/**
	 * 
	 * @param contextMap
	 * @return
	 */
	private WorkflowState retrieveWorkFlowState(Map<String, Object> contextMap) {

		WorkflowState workflowState = null;
		MediaDocument document = (MediaDocument) contextMap.get(WorkflowConstants.DOCUMENT_ID);
		String resource = WorkflowStateService.GET_WORKFLOW_STATES + document.getDocumentId();

		LOG.debug("Retrieve WorkFlowState: " + resource );
		
		workflowState = findWorkflowState(workflowState, resource);
		
		if (workflowState == null) {
			
			workflowState = createWorkflowState(document);
			
		}
		
		LOG.debug("WorkFlowState Retrieved: " + workflowState );
		
		return workflowState;
	}

	/**
	 * 
	 * @param workflowState
	 * @return
	 */
	private boolean checkForceReRunFromWorkflowState(WorkflowState workflowState) {
		
		LOG.debug("checkForceReRunFromWorkflowState " + workflowState.getForceRerun());
		return workflowState.getForceRerun();
		
	}

	/**
	 * 
	 * @param workflowState
	 * @return IngestionStep
	 */
	private IngestionStep retrievePreviousStepRun(WorkflowState workflowState) {
		
		String resource = IngestionStepService.GET_LATEST_STEP +  workflowState.getDocumentId() + 
						  "?ingestionStateCode=" + getWorkflowStateCode().toString(); 

		IngestionStep step = null;
		try {
		
			Response<IngestionStep> response = ingestionStepService.execute(resource, Methods.GET.toString());
			
			step = response.getData();
			
		} catch (Throwable e) {

			LOG.error(e.getMessage(), e);
			step = null;
			
		}
		
		return step;

	}

	
	/**
	 * 
	 * @param workflowState
	 * @return
	 */
	private boolean updateWorkflowState(Response<T> returnValue, WorkflowState workflowState, WorkflowStateCode code) {
		
		checkAndUpdateWorkflowState(workflowState, code);
		
		return mergeWorkflowSate(workflowState);
	}

	/**
	 * @param workflowState
	 * @param code
	 */
	private void checkAndUpdateWorkflowState(WorkflowState workflowState, WorkflowStateCode code) {
		/*if (code.equals(WorkflowStateCode.UPDATE_WORKFLOW_STATE)){
			workflowState.setRerunNumber( workflowState.getRerunNumber() + 1);
		}*/

		workflowState.setIngestionStateCode(code.toString());
		
		if (code.equals(WorkflowStateCode.UPDATE_RERUN_STATUS)){
			workflowState.setForceRerun(false);
			LOG.debug("Workflow about to finish " + workflowState.getDocumentId());
		}
	}

	/**
	 * 
	 * @param contextMap
	 * @param returnValue
	 * @param code
	 * @param steps
	 * @return IngestionStep
	 */
	private IngestionStep createOrUpdateIngestionStep(  Map<String, Object> contextMap, Response.Error error,
														WorkflowState workflowState, StepStatusCode code, Date start, 
														IngestionStep... steps) {

		IngestionStep step = null;

		if (steps != null && steps.length == 1 && steps[0] != null) {
			step = steps[0];
			step.setRerunNumber(workflowState.getRerunNumber());
			step.setId(null);
			//step.setIngestionStateCode(workflowState.getIngestionStateCode());
			step.setStatusCode(code.toString());
			step.setWorkflowStateId(workflowState.getId());
			step.setDescription(error != null ? error.getMessage() : step.getDescription());
			step.setCreateDate(start);
		} else {
			step = buildIngestionStep(contextMap, error, code, workflowState);
			step.setCreateDate(start);
		}
		//Description Field is Varchar (256)
		if (step.getDescription() != null && step.getDescription().length() > 250){
			step.setDescription(step.getDescription().substring(0, 250));
		}
		
		boolean isFailing = false;
		
		do {
			isFailing = false;
			try {
				
				Response<IngestionStep> response = ingestionStepService.execute(IngestionStepService.PUT_STEP,
						Methods.PUT.toString(), step);
				step = response.getData();
				LOG.debug("Step: " + step);
				
			} catch (Exception e) {
				isFailing = true;
				LOG.error(e.getMessage(), e);
				step = null;
			}
			
		}while (isFailing);
		

		return step;
	}

	/**
	 * 
	 * @param contextMap
	 * @param returnValue
	 * @param code
	 * @return IngestionStep
	 */
	private IngestionStep buildIngestionStep(Map<String, Object> contextMap, Response.Error error,
			StepStatusCode code, WorkflowState workflowState) {
		IngestionStep step = new IngestionStep();
		WorkflowStateCode workflowStateCode = getWorkflowStateCode();

		if (workflowStateCode.equals(WorkflowStateCode.UPDATE_WORKFLOW_STATE)){
			step.setRerunNumber(step.getRerunNumber() + 1);
		}else{
			step.setRerunNumber(workflowState.getRerunNumber());
		}
		//step.setDocumentId(document.getDocumentId());
		step.setIngestionStateCode(workflowStateCode.toString());
		step.setStatusCode(code.toString());
		step.setDescription(error.getMessage());
		step.setUpdatedBy("workflow");
		step.setWorkflowStateId(workflowState.getId());

		return step;
	}

	/**
	 * 
	 * @param document
	 * @return
	 */
	private WorkflowState buildWorkflowState(MediaDocument document) {

		WorkflowState workflowState = new WorkflowState();

		workflowState.setDocumentId(String.valueOf(document.getDocumentId()));
		workflowState.setIngestionStateCode(WorkflowStateCode.NEW.toString());
		workflowState.setForceRerun(true);
		workflowState.setRerunNumber(1);
		workflowState.setUpdatedBy("workflow");
		
		return workflowState;
	}

	/**
	 * 
	 * @param code
	 * @param message
	 * @return
	 */
	protected Response.Error buildError(int code, String message){
		Response.Error error = new Response.Error();
		
		error.setCode(code);
		error.setMessage(message);
		
		return error;
	}
	
	/**
	 * 
	 * @param step
	 * @return
	 */
	private Response<T> addPreviousStepToCurrentExecution(IngestionStep step) {
		
		Response<IngestionStep> response = null;
		
		boolean isFailing = false;
		
		do{
			isFailing = false;
			try {
				
				step.setId(null);
				step.setRerunNumber(step.getRerunNumber() + 1);
				step.setCreateDate(new Date());
				response = ingestionStepService.execute(IngestionStepService.PUT_STEP_VERSION_BY_ID,
						Methods.PUT.toString(), step);

			} catch (Exception e) {
				
				waitTime();
				isFailing = true;
				
				LOG.warn(e.getMessage(), e);
			}
			
		}while(isFailing);
		
		Response<T> newResponse = new Response<T>();
		newResponse.setError(response.getError());
		newResponse.setData((T)response.getData());
		
		return newResponse;

	}	
	
	/**
	 * @param workflowState
	 * @return
	 */
	private boolean mergeWorkflowSate(WorkflowState workflowState) {
		Response<WorkflowState> response = null;
		int responseCode = 0;
		
		boolean isFailing = false;
		
		do{
			isFailing = false;
			try {
	
				response = workflowStateService.execute(WorkflowStateService.PUT_WORKFLOW_STATES, Methods.PUT.toString(), workflowState);
				responseCode = response.getError().getCode();
				
				LOG.debug("WorkflowState updated to : " + response.getData());
			
			} catch (Exception e) {
	
				waitTime();
				isFailing = true;
				
				LOG.warn("mergeWorkflowSate -> " + e.getMessage(), e);

			}

		}while(isFailing);
		
		return  responseCode == EventCode.REQUEST_SUCCESS.getCode() || responseCode == EventCode.OBJECT_CREATED.getCode();
	}
	
	
	/**
	 * @param document
	 * @param resource
	 * @return
	 */
	private WorkflowState createWorkflowState(MediaDocument document) {
		
		WorkflowState workflowState = buildWorkflowState(document);
		
		Response<WorkflowState> response = null;
		boolean isFailing = false;
		
		do{
			isFailing = false;
			try {
	
				response = workflowStateService.execute(WorkflowStateService.PUT_WORKFLOW_STATES, Methods.PUT.toString(), workflowState);
				workflowState = response.getData();
	
			} catch (Exception ex) {
				
				waitTime();
				isFailing = true;
				
				LOG.warn("createWorkflowState " + workflowState.getDocumentId() +  " => " +  ex.getMessage(), ex);
	
			}
			
		}while(isFailing);
		
		return workflowState;
	}

	/**
	 * @param workflowState
	 * @param resource
	 * @return
	 */
	private WorkflowState findWorkflowState(WorkflowState workflowState, String resource) {

		Response<WorkflowState> response = null;

		try {

			response = workflowStateService.execute(resource, Methods.GET.toString());
			workflowState = response.getData();
			
			if (workflowState != null && StringUtils.equals(getWorkflowStateCode().toString(), WorkflowStateCode.UPDATE_WORKFLOW_STATE.toString()) ){
				workflowState.setRerunNumber(workflowState.getRerunNumber() + 1);
			}

		} catch (MediaServiceException e) {

			LOG.warn(e.getMessage(), e);
			
			workflowState = null;

		}
		
		return workflowState;
	}
	
	/**
	 * 
	 */
	private void waitTime(){
		 Random random = new Random();
		 long waitTime = 3000 + (random.nextInt(10) + 1) * 1000;
		 LOG.debug("Waiting time ---> " + waitTime / 1000);
	     try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			//nothing to do
		}
	}
	
	
}
