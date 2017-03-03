package com.mcmcg.dia.batchmanager.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.batchmanager.entity.BatchProfileJobDetails;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJobDocumentDetail;
import com.mcmcg.dia.batchmanager.exception.PersistenceException;
import com.mcmcg.dia.batchmanager.util.FilteringQueryUtils;

/**
 * @author Victor Arias
 *
 */
@Repository
public class FilteringDAO {

	private static final Logger LOG = Logger.getLogger(FilteringDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Resource(name = "queriesMap")
	private Map<String, String> queriesMap;

	private Map<String, String> filterValues;
	private Map<String, String> filterScheduledBatcheValues;
	private Map<String, String> filterBatchStatusDocumentDetailValues;

	private static final String BATCHPROFILE_JOB_STATUS_FILTERING = "BATCHPROFILE_JOB_STATUS_FILTERING";
	private static final String BATCHPROFILE_JOB_STATUS_FILTERING_COUNT = "BATCHPROFILE_JOB_STATUS_FILTERING_COUNT";

	private static final String SCHEDULED_BATCHES_FILTERING = "SCHEDULED_BATCHES_FILTERING";
	private static final String SCHEDULED_BATCHES_FILTERING_COUNT = "SCHEDULED_BATCHES_FILTERING_COUNT";

	private static final String BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_FILTERING = "BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_FILTERING";
	private static final String BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_FILTERING_COUNT = "BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_FILTERING_COUNT";
	private static final String BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_NOTFOUND_FILTERING = "BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_NOTFOUND_FILTERING";
	private static final String BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_NOTFOUND_FILTERING_COUNT = "BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_NOTFOUND_FILTERING_COUNT";
	private static final String BATCH_STATUS_DETAILS_FILTERING = "BATCH_STATUS_DETAILS_FILTERING";

	public FilteringDAO() {
		filterValues = new HashMap<String, String>();

		// for batchStatus
		filterValues.put("executionStartDateTime", "BatchProfileJob.ExecutionStartDateTime");
		filterValues.put("name", "BatchProfile.name");
		filterValues.put("actionDescription", "Action.ActionDescription");
		filterValues.put("batchType", "BatchProfile.BatchType");
		filterValues.put("status", "JOBSTATUS.Description");
		filterValues.put("version", "BatchProfileJob.Version");

		// for scheduled batches
		filterScheduledBatcheValues = new HashMap<String, String>();
		filterScheduledBatcheValues.put("name", "BatchProfile.Name");
		filterScheduledBatcheValues.put("description", "BatchProfile.Description");
		filterScheduledBatcheValues.put("actionDescription", "Action.ActionDescription");
		filterScheduledBatcheValues.put("status", "BatchProfile.Status");
		filterScheduledBatcheValues.put("frequency", "ScheduleFrequency.Name");
		filterScheduledBatcheValues.put("executionStartDateTime", "BatchProfileJobLatest.ExecutionStartDateTime");
		filterScheduledBatcheValues.put("version", "BatchProfile.Version");
		filterScheduledBatcheValues.put("changedBy", "BatchProfile.ChangedBy");

		// for filteringBatchStatusJobDocumentDetail
		filterBatchStatusDocumentDetailValues = new HashMap<String, String>();
		filterBatchStatusDocumentDetailValues.put("batchProfileJobId", "BatchProfileJob.batchProfileJobId");
		filterBatchStatusDocumentDetailValues.put("documentId", "DocumentException.documentId");
		filterBatchStatusDocumentDetailValues.put("type", "Document_Status.document_status_code");
		filterBatchStatusDocumentDetailValues.put("description", "DocumentException.errorDescription");
		filterBatchStatusDocumentDetailValues.put("bucketName", "Document_Images.s3_bucket");
		filterBatchStatusDocumentDetailValues.put("awsS3Url", "Document_Images.Aws_S3_Url");
		filterBatchStatusDocumentDetailValues.put("s3Folder", "Document_Images.S3_Folder");
		filterBatchStatusDocumentDetailValues.put("step", "Workflow_State.Ingestion_State_Code");

	}

	public List<Map<String, Object>> filteringBatchProfile(String filter, String sort, int page, int pageSize)
			throws PersistenceException {

		String query = queriesMap.get(BATCHPROFILE_JOB_STATUS_FILTERING);
		String groupBy = StringUtils.EMPTY;
		return filtering("BatchStatus", query, groupBy, filter, sort, page, pageSize);
	}

	public int filteringCountBatchProfile(String filter) throws PersistenceException {

		String query = queriesMap.get(BATCHPROFILE_JOB_STATUS_FILTERING_COUNT);
		String groupBy = StringUtils.EMPTY;
		return filteringCount("BatchStatus", query, groupBy, filter);
	}

	public List<Map<String, Object>> filtering(String filterPage, String query, String groupByClause, String filter,
			String sort, int page, int pageSize) throws PersistenceException {

		String filterClause = null;
		String sortingClause = null;

		if ("BatchStatus".equals(filterPage)) {
			filterClause = FilteringQueryUtils.buildFilterClause(filter, filterValues);
			sortingClause = FilteringQueryUtils.buildSortingClause(sort, filterValues);
		}
		if ("ScheduledBatches".equals(filterPage)) {
			filterClause = FilteringQueryUtils.buildFilterClause(filter, filterScheduledBatcheValues);
			sortingClause = FilteringQueryUtils.buildSortingClause(sort, filterScheduledBatcheValues);
		}
		String limitClause = FilteringQueryUtils.buildLimitClause(page, pageSize);

		String finalQuery = query + filterClause + groupByClause + sortingClause + limitClause;
		LOG.debug(String.format("Filtering query: \n %s", finalQuery));
		List<Map<String, Object>> results = jdbcTemplate.queryForList(finalQuery);
		return results;
	}

	public int filteringCount(String filterPage, String query, String groupByClause, String filter)
			throws PersistenceException {
		Integer count = 0;
		try {
			String filterClause = null;
			if ("BatchStatus".equals(filterPage)) {
				filterClause = FilteringQueryUtils.buildFilterClause(filter, filterValues);
			}

			if ("ScheduledBatches".equals(filterPage)) {
				filterClause = FilteringQueryUtils.buildFilterClause(filter, filterScheduledBatcheValues);
			}
			String finalQuery = query + filterClause + groupByClause;
			count = jdbcTemplate.queryForObject(finalQuery, Integer.class);
		} catch (Throwable t) {
			String message = StringUtils.EMPTY;
			if (!StringUtils.isBlank(t.getMessage())) {
				message = t.getMessage();
			} else {
				message = "An error occurred while trying to count the Failed Ingeston Steps";
			}
			LOG.error(message);
			throw new PersistenceException(message);
		}
		return count;
	}

	public List<Map<String, Object>> filteringScheduledBatches(String filter, String sort, int page, int pageSize)
			throws PersistenceException {

		String query = queriesMap.get(SCHEDULED_BATCHES_FILTERING);
		String groupBy = StringUtils.EMPTY;
		return filtering("ScheduledBatches", query, groupBy, filter, sort, page, pageSize);
	}

	public int filteringCountScheduledBatches(String filter) throws PersistenceException {

		String query = queriesMap.get(SCHEDULED_BATCHES_FILTERING_COUNT);
		String groupBy = StringUtils.EMPTY;
		return filteringCount("ScheduledBatches", query, groupBy, filter);
	}

	public List<BatchProfileJobDocumentDetail> filteringBatchJobStatusDocumentDetail(String filter, String sort,
			int page, int pageSize, String batchProfileJobId, final String status) throws PersistenceException {

		String filterClause = "";
		String sortingClause = "";
		String limitClause = "";
		String query = "";

		if (status.equals("NotFound")) {
			query = queriesMap.get(BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_NOTFOUND_FILTERING);
			sortingClause = FilteringQueryUtils.buildSortingClause(sort, filterBatchStatusDocumentDetailValues);

		} else {
			query = queriesMap.get(BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_FILTERING);
			filterClause = FilteringQueryUtils.buildFilterClause(filter, filterBatchStatusDocumentDetailValues)
					.replace("WHERE", "AND");
			limitClause = FilteringQueryUtils.buildLimitClause(page, pageSize);
			sortingClause = FilteringQueryUtils.buildSortingClause(sort, filterBatchStatusDocumentDetailValues);
		}
		String finalQuery = query + filterClause + sortingClause + limitClause;
		LOG.debug(String.format("query [%s] ", finalQuery));
		List<BatchProfileJobDocumentDetail> batchProfileJobDocumentDetailList = new ArrayList<BatchProfileJobDocumentDetail>();

		try {
			PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(finalQuery, batchProfileJobId,
					status);
			batchProfileJobDocumentDetailList = jdbcTemplate.query(psCreator,
					new ResultSetExtractor<List<BatchProfileJobDocumentDetail>>() {

						@Override
						public List<BatchProfileJobDocumentDetail> extractData(final ResultSet rs)
								throws SQLException, DataAccessException {
							List<BatchProfileJobDocumentDetail> batchProfileJobDocumentDetailList = new ArrayList<BatchProfileJobDocumentDetail>();

							while (rs.next()) {
								batchProfileJobDocumentDetailList.add(populateFrom(rs, status));
							}

							return batchProfileJobDocumentDetailList;
						}

					});

		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on filteringBatchJobStatusDocumentDetail()" + ex.getMessage(), ex);
		}
		return batchProfileJobDocumentDetailList;

	}

	public int filteringCountBatchJobStatusDocumentDetail(String filter, String batchProfileJobId, String status)
			throws PersistenceException {
		String filterClause = "";
		String query = "";

		if (status.equals("NotFound")) {
			query = queriesMap.get(BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_NOTFOUND_FILTERING_COUNT);
		} else {
			query = queriesMap.get(BATCHPROFILE_JOB_STATUS_DOCUMENT_DETAIL_FILTERING_COUNT);
			filterClause = FilteringQueryUtils.buildFilterClause(filter, filterBatchStatusDocumentDetailValues)
					.replace("WHERE", "AND");
		}
		String finalQuery = query + filterClause;
		LOG.debug(String.format("query [%s] ", finalQuery));

		return jdbcTemplate.queryForObject(finalQuery, new Object[] { batchProfileJobId, status }, Integer.class);

	}

	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private BatchProfileJobDocumentDetail populateFrom(ResultSet rs, String status) throws SQLException {
		/**
		 * BatchProfileJobId, DocumentId, DocumentStatusId,
		 * ErrorDescription,CreatedOnDate,CreatedBy,ChangedOnDate,ChangedBy,
		 * WorkstationName,ApplicationId
		 */
		BatchProfileJobDocumentDetail batchProfileJobDocumentDetail = new BatchProfileJobDocumentDetail();
		if (rs != null) {
			if (status.equals("NotFound")) {
				batchProfileJobDocumentDetail.setDocumentId(rs.getLong("documentId"));
			} else {
				batchProfileJobDocumentDetail.setDocumentId(rs.getLong("documentId"));
				batchProfileJobDocumentDetail.setStep(rs.getString("Ingestion_State_Code"));
				batchProfileJobDocumentDetail.setType(rs.getString("document_status_code"));
				batchProfileJobDocumentDetail.setDescription(rs.getString("errorDescription"));
				batchProfileJobDocumentDetail.setBucketName(rs.getString("s3_bucket"));
				batchProfileJobDocumentDetail
						.setDocumentNameString(rs.getString("S3_Folder") + "/" + rs.getString("Aws_S3_Url"));
			}

		}
		return batchProfileJobDocumentDetail;
	}

	public List<BatchProfileJobDetails> filteringBatchStatusDetails(String batchProfileJobId)
			throws PersistenceException {

		String filterClause = "";
		String sortingClause = "";
		String limitClause = "";
		String query = "";

		query = queriesMap.get(BATCH_STATUS_DETAILS_FILTERING);

		String finalQuery = query + filterClause + sortingClause + limitClause;
		LOG.debug(String.format("query [%s] ", finalQuery));
		List<BatchProfileJobDetails> batchProfileJobDetailList = new ArrayList<BatchProfileJobDetails>();
		BatchProfileJobDetails batchProfileJobDetails = new BatchProfileJobDetails();
		try {

			batchProfileJobDetails = jdbcTemplate.queryForObject(query,
					new Object[] { batchProfileJobId, batchProfileJobId, batchProfileJobId, batchProfileJobId,
							batchProfileJobId, batchProfileJobId, batchProfileJobId },
					new BatchProfileJobDetailsRowMapper());
			String completionTime = batchProfileJobDetails.getCompletionTime() + "  hr";
			batchProfileJobDetails.setCompletionTime(completionTime);
			batchProfileJobDetails.setBatchProfileJobId(Long.parseLong(batchProfileJobId));
			batchProfileJobDetailList.add(batchProfileJobDetails);

		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on filteringBatchStatusDetails()" + ex.getMessage(), ex);
		}
		return batchProfileJobDetailList;

	}

}
