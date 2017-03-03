/**
 * 
 */
package com.mcmcg.media.workflow.configuration;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.mcmcg.media.workflow.common.DocumentStatusCode;
import com.mcmcg.media.workflow.common.WorkflowStateCode;

/**
 * @author jaleman
 *
 */
@Configuration
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MediaWorkflowConfiguration {

	private final static Logger LOG = Logger.getLogger(MediaWorkflowConfiguration.class);
	/**
	 * 
	 */
	public MediaWorkflowConfiguration() {
		
	}

	@Bean
	public Map<WorkflowStateCode, Integer> workflowStateCodeMap(){
		
		 Map<WorkflowStateCode, Integer> workflowStateCodeMap = new HashMap<WorkflowStateCode, Integer>();
		 
		 workflowStateCodeMap.put(WorkflowStateCode.NEW, 1 );
		 workflowStateCodeMap.put(WorkflowStateCode.UPDATE_WORKFLOW_STATE, 100 );
		 workflowStateCodeMap.put(WorkflowStateCode.EXTRACTION, 200);
		 workflowStateCodeMap.put(WorkflowStateCode.AUTO_VALIDATION, 300);
		 workflowStateCodeMap.put(WorkflowStateCode.STATEMENT_TRANSLATION, 400);
		 workflowStateCodeMap.put(WorkflowStateCode.PDF_TAGGING, 500);
		 workflowStateCodeMap.put(WorkflowStateCode.RECEIVE, 600);
		 workflowStateCodeMap.put(WorkflowStateCode.UPDATE_RERUN_STATUS, 700 );
		 
		 return workflowStateCodeMap;
	}

	@Bean
	public Map<DocumentStatusCode, Integer> documentStatusCodeMap(){
		
		 Map<DocumentStatusCode, Integer> documentStatusMap = new HashMap<DocumentStatusCode, Integer>();
		 
		 documentStatusMap.put(DocumentStatusCode.NEW, 1 );
		 documentStatusMap.put(DocumentStatusCode.SUCCESS, 2 );
		 documentStatusMap.put(DocumentStatusCode.FAILED, 3 );
		 documentStatusMap.put(DocumentStatusCode.REPROCESS, 4 );
		 
		 return documentStatusMap;
	}	
	
	@Bean
	public Map<String, Object> workerMap(){
		
		 return new ConcurrentHashMap<String, Object>();
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		try{
	        final SSLContext sslContext = SSLContext.getInstance("TLS");
	        
	        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
	            }
	 
	            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
	            }
	 
	            public X509Certificate[] getAcceptedIssuers() {
	                return new X509Certificate[0];
	            }
	        }}, null);
	 
	        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        });
		}
		catch (Throwable e){
			//LOG.error(e.getMessage(), e);
		}
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	
	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(1);
		pool.setMaxPoolSize(10);
		//pool.setQueueCapacity(100000);
		pool.setWaitForTasksToCompleteOnShutdown(true);
		pool.setRejectedExecutionHandler(new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				LOG.debug("runnable " + r);
				executor.execute(r);
			}
		});

		return pool;
	}
	
 }
