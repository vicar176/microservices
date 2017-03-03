package com.mcmcg.utility.file;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import com.mcmcg.utility.exception.S3Exception;
import com.mcmcg.utility.util.MetaDataUtil.BUCKET_NAMES_ENUM;

/**
 * @author Jose Aleman
 *
 */
public interface IFileSystem {

	boolean createFile(FileVO fileVO);

	boolean createDirectory(String dirName);

	boolean createImage(BufferedImage image, int pageNum, String imagePath, BUCKET_NAMES_ENUM bucketEnum);

	void createImageAndUpload(FileVO fileVO, int pageNum, BUCKET_NAMES_ENUM bucketEnum, boolean upload);

	boolean createSnippet(BufferedImage image, String location, String imageName);

	InputStream getInputStream(String filename, BUCKET_NAMES_ENUM bucketEnum, String bucket) throws S3Exception;
	
	InputStream getInputStream(String filename, String bucketEnum);

	byte[] getImage(String fileName, int pageNumber, BUCKET_NAMES_ENUM bucketEnum, Boolean checkS3);

	byte[] getImage(String fileName, int pageNumber, BUCKET_NAMES_ENUM bucketEnum, int timeOut);

	void deleteDirectory(String dirName);

	void uploadFilesFromLocal(FileVO fileVO, boolean b);
	
	void uploadToS3(String rootFolder, String folder, String fileName, String bucket);
	
	void uploadToS3(String key, String bucketName, InputStream inputStream) throws S3Exception;
	
	void uploadSnippetsFromLocal(String location);
	
}
