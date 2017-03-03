package com.mcmcg.ingestion.configuration;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.mcmcg.ingestion.util.IngestionUtils;

/**
 * @author Jose Aleman
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.mcmcg")
@EnableAspectJAutoProxy(proxyTargetClass = false)
@PropertySource(value = "classpath:/application.properties")
public class IngestionServiceConfiguration {

	private final static Logger LOG = Logger.getLogger(IngestionServiceConfiguration.class);

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
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM, 
																				 MediaType.TEXT_HTML));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
		return restTemplate;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public Map<String, Integer> documentStatusMap(){
		
		 Map<String, Integer> documentStatusMap = new HashMap<String, Integer>();
		
		 documentStatusMap.put(IngestionUtils.DISCARDED, 1 );
		 documentStatusMap.put(IngestionUtils.TEMPLATE_NOT_FOUND, 2 );
		 documentStatusMap.put(IngestionUtils.EXTRACTED, 3 );
		 documentStatusMap.put(IngestionUtils.VALIDATED, 4 );
		 documentStatusMap.put(IngestionUtils.MANUAL_VALIDATED, 5 );
		 documentStatusMap.put(IngestionUtils.TRANSLATED, 6 );
		 documentStatusMap.put(IngestionUtils.RECEIVED, 7 );
		 documentStatusMap.put(IngestionUtils.TAGGED, 8 );
		 documentStatusMap.put(IngestionUtils.REPROCESS, 9 );
		 
		 return documentStatusMap;
	}
}

