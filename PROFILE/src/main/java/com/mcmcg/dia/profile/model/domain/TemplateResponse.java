package com.mcmcg.dia.profile.model.domain;

import java.util.List;
import java.util.Set;

import com.mcmcg.dia.profile.model.TemplateMappingProfileModel.ReferenceArea;
import com.mcmcg.dia.profile.model.TemplateMappingProfileModel.ZoneMapping;

public class TemplateResponse extends BaseDomain {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String sampleFileName;
	private int totalPages;
	private Set<String> affinities;
	private List<ReferenceArea> referenceAreas;
	private List<ZoneMapping> zoneMappings;

	public Set<String> getAffinities() {
		return affinities;
	}

	public void setAffinities(Set<String> affinities) {
		this.affinities = affinities;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSampleFileName() {
		return sampleFileName;
	}

	public void setSampleFileName(String sampleFileName) {
		this.sampleFileName = sampleFileName;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public List<ReferenceArea> getReferenceAreas() {
		return referenceAreas;
	}

	public void setReferenceAreas(List<ReferenceArea> referenceAreas) {
		this.referenceAreas = referenceAreas;
	}

	public List<ZoneMapping> getZoneMappings() {
		return zoneMappings;
	}

	public void setZoneMappings(List<ZoneMapping> zoneMappings) {
		this.zoneMappings = zoneMappings;
	}
}
