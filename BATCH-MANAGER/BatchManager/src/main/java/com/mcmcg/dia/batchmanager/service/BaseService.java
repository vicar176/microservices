package com.mcmcg.dia.batchmanager.service;

import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.mcmcg.dia.batchmanager.domain.BatchCreationOnDemand;
import com.mcmcg.dia.batchmanager.domain.JobStatus;
import com.mcmcg.dia.batchmanager.entity.BatchProfile;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJob;
import com.mcmcg.dia.batchmanager.entity.DocumentException;

public abstract class BaseService {

	public BaseService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param batchCreationDomain
	 * @return
	 */
	protected BatchProfile buildBatchProfileEntity(BatchCreationOnDemand batchCreationDomain) {
		BatchProfile batchProfile = null;

		if (batchCreationDomain != null) {
			batchProfile = new BatchProfile();
			batchProfile.setActionId(batchCreationDomain.getAction().getId());
			batchProfile.setCreationMethod(batchCreationDomain.getCreationMethod());
			batchProfile.setFileName(batchCreationDomain.getCsvFileName());
			batchProfile.setDescription(StringUtils.isNotBlank(batchCreationDomain.getDescription()) ? batchCreationDomain.getDescription() : " ");
			batchProfile.setName(batchCreationDomain.getName());
			batchProfile.setStatus(1);//Active
			batchProfile.setApplicationId(1);
			batchProfile.setChangedBy(batchCreationDomain.getUser());
			batchProfile.setCreatedBy(batchCreationDomain.getUser());
			batchProfile.setCreatedOnDate(new Date());
			batchProfile.setChangedOnDate(new Date());
			batchProfile.setScheduleTime(null);
			batchProfile.setScheduleDate(null);
			batchProfile.setFrecuency(null);
			batchProfile.setWorkstationName(" ");
			batchProfile.setBatchType("On Demand");
			batchProfile.setVersion(1);
		}
		return batchProfile;
	}
	
	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	protected BatchProfileJob populateBatchProfileJob(Long batchProfileKey, long total, String user) throws SQLException {
		BatchProfileJob batchProfile = new BatchProfileJob();
		batchProfile.setBatchProfileId(batchProfileKey);
		batchProfile.setDocumentsFailed(0L);
		batchProfile.setDocumentsInException(0L);
		batchProfile.setDocumentsProcessed(0L);
		batchProfile.setJobStatusId(JobStatus.currentlyProcessing.getId());
		batchProfile.setTotalDocumentsToBeProcessed(total);
		batchProfile.setDeleteTempDataAfter(30);

		batchProfile.setExecutionStartDateTime(new Date());
		batchProfile.setExecutionEndDateTime(new Date());
		
		batchProfile.setChangedBy(user);
		batchProfile.setCreatedBy(user);
		batchProfile.setChangedOnDate(new Date());
		batchProfile.setCreatedOnDate(new Date());
		batchProfile.setApplicationId(1);
		batchProfile.setWorkstationName(" ");
		batchProfile.setVersion(1);
		
		return batchProfile;
	}
	
	/**
	 * 
	 * @param documents
	 * @return
	 */
	protected DocumentException populateDocumentException(long batchProfileJobId, String documentId, String user, 
														  int statusId, String errorDescription){
		
		DocumentException documentException = new DocumentException();
		
		documentException.setBatchProfileJobId(batchProfileJobId);
		documentException.setDocumentId(documentId);

		documentException.setApplicationId(1);
		documentException.setChangedBy(user);
		documentException.setChangedOnDate(new Date());
		documentException.setCreatedBy(user);
		documentException.setCreatedOnDate(new Date());
		documentException.setErrorDescription(errorDescription);
		documentException.setStatus(statusId);
		documentException.setWorkstationName("");
		
		return documentException;
	}
}
