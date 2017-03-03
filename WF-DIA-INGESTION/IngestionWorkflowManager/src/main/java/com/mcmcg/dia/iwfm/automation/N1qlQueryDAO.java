package com.mcmcg.dia.iwfm.automation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.consistency.ScanConsistency;

/**
 * @author Jose Aleman
 *
 */
@Repository
public class N1qlQueryDAO {

	private static final Logger LOG = Logger.getLogger(N1qlQueryDAO.class);

	@Autowired
	private Bucket couchbaseBucket;

	public N1qlQueryDAO() {
	}

	/**
	 * @param results
	 * @param queryBuilder
	 */
	public List<JsonObject> executeQuery(StringBuilder queryBuilder) {

		N1qlParams ryow = N1qlParams.build().consistency(ScanConsistency.STATEMENT_PLUS);
		N1qlQuery n1qlQuery = N1qlQuery.simple(queryBuilder.toString(), ryow);
		N1qlQueryResult result = couchbaseBucket.query(n1qlQuery);
		List<JsonObject> results = null;
		if (result != null && result.finalSuccess()) {
			results = new ArrayList<JsonObject>();
			for (N1qlQueryRow row : result) {
				results.add(row.value());
				LOG.debug(row.value());
			}

		}

		return results;
	}

}
