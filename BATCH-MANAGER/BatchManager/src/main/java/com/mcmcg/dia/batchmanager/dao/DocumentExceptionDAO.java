package com.mcmcg.dia.batchmanager.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.batchmanager.entity.DocumentException;
import com.mcmcg.dia.batchmanager.exception.PersistenceException;
import com.mcmcg.dia.batchmanager.util.FilteringQueryUtils;

@Repository("documentExceptionDAO")
public class DocumentExceptionDAO extends AuroraRepository{

	private static final Logger LOG = Logger.getLogger(DocumentExceptionDAO.class);

	private final static String INSERT_DOCUMENT_EXCEPTIONS = "INSERT_DOCUMENT_EXCEPTIONS";//
	private final static String FIND_DOCUMENT_EXCEPTIONS_BY_DOCUMENT_ID = "FIND_DOCUMENT_EXCEPTIONS_BY_DOCUMENT_ID";
	private final static String REMOVE_DOCUMENT_EXCEPTIONS_BY_DOCUMENT_ID = "REMOVE_DOCUMENT_EXCEPTIONS_BY_DOCUMENT_ID";
	private final static String UPDATE_DOCUMENT_EXCEPTIONS_BY_DOCUMENT_ID = "UPDATE_DOCUMENT_EXCEPTIONS_BY_DOCUMENT_ID";
	private final static String FIND_DOCUMENT_EXCEPTIONS_BY_JOB_ID = "FIND_DOCUMENT_EXCEPTIONS_BY_JOB_ID";
	
