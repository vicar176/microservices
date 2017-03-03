package com.mcmcg.dia.iwfm.automation;

import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CouchBaseDAO {

	@Autowired
	N1qlQueryDAO n1qlQueryDAO;

	private static final Logger LOG = Logger.getLogger(CouchBaseDAO.class);

	public void cleanCouchBaseTables() {
		LOG.info("Start cleanCouchBaseTables");

		try {
			// MediaMetadataEntity
			StringBuilder query = new StringBuilder("delete from `media-dia` where type = 'MediaMetadataEntity'");
			n1qlQueryDAO.executeQuery(query);

			// AccountMetadataEntity
			query = new StringBuilder("delete from `media-dia` where type = 'AccountMetadataEntity'");
			n1qlQueryDAO.executeQuery(query);

			// AccountOALDEntity
			query = new StringBuilder("delete from `media-dia` where type = 'AccountOALDEntity'");
			n1qlQueryDAO.executeQuery(query);

			// HistoryEntity
//            query = new StringBuilder("delete from `media-dia` where type = 'HistoryEntity'");
//			n1qlQueryDAO.executeQuery(query);

		} catch (Throwable e) {
			String message = "Error on cleaning CouchBase tables";
			LOG.error(message, e);
			throw new PersistenceException(message, e);
		}

	}
}
