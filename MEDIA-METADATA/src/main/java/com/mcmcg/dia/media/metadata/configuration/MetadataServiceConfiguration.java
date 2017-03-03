package com.mcmcg.dia.media.metadata.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.couchbase.client.java.Cluster;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment.Builder;

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
@EnableCouchbaseRepositories("com.mcmcg.dia.media.metadata.dao")
public class MetadataServiceConfiguration extends AbstractCouchbaseConfiguration {

	private final static Logger LOG = Logger.getLogger(MetadataServiceConfiguration.class);

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
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM, 
																				 MediaType.TEXT_HTML));
		return restTemplate;
	}

	@Bean
	public AWSCredentials awsCredentials() {
		if (!StringUtils.equals(env.getProperty("aws.accessKey"), "default")
				&& !StringUtils.equals(env.getProperty("aws.secretKey"), "default")) {
			return new BasicAWSCredentials(env.getProperty("s3.accessKey"), env.getProperty("s3.secretKey"));
		}

		return null;
	}

	@Bean
	public AWSKMS kms() {
		AWSKMSClient kms = null;
		if (!StringUtils.equalsIgnoreCase("local", server) && awsCredentials() != null) {
			kms = new AWSKMSClient(awsCredentials());
			kms.setEndpoint(env.getProperty("aws.kms.endpoint"));
		}
		return kms;
	}

	@Bean
	public String awsEncryptionKey() {
		return env.getProperty("aws.encryptionKey");
	}

	@Bean
	public String awsEncryptionKeyArn() {
		return env.getProperty("aws.encryptionKey.arn") + awsEncryptionKey();
	}

	// To resolve ${} in @Value
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
