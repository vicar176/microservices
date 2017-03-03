package com.mcmcg.media.workflow.configuration.sqs;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.mcmcg.media.workflow.configuration.aws.CustomAWSCredentialsProvider;

/**
 * @author jaleman
 *
 */
@Configuration
@PropertySource(value = {"classpath:/application.properties", "file:/opt/dia/java/configuration.properties"})
public class SQSConsumerConfiguration {

	@Autowired
	private Environment env;

	@Value("${aws.swf.service.url}")
	private String swfServiceUrl;

	@Value("${aws.sqs.service.url}")
	private String sqsServiceUrl;

	@Value("${application.env}")
	private String environmentName;

	public final static String QUEUE_NAME = "mcm-sqs-everest-ingestion";
	public final static String ON_DEMAND_QUEUE_NAME = "mcm-sqs-everest-ingestion-ondemand";
	
	@Bean(name = { "customAWSCredentialsProvider" })
	public AWSCredentialsProvider customAWSCredentialsProvider() {
		if (StringUtils.equalsIgnoreCase("local", environmentName)) {
			return new CustomAWSCredentialsProvider(env.getProperty("aws.AccessKeyId"),
					env.getProperty("aws.AWSSecretKey"));
		} else {
			return new CustomAWSCredentialsProvider();
		}
	}

	@Bean(name = { "queue" })
	public String queue() {
		return getQueue(QUEUE_NAME);
	}

	@Bean(name = { "onDemandQueue" })
	public String onDemandQueue() {
		return getQueue(ON_DEMAND_QUEUE_NAME);
	}

	/**
	 * @return
	 */
	private String getQueue(String queueName) {
		String env = environmentName;
		if (StringUtils.contains(env, "prod")) {
			env = "prd";
		}
		return queueName + "-" + env;
	}

	@Bean(name = { "sqsServiceUrl" })
	public String sqsServiceUrl() {
		return sqsServiceUrl;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean(name = {"env"})
	public String env(){
		return environmentName;
	}
}
