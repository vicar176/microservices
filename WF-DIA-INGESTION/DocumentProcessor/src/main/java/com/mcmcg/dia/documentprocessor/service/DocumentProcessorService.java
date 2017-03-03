package com.mcmcg.dia.documentprocessor.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mcmcg.dia.documentprocessor.common.MediaUtil;
import com.mcmcg.dia.documentprocessor.exception.MediaServiceException;
import com.mcmcg.dia.documentprocessor.exception.PersistenceException;
import com.mcmcg.dia.documentprocessor.exception.ServiceException;
import com.mcmcg.dia.documentprocessor.media.DocumentExceptionBatchManagerService;
import com.mcmcg.dia.documentprocessor.media.IService;
import com.mcmcg.dia.documentprocessor.media.OnDemandBatchManagerService;
import com.mcmcg.dia.documentprocessor.media.S3UtilityService;
import com.mcmcg.dia.documentprocessor.media.ServiceLocator;
import com.mcmcg.dia.iwfm.domain.BatchCreationOnDemand;
import com.mcmcg.dia.iwfm.domain.BatchProfileJobIdAndDocumentList;
import com.mcmcg.dia.iwfm.domain.FoundAndNotFoundDocumentList;
import com.mcmcg.dia.iwfm.domain.MediaDocument;
import com.mcmcg.dia.iwfm.domain.NewDocumentStatus;
import com.mcmcg.dia.iwfm.domain.Reprocess;
import com.mcmcg.dia.iwfm.domain.Response;

/**
 * 
 * @author wporras
 *
 */
@Service
public class DocumentProcessorService extends BaseService {

	private static final Logger LOG = Logger.getLogger(DocumentProcessorService.class);

	@Value("${aws.s3Bucket.batchFiles}")
	private String s3BucketBatchFiles;

	@Autowired
	private ServiceLocator serviceLocator;

	@Resource
	private Set<String> supportedDocumentIdSet;

	private IService<Boolean> s3UtilityService;
	private IService<Long> onDemandBatchManagerService;
	private IService<Boolean> docExceptionBatchManagerService;

