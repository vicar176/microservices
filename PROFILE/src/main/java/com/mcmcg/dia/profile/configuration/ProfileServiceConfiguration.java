package com.mcmcg.dia.profile.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment.Builder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmcg.dia.profile.model.domain.DocumentFieldsDefinition;

/**
 * @author Jose Aleman
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.mcmcg")
@EnableAspectJAutoProxy(proxyTargetClass = false)
@PropertySource(value = "classpath:/application.properties")
@ImportResource("classpath:/queries.xml")
@EnableCouchbaseRepositories("com.mcmcg.dia.profile.dao")
public class ProfileServiceConfiguration extends AbstractCouchbaseConfiguration{

	private final static Logger LOG = Logger.getLogger(ProfileServiceConfiguration.class);

	@Autowired
	private Environment env;

	@Value("${application.env}")
	private String server;
	
	@Value("${couchbase.cluster.bucket}")
	private String bucketName;

	@Value("${couchbase.cluster.password}")
	private String password;

	@Value("${couchbase.cluster.host}")
	private String host;
	
	@Override
	protected List<String> getBootstrapHosts() {
		List<String> hosts = new ArrayList<String>(Arrays.asList(host.split(",")));
		return hosts;
	}

	@Override
	protected String getBucketName() {
		return this.bucketName;
	}

	@Override
	protected String getBucketPassword() {
		return this.password;
	}
	
	@Override
	protected CouchbaseEnvironment getEnvironment() {
		
		Builder builder =DefaultCouchbaseEnvironment.builder();
		builder.connectTimeout(TimeUnit.SECONDS.toMillis(120));
		builder.socketConnectTimeout(10000);
		builder.queryTimeout(120000);
		builder.viewTimeout(120000);
		builder.kvTimeout(8000);
		builder.disconnectTimeout(60000);
		
		return builder.computationPoolSize(6).build();
	}
	
	@Bean
	@Override
	public Cluster couchbaseCluster() throws Exception {
		return super.couchbaseCluster();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	// To resolve ${} in @Value
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	/**
	 * 
	 * Private Methods
	 */
	private List<DocumentFieldsDefinition> documentFieldDefinitions() {
		List<DocumentFieldsDefinition> documentFieldDefinitions = null;
		ObjectMapper mapper = new ObjectMapper();
		Resource resource = new ClassPathResource("documentFieldDefinitions.json");
		try {
			documentFieldDefinitions = mapper.readValue(resource.getFile(),
					mapper.getTypeFactory().constructCollectionType(List.class, DocumentFieldsDefinition.class));
			LOG.debug(documentFieldDefinitions);
		} catch (IOException e) {
			LOG.error(e);
		}

		return documentFieldDefinitions;
	}
	
	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(5);
		pool.setMaxPoolSize(10);
		pool.setQueueCapacity(200);
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
