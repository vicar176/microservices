package com.mcmcg.dia.batchmanager.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.mcmcg.dia.batchmanager.entity.BatchProfileJobDetails;

public class BatchProfileJobDetailsRowMapper implements RowMapper<BatchProfileJobDetails> {

	@Override
	public BatchProfileJobDetails mapRow(ResultSet rs, int rowNum) throws SQLException {

		BatchProfileJobDetails batchProfileJobDetails = new BatchProfileJobDetails();

		batchProfileJobDetails.getTotalDocumentsToBeProcessed().setCount(rs.getLong("TotalDocumentsToBeProcessed"));
		batchProfileJobDetails.getTotalDocumentsToBeProcessed().setStatus(("Total"));
		batchProfileJobDetails.getDocumentsNotFound().setCount(rs.getLong("NotFound"));
		batchProfileJobDetails.getDocumentsNotFound().setStatus("NotFound");
		batchProfileJobDetails.getDocumentsProcessed().setCount(rs.getLong("Processed"));
		batchProfileJobDetails.getDocumentsProcessed().setStatus("Processed");
		batchProfileJobDetails.getDocumentsProcessing().setCount(rs.getLong("Processing"));
		batchProfileJobDetails.getDocumentsProcessing().setStatus("Processing");
		batchProfileJobDetails.getDocumentsInException().setCount(rs.getLong("Exception"));
		batchProfileJobDetails.getDocumentsInException().setStatus("Exception");
		batchProfileJobDetails.getDocumentsFailed().setCount(rs.getLong("Failed"));
		batchProfileJobDetails.getDocumentsFailed().setStatus("Failed");
		batchProfileJobDetails.setExecutionStartTime(rs.getDate("ExecutionStartDateTime"));
		batchProfileJobDetails.setExecutionEndTime(rs.getDate("ExecutionEndDateTime"));
		batchProfileJobDetails.setCompletionTime(rs.getString("TotalTime"));
		batchProfileJobDetails.setCompletionTimeinMilliSeconds(rs.getLong("TotalTimeInMiliSecond"));

		return batchProfileJobDetails;
	}

}
