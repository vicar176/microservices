package com.mcmcg.dia.profile.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author wPorras
 *
 */
@JsonInclude(Include.NON_NULL)
public class SampleFile extends BaseDomain {
	private static final long serialVersionUID = 1L;

	private String source;
	private String fileName;
	private int totalPages;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}	
}
