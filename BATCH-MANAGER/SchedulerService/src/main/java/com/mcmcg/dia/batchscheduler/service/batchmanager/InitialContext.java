/**
 * 
 */
package com.mcmcg.dia.batchscheduler.service.batchmanager;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * @author jaleman
 *
 */
@Component
public class InitialContext {

	/*@Autowired
	AccountService accountService;

	@Autowired
	DocumentFieldProfileService documentFieldProfileService;
	
*/
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<String, IService> serviceMap;

	@SuppressWarnings("rawtypes")
	@PostConstruct
	public void init() {
		serviceMap = new ConcurrentHashMap<String, IService>();

		//serviceMap.put(AccountService.class.getSimpleName(), accountService);
		//serviceMap.put(DocumentFieldProfileService.class.getSimpleName(), documentFieldProfileService);
	}

	/**
	 * 
	 */
	public InitialContext() {
		
	}

	public Object lookup(String serviceName) {

		return serviceMap.get(serviceName);
	}
}
