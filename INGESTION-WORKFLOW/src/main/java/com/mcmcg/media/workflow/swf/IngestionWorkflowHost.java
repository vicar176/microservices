package com.mcmcg.media.workflow.swf;

import java.util.Calendar;
import java.util.Random;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.mcmcg.media.workflow.configuration.MediaWorkflowConfiguration;
import com.mcmcg.media.workflow.configuration.sqs.SQSConsumerConfiguration;
import com.mcmcg.media.workflow.service.BaseService.Methods;
import com.mcmcg.media.workflow.service.domain.MediaDocument;
import com.mcmcg.media.workflow.service.domain.MediaMetadataModel;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.domain.WorkflowShutdownStateModel;
import com.mcmcg.media.workflow.service.exception.MediaServiceException;
import com.mcmcg.media.workflow.service.ingestionstate.WorkflowShutdownStateService;
import com.mcmcg.media.workflow.service.media.MediaMetadataService;
import com.mcmcg.media.workflow.util.WorkflowUtil;

/**
 * This is the process which hosts all Activities in this sample
 */
public class IngestionWorkflowHost {

	private final static Logger LOG = Logger.getLogger(IngestionWorkflowHost.class);

    private static final String SQS_ENDPOINT = "sqsServiceUrl";
    private static final String QUEUE = "queue";
    private static final String ON_DEMAND_QUEUE = "onDemandQueue";

	public static final int ACTIVITIES_POOL_SIZE = 1;
	public static final int RECEIVE_MESSAGE_TIMEOUT = 1000;
	

