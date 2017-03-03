package com.mcmcg.dia.batchmanager.domain;

import com.mcmcg.dia.batchmanager.entity.Action;
import com.mcmcg.dia.batchmanager.entity.BatchProfile;

public class BatchProfileWithAction {

	private BatchProfile batchProfile;
	private Action action;
	
	public BatchProfileWithAction() {
		// TODO Auto-generated constructor stub
	}

	public BatchProfile getBatchProfile() {
		return batchProfile;
	}

	public void setBatchProfile(BatchProfile batchProfile) {
		this.batchProfile = batchProfile;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	
}
