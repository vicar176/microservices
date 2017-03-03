/**
 * 
 */
package com.mcmcg.ingestion.service.media;

import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.exception.MediaServiceException;

/**
 * @author jaleman
 *
 */
public interface IService<T> {

	public final String GET  = "GET";
	public final String POST = "POST";
	public final String PUT = "PUT";
	public final String GET_FOR_OBJECT  = "GET_FOR_OBJECT";
	
	public String getName();
	
	public Response<T> execute(String command, String httpMethod, Object... params ) throws MediaServiceException;
	
	public String getEndpoint();
	
}