	private static MessageConsumer messageConsumer;
	private static MessageConsumer onDemandMessageConsumer;
	private static SQSConnection connection;
	public static AmazonSQSMessagingClientWrapper sqsClient;
	private static String savedHour = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) ;
	private static Object lock = new Object();
	public static ThreadPoolTaskExecutor taskExecutor = null;
	public static int maxPoolSize = 8;
	public static int retryAttempts = 3;


	public static void main(String[] args) throws Exception {

		AnnotationConfigApplicationContext ctx = null;

		try {
			ctx = initContext();
			
            tryToReconnect(ctx);
            
			getMessageAndStartWF(ctx);

		} catch (Throwable exception) {
			LOG.error(exception.getMessage(), exception);

		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}

		System.exit(0);
	}

	/**
	 * @param ctx
	 * @param swfService
	 * @param domain
	 * @throws JMSException
	 */
	private static void getMessageAndStartWF(AnnotationConfigApplicationContext ctx)
			throws JMSException {

        String documentAsString = StringUtils.EMPTY;

		do {	
			tryToReconnect(ctx);
			
			checkForWFState(ctx);
			
			documentAsString = pollMessagesFromQueue(ctx, onDemandMessageConsumer);

			documentAsString = pollMessagesFromQueue(ctx, messageConsumer);

		} while (!StringUtils.equalsIgnoreCase(documentAsString, "exit"));
	}

	
	/**
	 * 
	 * @param ctx
	 * @param messageConsumer
	 */
	private static String pollMessagesFromQueue(AnnotationConfigApplicationContext ctx, MessageConsumer messageConsumer) throws JMSException{
		waitForAvailableWorkers();
		
		Message message = messageConsumer.receive(RECEIVE_MESSAGE_TIMEOUT);  
		String documentAsString = StringUtils.EMPTY;
		
		if (message != null) {
		
			message.acknowledge();
			documentAsString =  ((SQSTextMessage)message).getText();
			
			if (documentAsString != null){
				
				ingestDocument(ctx, documentAsString, ((SQSTextMessage)message).getReceiptHandle());
				
			}
			
		}
		
		return documentAsString;
	}
	
	/**
	 * @param domain
	 * @param documentAsString
	 */
	private static void ingestDocument(ApplicationContext context, String documentAsString, String messageId) {
		LOG.info("Document Received: " + documentAsString);

		IngestionWorkflow ingestionWorkflow = new IngestionWorkflow(documentAsString, messageId, context);
		
		taskExecutor.execute(ingestionWorkflow);

	}
	
	/**
	 * @param ctx
	 * @param isWFShutdown
	 * @return
	 */
	private static void checkForWFState(AnnotationConfigApplicationContext ctx) {
        boolean isWFShutdown = isWorkflowShutdown(ctx);

		if (isWFShutdown){
			do {
				 waitForNextRound();
				 isWFShutdown = isWorkflowShutdown(ctx);
			}while(isWFShutdown);
			
		}
	}
	
	private static void waitForAvailableWorkers() {		
		
		LOG.debug("Thread logs - Actives " + taskExecutor.getActiveCount());
		
		while (taskExecutor.getActiveCount() >= maxPoolSize){
			try {
				LOG.debug("Thread logs - sleep");
				Thread.sleep(1000);
			 } catch (InterruptedException e) {
			     //nothing 
			 }
		}
	}


	/**
	 * 
	 * @param ctx
	 * @return
	 */
	private static boolean isWorkflowShutdown(AnnotationConfigApplicationContext ctx) {
		boolean isWorkflowShutdown = false;
		WorkflowShutdownStateService workflowShutdownStateService = (WorkflowShutdownStateService) ctx.getBean("workflowShutdownStateService");
		
		if (workflowShutdownStateService != null){
			String resource = WorkflowShutdownStateService.GET_WORKFLOW_SHUTDOWN_STATE;
			try {
				Response<WorkflowShutdownStateModel> response = workflowShutdownStateService.execute(resource, Methods.GET.toString());
				if (response.getData() != null && response.getData().isShutdownState()){
					isWorkflowShutdown = true;
				}
			} catch (MediaServiceException e) {
				LOG.warn(e);
			}
		}
		return isWorkflowShutdown;
	}
	
	
	private static MessageConsumer createMessageConsumer(SQSConnection connection, String queueName, 
			AnnotationConfigApplicationContext ctx) throws JMSException {

		Session session = connection.createSession(false, SQSSession.AUTO_ACKNOWLEDGE);

		// Create the producer and consumer
		MessageConsumer messageConsumer = session.createConsumer(session.createQueue((String) ctx.getBean(queueName)));
		LOG.info((String) ctx.getBean(QUEUE));
		connection.start();

		return messageConsumer;
	}

	private static SQSConnection createConnection(AnnotationConfigApplicationContext ctx) throws JMSException {

		AWSCredentialsProvider awsCredentialsProvider = (AWSCredentialsProvider) ctx
				.getBean("customAWSCredentialsProvider");

		if (awsCredentialsProvider.getCredentials() == null) {
			awsCredentialsProvider = new InstanceProfileCredentialsProvider(true);
		}

		SQSConnectionFactory connectionFactory = 
				SQSConnectionFactory
				.builder()
				.withEndpoint((String) ctx.getBean(SQS_ENDPOINT))
				.withAWSCredentialsProvider(awsCredentialsProvider)
				.build();
		SQSConnection connection = connectionFactory.createConnection();

		return connection;

	}

	public static void tryToReconnect(AnnotationConfigApplicationContext ctx) throws JMSException {
		String currentHour = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) ;
		
		if (!savedHour.equals(currentHour) || messageConsumer == null){
			synchronized(lock){
				if (messageConsumer == null){
					savedHour = currentHour;
					connection = createConnection(ctx);
					sqsClient = connection.getWrappedAmazonSQSClient();
					messageConsumer = createMessageConsumer(connection, QUEUE, ctx);	
					onDemandMessageConsumer = createMessageConsumer(connection, ON_DEMAND_QUEUE, ctx);	
				}
			}
		}
	}

	private static void waitForNextRound() {
		try {
			 Random random = new Random();
			 long waitTime = (random.nextInt(10) + 1) * 1000;
			 LOG.debug("Waiting time ---> " + waitTime / 1000);
		     Thread.sleep(waitTime);
		 } catch (InterruptedException e) {
		     //nothing 
		 }
	}
	
	private static AnnotationConfigApplicationContext initContext() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(SQSConsumerConfiguration.class, MediaWorkflowConfiguration.class);
		ctx.scan("com.mcmcg.media");
		ctx.refresh();
		
		taskExecutor = (ThreadPoolTaskExecutor)ctx.getBean("taskExecutor");
		
		return ctx;
	}

}
