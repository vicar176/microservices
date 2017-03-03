package com.mcmcg.dia.batchmanager.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mcmcg.dia.batchmanager.util.CustomDateDeserializer;
import com.mcmcg.dia.batchmanager.util.CustomDateSerializer;

/**
 * @author Victor Arias
 *
 */
@JsonAutoDetect
public class BatchProfile extends BaseEntity {

	private Long batchProfileId;
	private Long actionId;
	private String name;
	private String description;
	private String batchType;
	private String creationMethod;
	private String fileName;
	private Long frecuencyId;
	private Integer status;
	private Date scheduleDate;
	private Date scheduleTime;
	private Integer version;

	public Long getBatchProfileId() {
		return batchProfileId;
	}

	public void setBatchProfileId(Long batchProfileId) {
		this.batchProfileId = batchProfileId;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public String getCreationMethod() {
		return creationMethod;
	}

	public void setCreationMethod(String creationMethod) {
		this.creationMethod = creationMethod;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFrecuency() {
		return frecuencyId;
	}

	public void setFrecuency(Long frecuencyId) {
		this.frecuencyId = frecuencyId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getScheduleDate() {
		return scheduleDate;
	}

	@JsonDeserialize(using = CustomDateDeserializer.class)
	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getScheduleTime() {
		return scheduleTime;
	}

	@JsonDeserialize(using = CustomDateDeserializer.class)
	public void setScheduleTime(Date scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
