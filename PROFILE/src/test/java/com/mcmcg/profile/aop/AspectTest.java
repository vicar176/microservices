/**
 * 
 */
package com.mcmcg.profile.aop;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mcmcg.dia.profile.annotation.Auditable;

/**
 * @author Jose Aleman
 *
 */
@Component
public class AspectTest {

	private final static Logger LOG = Logger.getLogger(AspectTest.class);
	/**
	 * 
	 */
	public AspectTest() {
		// TODO Auto-generated constructor stub
	}

	@Auditable
	public int evaluate(int x, int y){
		LOG.info(x + y);
		return x + y;
	}
	
	@Auditable(eventCode = com.mcmcg.dia.profile.util.EventCode.OBJECT_CREATED)
	public int multiply(int x, int y){
		LOG.info(x * y);
		return x * y;
	}
}
