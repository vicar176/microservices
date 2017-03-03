/**
 * 
 */
package com.mcmcg.ingestion;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Jose Aleman
 *
 */
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass=false)
@ComponentScan("com.mcmcg")
@PropertySource(value = "classpath:/application-test.properties")
public class IngestionWorkflowManagerConfigurationTest {
	private final static Logger LOG = Logger.getLogger(IngestionWorkflowManagerConfigurationTest.class);

	@Bean
	public RestTemplate getRestTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
}
