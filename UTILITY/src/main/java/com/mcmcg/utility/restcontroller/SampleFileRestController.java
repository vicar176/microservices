package com.mcmcg.utility.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mcmcg.utility.annotation.Auditable;
import com.mcmcg.utility.domain.Response;
import com.mcmcg.utility.domain.SampleFile;
import com.mcmcg.utility.service.SampleFileService;
import com.mcmcg.utility.util.EventCode;

/**
 * 
 * @author wporras
 *
 */
@RestController
@RequestMapping(value = "/sample-files")
public class SampleFileRestController extends BaseRestController {

	@Autowired
	SampleFileService sampleFileService;

	/**
	 * Handle the upload of sample files and creates an image for each page
	 * 
	 * @param fileName
	 * @param multipartFile
	 * @param documentTypeCode
	 * @param sellerId
	 * @param originalLenderName
	 * @return Response<SampleFileUpload>
	 */
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Response<SampleFile>> handleSampleFileUpload(
			@RequestParam("fileName") String fileName,
			@RequestParam("file") MultipartFile multipartFile, 
			@RequestParam("documentType.code") String documentTypeCode,
			@RequestParam("seller.id") Long sellerId, 
			@RequestParam("originalLender.name") String originalLenderName) {

		Response<SampleFile> response = sampleFileService.handleSampleFileUpload(fileName, multipartFile, documentTypeCode,
				sellerId, originalLenderName);
		return new ResponseEntity<Response<SampleFile>>(response, HttpStatus.CREATED);
	}

	/**
	 * Retrieve the image of a specific sample file page number
	 * 
	 * @param fileName
	 * @param pageNumber
	 * @return ResponseEntity<byte[]>
	 */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "image/jpg")
	public ResponseEntity<byte[]> getFile(@RequestParam("fileName") String fileName,
			@RequestParam("pageNumber") int pageNumber) {

		byte[] response = sampleFileService.getSampleFile(fileName, pageNumber);
		
		HttpStatus status = null;
		if (response == null || response.length == 0) {
			status = HttpStatus.NOT_FOUND;
		} else {
			status = HttpStatus.OK;
		}
		
		return new ResponseEntity<byte[]>(response, status);
	}
}
