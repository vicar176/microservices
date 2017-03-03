package com.mcmcg.ingestion.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.MediaDocument;
import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.MediaMetadataModel.PdfTagging;
import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.exception.IngestionServiceException;
import com.mcmcg.ingestion.exception.MediaServiceException;
import com.mcmcg.ingestion.service.media.IService;
import com.mcmcg.ingestion.service.media.MediaMetadataModelService;
import com.mcmcg.ingestion.service.media.PdfTaggingUtilityService;
import com.mcmcg.ingestion.util.IngestionUtils;

@Service
public class TagPdfService extends BaseIngestionService{

	private static final Logger LOG = Logger.getLogger(TagPdfService.class);

	@SuppressWarnings("unchecked")
	@Override
	protected  MediaMetadataModel executeService(MediaDocument mediaDocument, Object ...params) throws IngestionServiceException, MediaServiceException {
		
		return tagPdf(mediaDocument);
	}
	
	@Override
	protected String getDocumentStatus() {
		
		return IngestionUtils.TAGGED;
	}
	
	/**
	 * 
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	private MediaMetadataModel tagPdf(MediaDocument mediaDocument) throws IngestionServiceException, MediaServiceException {
		// TODO Auto-generated method stub
		
		//Get MediaMetadataModel
		MediaMetadataModel mediaMetadata = getMediaMetadataByDocumentId(mediaDocument);
		LOG.debug(IngestionUtils.getJsonObject(mediaMetadata));
		//Tag Pdf 
		Response<MediaMetadataModel> response = dotagPdf(mediaMetadata, mediaDocument.getBucket());

		// Update MediaMetadataModel
		if (response != null && response.getData() != null){
			mediaMetadata = updateMetadata(mediaMetadata);
		}
		
		LOG.debug(String.format("MediaMetadataModel Info: %s for MediaDocument %s", mediaMetadata, mediaDocument));
		

		return  response.getData();	
	}

	/**
	 * @param mediaMetadata
	 * @return
	 * @throws MediaServiceException
	 */
	private Response<MediaMetadataModel> dotagPdf(MediaMetadataModel mediaMetadata, String bucket) throws MediaServiceException {
		///
		String resource = String.format(PdfTaggingUtilityService.PUT_TAG_METADATA, mediaMetadata.getDocumentId(), bucket);
		Response<MediaMetadataModel> response = pdfTaggingUtilityService.execute( resource, IService.PUT,mediaMetadata);
		return response;
	}

	/**
	 * 
	 * @param mediaMetadata
	 * @return
	 * @throws MediaServiceException
	 */
	private MediaMetadataModel updateMetadata(MediaMetadataModel mediaMetadata) throws MediaServiceException {
		
		String resource = MediaMetadataModelService.PUT_OR_GET_MEDIAMETADATA + mediaMetadata.getDocumentId();
		mediaMetadata.setPdfTagging(new PdfTagging(true, IngestionUtils.formatDate(new Date())));
		
		Response<MediaMetadataModel> response = mediaMetadataService.execute( resource, IService.PUT, mediaMetadata);
		
		return response.getData();
	}


}
