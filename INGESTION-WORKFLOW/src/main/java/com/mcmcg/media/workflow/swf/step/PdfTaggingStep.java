/**
 * 
 */
package com.mcmcg.media.workflow.swf.step;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.common.WorkflowConstants;
import com.mcmcg.media.workflow.common.WorkflowStateCode;
import com.mcmcg.media.workflow.service.BaseService.Methods;
import com.mcmcg.media.workflow.service.domain.MediaDocument;
import com.mcmcg.media.workflow.service.domain.MediaMetadataModel;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.exception.MediaServiceException;
import com.mcmcg.media.workflow.service.ingestion.MetadataIngestionService;

/**
 * @author jaleman
 *
 */
@Component
public class PdfTaggingStep extends BaseStep<MediaMetadataModel>{

	private static final Logger LOG = Logger.getLogger(PdfTaggingStep.class);
	/**
	 * 
	 */
	public PdfTaggingStep() {

	}

	@Override
	public Response<MediaMetadataModel> execute(Map<String, Object> contextMap) throws MediaServiceException{
		MediaDocument mediaDocument = (MediaDocument)contextMap.get(WorkflowConstants.DOCUMENT_ID);
		
		String resource = MetadataIngestionService.PUT_PDF_TAGGING + mediaDocument.getDocumentId();
		
		Response<MediaMetadataModel> response = metadataIngestionService.execute(resource, Methods.PUT.toString(), mediaDocument);
			
		LOG.info(String.format("Step [PDF Tagging] Document [%s] Error[%s]  Data[%s] ", mediaDocument.getDocumentId(), response.getError().getMessage(), response.getData()));
		
		return response;
	}
	
	@Override
	public WorkflowStateCode getWorkflowStateCode() {

		return WorkflowStateCode.PDF_TAGGING;

	}

}
