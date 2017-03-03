package com.mcmcg.dia.profile.model.domain;

import java.util.Set;

/**
 * 
 * @author wporras
 *
 */
public class OriginalLenderList {

	private Set<String> originalLenders;

	public OriginalLenderList(Set<String> originalLenders) {
		super();
		this.originalLenders = originalLenders;
	}

	public OriginalLenderList() {
		
	}
	public Set<String> getOriginalLenders() {
		return originalLenders;
	}

	public void setOriginalLenders(Set<String> originalLenders) {
		this.originalLenders = originalLenders;
	}

}
