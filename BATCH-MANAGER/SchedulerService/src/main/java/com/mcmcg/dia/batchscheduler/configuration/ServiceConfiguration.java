package com.mcmcg.dia.batchscheduler.configuration;


import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
@PropertySources({
    @PropertySource("classpath:/application.properties"),
    @PropertySource("classpath:/quartz.properties")
})
public class ServiceConfiguration {

	@Bean(destroyMethod="")
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
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(1);
		pool.setMaxPoolSize(5);
		pool.setWaitForTasksToCompleteOnShutdown(true);
		pool.setRejectedExecutionHandler(new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				executor.execute(r);
			}
		});

		return pool;
	}
}
