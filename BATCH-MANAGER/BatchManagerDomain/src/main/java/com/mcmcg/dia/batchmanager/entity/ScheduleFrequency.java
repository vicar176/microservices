package com.mcmcg.dia.batchmanager.entity;

public class ScheduleFrequency extends BaseEntity {

	public Long frequencyId;
	public String name;
	public String description;

	public Long getFrequencyId() {
		return frequencyId;
	}

	public void setFrequencyId(Long frequencyId) {
		this.frequencyId = frequencyId;
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
}
