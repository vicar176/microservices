
package com.mcmcg.utility.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mcmcg.utility.exception.S3Exception;
import com.mcmcg.utility.exception.ServiceException;
import com.mcmcg.utility.file.IFileSystem;

/**
 * @author jaleman
 *
 */
@Service
public class S3Service {
	
	private static final Logger LOG = Logger.getLogger(S3Service.class);

	@Autowired
	private IFileSystem fileSystem;
	
	
	public S3Service() {
	}

	public InputStream getInputStreamByKey (String bucket, String fileKey) throws ServiceException{
		
		return fileSystem.getInputStream(fileKey, bucket);
	}

	public void saveFile(MultipartFile multipartFile, String key, String bucketName)
			throws ServiceException, S3Exception {

		try {
			fileSystem.uploadToS3(key, bucketName, multipartFile.getInputStream());
		} catch (IOException e) {
			String message = String.format("Error occurred accessing the file with key %s -- %s", key, e.getMessage());
			LOG.error(message, e);
			throw new S3Exception(message, e.getCause());
		}

		LOG.info("File saved with key: " + key);

	}
}
