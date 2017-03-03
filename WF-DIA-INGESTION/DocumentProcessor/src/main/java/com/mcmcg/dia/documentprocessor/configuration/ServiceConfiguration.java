package com.mcmcg.dia.documentprocessor.configuration;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.amazonaws.util.EC2MetadataUtils;

/**
 * @author Jose Aleman
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.mcmcg")
@EnableAspectJAutoProxy(proxyTargetClass = false)
@ImportResource("classpath:/queries.xml")
@PropertySource(value = "classpath:/application.properties")
public class ServiceConfiguration {

	private final static Logger LOG = Logger.getLogger(ServiceConfiguration.class);

	@Value("${aurora.datasource}")
	private String dataSource;
	
	@Autowired
	private Environment env;

	@Bean
	public DataSource dataSource() {
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		dsLookup.setResourceRef(true);
		DataSource dataSource = dsLookup.getDataSource(this.dataSource);
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template;
	}

	@Bean
	public NamedParameterJdbcTemplate  namedParameterJdbcTemplate (DataSource dataSource) {
		NamedParameterJdbcTemplate  template = new NamedParameterJdbcTemplate (dataSource);
		return template;
	}

	@Bean
	public RestTemplate restTemplate() {
		try {
			final SSLContext sslContext = SSLContext.getInstance("TLS");

			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
						throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
						throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, null);

			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
		RestTemplate restTemplate = new RestTemplate();
		
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		
		return restTemplate;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(10);
		pool.setMaxPoolSize(15);
		pool.setQueueCapacity(1000);
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

	@Bean
	public String EC2() {
		String ec2 = "Unknown";

		try {
			ec2 = EC2MetadataUtils.getInstanceId();
		} catch (Throwable e) {
			//
		}
		return ec2;
	}

	@Bean
	public String privateIp() {
		String privateAddress = "127.0.0.1";
		try {
			privateAddress = EC2MetadataUtils.getInstanceInfo().getPrivateIp();
		} catch (Throwable e) {
			//
		}

		return privateAddress;
	}

	@Bean
	public Set<String> supportedDocumentIdSet(){
		Set<String> supportedDocumentIdSet = new HashSet<String>();
		
		supportedDocumentIdSet.add("DOCID");
		supportedDocumentIdSet.add("BOXID");
		
		return Collections.unmodifiableSet(supportedDocumentIdSet);
	}
	
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("utf-8");
		resolver.setMaxUploadSize(Integer.parseInt(env.getProperty("multipart.maxFileSize")));
		resolver.setMaxInMemorySize(Integer.parseInt(env.getProperty("multipart.maxinMemory")));
		return resolver;
	}
	
}
