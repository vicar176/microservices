package com.mcmcg.utility.restcontroller;

import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mcmcg.utility.domain.Response;
import com.mcmcg.utility.exception.ServiceException;
import com.mcmcg.utility.service.S3Service;
import com.mcmcg.utility.util.EventCode;

/**
 * @author jaleman
 *
 */
@RestController
@RequestMapping(value = "/s3")
public class S3RestController extends BaseRestController{

	
	@Autowired
	private S3Service s3Service;
	
	public S3RestController() {
	}
	
	@RequestMapping(value = "/{bucket}/objects", method = RequestMethod.GET)
	public ResponseEntity<Object> extractDocumentData(@PathVariable ("bucket") String bucket,
									@RequestParam String key, HttpServletResponse httpServletResponse) {

		ResponseEntity<Object> responseEntity = null;
		Response<Object> response = null;
		try {
			
			InputStream inputStream = s3Service.getInputStreamByKey(bucket, URLDecoder.decode(key, "UTF-8"));
			byte[] fileBytes = IOUtils.toByteArray(inputStream);
		    HttpHeaders respHeaders = new HttpHeaders();
		    mediaTypeResolver(respHeaders, key);
			responseEntity = new ResponseEntity<Object>(fileBytes, respHeaders,  HttpStatus.OK);
			
		} catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), null);
			responseEntity = new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), null);
			responseEntity = new ResponseEntity<Object>(response, HttpStatus.OK);
		}

		return responseEntity;
	}

	/**
	 * 
	 * @param bucket
	 * @param key
	 * @param httpServletResponse
	 * @return
	 */
	@RequestMapping(value = "/{bucket}/keys", method = RequestMethod.GET)
	public ResponseEntity<Object> findKey(@PathVariable ("bucket") String bucket,
									@RequestParam String key, HttpServletResponse httpServletResponse) {

		ResponseEntity<Object> responseEntity = null;
		Response<Object> response = null;
		try {
			
			InputStream inputStream = s3Service.getInputStreamByKey(bucket, URLDecoder.decode(key, "UTF-8"));
			if (inputStream != null){
				response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "FOUND", Boolean.TRUE);
				inputStream.close();
			}else{
				response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "NOT FOUND", Boolean.FALSE);
			}
			responseEntity = new ResponseEntity<Object>(response, HttpStatus.OK);
			
		} catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), null);
			responseEntity = new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), null);
			responseEntity = new ResponseEntity<Object>(response, HttpStatus.OK);
		}

		return responseEntity;
	}
	
	@RequestMapping(value = "/{bucketName}/objects", method = RequestMethod.POST)
	public ResponseEntity<Object> saveFile(
			@PathVariable("bucketName") String bucketName,
			@RequestParam("multipartFile") MultipartFile multipartFile, 
			@RequestParam String key) {

		ResponseEntity<Object> responseEntity = null;
		Response<Object> response = null;

		try {

			s3Service.saveFile(multipartFile, key, bucketName);

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(),
					"Operation upload file was executed succesfully for " + key, Boolean.TRUE);
			responseEntity = new ResponseEntity<Object>(response, HttpStatus.OK);

		} catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), Boolean.FALSE);
			responseEntity = new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), Boolean.FALSE);
			responseEntity = new ResponseEntity<Object>(response, HttpStatus.OK);
		}

		return responseEntity;
	}

	/**
	 * response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".csv");
	 * @param key
	 * @return
	 */
	private void mediaTypeResolver (HttpHeaders respHeaders, String key){
		
		MediaType mediaType = MediaType.parseMediaType("application/json");
		
		if (StringUtils.endsWith(key,  ".pdf")){
			mediaType = MediaType.parseMediaType("application/pdf");
		}
		if (StringUtils.endsWith(key,  ".jpg")){
			mediaType = MediaType.parseMediaType("image/jpeg");
		}
		if (StringUtils.endsWith(key,  ".csv")){
			mediaType = MediaType.parseMediaType("text/csv");
			respHeaders.setContentDispositionFormData("attachment", FilenameUtils.getName(key));
		}
		//
		
		respHeaders.setContentType(mediaType); ;
	}

}
