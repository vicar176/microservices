package com.mcmcg.dia.iwfm.configuration;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;

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
@EnableCouchbaseRepositories("com.mcmcg.dia.iwfm.automation")
public class IngestionServiceConfiguration extends AbstractCouchbaseConfiguration{

	private final static Logger LOG = Logger.getLogger(IngestionServiceConfiguration.class);

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
		
		return DefaultCouchbaseEnvironment.builder().connectTimeout(TimeUnit.SECONDS.toMillis(120))
				.computationPoolSize(6).build();
	}
	
	@Bean
	@Override
	public Cluster couchbaseCluster() throws Exception {
		return super.couchbaseCluster();
	}
	
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
		//pool.setQueueCapacity(1000);
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
