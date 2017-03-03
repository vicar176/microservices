package com.mcmcg.dia.ingestionState.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mcmcg.dia.ingestionState.util.CustomDateSerializer;

@JsonAutoDetect
public class WorkflowShutdownStateModel {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private boolean shutdownState;
	
	private String updatedBy;
	
	private Date updateDate;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isShutdownState() {
		return shutdownState;
	}

	public void setShutdownState(boolean shutdownState) {
		this.shutdownState = shutdownState;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getUpdateDate() {
		return updateDate;
	}
	
}
