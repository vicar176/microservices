/**
 * 
 */
package com.mcmcg.utility.S3;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.mcmcg.utility.domain.Response;

/**
 * @author Victor Arias
 *
 */
public class SSSTest {

	String utilityEndpoint = "http://localhost:8080/media-utilities/sample-files/uploadS3";

	RestTemplate restTemplate = new RestTemplate();

	/**
	 * 
	 */
	public SSSTest() {
	}

	@Ignore
	@Test
	@SuppressWarnings(value = { "rawtypes" })
	public void testUtilityRestCall() {
		String fileName = "STMT_411_BENEFICIAL-COMPANY-LLC_xuGlPkjI.pdf";
		Response response = restTemplate.postForObject(utilityEndpoint, fileName, Response.class);
		
		System.out.println(response.getError().getMessage());
		
	}
}