	private String DOCUMENT_STATUS_INVALID_URL = "InvalidURL";
	private String INVALID_FORMAT = "BadFormat";

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		try {
			s3UtilityService = serviceLocator.getService(S3UtilityService.class.getSimpleName());
			onDemandBatchManagerService = serviceLocator.getService(OnDemandBatchManagerService.class.getSimpleName());
			docExceptionBatchManagerService = serviceLocator.getService(DocumentExceptionBatchManagerService.class.getSimpleName());
		} catch (MediaServiceException e) {
			LOG.error(e);
		}
	}

	/**
	 * 
	 * @param batchSize
	 * @param user
	 * @return
	 * @throws MediaServiceException
	 * @throws PersistenceException
	 * @throws ServiceException
	 * @throws IOException 
	 * @throws JMSException 
	 */
	public void processOnDemandBatch(MultipartFile multipartFile, BatchCreationOnDemand batchCreationDomain)
			throws MediaServiceException, PersistenceException, ServiceException, IOException, JMSException {

		LOG.debug(String.format("Start processing On Demand Batch by user (%s)", batchCreationDomain.getUser()));

		List<String> documentIdList = getDocumentIdsTotal(multipartFile);
		batchCreationDomain.setTotalOfDocuments(new Long(documentIdList.size()));
		
		// Persist On Demand Batch
		Long batchProfileJobId = saveOnDemandBatch(batchCreationDomain);

		// Save CSV file to S3
		saveCSVtoS3(multipartFile, batchCreationDomain, batchProfileJobId);
		
		// Add information from Document Images Table
		FoundAndNotFoundDocumentList documentDetailList = addDocumentImagesInfo(documentIdList, batchProfileJobId);
		
		//With the Documents Id Not Found update Document Exceptions
		saveDocumentExceptions(new HashSet<String>(documentDetailList.getNotFoundDocumentIds()), 
											  batchCreationDomain.getUser(), batchProfileJobId);
		
		//Send the rest to On Demand Queue 
		sendDocumentBatchToQueue(documentDetailList.getFoundDocumentIds(), batchCreationDomain.getUser());
		
	}

	/**
	 * 
	 * @param reprocessList
	 * @param user
	 * @throws PersistenceException
	 * @throws ServiceException
	 * @throws JMSException
	 */
	public void reprocessOnDemandBatch(List<Reprocess> reprocessList, String user)
			throws PersistenceException, ServiceException, JMSException {

		LOG.debug(String.format("Start reprocessing On Demand Batch by user (%s)", user));

		// Add information from Document Images Table
		List<MediaDocument> foundDocumentList = addDocumentImagesInfo(reprocessList);

		// Send Found documents to On Demand Queue
		sendDocumentBatchToQueue(foundDocumentList, user);
	}


	

	/*********************************************************************************************************
	 * PRIVATE METHODS
	 *********************************************************************************************************/
	
	/**
	 * 
	 * @param documentIdList
	 * @param batchProfileJobId
	 * @return
	 * @throws PersistenceException
	 * @throws ServiceException
	 */
	private FoundAndNotFoundDocumentList addDocumentImagesInfo(List<String> documentIdList,
			Long batchProfileJobId) throws PersistenceException, ServiceException {

		return retrieveDocumentsByIds(new HashSet<String>(documentIdList), batchProfileJobId);

	}
	
	/**
	 * 
	 * @param reprocessList
	 * @return List<MediaDocument>
	 * @throws PersistenceException
	 * @throws ServiceException
	 */
	private List<MediaDocument> addDocumentImagesInfo(List<Reprocess> reprocessList)
			throws PersistenceException, ServiceException {
		
		Set<String> documentIds = reprocessList.stream().map(Reprocess::getDocumentId).collect(Collectors.toSet());
				
		List<Map<String,Object>> resultset = documentImagesDAO.findDocumentsByIds(documentIds);	
		
		//Create Found List
		List<MediaDocument> foundDocumentList = buildFoundDocumentIdList(resultset, reprocessList);
		
		return foundDocumentList;

	}
	
	/**
	 * 
	 * @param in
	 * @return
	 * @throws ServiceException
	 */
	private List<String> getDocumentIdsTotal(MultipartFile multipartFile) throws ServiceException{
		List<String> documentIdList = new ArrayList<String>();
		try {
			
			LOG.debug("Name --->  " + multipartFile.getOriginalFilename());
			if (!StringUtils.endsWith(multipartFile.getOriginalFilename(), "csv")){
				throw new ServiceException("File is not supported");
			}
			
			LOG.debug("Content Type --->  " + multipartFile.getContentType());
			if (!(StringUtils.contains(multipartFile.getContentType(), "text") || 
				StringUtils.contains(multipartFile.getContentType(), "excel"))	){
				throw new ServiceException("Content Type is not supported");
			}

			
			String content = IOUtils.toString(multipartFile.getInputStream(), "UTF-8");
			
			if (!StringUtils.contains(content, ",")){
				throw new ServiceException("File Content is expecting document Ids separated by comma");
			}
						
			String documentIds [] = StringUtils.split(content, ",");
			
			if (documentIds.length > 0){
				
				documentIdList.addAll(Arrays.asList(documentIds));
				
			}else {
				String message = "File is empty " ; 
				LOG.warn(message);
				throw new ServiceException(message);
			}
			
		} catch (IOException e) {
			String message = "Unable to parse file content due to: " + e.getMessage(); 
			LOG.warn(message, e);
			throw new ServiceException(message, e);
		}
		
		return documentIdList;
		
	}
	
	/**
	 * 
	 * @param batchCreationDomain
	 * @return
	 * @throws ServiceException
	 * @throws MediaServiceException
	 */
	private void saveDocumentExceptions(Set<String> documentIds, String user, long batchProfileJobId)
											throws ServiceException, MediaServiceException {

		if (documentIds == null || documentIds.isEmpty()) {
			return;
		}
		BatchProfileJobIdAndDocumentList container = new BatchProfileJobIdAndDocumentList();
		
		container.setBatchProfileJobId((int)batchProfileJobId);
		container.setDocuments(documentIds);
		container.setUser(user);
		
		documentIds.forEach(documentId -> LOG.debug("Document Exception --> " + documentId));
		
		String resource = DocumentExceptionBatchManagerService.POST_DOCUMENT_EXCEPTIONS;

		Response<Boolean> response = docExceptionBatchManagerService.execute(resource, IService.POST, container);

		if (response.getData() == null ) {
			String messageError = response.getError().getMessage();
			LOG.error(messageError);
			throw new ServiceException(messageError);
		}

	}

	/**
	 * 
	 * @param batchCreationDomain
	 * @return
	 * @throws ServiceException
	 * @throws MediaServiceException
	 */
	private Long saveOnDemandBatch(BatchCreationOnDemand batchCreationDomain)
			throws ServiceException, MediaServiceException {

		String resource = OnDemandBatchManagerService.PUT_BATCH_PROFILE_ON_DEMAND;

		Response<Long> response = onDemandBatchManagerService.execute(resource, IService.PUT, batchCreationDomain);

		if (response.getData() != null && response.getData() < 1) {
			String messageError = response.getError().getMessage();
			LOG.error(messageError);
			throw new ServiceException(messageError);
		}

		return response.getData();

	}
	
	private void saveCSVtoS3(MultipartFile multipartFile, BatchCreationOnDemand batchCreationDomain, Long batcProfileId)
			throws ServiceException, MediaServiceException {

		String key = String.format("%s/%s_%s_%s", batchCreationDomain.getAction().getDescription(), batcProfileId,
				batchCreationDomain.getName(), batchCreationDomain.getCsvFileName());

		String resource = String.format(S3UtilityService.POST_FILE_BY_KEY, s3BucketBatchFiles, key);

		Response<Boolean> response = s3UtilityService.execute(resource, IService.POST_FILE, multipartFile);

		if (response.getData() != null && !response.getData()) {
			String messageError = response.getError().getMessage();
			LOG.error(messageError);
			throw new ServiceException(messageError);
		}

	}

	/**
	 * 
	 * @param documentBatchList
	 * @param user
	 * @return Integer realBatchSize = -1;
	 * @throws MediaServiceException
	 * @throws ServiceException
	 * @throws JMSException 
	 */
	private void sendDocumentBatchToQueue(List<MediaDocument> documentBatchList, String user)
			throws ServiceException, JMSException {
		
		for (MediaDocument documentBatch : documentBatchList) {

			if (StringUtils.isNotBlank(documentBatch.getFilename())) {
				
				String documentId = documentBatch.getFilename().substring(0, documentBatch.getFilename().indexOf("="));
				
				if (supportedDocumentIdSet.parallelStream().anyMatch(documentId.toUpperCase().trim()::endsWith)) {
					sendMessageToQueue(documentBatch, getOnDemandMessageProducer());
				} else {
					throwInvalidFormatException(user, documentBatch, INVALID_FORMAT);
				}
				
			} else {
				throwInvalidFormatException(user, documentBatch, DOCUMENT_STATUS_INVALID_URL);
			}
		}
	}

	/**
	 * @param user
	 * @param documentBatch
	 */
	protected void throwInvalidFormatException(String user, MediaDocument documentBatch, String status) {
		LOG.info("Discarding -- > " + documentBatch.getFilename());
		
		documentImagesDAO.update(String.valueOf(documentBatch.getDocumentId()), new NewDocumentStatus(status, user));
	}

	/**
	 * Send a TextMessage to SQS
	 * 
	 * @param message
	 * @throws ServiceException
	 */
	private void sendMessageToQueue(MediaDocument mediaDocument, MessageProducer messageProducer)
			throws ServiceException {

		TextMessage objMessage = null;

		try {
			String message = MediaUtil.getJsonObject(mediaDocument);
			LOG.debug("TO queue " + message);
			objMessage = getSqsSession().createTextMessage(message);
			messageProducer.send(objMessage);
			
		} catch (JMSException e) {
			try {
				waitTime();
				objMessage = getSqsSession().createTextMessage(MediaUtil.getJsonObject(mediaDocument));
				messageProducer.send(objMessage);

			} catch (Throwable t) {
				String messageError = null;
				try {
					messageError = String.format("Unable to send the message (%s) to the Queue %s",
							mediaDocument.toString(), messageProducer.getDestination().toString());
				} catch (JMSException e1) {
					LOG.error("Log Error: " + e.getMessage(), e);
				}
				LOG.error(messageError, e);
				throw new ServiceException(messageError + " --> " + t.getMessage(), t);
			}
		}
		try {
			LOG.debug(String.format("Messege sent (%s) to the Ingestion Queue (%s) ", mediaDocument.toString(),
					messageProducer.getDestination().toString()));
		} catch (JMSException e) {
			LOG.error("Log Error: " + e.getMessage(), e);
		}
	}

}
