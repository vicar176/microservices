package com.mcmcg.media.workflow.swf.activity;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.exception.WorkflowException;
import com.mcmcg.media.workflow.service.domain.AccountOALDModel;
import com.mcmcg.media.workflow.service.domain.BaseDomain;
import com.mcmcg.media.workflow.service.domain.MediaMetadataModel;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.swf.IngestionWorkflow;
import com.mcmcg.media.workflow.swf.step.AutoValidationStep;
import com.mcmcg.media.workflow.swf.step.BaseStep;
import com.mcmcg.media.workflow.swf.step.ExtractionStep;
import com.mcmcg.media.workflow.swf.step.PdfTaggingStep;
import com.mcmcg.media.workflow.swf.step.ReceiveStep;
import com.mcmcg.media.workflow.swf.step.StatementTranslationStep;
import com.mcmcg.media.workflow.swf.step.UpdateRerunStatusStep;
import com.mcmcg.media.workflow.swf.step.UpdateWorkflowStateStep;

/**
 * @author jaleman
 *
 */
@SuppressWarnings({"unchecked","rawtypes" })
@Component
public class IngestionActivities{

	private final static Logger LOG = Logger.getLogger(IngestionActivities.class);

	@Autowired
	private ExtractionStep extractionStep;

	@Autowired
	private UpdateRerunStatusStep updateRerunStatusStep;

	@Autowired
	private UpdateWorkflowStateStep updateWorkflowStateStep;

	@Autowired
	private AutoValidationStep autoValidationStep;

	@Autowired
	private PdfTaggingStep pdfTaggingStep;

	@Autowired
	private StatementTranslationStep statementTranslationStep;
	
	@Autowired
	private ReceiveStep receiveStep;
	
	
	public IngestionActivities() {
		
	}

	
	
	public Response<BaseDomain> updateWorkflowState(Map<String, Object> contextMap) throws WorkflowException{

		return execute(updateWorkflowStateStep, contextMap);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mcmcg.media.workflow.aws.activity.IngestionActivities#receive(java.
	 * util.Map)
	 */
	
	public Response<AccountOALDModel> receive(Map<String, Object> contextMap) throws WorkflowException{

		return execute(receiveStep, contextMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mcmcg.media.workflow.aws.activity.IngestionActivities#extraction(java
	 * .util.Map)
	 */
	
	public Response<MediaMetadataModel> extraction(Map<String, Object> contextMap) throws WorkflowException{

		return execute(extractionStep, contextMap);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mcmcg.media.workflow.aws.activity.IngestionActivities#autoValidation(
	 * java.util.Map)
	 */
	
	public Response<MediaMetadataModel> autoValidation(Map<String, Object> contextMap) throws WorkflowException{

		return execute(autoValidationStep, contextMap);

	}

	/**
	 * Executes the Statement Translation Step
	 * 
	 * @see com.mcmcg.media.workflow.aws.activity.IngestionActivities#
	 *      statementTranslation(java.util.Map)
	 */
	
	public Response<MediaMetadataModel> statementTranslation(Map<String, Object> contextMap) throws WorkflowException {

		return execute(statementTranslationStep, contextMap);

	}

	
	public Response<MediaMetadataModel> pdfTagging(Map<String, Object> contextMap) throws WorkflowException{

		return execute(pdfTaggingStep, contextMap);
	}

	
	public Response<BaseDomain> updateRerunStatus(Map<String, Object> contextMap) throws WorkflowException{

		Response<BaseDomain> result = execute(updateRerunStatusStep, contextMap); 

		return result;

	}
	
	/*******************
	 * 
	 * 
	 * 					PRIVATE METHODS
	 * 
	 */
	
	/**
	 * 
	 * @param step
	 * @param context
	 * @return
	 */
	private Response execute(BaseStep step, Map<String, Object> contextMap) throws WorkflowException {
		
		Response<Object> result = executeStep(step, contextMap);		
		
		contextMap.put(step.getName(), result);
		
		return result;
	}
	
	/**
	 * @param step
	 * @param contextMap
	 * @return
	 */
	private Response<Object> executeStep(BaseStep step, Map<String, Object> contextMap) {
		Response<Object> result = null;
		try{
			result = step.doExecution(contextMap);
		}
		catch (Throwable t){
			contextMap.put(IngestionWorkflow.FAILED_CODE, true);
			LOG.error(t.getMessage(), t);
		}
		return result;
	}

	
}
