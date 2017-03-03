package com.mcmcg.utility.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.mcmcg.utility.file.FileSystem;
import com.mcmcg.utility.file.IFileSystem;
import com.mcmcg.utility.file.S3FileSystem;
import com.mcmcg.utility.util.MetaDataUtil.BUCKET_NAMES_ENUM;

/**
 * @author Jose Aleman
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.mcmcg")
@EnableAspectJAutoProxy(proxyTargetClass = false)
@PropertySource(value = "classpath:/application.properties")
public class UtilityServiceConfiguration {

	private final static Logger LOG = Logger.getLogger(UtilityServiceConfiguration.class);

	@Autowired
	private Environment env;

	@Value("${application.env}")
	private String server;

	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	public AWSCredentials s3AwsCredentials() {
		
		if (StringUtils.equalsIgnoreCase("local", server)) {
			return new BasicAWSCredentials(env.getProperty("s3.accessKey"), env.getProperty("s3.secretKey"));
		} else {
			return null;
		}
	}
	

	@Bean
	public String s3EncryptionKey() {
		return env.getProperty("s3.encryptionKey");
	}

	// To resolve ${} in @Value
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("utf-8");
		resolver.setMaxUploadSize(Integer.parseInt(env.getProperty("multipart.maxFileSize")));
		resolver.setMaxInMemorySize(Integer.parseInt(env.getProperty("multipart.maxinMemory")));
		return resolver;
	}

	@Bean
	public IFileSystem fileSystem() {

//		String server = env.getProperty("application.env");
		FileSystem fileSystem = null;

		// TODO: remove the switch if local file system is not longer used
//		switch (server.toLowerCase().trim()) {
//		case "local2":
//			fileSystem = new FileSystem();
//			break;
//		default:
			Map<BUCKET_NAMES_ENUM, String> bucketNames = new HashMap<>();
			bucketNames.put(BUCKET_NAMES_ENUM.TEMPLATE, env.getProperty("s3.bucket.template"));
			bucketNames.put(BUCKET_NAMES_ENUM.EXTRACTION, env.getProperty("s3.bucket.extraction"));
			bucketNames.put(BUCKET_NAMES_ENUM.ORIGINAL, env.getProperty("s3.bucket.original"));
			fileSystem = new S3FileSystem(bucketNames, s3EncryptionKey(), s3AwsCredentials(), 
										  Integer.parseInt(env.getProperty("s3.maxErrorRetry")));
//			break;
//		}

		return fileSystem;
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
