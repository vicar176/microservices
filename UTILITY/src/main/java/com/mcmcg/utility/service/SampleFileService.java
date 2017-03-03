package com.mcmcg.utility.service;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mcmcg.utility.domain.Response;
import com.mcmcg.utility.domain.Response.Error;
import com.mcmcg.utility.domain.SampleFile;
import com.mcmcg.utility.exception.PersistenceException;
import com.mcmcg.utility.exception.ServiceException;
import com.mcmcg.utility.file.FileVO;
import com.mcmcg.utility.file.IFileSystem;
import com.mcmcg.utility.util.EventCode;
import com.mcmcg.utility.util.MetaDataUtil;
import com.mcmcg.utility.util.MetaDataUtil.BUCKET_NAMES_ENUM;

/**
 * 
 * @author wporras
 *
 */
@Service
public class SampleFileService {

	private static final Logger LOG = Logger.getLogger(SampleFileService.class);

	@Value("${sample.document.source.dir}")
	private String SOURCE_DIR;

	@Autowired
	private IFileSystem fileSystem;

	/**
	 * Handle the upload of sample files and creates an image for each page
	 * 
	 * @param originalName
	 * @param multipartFile
	 * @param documentTypeCode
	 * @param sellerId
	 * @param originalLenderName
	 * @return Response<SampleFileUpload>
	 * @throws PersistenceException
	 */
	public Response<SampleFile> handleSampleFileUpload(String originalName, MultipartFile multipartFile,
			String documentTypeCode, Long sellerId, String originalLenderName) {

		Response<SampleFile> response = new Response<SampleFile>();
		Error status = new Error();
		FileVO fileVO = null;

		if (!multipartFile.isEmpty()) {
			try {

				if (originalLenderName.contains("/")) {
					originalLenderName = originalLenderName.replaceAll("/", "-");
				}
				
				if (documentTypeCode.contains("_")) {
					documentTypeCode = documentTypeCode.replaceAll("_", "-");
				}

				// Validate extension
				String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
				if (!MetaDataUtil.validateFileExtension(fileExtension)) {
					throw new ServiceException(String.format("The file format %s is not supported", fileExtension));
				}

				// Save the template file
				try {
					String fileName = generateFileName(documentTypeCode, sellerId, originalLenderName).toString() + "."
							+ fileExtension;
					fileVO = new FileVO(multipartFile.getInputStream(), fileName);
					fileVO.setPath(documentTypeCode + "/" + FilenameUtils.removeExtension(fileVO.getFileName()) + "/");
				} catch (IOException e) {
					throw new ServiceException("Failed to read the the sample file: " + originalName);
				}

				// Create Folder
				if (!fileSystem.createDirectory(SOURCE_DIR + fileVO.getPath())) {
					throw new ServiceException("Failed to create the folder: " + fileVO.getPath());
				}
				// Save file
				fileSystem.createFile(fileVO);
				LOG.info("Sample File saved with name: " + fileVO.toString());

				// Create images
				Integer totalImages = 0;
				if (fileExtension.equalsIgnoreCase(MetaDataUtil.EXTENSION_TYPES.pdf.toString())) {
					totalImages = MetaDataUtil.convertPdfToImages(fileVO, fileSystem, BUCKET_NAMES_ENUM.TEMPLATE, true);
				}
				// else if (StringUtils.startsWithIgnoreCase(fileExtension,
				// MetaDataUtil.EXTENSION_TYPES.tif.toString())) {
				// totalImages = MetaDataUtil.convertTiffToImages(fileVO,
				// fileSystem);
				// }

				if (totalImages == null || totalImages == 0) {
					throw new ServiceException("Failed to create the images for the file " + originalName);
				} else {
					LOG.info(totalImages + " images created for the Sample File: " + fileVO.getFileName());
				}

				// Upload files to S3
				uploadFilesToS3Synchronic(fileVO);

				SampleFile sampleFile = new SampleFile();
				sampleFile.setFileName(fileVO.getFileName());
				sampleFile.setTotalPages(totalImages);

				status.setCode(EventCode.OBJECT_CREATED.getCode());
				status.setMessage("Operation upload sample file was executed succesfully for " + fileVO.getFileName());
				response.setData(sampleFile);
			} catch (ServiceException e) {
				LOG.error(e.getMessage(), e);
				status.setCode(EventCode.SERVICE_ERROR.getCode());
				status.setMessage(e.getMessage());
			}
		}

		response.setError(status);
		return response;
	}

	/**
	 * Retrieve the image of a specific sample file page number
	 * 
	 * @param fileName
	 * @param pageNumber
	 * @return byte[]
	 */
	public byte[] getSampleFile(String fileName, int pageNumber) {
		byte[] bao = null;
		try {
			LOG.info("Retrieving sample file with name: " + fileName);
			bao = fileSystem.getImage(fileName, pageNumber, BUCKET_NAMES_ENUM.TEMPLATE, true);
			LOG.info(String.format("Sample file %s retrieved successfully", fileName));
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}

		return bao;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	///								PRIVATE METHODS
	/////////////////////////////////////////////////////////////////////////////////////////
	
	private void uploadFilesToS3Synchronic(FileVO fileVO) throws ServiceException {

		LOG.info("Start upload sample file with name: " + fileVO.getFileName());

		try {
			fileSystem.uploadFilesFromLocal(fileVO, false);
		} catch (Exception e) {
			String message = "An error occurred while trying to upload files to S3, " + fileVO.getFileName();
			LOG.error(message, e);
			throw new ServiceException(message);
		}

		LOG.info("End upload sample file with name: " + fileVO.getFileName());
	}

	

	private StringBuffer generateFileName(String documentTypeCode, Long sellerId, String originalLenderName) {
		StringBuffer fileName = new StringBuffer(documentTypeCode + "_" + sellerId + "_");
		fileName.append(originalLenderName.replaceAll(" ", "-") + "_");
		fileName.append(RandomStringUtils.random(8, true, true));
		return fileName;
	}

}
