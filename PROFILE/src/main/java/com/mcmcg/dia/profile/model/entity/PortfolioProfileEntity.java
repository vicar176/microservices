package com.mcmcg.dia.profile.model.entity;


/**
 * 
 * @author Victor Arias
 *
 */
public class PortfolioProfileEntity {

	private String id;
	private Long automatedOrderingPeriod;
	private String sourceLocationProfile;
	private Long version;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getAutomatedOrderingPeriod() {
		return automatedOrderingPeriod;
	}

	public void setAutomatedOrderingPeriod(Long automatedOrderingPeriod) {
		this.automatedOrderingPeriod = automatedOrderingPeriod;
	}

	public String getSourceLocationProfile() {
		return sourceLocationProfile;
	}

	public void setSourceLocationProfile(String sourceLocationProfile) {
		this.sourceLocationProfile = sourceLocationProfile;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}
