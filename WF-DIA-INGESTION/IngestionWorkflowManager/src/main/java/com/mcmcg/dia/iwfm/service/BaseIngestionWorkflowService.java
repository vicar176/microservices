package com.mcmcg.dia.iwfm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public abstract class BaseIngestionWorkflowService {

	@Autowired
	protected ThreadPoolTaskExecutor taskExecutor;
	
	protected static int REPROCESS_CODE = 4; 
	
	public BaseIngestionWorkflowService() {
		// TODO Auto-generated constructor stub
	}
		
}
