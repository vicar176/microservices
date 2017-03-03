package com.mcmcg.dia.iwfm.automation;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.iwfm.dao.AuroraRepository;

@Repository
public class AuroraDAO extends AuroraRepository {

	private static final Logger LOG = Logger.getLogger(AuroraDAO.class);

	public AuroraDAO() {
	}

	public void cleanAurotaTables() throws PersistenceException {

		LOG.info("Start cleanAurotaTables");

		try {
			jdbcTemplate.execute("DELETE FROM Ingestion_Step");
			jdbcTemplate.execute("DELETE FROM Ingestion_Tracker");
			jdbcTemplate.execute("DELETE FROM Batch_Execution;");
			jdbcTemplate.execute("DELETE FROM Workflow_State");
		} catch (DataAccessException e) {
			String message = "Error on cleaning Aurora tables";
			LOG.error(message, e);
			throw new PersistenceException(message, e);
		}
	}

	public void prepareDocuments(Long totalDocuments) throws PersistenceException {

		LOG.info("Start prepareDocuments");

		String query = "update Document_Images set Ingestion_Status = null "
				+ "where Ingestion_Status = 'ToIngest' and LENGTH(TRIM(S3_Bucket)) = 0 LIMIT %s";
		query = String.format(query, totalDocuments);
		try {
			jdbcTemplate.execute(query);
		} catch (DataAccessException e) {
			String message = "Error on preparing documents";
			LOG.error(message, e);
			throw new PersistenceException(message, e);
		}

	}

	public void changeWfShutdownState(String user, Boolean shutdownState) throws PersistenceException {
		LOG.info("Start changeWfShutdownState");

		String query = "INSERT INTO Workflow_Shutdown_State (Shutdown_State, Created_By, Changed_By) "
				+ "VALUES (%s, '%s', '%s')";
		query = String.format(query, shutdownState, user, user);
		try {
			jdbcTemplate.execute(query);
		} catch (DataAccessException e) {
			String message = "Error on changing the workflow state";
			LOG.error(message, e);
			throw new PersistenceException(message, e);
		}
	}

	public void save(IngestionTest ingestionTest) throws PersistenceException {
		LOG.info("Start save");

		String query = "INSERT INTO DIAVELOCITYMATRIX (NumberOfNodes, NumberOfDocs, BatchSize, NumberOfThreads, "
				+ " NumberOdCores, CouchbaseIOPs, CreatedBy) " + " VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')";
		query = String.format(query, ingestionTest.getNodes(), ingestionTest.getTotalDocuments(),
				ingestionTest.getBatchSize(), ingestionTest.getWorkFlowThreads(), ingestionTest.getCores(),
				ingestionTest.getCouchBaseIOPS(), ingestionTest.getUser());
		try {
			jdbcTemplate.execute(query);
		} catch (DataAccessException e) {
			String message = "Error on saving the Ingestion Test";
			LOG.error(message, e);
			throw new PersistenceException(message, e);
		}
	}

	/****************************************************************************
	 * 
	 * PRIVATE METHODS
	 * 
	 ****************************************************************************/

}
