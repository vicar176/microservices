package com.mcmcg.dia.batchmanager.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mcmcg.dia.batchmanager.util.CustomDateDeserializer;
import com.mcmcg.dia.batchmanager.util.CustomDateSerializer;

public class BatchProfileJobDetails  {

	private static final long serialVersionUID = 1L;

	private Long batchProfileJobId;
	private Long batchProfileId;
	private Date executionStartTime;
	private Date executionEndTime;
	private String completionTime;
	private Long completionTimeinMilliSeconds;
	private BatchProfileJobDetailsTO totalDocumentsToBeProcessed;
	private BatchProfileJobDetailsTO documentsNotFound;
	private BatchProfileJobDetailsTO documentsProcessed;
	private BatchProfileJobDetailsTO documentsProcessing;
	private BatchProfileJobDetailsTO documentsInException;
	private BatchProfileJobDetailsTO documentsFailed;
	
	public BatchProfileJobDetails(){
		this.totalDocumentsToBeProcessed=new BatchProfileJobDetailsTO();
		this.documentsNotFound=new BatchProfileJobDetailsTO();
		this.documentsProcessed=new BatchProfileJobDetailsTO();
		this.documentsProcessing=new BatchProfileJobDetailsTO();
		this.documentsFailed=new BatchProfileJobDetailsTO();
		this.documentsInException=new BatchProfileJobDetailsTO();
	}
	
	
	
	public Date getExecutionStartTime() {
		return executionStartTime;
	}



	public void setExecutionStartTime(Date executionStartTime) {
		this.executionStartTime = executionStartTime;
	}



	public Date getExecutionEndTime() {
		return executionEndTime;
	}



	public void setExecutionEndTime(Date executionEndTime) {
		this.executionEndTime = executionEndTime;
	}



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
	public String getCompletionTime() {
		return completionTime;
	}
	public void setCompletionTime(String completionTime) {
		this.completionTime = completionTime;
	}
	public BatchProfileJobDetailsTO getTotalDocumentsToBeProcessed() {
		return totalDocumentsToBeProcessed;
	}
	public void setTotalDocumentsToBeProcessed(BatchProfileJobDetailsTO totalDocumentsToBeProcessed) {
		this.totalDocumentsToBeProcessed = totalDocumentsToBeProcessed;
	}
	public BatchProfileJobDetailsTO getDocumentsNotFound() {
		return documentsNotFound;
	}
	public void setDocumentsNotFound(BatchProfileJobDetailsTO documentsNotFound) {
		this.documentsNotFound = documentsNotFound;
	}
	public BatchProfileJobDetailsTO getDocumentsProcessed() {
		return documentsProcessed;
	}
	public void setDocumentsProcessed(BatchProfileJobDetailsTO documentsProcessed) {
		this.documentsProcessed = documentsProcessed;
	}
	public BatchProfileJobDetailsTO getDocumentsProcessing() {
		return documentsProcessing;
	}
	public void setDocumentsProcessing(BatchProfileJobDetailsTO documentsProcessing) {
		this.documentsProcessing = documentsProcessing;
	}
	public BatchProfileJobDetailsTO getDocumentsInException() {
		return documentsInException;
	}
	public void setDocumentsInException(BatchProfileJobDetailsTO documentsInException) {
		this.documentsInException = documentsInException;
	}
	public BatchProfileJobDetailsTO getDocumentsFailed() {
		return documentsFailed;
	}
	public void setDocumentsFailed(BatchProfileJobDetailsTO documentsFailed) {
		this.documentsFailed = documentsFailed;
	}

	public Long getCompletionTimeinMilliSeconds() {
		return completionTimeinMilliSeconds;
	}

	public void setCompletionTimeinMilliSeconds(Long completionTimeinMilliSeconds) {
		this.completionTimeinMilliSeconds = completionTimeinMilliSeconds;
	}
	
	
	
	

}
