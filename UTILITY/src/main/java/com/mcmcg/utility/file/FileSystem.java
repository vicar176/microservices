package com.mcmcg.utility.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.mcmcg.utility.exception.S3Exception;
import com.mcmcg.utility.threadpool.FileSystemWorker;
import com.mcmcg.utility.util.MetaDataUtil;
import com.mcmcg.utility.util.MetaDataUtil.BUCKET_NAMES_ENUM;

/**
 * @author jaleman
 *
 */
public class FileSystem implements IFileSystem {

	private static final Logger LOG = Logger.getLogger(FileSystem.class);

	@Value("${sample.document.source.dir}")
	protected String SOURCE_DIR;

	@Autowired
	protected ThreadPoolTaskExecutor taskExecutor;

	/**
	 * 
	 */
	public FileSystem() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mcmcg.profile.file.IFileSystem#createFile(java.io.File)
	 */

	public boolean createFile(FileVO fileVO) {

		final Map<String, Object> s3Map = new TreeMap<String, Object>();
		s3Map.put("fileVO", fileVO);
		s3Map.put("sourceDir", SOURCE_DIR);

		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				FileSystemWorker s3Worker = new FileSystemWorker();
				s3Worker.createFile(s3Map);
			}
		});

		return true;
	}

	public void createImageAndUpload(FileVO fileVO, int pageNum, BUCKET_NAMES_ENUM bucketEnum, boolean upload) {

		final Map<String, Object> s3Map = new TreeMap<String, Object>();

		s3Map.put("fileVO", fileVO);
		s3Map.put("pageNum", pageNum);
		s3Map.put("sourceDir", SOURCE_DIR);
		s3Map.put("bucketEnum", bucketEnum);

		if (pageNum > 0) {
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					FileSystemWorker s3Worker = new FileSystemWorker();
					s3Worker.createImage(s3Map);
				}
			});
		} else {
			FileSystemWorker s3Worker = new FileSystemWorker();
			s3Worker.createImage(s3Map);
		}

	}

	public boolean createImage(BufferedImage image, int pageNum, String imagePath, BUCKET_NAMES_ENUM bucketEnum) {

		File imageFile = new File(MetaDataUtil.getImagePath(SOURCE_DIR + imagePath, pageNum, true, bucketEnum));
		boolean result = false;
		try {
			result = ImageIO.write(image, "jpg", imageFile);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	public boolean createDirectory(String dirName) {
		File fileDir = new File(dirName);
		return fileDir.mkdirs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mcmcg.profile.file.IFileSystem#getFile(java.lang.String,
	 * java.lang.String)
	 */

	public InputStream getInputStream(String filename, BUCKET_NAMES_ENUM bucketEnum, String bucket) throws S3Exception {
		InputStream stream = null;
		try {
			File file = new File(SOURCE_DIR + MetaDataUtil.getFilePath(filename, bucketEnum) + filename);
			stream = new FileInputStream(file);
		} catch (Throwable e) {
			LOG.debug(e.getMessage(), e);
		}
		return stream;
	}
	
	/**
	 * 
	 */
	public InputStream getInputStream(String filename, String bucketName) {
		return null;
	}

	public byte[] getImage(String fileName, int pageNumber, BUCKET_NAMES_ENUM bucketEnum, Boolean checkS3) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		byte[] bytes = null;
		if (fileName != null) {
			try {
				File file = new File(SOURCE_DIR + MetaDataUtil.getImagePath(fileName, pageNumber, false, bucketEnum));
				BufferedImage img = ImageIO.read(file);
				ImageIO.write(img, "jpg", bao);
				bytes = bao.toByteArray();
			} catch (IOException e) {
				LOG.debug(e.getMessage(), e);
			} finally {
				try {
					bao.close();
				} catch (IOException e) {
					LOG.warn(e.getMessage(), e);
				}
			}
		}
		return bytes;
	}

	public byte[] getImage(String fileName, int pageNumber, BUCKET_NAMES_ENUM bucketEnum, int timeOut) {

		byte[] bytes = null;
		int timeOutMilliseconds = 250;
		int attemptNumber = 0;

		do {
			bytes = this.getImage(fileName, pageNumber, bucketEnum, false);
			if (bytes == null) {
				timeOut -= timeOutMilliseconds;
				attemptNumber += 1;
				try {
					LOG.info(String.format(
							"Retriable error detected while reading the file %s, will retry in %sms, attempt number: %s",
							fileName, timeOutMilliseconds, attemptNumber));
					TimeUnit.MILLISECONDS.sleep(timeOutMilliseconds);
				} catch (InterruptedException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		} while (timeOut > 0 && bytes == null);

		return bytes;
	}

	public boolean createSnippet(BufferedImage image, String location, String imageName) {

		String filePath = MetaDataUtil.getFilePath(location, BUCKET_NAMES_ENUM.EXTRACTION) + imageName + ".jpg";
		File imageFile = new File(SOURCE_DIR + filePath);
		boolean result = false;
		try {
			LOG.info("Create snippet: " + filePath);
			result = ImageIO.write(image, "jpg", imageFile);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public void deleteDirectory(String dirName) {
		LOG.info("Delete directory: " + dirName);
		File fileDir = new File(dirName);
		try {
			FileUtils.deleteDirectory(fileDir);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void uploadFilesFromLocal(FileVO fileVO, boolean b) {
	}


	@Override
	public void uploadToS3(String rootFolder, String folder, String fileName, String bucket) {
		
	}

	@Override
	public void uploadSnippetsFromLocal(String location) {

	}

	@Override
	public void uploadToS3(String key, String bucketName, InputStream inputStream) throws S3Exception {
		// Disabled for local File System
	}
}
