
package com.mcmcg.dia.documentprocessor.media;

import com.mcmcg.dia.documentprocessor.exception.MediaServiceException;
import com.mcmcg.dia.iwfm.domain.Response;

/**
 * @author jaleman
 *
 */
public interface IService<T> {

	public final String GET = "GET";
	public final String POST = "POST";
	public final String PUT = "PUT";
	public final String POST_FILE = "POST_FILE";

	public String getName();

	public Response<T> execute(String command, String httpMethod, Object... params) throws MediaServiceException;

	public String getEndpoint();

}
