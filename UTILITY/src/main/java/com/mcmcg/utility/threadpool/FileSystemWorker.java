package com.mcmcg.utility.threadpool;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.mcmcg.utility.annotation.Diagnostics;
import com.mcmcg.utility.aop.DiagnosticsAspect;
import com.mcmcg.utility.exception.S3Exception;
import com.mcmcg.utility.file.FileVO;
import com.mcmcg.utility.util.MetaDataUtil;
import com.mcmcg.utility.util.MetaDataUtil.BUCKET_NAMES_ENUM;

/**
 * @author varias
 *
 */
@Component("fileSystemWorker")
@Scope("prototype")
public class FileSystemWorker {

	private final static Logger LOG = Logger.getLogger(FileSystemWorker.class);
	
	@Autowired
	private DiagnosticsAspect diagnosticsAspect;

	public boolean createFile(Map<String, Object> s3Map) {
		
		long start = System.currentTimeMillis();
		
		FileVO fileVO = (FileVO) s3Map.get("fileVO");
		String sourceDir = (String) s3Map.get("sourceDir");

		boolean success = false;
		File file = new File(sourceDir + fileVO.getPath() + fileVO.getFileName());
		BufferedOutputStream stream = null;
		try {
			LOG.info("Create file " + file.getPath());
			stream = new BufferedOutputStream(new FileOutputStream(file));
			stream.write(IOUtils.toByteArray(fileVO.getInputStream()));
			success = true;
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e1) {
			LOG.error(e1.getMessage(), e1);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		long end = System.currentTimeMillis();
			
		diagnosticsAspect.log("createFile", new Object[] {s3Map}, "FileSystem-Writes", start, end);
		
		return success;
	}

	@Diagnostics(area = "PDF-CreateImagesLocally")
	public boolean createImage(Map<String, Object> s3Map) {

		FileVO fileVO = (FileVO) s3Map.get("fileVO");
		int pageNum = (Integer) s3Map.get("pageNum");
		String sourceDir = (String) s3Map.get("sourceDir");
		BUCKET_NAMES_ENUM bucketEnum = (BUCKET_NAMES_ENUM) s3Map.get("bucketEnum");

		boolean result = false;
		PDDocument document = null;
		try {
			document = PDDocument.load(fileVO.getInputStream());
			PDFRenderer renderer = new PDFRenderer(document);

			LOG.info("Rendering image start");
			BufferedImage bi = renderer.renderImageWithDPI(pageNum, 300);

			File imageFile = new File(
					MetaDataUtil.getImagePath(sourceDir + fileVO.getPath(), pageNum, true, bucketEnum));

			result = ImageIO.write(bi, "jpg", imageFile);			

		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			if (document != null) {
				try {
					document.close();
					LOG.info("End convert PDF to Images process");
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}

		return result;
	}

	public void uploadFileFromLocal(Map<String, Object> s3Map) {

		long start = System.currentTimeMillis();
		
		File fileEntry = (File) s3Map.get("file");
		String bucketName = (String) s3Map.get("bucketName");
		String filesPath = (String) s3Map.get("path");
		AmazonS3 s3 = (AmazonS3) s3Map.get("s3");
		try {
			LOG.debug("Moving file " + fileEntry.getName() + " to " + filesPath);

			PutObjectRequest putRequest = new PutObjectRequest(bucketName, filesPath, fileEntry);
			
			// Request server-side encryption. Seeing up objectmeta data
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);     
			putRequest.setMetadata(objectMetadata);
			
			//putObject command 
			PutObjectResult result = s3.putObject(putRequest);

			if (result == null){
				throw new S3Exception("File " + fileEntry + " was not saved into S3");
			}
			LOG.debug("Deleting file " + fileEntry.getName() + " from local");
		} catch (Throwable t) {
			LOG.error("Error occurred while trying to upload file " + fileEntry.getName(), t);
		}finally{
			fileEntry.delete();
		}
		
		long end = System.currentTimeMillis();
		
		diagnosticsAspect.log("uploadFileFromLocal", new Object[] {s3Map}, "S3-Upload", start, end);
	}
	
}
