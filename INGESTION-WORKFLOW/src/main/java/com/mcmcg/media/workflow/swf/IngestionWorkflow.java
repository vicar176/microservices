package com.mcmcg.media.workflow.swf;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.mcmcg.media.workflow.common.EventCode;
import com.mcmcg.media.workflow.common.WorkflowConstants;
import com.mcmcg.media.workflow.domain.MediaDocumentStatus;
import com.mcmcg.media.workflow.exception.WorkflowException;
import com.mcmcg.media.workflow.service.BaseService.Methods;
import com.mcmcg.media.workflow.service.domain.AccountOALDModel;
import com.mcmcg.media.workflow.service.domain.BaseDomain;
import com.mcmcg.media.workflow.service.domain.MediaDocument;
import com.mcmcg.media.workflow.service.domain.MediaMetadataModel;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.exception.MediaServiceException;
import com.mcmcg.media.workflow.service.ingestion.IngestionWorkflowManager;
import com.mcmcg.media.workflow.service.media.MediaMetadataService;
import com.mcmcg.media.workflow.swf.activity.IngestionActivities;
import com.mcmcg.media.workflow.util.WorkflowUtil;

/**
 * @author jaleman
 *
 */
@SuppressWarnings({"rawtypes" })
public class IngestionWorkflow implements Runnable{

	private final static Logger LOG = Logger.getLogger(IngestionWorkflow.class);
	
	private final String filename;
	
	private final String message;
	
	private String env;
	private final ApplicationContext context;
	
	public static final String FAILED_CODE = "Failed";
	public static final String SUCCESS_CODE = "Success";
	public static final String EXCEPTION_CODE = "Exception";
	
	public static final String DISCARDED = "discarded";
	public static final String TEMPLATE_NOT_FOUND = "templateNotFound";
	
	/**
	 * 
	 * @param filename
	 * @param message
	 */
	public IngestionWorkflow(final String filename, final String message, final ApplicationContext context) {
		
		this.filename = filename;
		this.message = message;
		this.context = context;
	}

	public void execute() {
		env = (String)this.context.getBean("env");
		
		final Map<String, Object> contextMap = startWorkflow(filename, message, env);
		
		try{
			IngestionActivities ingestionActivities = (IngestionActivities)context.getBean(IngestionActivities.class);
			
			//Step 1
			Response<BaseDomain> updateWorkflowState = ingestionActivities.updateWorkflowState(contextMap);
			evaluateStepResult(contextMap, updateWorkflowState);
			
			//Step 2
			Response<MediaMetadataModel> extraction = ingestionActivities.extraction(contextMap);
			evaluateStepResult(contextMap, extraction);

			//Step 3
			Response<MediaMetadataModel> autoValidation = ingestionActivities.autoValidation(contextMap);
			evaluateStepResult(contextMap, autoValidation);
			
			//Step 4 - Statement Translation
			Response<MediaMetadataModel> statementTranslation = ingestionActivities.statementTranslation(contextMap);
			evaluateStepResult(contextMap, statementTranslation);
			
			//Step 5
			Response<AccountOALDModel> receiveStep = ingestionActivities.receive(contextMap);
			evaluateStepResult(contextMap, receiveStep);
		
			//Step 6
			if (isOkToProceed(IngestionWorkflowManager.GET_PARAM_PDF_TAGGING)){
				Response<MediaMetadataModel> pdfTagging = ingestionActivities.pdfTagging(contextMap);
				evaluateStepResult(contextMap, pdfTagging);
			}
			
			//Step 7
			Response<BaseDomain> updateRerunStatus = ingestionActivities.updateRerunStatus(contextMap);
			evaluateStepResult(contextMap, updateRerunStatus);
			
			stopWorflow(contextMap, updateRerunStatus);
			
		}
		catch (Throwable e){
			
			LOG.error(e.getMessage(), e);
		}

	}

	/*******************
	 * 
	 * 
	 * 					PRIVATE METHODS
	 * 
	 */


	/**
	 * @param contextMap
	 * @param updateRerunStatus
	 */
	private void stopWorflow(final Map<String, Object> contextMap, Response<BaseDomain> updateRerunStatus) {

		if ( updateRerunStatus != null){
		
			MediaDocument document = (MediaDocument) contextMap.get(WorkflowConstants.DOCUMENT_ID);
			String env = (String) contextMap.get(WorkflowConstants.ENV);
			updateDocumentStatus(String.valueOf(document.getDocumentId()), IngestionWorkflow.SUCCESS_CODE, SUCCESS_CODE, document.getBatchProfileJobId(), env);
			LOG.info("Document Finished: " + document.getDocumentId());
			LOG.info("Batch Profile Job Id: " + document.getBatchProfileJobId());
			
		}
		
	}
	
	/**
	 * @param contextMap
	 * @param result
	 * @throws WorkflowException
	 */
	private void evaluateStepResult(Map<String, Object> contextMap, Response result) throws WorkflowException {
		
		
		if (result != null && 
		   (
			 result.getError().getCode() == EventCode.REQUEST_SUCCESS.getCode() 
			 	|| 
		     result.getError().getCode() == EventCode.OBJECT_CREATED.getCode())
		   ) 
		{

			LOG.info(result.getError().getMessage());
			
		} else {
			
			markDocumentAsFailed(contextMap, result);

		}
		
	}

