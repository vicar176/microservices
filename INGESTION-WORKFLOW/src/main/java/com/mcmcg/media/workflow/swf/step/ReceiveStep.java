package com.mcmcg.media.workflow.swf.step;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.common.WorkflowConstants;
import com.mcmcg.media.workflow.common.WorkflowStateCode;
import com.mcmcg.media.workflow.service.BaseService.Methods;
import com.mcmcg.media.workflow.service.domain.AccountOALDModel;
import com.mcmcg.media.workflow.service.domain.MediaDocument;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.exception.MediaServiceException;
import com.mcmcg.media.workflow.service.ingestion.AccountMetadataIngestionService;


/**
 * @author 
 *
 */

@Component
public class ReceiveStep extends BaseStep<AccountOALDModel> {

	private static final Logger LOG = Logger.getLogger(PdfTaggingStep.class);
	/**
	 * 
	 */
	public ReceiveStep() {

	}

	public Response<AccountOALDModel> execute(Map<String, Object> contextMap) throws MediaServiceException{
		MediaDocument mediaDocument = (MediaDocument)contextMap.get(WorkflowConstants.DOCUMENT_ID);
		
		String resource = AccountMetadataIngestionService.PUT_RECEIVE + mediaDocument.getDocumentId();
		
		Response<AccountOALDModel> response = accountMetadataIngestionService.execute(resource, Methods.PUT.toString(), mediaDocument);
			
		LOG.info(String.format("Step [Receive] Document [%s] Error[%s]  Data[%s] ", mediaDocument.getDocumentId(), response.getError().getMessage(), response.getData()));
		
		return response;
	}

	@Override
	public WorkflowStateCode getWorkflowStateCode() {

		return WorkflowStateCode.RECEIVE;

	}

	
	
}