	/**
	 * 
	 * @param documentExceptions
	 * @return
	 * @throws PersistenceException
	 */
	public Long saveDocException(DocumentException documentExceptions) throws PersistenceException {

		String query = queriesMap.get(INSERT_DOCUMENT_EXCEPTIONS);
		LOG.debug(String.format("query [%s] ", query));
		Long key = null;
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, 
																					documentExceptions.getBatchProfileJobId(), documentExceptions.getDocumentId(),
																					documentExceptions.getStatus(), documentExceptions.getErrorDescription(),
																					documentExceptions.getCreatedOnDate(), documentExceptions.getCreatedBy(),
																					documentExceptions.getChangedOnDate(), documentExceptions.getChangedBy(),
																					documentExceptions.getWorkstationName(), documentExceptions.getApplicationId());
			jdbcTemplate.update(psCreator, keyHolder);
			key = keyHolder.getKey().longValue();
			
		} catch (DataAccessException ex) {
			LOG.error(ex.getMessage(), ex);
			throw new PersistenceException("Error on saveDocException()" + ex.getMessage(), ex);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			throw new PersistenceException("Error on saveDocException()" + ex.getMessage(), ex);
		}
		
		return key;

	}
	
	/**
	 * 
	 * @param documentExceptions
	 * @return
	 */
	public boolean saveBatchOfDocumentExceptions(final List<DocumentException> documentExceptions){
		
		String query = queriesMap.get(INSERT_DOCUMENT_EXCEPTIONS);
		LOG.debug(String.format("query [%s] ", query));
		
		int ids[] = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				
				DocumentException documentException = documentExceptions.get(index);
				
				populatePreparedStatement(ps, documentException);
			}
			
			@Override
			public int getBatchSize() {
				
				return documentExceptions.size();
			}
		});
		
		return ids.length >= 0;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws PersistenceException
	 */
	public DocumentException findByDocumentId(String id, Long batchProfileJobId ) throws PersistenceException{

		String query = queriesMap.get(FIND_DOCUMENT_EXCEPTIONS_BY_DOCUMENT_ID);
		LOG.debug(String.format("query [%s] ", query));
		DocumentException documentException = new DocumentException();
		
		try {
			PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, id, batchProfileJobId);
			documentException = jdbcTemplate.query(psCreator, new ResultSetExtractor<DocumentException>() {

				@Override
				public DocumentException extractData(final ResultSet rs) throws SQLException, DataAccessException {
					
					return populateFrom(rs.next() ? rs : null);
				}
				
			});


		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on findOne()" + ex.getMessage(), ex);
		}
		return documentException;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws PersistenceException
	 */
	public List<DocumentException> findByProfileJobId(Long batchProfileJobId, int page, int size) throws PersistenceException{

		String query = queriesMap.get(FIND_DOCUMENT_EXCEPTIONS_BY_JOB_ID) + FilteringQueryUtils.buildLimitClause(page, size);
		LOG.debug(String.format("query [%s] ", query));
		List<DocumentException> documentExceptions = new ArrayList<DocumentException>();
		
		try {
			PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, batchProfileJobId);
			documentExceptions = jdbcTemplate.query(psCreator, new ResultSetExtractor<List<DocumentException>>() {

				@Override
				public List<DocumentException> extractData(final ResultSet rs) throws SQLException, DataAccessException {
					List<DocumentException> documentExceptions = new ArrayList<DocumentException>();
					
					while (rs.next()){
						documentExceptions.add(populateFrom(rs));
					}
					
					return documentExceptions;
				}
				
			});


		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on findOne()" + ex.getMessage(), ex);
		}
		return documentExceptions;
	}
	/**
	 * 
	 * @param id
	 * @param batchProfileJobId
	 * @return
	 * @throws PersistenceException
	 */
	public boolean removeByDocumentId(String id, Long batchProfileJobId) throws PersistenceException{
		
		String query = queriesMap.get(REMOVE_DOCUMENT_EXCEPTIONS_BY_DOCUMENT_ID);
		
		return executeUpdate(query, id, batchProfileJobId);
	}
	
	/**
	 * 
	 * @param id
	 * @param batchProfileJobId
	 * @return
	 * @throws PersistenceException
	 */
	public boolean updateByDocumentId(String id, Long batchProfileJobId, int status, String description) throws PersistenceException{
		
		String query = queriesMap.get(UPDATE_DOCUMENT_EXCEPTIONS_BY_DOCUMENT_ID);
		
		return executeUpdate(query, status, description, id, batchProfileJobId);
	}
	/*********************
	 * 
	 * 
	 * 
	 *			PRIVATE METHODS 
	 *  
	 * 
	 * 
	 *********************************/
	/**
	 * 
	 * @param query
	 * @param objects
	 * @return
	 * @throws PersistenceException
	 */
	private boolean executeUpdate (String query, Object...objects) throws PersistenceException{
		//String query = queriesMap.get(REMOVE_DOCUMENT_EXCEPTIONS_BY_DOCUMENT_ID);
		LOG.debug(String.format("query [%s] ", query));
		int affectedRows = -1;
		try {

			PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, objects);
			affectedRows = jdbcTemplate.update(psCreator);
			
		} catch (DataAccessException ex) {
			LOG.error(ex.getMessage(), ex);
			throw new PersistenceException("Error on findOne()" + ex.getMessage(), ex);
		}
		
		return affectedRows != -1;
	}
	
	/**
	 * 
	 * @param ps
	 * @param documentException
	 * @throws SQLException
	 */
	private void populatePreparedStatement(PreparedStatement ps, DocumentException documentException) throws SQLException{
		ps.setLong(1, documentException.getBatchProfileJobId());
		ps.setString(2, documentException.getDocumentId());
		ps.setInt(3,  documentException.getStatus());
		ps.setString(4, documentException.getErrorDescription());
		ps.setDate(5, new java.sql.Date(documentException.getCreatedOnDate().getTime()));
		ps.setString(6,  documentException.getCreatedBy());
		ps.setDate(7, new java.sql.Date(documentException.getChangedOnDate().getTime()));
		ps.setString(8,  documentException.getChangedBy());
		ps.setString(9, documentException.getWorkstationName());
		ps.setInt(10,  documentException.getApplicationId());
	}
	
	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException 
	 */
	private DocumentException populateFrom(ResultSet rs) throws SQLException{
		/**
		 * BatchProfileJobId,
				DocumentId, DocumentStatusId,
				ErrorDescription,CreatedOnDate,CreatedBy,ChangedOnDate,ChangedBy,WorkstationName,ApplicationId
		 */
		DocumentException documentException = new DocumentException();
		if (rs != null){
			documentException.setApplicationId(rs.getInt("ApplicationId"));
			documentException.setBatchProfileJobId(rs.getLong("BatchProfileJobId"));
			documentException.setChangedBy(rs.getString("ChangedBy"));
			documentException.setChangedOnDate(new Date (rs.getDate("ChangedOnDate").getTime()));
			documentException.setCreatedBy(rs.getString("CreatedBy"));
			documentException.setCreatedOnDate(new Date (rs.getDate("CreatedOnDate").getTime()));
			documentException.setDocumentId(rs.getString("DocumentId"));
			documentException.setErrorDescription(rs.getString("ErrorDescription"));
			documentException.setExceptionId(rs.getLong("ExceptionId"));
			documentException.setStatus(rs.getInt("DocumentStatusId"));
			documentException.setWorkstationName("WorkstationName");
		}
		return documentException;
	}
}