	/**
	 * @param contextMap
	 * @param result
	 * @throws WorkflowException
	 */
	private void markDocumentAsFailed(Map<String, Object> contextMap, Response result) throws WorkflowException {
		MediaDocument document = (MediaDocument) contextMap.get(WorkflowConstants.DOCUMENT_ID);
		String env = (String) contextMap.get(WorkflowConstants.ENV);
		String mediaMetadata = FAILED_CODE;
		if (result != null){
			
			String errorMessage = result.getError().getMessage();

			if (StringUtils.contains(errorMessage, DISCARDED) ){
				
				mediaMetadata = DISCARDED;
				
			}else if (StringUtils.contains(errorMessage, TEMPLATE_NOT_FOUND)){
				
				mediaMetadata = TEMPLATE_NOT_FOUND;
				
			}
			
			updateDocumentStatus(String.valueOf(document.getDocumentId()), EXCEPTION_CODE, mediaMetadata, document.getBatchProfileJobId(), env);
			
		}else{
			
			updateDocumentStatus(String.valueOf(document.getDocumentId()), FAILED_CODE, FAILED_CODE, document.getBatchProfileJobId(), env);
			
		}
		
		contextMap.put(FAILED_CODE, true);
		
		String message = (result != null 
							&& result.getError() != null 
							&& !StringUtils.startsWith(result.getError().getMessage(), "null")) 
							? result.getError().getMessage() : mediaMetadata;
 
		throw new WorkflowException(message);
	}

	/**
	 * 
	 * @param ctx
	 * @return
	 */
	private boolean updateDocumentStatus(String documentId, String documentStatus, String mediaMetadataStatus, int batchProfileJobId, String env) {
		boolean isOk = false;
		
		String resource = String.format(IngestionWorkflowManager.POST_DOCUMENT_ID, documentId, batchProfileJobId, env);
		MediaDocumentStatus mediaDocumentStatus = new MediaDocumentStatus();
		mediaDocumentStatus.setDocumentStatus(documentStatus);
		mediaDocumentStatus.setMediaMetadataStatus(mediaMetadataStatus);
		LOG.info(documentId + " " + documentStatus + " " + mediaMetadataStatus);
		
		boolean isFailing = false;
		int attempts = 3;
		do{
			isFailing = false;
			try {
				Response<Object> response = ((IngestionWorkflowManager)context.getBean("ingestionWorkflowManager")).execute(resource, Methods.PUT.toString(), mediaDocumentStatus);
				
				if (response.getData() != null){
					isOk = (Boolean)response.getData();
				}
				
			} catch (Throwable e) {
				isFailing = true;
				
				StringBuilder messageBuilder = new StringBuilder();
				messageBuilder.append(filename).append(" ").
				   			   append(mediaMetadataStatus).append(" ").
							   append(documentStatus).append(" ").append(e.getMessage());
				
				LOG.warn(filename + "---->" + messageBuilder.toString(), e);
			}
			
		}while(isFailing  && --attempts > 0);
		
		return isOk;
	}
	/**
	 * @param filename
	 * @param contextMap
	 */
	private Map<String, Object> startWorkflow(String filename, String message, String env) {
		Map<String, Object> contextMap = new HashMap<String, Object>();
		MediaDocument mediaDocument = WorkflowUtil.buildMediaDocument(filename);
		
		contextMap.put(WorkflowConstants.DOCUMENT_ID, mediaDocument);
		contextMap.put(WorkflowConstants.SQS_DOCUMENT, message);
		contextMap.put(WorkflowConstants.ENV, env);

		//delete Metadata
		//deleteMetadata(String.valueOf(mediaDocument.getDocumentId()));
		
		return contextMap;
	}

	@Override
	public void run() {
		execute();
	}

	/**
	 * 
	 * @param ctx
	 * @return
	 */
	private boolean isOkToProceed(String resource) {

		boolean isFlagOn = true;
		
		try {
			
			IngestionWorkflowManager ingestionWorkflowManager = (IngestionWorkflowManager) context.getBean("ingestionWorkflowManager");
			Response<Object> response = ingestionWorkflowManager.execute(resource, Methods.GET.toString());
			
			if (response.getData() != null){
				isFlagOn = Boolean.parseBoolean(response.getData().toString());
			}
			
		} catch (Throwable e) {

			LOG.warn(resource + " =>" + e.getMessage(), e);
			
		}
		LOG.info("resource -> " + resource + "   isOkToProceed --> " + isFlagOn);
		
		return isFlagOn;
	}
	
	/**
	 * @param ctx
	 * @param documentAsString
	 * @param proceed
	 * @return
	 */
	private boolean deleteMetadata(String documentAsString) {
		boolean isDeleted = false;
		MediaDocument mediaDocument = WorkflowUtil.buildMediaDocument(documentAsString);
		MediaMetadataService mediaMetadataService = (MediaMetadataService) context.getBean("mediaMetadataService");
		
		if (mediaMetadataService != null){
			String resource = MediaMetadataService.GET_MEDIAMETADATA + mediaDocument.getDocumentId();
			try {
				mediaMetadataService.execute(resource, Methods.DELETE.toString());
				isDeleted = true;
			} catch (Throwable e) {
				LOG.warn(e.getMessage(), e);
			}
		}
		return isDeleted;
	}
}
