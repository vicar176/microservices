package com.mcmcg.dia.documentprocessor.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.mcmcg.dia.documentprocessor.common.MediaUtil;
import com.mcmcg.dia.documentprocessor.configuration.CustomAWSCredentialsProvider;
import com.mcmcg.dia.documentprocessor.dao.DocumentImagesDAO;
import com.mcmcg.dia.documentprocessor.entity.DocumentBatch;
import com.mcmcg.dia.documentprocessor.exception.PersistenceException;
import com.mcmcg.dia.documentprocessor.exception.ServiceException;
import com.mcmcg.dia.iwfm.domain.FoundAndNotFoundDocumentList;
import com.mcmcg.dia.iwfm.domain.MediaDocument;
import com.mcmcg.dia.iwfm.domain.Reprocess;

/**
 * 
 * @author wporras
 *
 */
public abstract class BaseService {
	
	private static final Logger LOG = Logger.getLogger(BaseService.class);

	@Value("${aws.sqs.service.url}")
	private String sqsServiceUrl;

	@Value("${aws.sqs.name}")
	public String queueName;
	
	@Value("${aws.ondemand.sqs.name}")
	public String onDemandQueueName;
	
	
	@Value("${aws.original.s3Bucket}")
	private String originalS3Bucket;

	@Autowired
	protected DocumentImagesDAO documentImagesDAO;

	@Autowired
	private Environment env;

	private MessageProducer messageProducer;
	private MessageProducer onDemandMessageProducer;
	private Session session;
	private String savedHour = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-"
			+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

	public BaseService() {
	}

	public Session getSqsSession() throws JMSException {
		tryToReconnect();
		return session;
	}

	public MessageProducer getMessageProducer() throws JMSException {
		tryToReconnect();
		return messageProducer;
	}
	
	public MessageProducer getOnDemandMessageProducer() throws JMSException {
		tryToReconnect();
		return onDemandMessageProducer;
	}

