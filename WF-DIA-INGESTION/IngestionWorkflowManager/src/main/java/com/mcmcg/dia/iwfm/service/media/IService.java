/**
 * 
 */
package com.mcmcg.dia.iwfm.service.media;

import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.exception.MediaServiceException;

/**
 * @author jaleman
 *
 */
public interface IService<T> {

	public final String GET  = "GET";
	public final String POST = "POST";
	public final String PUT = "PUT";
	
	public String getName();
	
	public Response<T> execute(String command, String httpMethod, Object... params ) throws MediaServiceException;
	
	public String getEndpoint();
	
	public void setEnv(String env);
	
}
