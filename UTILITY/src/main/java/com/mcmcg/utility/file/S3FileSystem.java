package com.mcmcg.utility.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.mcmcg.utility.annotation.Diagnostics;
import com.mcmcg.utility.exception.S3Exception;
import com.mcmcg.utility.threadpool.FileSystemWorker;
import com.mcmcg.utility.util.MetaDataUtil;
import com.mcmcg.utility.util.MetaDataUtil.BUCKET_NAMES_ENUM;

/**
 * @author jaleman
 *
 */
public class S3FileSystem extends FileSystem implements ApplicationContextAware {

	private final static Logger LOG = Logger.getLogger(S3FileSystem.class);
	@Value("${sample.document.source.dir}")
	protected String SOURCE_DIR;
	
	private AmazonS3 s3;
	private final Map<BUCKET_NAMES_ENUM, String> bucketNames;
	private final String s3EncryptionKey;

	private AWSCredentials credentials;
	private int numberOfAttemps;
	private String savedHour = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) ;

	private ApplicationContext context;
	
	/**
	 * 
	 * @param s3
	 * @param bucketNames
	 * @param s3EncryptionKey
	 */
	public S3FileSystem(AmazonS3 s3, Map<BUCKET_NAMES_ENUM, String> bucketNames, String s3EncryptionKey) {
		this.s3 = s3;
		this.bucketNames = bucketNames;
		this.s3EncryptionKey = s3EncryptionKey;
	}
	
	public S3FileSystem(Map<BUCKET_NAMES_ENUM, String> bucketNames, String s3EncryptionKey, AWSCredentials credentials, int numberOfAttemps) {
		this.s3 = null;
		this.bucketNames = bucketNames;
		this.s3EncryptionKey = s3EncryptionKey;
		this.credentials = credentials;
		this.numberOfAttemps = numberOfAttemps;
	}

	@Diagnostics(area = "S3-Downloads")
	@Override
	public InputStream getInputStream(String filename, BUCKET_NAMES_ENUM bucketEnum, String bucket) throws S3Exception {

		InputStream stream = super.getInputStream(filename, bucketEnum, bucket);
		if (stream == null) {
			try {
				LOG.debug("fileName ------>" + filename);
				String key = MetaDataUtil.getFilePath(filename, bucketEnum) + filename;
				LOG.debug("key ------>" + key);
				String targetBucket = bucket;
				if (StringUtils.isEmpty(bucket)){
					targetBucket = this.bucketNames.get(bucketEnum);
				}
				LOG.debug("Bucket ------>" + targetBucket);
				S3Object object = getS3Client().getObject(targetBucket, key);
				stream = object.getObjectContent();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				throw new S3Exception(e.getMessage(), e);
			}
		}

		return stream;
	}
	
	@Diagnostics(area = "S3-Read")
	@Override
	public InputStream getInputStream(String key, String bucketName) {
		InputStream stream = super.getInputStream(key, bucketName);
		
		String targetBucket = bucketName;
		try{
			BUCKET_NAMES_ENUM bucketEnum = BUCKET_NAMES_ENUM.valueOf(bucketName.toUpperCase());
			targetBucket = this.bucketNames.get(bucketEnum);
		}catch (Exception e){
			//nothing
		}
		
		if (stream == null) {
			try {
				LOG.debug("key ------>" + key);
				LOG.debug("Bucket ------>" + targetBucket);
				S3Object object = getS3Client().getObject(targetBucket, key);
				stream = object.getObjectContent();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				throw e;
			}
		}
		
		return stream;
	}

	@Override
	public byte[] getImage(String fileName, int pageNumber, BUCKET_NAMES_ENUM bucketEnum, Boolean checkS3) {

		byte[] bytes = super.getImage(fileName, pageNumber, bucketEnum, checkS3);

		if (bytes == null && checkS3) {
			if (fileName != null) {
				try {
					LOG.debug("fileName " + fileName);

					String key = MetaDataUtil.getImagePath(fileName, pageNumber, false, bucketEnum);
					LOG.info(String.format("Getting object with key %s from S3 bucket %s ", key, bucketEnum));

					S3Object object = getS3Client().getObject(this.bucketNames.get(bucketEnum), key);
					LOG.info(String.format("Object retrieved %s ", object.toString()));

					bytes = IOUtils.toByteArray(object.getObjectContent());
					object.getObjectContent().close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}

		return bytes;
	}

	/**
	 * 
	 * @param rootFolder
	 * @param folder
	 * @param fileName
	 */
	@Override
	public void uploadToS3(String rootFolder, String folder, String fileName, String bucket) {
		BUCKET_NAMES_ENUM bucketEnum = BUCKET_NAMES_ENUM.ORIGINAL;
		File fileEntry = new File(rootFolder + folder + fileName);

		try {

			final Map<String, Object> s3Map = new TreeMap<String, Object>();
			s3Map.put("s3", getS3Client());
			s3Map.put("bucketEnum", bucketEnum);
			String targetBucket = bucket;
			if (StringUtils.isEmpty(bucket)){
				targetBucket = this.bucketNames.get(bucketEnum);
			}
			s3Map.put("bucketName", targetBucket);
			s3Map.put("file", fileEntry);
			s3Map.put("path", folder + fileEntry.getName());
			s3Map.put("s3EncryptionKey", s3EncryptionKey);
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					FileSystemWorker s3Worker = getFileSystemWorker();
					s3Worker.uploadFileFromLocal(s3Map);
				}
			});

		} catch (Exception e) {
			LOG.error("Files do not exist", e);
		}
	}
	
	public void uploadToS3(String key, String bucketName, InputStream inputStream)
			throws S3Exception {

		try {
			LOG.debug("Uploading file " + key);

			// Set Metadata, request server-side encryption and File Length
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
			byte[] bytes = IOUtils.toByteArray(inputStream);
			metadata.setContentLength(bytes.length);

			// put Object command
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, byteArrayInputStream, metadata);
			PutObjectResult result = getS3Client().putObject(putObjectRequest);

			if (result == null) {
				String message = String.format("The file with key %s was not uploaded to the S3 bucket %s", key,
						bucketName);
				LOG.error(message);
				throw new S3Exception(message);
			}

		} catch (Throwable t) {
			String message = String.format(
					"An error occurred while trying to upload the file with key %s to the S3 bucket %s -- %s", key,
					bucketName, t.getMessage());
			LOG.error(message, t);
			throw new S3Exception(message, t.getCause());
		}

	}

	public void uploadFilesFromLocal(FileVO fileVO, boolean asynchronous) {
		BUCKET_NAMES_ENUM bucketEnum = BUCKET_NAMES_ENUM.TEMPLATE;
		String filesPath = fileVO.getPath();
		File folder = new File(SOURCE_DIR + filesPath);

		try {
			for (File fileEntry : folder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					boolean accepted = false;
					String extension = FilenameUtils.getExtension(name);
					switch (extension.toLowerCase()) {
					case "pdf":
					case "tif":
					case "tiff":
					case "jpg":
						accepted = true;
						break;
					default:
						accepted = false;
						break;
					}
					return accepted;
				}
			})) {
				final Map<String, Object> s3Map = new TreeMap<String, Object>();
				s3Map.put("s3", getS3Client());
				s3Map.put("bucketEnum", bucketEnum);
				s3Map.put("bucketName", this.bucketNames.get(bucketEnum));
				s3Map.put("file", fileEntry);
				s3Map.put("path", filesPath + fileEntry.getName());
				s3Map.put("s3EncryptionKey", s3EncryptionKey);

				if (asynchronous) {
					taskExecutor.execute(new Runnable() {
						@Override
						public void run() {
							FileSystemWorker s3Worker = getFileSystemWorker();
							s3Worker.uploadFileFromLocal(s3Map);
						}
					});
				} else {
					FileSystemWorker s3Worker = getFileSystemWorker();
					s3Worker.uploadFileFromLocal(s3Map);
				}
			}
		} catch (Exception e) {
			LOG.error("Files do not exist", e);
		}
	}

	public void uploadSnippetsFromLocal(String location) {
		BUCKET_NAMES_ENUM bucketEnum = BUCKET_NAMES_ENUM.EXTRACTION;
		String filesPath = MetaDataUtil.getFilePath(location, bucketEnum);
		File folder = new File(SOURCE_DIR + filesPath);

		try {
			LOG.info("Start Upload Snippets to: " + this.bucketNames.get(bucketEnum));
			for (File fileEntry : folder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					boolean accepted = false;
					if (StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(name), "jpg")) {
						if (!StringUtils.startsWithIgnoreCase(name, "page")) {
							accepted = true;
						}
					}
					return accepted;
				}
			})) {
				final Map<String, Object> s3Map = new TreeMap<String, Object>();
				s3Map.put("s3", getS3Client());
				s3Map.put("bucketEnum", bucketEnum);
				s3Map.put("bucketName", this.bucketNames.get(bucketEnum));
				s3Map.put("file", fileEntry);
				s3Map.put("path", filesPath + fileEntry.getName());
				s3Map.put("s3EncryptionKey", s3EncryptionKey);

				FileSystemWorker s3Worker = getFileSystemWorker();
				
				s3Worker.uploadFileFromLocal(s3Map);
			}
			LOG.info("End Upload Snippets to: " + this.bucketNames.get(bucketEnum));
		} catch (Exception e) {
			LOG.error("Files do not exist", e);
		}
	}

	public void createImageAndUpload(FileVO fileVO, int pageNum, BUCKET_NAMES_ENUM bucketEnum,
			final boolean upload) {

		// s3Map
		final Map<String, Object> s3Map = new TreeMap<String, Object>();
		s3Map.put("fileVO", fileVO);
		s3Map.put("pageNum", pageNum);
		s3Map.put("sourceDir", SOURCE_DIR);
		s3Map.put("bucketEnum", bucketEnum);
		
		// s3MapUpload
		final Map<String, Object> s3MapUpload = new TreeMap<String, Object>();
		s3MapUpload.put("s3", getS3Client());
		s3MapUpload.put("bucketEnum", bucketEnum);
		s3MapUpload.put("bucketName", this.bucketNames.get(bucketEnum));
		File fileEntry = new File(
				MetaDataUtil.getImagePath(SOURCE_DIR + fileVO.getPath(), pageNum, true, bucketEnum));
		s3MapUpload.put("file", fileEntry);
		s3MapUpload.put("path", fileVO.getPath() + fileEntry.getName());
		s3MapUpload.put("s3EncryptionKey", s3EncryptionKey);

		if (pageNum > 0) {		

			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					FileSystemWorker s3Worker = getFileSystemWorker();
					
					s3Worker.createImage(s3Map);

					// Upload to s3 asynchronously
					if (upload) {
						s3Worker.uploadFileFromLocal(s3MapUpload);
					}
				}
			});
		} else {
			FileSystemWorker s3Worker = getFileSystemWorker();
			
			s3Worker.createImage(s3Map);
			
			// Upload to s3 asynchronously
			if (upload) {
				s3Worker.uploadFileFromLocal(s3MapUpload);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private AmazonS3 getS3Client(){
		String currentHour = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (!savedHour.equals(currentHour) || s3 == null){
			if (System.getProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY) == null){
				System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
			}
			savedHour = currentHour;
			ClientConfiguration clientConfi = new ClientConfiguration();
			clientConfi.setMaxErrorRetry(numberOfAttemps);
			if (credentials == null){
				AWSCredentials tempCredentials = new InstanceProfileCredentialsProvider(true).getCredentials();
				LOG.info("Keys--> "  + tempCredentials.getAWSAccessKeyId() + " " + tempCredentials.getAWSSecretKey());
				s3 = new AmazonS3Client(tempCredentials, clientConfi);
			}else{
				s3 = new AmazonS3Client(credentials, clientConfi);
			}
			s3.setRegion(Region.getRegion(Regions.US_EAST_1));
		}
		
		return s3;
	}
	
	private FileSystemWorker getFileSystemWorker(){
		FileSystemWorker s3Worker = (FileSystemWorker)context.getBean("fileSystemWorker");
		
		if (s3Worker == null){
			LOG.info("s3Worker was null");
			s3Worker = new FileSystemWorker();
		}

		return s3Worker;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
		
	}
}
