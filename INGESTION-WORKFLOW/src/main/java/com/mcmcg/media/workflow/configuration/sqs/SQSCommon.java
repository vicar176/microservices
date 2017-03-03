/**
 * 
 */
package com.mcmcg.media.workflow.configuration.sqs;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.Base64;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jaleman
 *
 */
public class SQSCommon {
    /**
     * A utility function to check the queue exists and create it if needed. For most  
     * use cases this will usually be done by an administrator before the application
     * is run. 
     */
    public static void ensureQueueExists(SQSConnection connection, String queueName) throws JMSException {
        AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
        
        /**
         * For most cases this could be done with just a createQueue call, but GetQueueUrl 
         * (called by queueExists) is a faster operation for the common case where the queue 
         * already exists. Also many users and roles have permission to call GetQueueUrl
         * but do not have permission to call CreateQueue.
         */
        if( !client.queueExists(queueName) ) {
            client.createQueue( queueName );
        }
    }

    public static void handleMessage(Message message) throws JMSException {
        System.out.println( "Got message " + message.getMessageId() );
        System.out.println( "Content: ");
        if( message instanceof TextMessage ) {
            TextMessage txtMessage = ( TextMessage ) message;
            System.out.println( "\t" + txtMessage.getText() );
        } else if( message instanceof BytesMessage ){
            BytesMessage byteMessage = ( BytesMessage ) message;
            // Assume the length fits in an int - SQS only supports sizes up to 256k so that
            // should be true
            byte[] bytes = new byte[(int)byteMessage.getBodyLength()];
            byteMessage.readBytes(bytes);
            System.out.println( "\t" +  Base64.encodeAsString( bytes ) );
        } else if( message instanceof ObjectMessage ) {
            ObjectMessage objMessage = (ObjectMessage) message;
            System.out.println( "\t" + objMessage.getObject() );
        }
    }
    
	/**
	 * 
	 * @param object
	 * @return
	 */
	public static String getJsonObject(Object object){
		
		ObjectMapper mapper = new ObjectMapper();
		String value = new String();
		try {
			value = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			// 
		}
		
		return value;
	}
}
