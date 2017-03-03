/**
 * 
 */
package com.mcmcg.ingestion.service.media;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.DataElement;
import com.mcmcg.ingestion.domain.Response;

/**
 * @author jaleman
 *
 */

@Service
public class ExtractionUtilityService extends BaseService<List<DataElement>> {

	@Value("${mediautility.service.url}")
	private String serviceUrl;
	
	public static final String PUT_EXTRACT_METADATA = "/document-extractions?location=%s&bucket=%s";
	
	/**
	 * 
	 */
	public ExtractionUtilityService() {

	}

	@Override
	public String getName() {
		return ExtractionUtilityService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl ;
	}

	@Override
	public ParameterizedTypeReference<Response<List<DataElement>>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<List<DataElement>>>() {};
	}
	
}
