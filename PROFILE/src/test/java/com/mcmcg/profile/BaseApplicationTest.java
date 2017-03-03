package com.mcmcg.profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 
 */

/**
 * @author jaleman
 *
 */
@WebAppConfiguration("src/main/java")
@ContextConfiguration(loader = AnnotationConfigWebContextLoader.class, classes = {ProfileServiceConfigurationTest.class})
public abstract class BaseApplicationTest extends AbstractJUnit4SpringContextTests  {

	/**
	 * 
	 */
	public BaseApplicationTest() {
		// TODO Auto-generated constructor stub
	}

}