	public void waitTime(){
		 Random random = new Random();
		 long waitTime = 3000 + (random.nextInt(10) + 1) * 1000;
		 LOG.debug("Waiting time ---> " + waitTime / 1000);
	     try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			//nothing to do
		}
	}

	/*********************************************************************************************************
	 * 
	 * PRIVATE METHODS
	 * 
	 *********************************************************************************************************/

	/**
	 * Create a Message Producer for the specified connection
	 * 
	 * @param connection
	 * @return MessageProducer
	 * @throws JMSException
	 */
	private MessageProducer createMessageProducer(SQSConnection connection, String queueName) throws JMSException {

		MessageProducer messageProducer = session.createProducer(session.createQueue(queueName));
		connection.start();

		return messageProducer;
	}
	
	

	/**
	 * 
	 * @param connection
	 * @return
	 * @throws JMSException
	 */
	private Session createSqsSession(SQSConnection connection) throws JMSException {
		return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	private AWSCredentialsProvider getCustomAWSCredentialsProvider() {

		try {
			return new CustomAWSCredentialsProvider(env.getProperty("aws.AccessKeyId"),
					env.getProperty("aws.SecretKey"));
		} catch (IllegalArgumentException e) {
			return new CustomAWSCredentialsProvider();
		}
	}

	private SQSConnection createSqsConnection() throws JMSException {

		AWSCredentialsProvider awsCredentialsProvider = getCustomAWSCredentialsProvider();

		if (awsCredentialsProvider.getCredentials() == null) {
			awsCredentialsProvider = new InstanceProfileCredentialsProvider(true);
		}

		SQSConnectionFactory connectionFactory = SQSConnectionFactory.builder().withEndpoint(sqsServiceUrl)
				.withAWSCredentialsProvider(awsCredentialsProvider).build();
		SQSConnection connection = connectionFactory.createConnection();

		return connection;

	}

	private void tryToReconnect() throws JMSException {

		String currentHour = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-"
				+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

		if (!savedHour.equals(currentHour) || (session == null || messageProducer == null)) {
			savedHour = currentHour;
			SQSConnection connection = createSqsConnection();
			session = createSqsSession(connection);
			messageProducer = createMessageProducer(connection, queueName);
			onDemandMessageProducer = createMessageProducer(connection, onDemandQueueName);
		}
	}

	/**
	 * 
	 * @param filenameUrl
	 * @return
	 */
	protected Map<String, String> buildFilenameTokens(DocumentBatch documentBatch){
		// 2016/12/docid=43877169741_account=8568634804_DocType=stmt_DocDate=05-17-2014.pdf|s3bucket=
		
		String tokens[] = StringUtils.split(documentBatch.getAwsS3Url(), "/=_");
		Map<String, String> filenameTokensMap = new HashMap<String, String>();
		if (tokens.length > 1){
			if (StringUtils.isNotBlank(documentBatch.getS3Folder())){
				filenameTokensMap.put("folder", buildFolder(documentBatch));
			}
			filenameTokensMap.put("docid",    documentBatch.getDocumentId());
			filenameTokensMap.put("account",  tokens[3]);
			filenameTokensMap.put("docType",  documentBatch.getDocumentType());
			filenameTokensMap.put("docDate", tokens[7]);
		}
		
		return filenameTokensMap;
	}
	
	
	/**
	 * 
	 * 
	 * @param documentBatch
	 */
	protected MediaDocument buildMediaDocument(DocumentBatch documentBatch, Map<String, String> filenameMap, Long batchProfileJobId){
		MediaDocument mediaDocument = new MediaDocument();
		
		mediaDocument.setDocumentId(Long.parseLong(documentBatch.getDocumentId()));
		
		String bucketName = documentBatch.getS3Bucket();
		
		if (StringUtils.isBlank(bucketName)){
			bucketName =  originalS3Bucket;
		}
		
		mediaDocument.setBucket(bucketName);
		mediaDocument.setFolder(buildFolder(documentBatch));
		mediaDocument.setDocumentType(documentBatch.getDocumentType());
		mediaDocument.setAccountNumber(Long.parseLong(filenameMap.get("account")));
		mediaDocument.setFilename(mediaDocument.getFolder() + documentBatch.getAwsS3Url());
		mediaDocument.setDocumentDate(MediaUtil.formatDateShort(new Date()));
		mediaDocument.setBatchProfileJobId(batchProfileJobId.intValue());
		return mediaDocument;
	}
	/**
	 * 
	 * @param documentBatch
	 * @return
	 */
	protected String buildFolder(DocumentBatch documentBatch){
		StringBuilder folder = new StringBuilder();
		
		if (StringUtils.isNotBlank(documentBatch.getS3Folder())) {
			folder.append(documentBatch.getS3Folder())
				  .append("/");
		} 
		
		return folder.toString();
	}
	
	
	/**
	 * 
	 * @param documentIds
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	@SuppressWarnings("unchecked")
	protected FoundAndNotFoundDocumentList retrieveDocumentsByIds(Set<String> documentIds, Long batchProfileJobId) throws ServiceException, PersistenceException {
		
		List<Map<String,Object>> resultset = documentImagesDAO.findDocumentsByIds(documentIds);

		List<String> foundDocumentIdList = new ArrayList<String>();
		
		//Create Found List
		List<MediaDocument> foundDocumentList = buildFoundDocumentIdList(resultset, foundDocumentIdList, batchProfileJobId);
		
		//Create Not Found List
		Collection<String> notFoundDocumentIdList = CollectionUtils.subtract(documentIds, foundDocumentIdList);
		
		FoundAndNotFoundDocumentList finalDocumentList = populateFinalDocumentList(foundDocumentList, notFoundDocumentIdList);
		
		return finalDocumentList;
	}

	/**
	 * @param foundDocumentList
	 * @param notFoundDocumentIdList
	 * @return
	 */
	protected FoundAndNotFoundDocumentList populateFinalDocumentList(List<MediaDocument> foundDocumentList, Collection<String> notFoundDocumentIdList) {
		FoundAndNotFoundDocumentList finalDocumentList = new FoundAndNotFoundDocumentList();
		
		finalDocumentList.setFoundDocumentIds(foundDocumentList);
		finalDocumentList.setNotFoundDocumentIds(new ArrayList<String>(notFoundDocumentIdList));
		return finalDocumentList;
	}

	/**
	 * 
	 * 
	 * @param resultset
	 * @param foundDocumentIdList
	 * @return
	 */
	protected List<MediaDocument> buildFoundDocumentIdList(List<Map<String, Object>> resultset,	List<String> foundDocumentIdList, Long batchProfileJobId) {
		
		List<MediaDocument> foundDocumentList = new ArrayList<MediaDocument>();
		
		if (resultset != null && !resultset.isEmpty()){
			
			for (Map<String,Object> fields : resultset){

				foundDocumentIdList.add(fields.get("Document_Id").toString());
				
				DocumentBatch documentBatch = buildDocumentBatchFromMap(fields);
				Map<String, String> documentMap = buildFilenameTokens(documentBatch);
				MediaDocument mediaDocument = buildMediaDocument(documentBatch, documentMap, batchProfileJobId);
				
				foundDocumentList.add(mediaDocument);
				
			}
		}
		
		return foundDocumentList;
	}
	
	
	/**
	 * 
	 * @param resultset
	 * @param reprocessList
	 * @return List<MediaDocument>
	 */
	protected List<MediaDocument> buildFoundDocumentIdList(List<Map<String, Object>> resultset,
			List<Reprocess> reprocessList) {

		List<MediaDocument> foundDocumentList = new ArrayList<MediaDocument>();

		if (resultset != null && !resultset.isEmpty()) {

			for (Map<String, Object> resultRow : resultset) {

				DocumentBatch documentBatch = buildDocumentBatchFromMap(resultRow);
				Map<String, String> documentMap = buildFilenameTokens(documentBatch);

				Reprocess reprocess = reprocessList.stream().filter(o -> o.getDocumentId().equals(documentBatch.getDocumentId())).findFirst().get();

				MediaDocument mediaDocument = buildMediaDocument(documentBatch, documentMap, reprocess.getBatchProfileJobId());

				foundDocumentList.add(mediaDocument);

			}
		}

		return foundDocumentList;
	}
	
	/**
	 * 
	 * @param fields
	 * @return
	 */
	private DocumentBatch buildDocumentBatchFromMap(Map<String,Object> fields){
		//Document_Id, Aws_S3_Url, S3_Bucket, S3_Folder, Document_Type
		DocumentBatch document = new DocumentBatch();
		
		document.setAwsS3Url(fields.get("Aws_S3_Url").toString());
		document.setDocumentId(fields.get("Document_Id").toString());
		document.setDocumentType(fields.get("Document_Type").toString());
		document.setS3Bucket(fields.get("S3_Bucket").toString());
		document.setS3Folder(fields.get("S3_Folder").toString());
		
		return document;
	}
}
