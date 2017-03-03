package com.mcmcg.dia.ingestionState.configuration;


import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.web.client.RestTemplate;
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

	@Bean
	public DataSource dataSource() {
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		dsLookup.setResourceRef(true);
		DataSource dataSource = dsLookup.getDataSource("everest");
		return dataSource;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate() {
		JdbcTemplate template = new JdbcTemplate(dataSource());
		return template; 
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
	
	@Bean
	public String EC2() {
		String ec2 = StringUtils.EMPTY;

		try{
			ec2 = EC2MetadataUtils.getInstanceId(); 
			if(StringUtils.isBlank(ec2)) {
				ec2 = "Unknown";
			}
		}
		catch (Throwable e){
			//
		}
		return ec2;
	}

	@Bean
	public String privateIp() {
		String privateAddress = "127.0.0.1";
		try{
			privateAddress = EC2MetadataUtils.getInstanceInfo().getPrivateIp(); 
		}
		catch (Throwable e){
			//
		}

		return privateAddress;
	}
	
	@Bean
	public Map<Integer, String> statusMap() {
		Map<Integer, String> status = new HashMap<Integer, String>();
		status.put(1, "new");
		status.put(100, "update-workflowstate");
		status.put(200, "receive");
		status.put(300, "extraction");
		status.put(400, "auto-validation");
		status.put(500, "statement-translation");
		status.put(600, "pdf-tagging");
		status.put(700, "update-rerun-status");
		return status;
	}
}
