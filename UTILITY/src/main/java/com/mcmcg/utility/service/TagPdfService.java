package com.mcmcg.utility.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.mcmcg.utility.domain.DataElement;
import com.mcmcg.utility.domain.MediaMetadataModel;
import com.mcmcg.utility.domain.Response;
import com.mcmcg.utility.domain.Response.Error;
import com.mcmcg.utility.exception.S3Exception;
import com.mcmcg.utility.exception.ServiceException;
import com.mcmcg.utility.file.IFileSystem;
import com.mcmcg.utility.util.EventCode;
import com.mcmcg.utility.util.MetaDataUtil.BUCKET_NAMES_ENUM;

@Service
public class TagPdfService {
	private static final Logger LOG = Logger.getLogger(TagPdfService.class);

	@Value("${s3.bucket.original}")
	private String SOURCE_DIR;
	@Value("${sample.document.source.dir}")
	private String DEST_DIR;

	@Autowired
	private IFileSystem fileSystem;
	@Autowired
	SampleFileService sampleFileService;

	public MediaMetadataModel tagPdfwithMetadata(MediaMetadataModel mediaMetadataModel, String bucket) throws  ServiceException {

		LOG.info("Get the document " + mediaMetadataModel.getDocument().getDocumentNameString());

		try {
			//Get folder and filename
			String folder = FilenameUtils.getPath(mediaMetadataModel.getDocument().getDocumentNameString());     
			String filename = FilenameUtils.getName( mediaMetadataModel.getDocument().getDocumentNameString());
			
			//Do Pdf Tagging
			doPdfTagging(mediaMetadataModel, folder, bucket);
			
			uploadUpdatedPdfToS3(folder, filename, bucket);
			
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (DocumentException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}

		return mediaMetadataModel;

	}
	/**
	 * @param mediaMetadataModel
	 * @param folder
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 * @throws ServiceException 
	 */
	private void doPdfTagging(MediaMetadataModel mediaMetadataModel, String folder, String bucket)
			throws IOException, FileNotFoundException, DocumentException, ServiceException {
		
		InputStream is = null;
		PdfReader reader = null;
		PdfStamper stamper = null;
		
		try {
			is = fileSystem.getInputStream(mediaMetadataModel.getDocument().getDocumentNameString(),
					BUCKET_NAMES_ENUM.ORIGINAL, bucket);
			reader = new PdfReader(is);
		} catch (S3Exception e) {
			String message = String.format("Failed to access the document (%s)",
					mediaMetadataModel.getDocument().getDocumentNameString());
			LOG.error(message, e);
			throw new ServiceException(message, e.getCause());
		}		
		
		File newFile = new File(DEST_DIR + folder);
		newFile.mkdirs();
		FileOutputStream pdfOutputFile = new FileOutputStream(DEST_DIR + mediaMetadataModel.getDocument().getDocumentNameString());
		
		IOUtils.copy(is, pdfOutputFile);
		
		stamper = new PdfStamper(reader, pdfOutputFile);

		Map<String, String> info = reader.getInfo();
		info.put("accountNumber", "" + mediaMetadataModel.getAccountNumber() + "");
		info.put("documentDate", formatDateString(mediaMetadataModel.getDocumentDate()));
		info.put("originalDocumentType", mediaMetadataModel.getOriginalDocumentType().getCode());
		info.put("translatedDocumentType", mediaMetadataModel.getDocument().getTranslatedDocumentType());
		for (DataElement dataElement : mediaMetadataModel.getDataElements()) {
			info.put(dataElement.getFieldDefinition().getFieldName(), dataElement.getValue());

		}
		
		stamper.setMoreInfo((HashMap<String, String>) info);
		stamper.close();
		reader.close();
	}
	/**
	 * 
	 * @param sampleFileName
	 * @return
	 */
	public Response<String> uploadUpdatedPdfToS3(String folder, String filename, String bucket) {

		Response<String> response = new Response<String>();
		Error status = new Error();
		try {
			fileSystem.uploadToS3(DEST_DIR, folder, filename, bucket);
			status.setMessage("Uploading files to S3 in progress");
			status.setCode(EventCode.REQUEST_SUCCESS.getCode());
		} catch (Exception e) {
			String message = "An error occurred while trying to upload files to S3";
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(message);
			LOG.error(e.getMessage(), e);
		}
		response.setError(status);
		return response;
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	private String formatDateString(String date) {
		
		String dateString = date;
		
		if (date != null){
			String tokens[] = date.split("-");
			
			if (tokens.length == 3){
				dateString = tokens[2] + "/" + tokens[1] + "/" + tokens[0];
			}
		}

		return dateString;
	}
}
