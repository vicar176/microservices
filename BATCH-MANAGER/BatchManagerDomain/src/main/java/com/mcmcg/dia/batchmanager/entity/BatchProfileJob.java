package com.mcmcg.dia.batchmanager.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mcmcg.dia.batchmanager.util.CustomDateDeserializer;
import com.mcmcg.dia.batchmanager.util.CustomDateSerializer;

/**
 * @author Victor Arias
 *
 */
public class BatchProfileJob extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private Long batchProfileJobId;
	private Long batchProfileId;
	private Date executionStartDateTime;
	private Date executionEndDateTime;
	private int jobStatusId;
	private Long totalDocumentsToBeProcessed;
	private Long documentsProcessed;
	private Long documentsInException;
	private Long documentsFailed;
	private int deleteTempDataAfter;
	private int version;

	public Long getBatchProfileJobId() {
		return batchProfileJobId;
	}

	public void setBatchProfileJobId(Long batchProfileJobId) {
		this.batchProfileJobId = batchProfileJobId;
	}

	public Long getBatchProfileId() {
		return batchProfileId;
	}

	public void setBatchProfileId(Long batchProfileId) {
		this.batchProfileId = batchProfileId;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getExecutionStartDateTime() {
		return executionStartDateTime;
	}

	@JsonDeserialize(using = CustomDateDeserializer.class)
	public void setExecutionStartDateTime(Date executionStartDateTime) {
		this.executionStartDateTime = executionStartDateTime;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getExecutionEndDateTime() {
		return executionEndDateTime;
	}

	@JsonDeserialize(using = CustomDateDeserializer.class)
	public void setExecutionEndDateTime(Date executionEndDateTime) {
		this.executionEndDateTime = executionEndDateTime;
	}

	public int getJobStatusId() {
		return jobStatusId;
	}

	public void setJobStatusId(int jobStatusId) {
		this.jobStatusId = jobStatusId;
	}

	public Long getTotalDocumentsToBeProcessed() {
		return totalDocumentsToBeProcessed;
	}

	public void setTotalDocumentsToBeProcessed(Long totalDocumentsToBeProcessed) {
		this.totalDocumentsToBeProcessed = totalDocumentsToBeProcessed;
	}

	public Long getDocumentsProcessed() {
		return documentsProcessed;
	}

	public void setDocumentsProcessed(Long documentsProcessed) {
		this.documentsProcessed = documentsProcessed;
	}

	public Long getDocumentsInException() {
		return documentsInException;
	}

	public void setDocumentsInException(Long documentsInException) {
		this.documentsInException = documentsInException;
	}

	public Long getDocumentsFailed() {
		return documentsFailed;
	}

	public void setDocumentsFailed(Long documentsFailed) {
		this.documentsFailed = documentsFailed;
	}

	public int getDeleteTempDataAfter() {
		return deleteTempDataAfter;
	}

	public void setDeleteTempDataAfter(int deleteTempDataAfter) {
		this.deleteTempDataAfter = deleteTempDataAfter;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
