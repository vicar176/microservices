package com.mcmcg.dia.batchscheduler.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.batchmanager.domain.BatchProfileSearchFilterDetail;
import com.mcmcg.dia.batchmanager.domain.BatchProfileWithAction;
import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchmanager.entity.Action;
import com.mcmcg.dia.batchmanager.entity.SearchFiltersBatchProfile;
import com.mcmcg.dia.batchscheduler.exception.ServiceException;
import com.mcmcg.dia.batchscheduler.service.batchmanager.BaseService;
import com.mcmcg.dia.batchscheduler.service.batchmanager.BatchProfileSearchFilterService;
import com.mcmcg.dia.batchscheduler.service.batchmanager.BatchProfileService;
import com.mcmcg.dia.batchscheduler.service.batchmanager.IService;

@Repository("schedularDAO")
public class SchedularDAO {

	@Resource(name = "queriesMap")
	private Map<String, String> queriesMap;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	
	private static final Logger LOG = Logger.getLogger(SchedularDAO.class);
	
	private final static String TEMPTABLE_CREATE_FOR_DOCID = "TEMPTABLE_CREATE_FOR_DOCID";
	
	private final static String TEMPTABLE_DOCUMENTID = "TEMPTABLE_DOCUMENTID";// Temporary table name for Doc Id. 
	
	
	public void saveDocumentIdInTempTable(String filter ){
		
		LOG.info(" Start saveDocumentIdInTempTable() ");

		String query = queriesMap.get(TEMPTABLE_CREATE_FOR_DOCID);
		LOG.debug(String.format("query [%s] ", query));
	
		jdbcTemplate.update(query, TEMPTABLE_DOCUMENTID);
		
		
	}


}
