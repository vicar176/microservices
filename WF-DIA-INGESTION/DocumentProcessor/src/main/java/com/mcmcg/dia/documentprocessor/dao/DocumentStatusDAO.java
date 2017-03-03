package com.mcmcg.dia.documentprocessor.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.documentprocessor.entity.DocumentStatus;
import com.mcmcg.dia.documentprocessor.exception.PersistenceException;

@Repository
public class DocumentStatusDAO extends AuroraRepository {
	
	private static final Logger LOG = Logger.getLogger(DocumentStatusDAO.class);

	private static final String DOCUMENTSTATUS_FIND_ALL = "DOCUMENTSTATUS_FIND_ALL";
	
	public DocumentStatusDAO() {
	}

	/**
	 * 
	 * @return
	 * @throws PersistenceException
	 */
	public List<DocumentStatus> findAll() throws PersistenceException{
		
		String query = queriesMap.get(DOCUMENTSTATUS_FIND_ALL);
		List<DocumentStatus> documentStatusList = null;
		try{
			documentStatusList = populateDocumentStatus(query);
		}
		catch (DataAccessException ex){
			throw new PersistenceException("Error on findAll()", ex );
		}
		
		return documentStatusList;
	}


	/****************************************************************************
	 * 
	 * 			PRIVATE METHODS
	 * 
	 * 
	 ****************************************************************************/
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	private List<DocumentStatus> populateDocumentStatus(String query) {
		
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query);

		List<DocumentStatus> documentStatusList =jdbcTemplate.execute(psCreator, new PreparedStatementCallback<List<DocumentStatus>>() {

			@Override
			public List<DocumentStatus> doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				ResultSet rs = ps.executeQuery();
				List<DocumentStatus> documentStatusList = new ArrayList<DocumentStatus>();
				while (rs.next()){
					//Sets
					DocumentStatus status = new DocumentStatus();
					status.setId(rs.getLong("Document_Status_Id"));
					status.setCode(rs.getString("Document_Status_Code"));
					status.setDescription(rs.getString("Document_Status_Description"));
					documentStatusList.add(status);
				}
				return documentStatusList;
			}

		});		
		
		return documentStatusList;
	}
}
