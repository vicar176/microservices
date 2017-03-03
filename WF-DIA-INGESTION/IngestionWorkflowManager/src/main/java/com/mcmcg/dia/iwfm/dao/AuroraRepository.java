package com.mcmcg.dia.iwfm.dao;

import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author jaleman
 *
 */
public abstract class AuroraRepository {
	
	private static final Logger LOG = Logger.getLogger(AuroraRepository.class);

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Resource(name = "queriesMap")
	protected Map<String, String> queriesMap;
	
	public void waitTime(){
		 Random random = new Random();
		 long waitTime = 3000 + (random.nextInt(10) + 1) * 1000;
		 LOG.debug("Waiting time ---> " + waitTime / 1000);
	     try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			//nothing to do
		}
	}

}
