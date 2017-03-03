/**
 * 
 */
package com.mcmcg.profile.aop;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mcmcg.profile.BaseApplicationTest;

/**
 * 
 * 
 * @author Jose Aleman
 *
 */
public class AuditableAspectTest extends BaseApplicationTest {

	private final static Logger LOG = Logger.getLogger(AuditableAspectTest.class);
	
	@Autowired
	AspectTest aspectTest;
	/**
	 * 
	 */
	public AuditableAspectTest() {
		// TODO Auto-generated constructor stub
	}

	@Ignore
	@Test
	public void testAspect(){
		aspectTest.evaluate(1, 3);
		aspectTest.multiply(1, 4);
	}
}
