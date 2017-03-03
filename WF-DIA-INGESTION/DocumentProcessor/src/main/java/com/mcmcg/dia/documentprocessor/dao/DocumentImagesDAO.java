package com.mcmcg.dia.documentprocessor.dao;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.documentprocessor.exception.PersistenceException;
import com.mcmcg.dia.iwfm.domain.NewDocumentStatus;

/**
 * 
 * @author Victor Arias
 *
 */

@Repository
public class DocumentImagesDAO extends AuroraRepository {

	private static final Logger LOG = Logger.getLogger(DocumentImagesDAO.class);

	private final static String DOCUMENTIMAGES_UPDATE = "DOCUMENTIMAGES_UPDATE";
	private final static String DOCUMENTSIMAGES_STATUS = "DOCUMENTSIMAGES_STATUS";
	private final static String DOCUMENTIMAGES_GET = "DOCUMENTIMAGES_GET";

	public boolean update(String documentId, NewDocumentStatus documentStatus) {
		String query = queriesMap.get(DOCUMENTIMAGES_UPDATE);
		LOG.debug(String.format("query [%s] ", query));
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, documentStatus.getStatus(), documentId);

		return jdbcTemplate.update(psCreator) > 0;
	}
	

	public List<Map<String,Object>>  findDocumentImageStatus() throws PersistenceException {
		List<Map<String,Object>> fields = null;
		String query = queriesMap.get(DOCUMENTSIMAGES_STATUS);
		LOG.debug(String.format("query [%s] ", query));
		
		try {
			fields = jdbcTemplate.queryForList(query);
		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on findIngestionStatus()", ex);
		} catch (Throwable t) {
			LOG.error(t);
			throw new PersistenceException("Error on findIngestionStatus()", t);
		}
		
		return fields;
	}
	
	/**
	 * 
	 * @param documentIds
	 * @return
	 * @throws PersistenceException
	 */
	public List<Map<String,Object>> findDocumentsByIds(Set<String> documentIds) throws PersistenceException{
		List<Map<String,Object>> fields = null;
		String query = queriesMap.get(DOCUMENTIMAGES_GET);
		Set<String> filteredSet = documentIds.stream().map(id -> id.trim()).collect(Collectors.toCollection(HashSet::new));
		
		LOG.debug(String.format("query [%s] ", query));
		
		try {
			Map<String, Set<String>> paramMap = Collections.singletonMap("documentIds", filteredSet);
			fields = namedParameterJdbcTemplate.queryForList(query, paramMap);
		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on findDocumentsByIds() " + ex.getMessage(), ex);
		} catch (Throwable t) {
			LOG.error(t);
			throw new PersistenceException("Error on findDocumentsByIds() " + t.getMessage(), t);
		}
		
		return fields;
	}
	
	
	
	
}
