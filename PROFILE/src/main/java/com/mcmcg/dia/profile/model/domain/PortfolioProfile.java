package com.mcmcg.dia.profile.model.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author varias
 *
 */

public class PortfolioProfile {
	
	private Long id;
	private String automatedOrderingPeriod;
	private List<SourceLocationProfile> sourceLocationProfile = new ArrayList<SourceLocationProfile>();

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getAutomatedOrderingPeriod() {
		return automatedOrderingPeriod;
	}

	public void setAutomatedOrderingPeriod(String automatedOrderingPeriod) {
		this.automatedOrderingPeriod = automatedOrderingPeriod;
	}

	public List<SourceLocationProfile> getSourceLocationProfile() {
		return sourceLocationProfile;
	}

	public void setSourceLocationProfile(List<SourceLocationProfile> sourceLocationProfile) {
		this.sourceLocationProfile = sourceLocationProfile;
	}
}
