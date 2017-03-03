/**
 * 
 */
package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.DocumentFieldsDefinitionModel;
import com.mcmcg.ingestion.domain.Response;

/**
 * @author jaleman
 *
 */

@Service
public class DocumentFieldProfileService extends BaseService<DocumentFieldsDefinitionModel> {

	@Value("${documentfielddefinition.service.url}")
	private String serviceUrl;
	
	public static final String GET_DOCUMENT_FIELD_DEFINITION_BY_CODE = "/document-fields/document-field-definitions?code=";
	
	/**
	 * 
	 */
	public DocumentFieldProfileService() {
		
	}

	/**
	 * 
	 */
	@Override
	public String getName() {
		return DocumentFieldProfileService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<DocumentFieldsDefinitionModel>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<DocumentFieldsDefinitionModel>>() {};
	}

}
