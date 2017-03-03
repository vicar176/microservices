package com.mcmcg.media.workflow.swf.step;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.common.DocumentTypes;
import com.mcmcg.media.workflow.common.EventCode;
import com.mcmcg.media.workflow.common.StepStatusCode;
import com.mcmcg.media.workflow.common.WorkflowConstants;
import com.mcmcg.media.workflow.common.WorkflowStateCode;
import com.mcmcg.media.workflow.service.BaseService.Methods;
import com.mcmcg.media.workflow.service.domain.MediaDocument;
import com.mcmcg.media.workflow.service.domain.MediaMetadataModel;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.exception.MediaServiceException;
import com.mcmcg.media.workflow.service.ingestion.MetadataIngestionService;

/**
 * 
 * @author wporras
 *
 */
@Component
public class StatementTranslationStep extends BaseStep<MediaMetadataModel> {

	private static final Logger LOG = Logger.getLogger(PdfTaggingStep.class);

	public StatementTranslationStep() {
	}

	@Override
	public Response<MediaMetadataModel> execute(Map<String, Object> contextMap) throws MediaServiceException{

		Response<MediaMetadataModel> response = null;

		MediaDocument mediaDocument = (MediaDocument) contextMap.get(WorkflowConstants.DOCUMENT_ID);

		if (StringUtils.equalsIgnoreCase(mediaDocument.getDocumentType(), DocumentTypes.STATEMENT.getType())) {

			String resource = MetadataIngestionService.PUT_STATEMENT_TRANSLATION + mediaDocument.getDocumentId();
			
			response = metadataIngestionService.execute(resource, Methods.PUT.toString(), mediaDocument);
			
			LOG.info(String.format("Step [Stmt Translation] Document [%s] Error[%s]  Data[%s] ", mediaDocument.getDocumentId(), 
																								 response.getError().getMessage(), 
																								 response.getData()));
				
		} else {
			
			response = new Response<MediaMetadataModel>();

			Response.Error error = new Response.Error();
			error.setCode(EventCode.REQUEST_SUCCESS.getCode());
			error.setMessage(StepStatusCode.SKIP.toString());
			
			response.setError(error);
		}

		return response;
	}

	@Override
	public WorkflowStateCode getWorkflowStateCode() {

		return WorkflowStateCode.STATEMENT_TRANSLATION;
	}
}
