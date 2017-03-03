/**
 * 
 */
package com.mcmcg.dia.iwfm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author jaleman
 *
 */
public class MediaDocument extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String originalAccountNumber ;
	
	private String documentType;
	
	private Long documentId;
	
	private Long accountNumber;
	
	private String year;
	
	private String month; 
	
	private String filename;
	
	private String documentDate;
	
	private String folder;
	
	private String bucket;
	
	private int batchProfileJobId;
	
	/**
	 * 
	 */
	public MediaDocument() {
		// TODO Auto-generated constructor stub
	}

	public String getOriginalAccountNumber() {
		return originalAccountNumber;
	}

	public void setOriginalAccountNumber(String originalAccountNumber) {
		this.originalAccountNumber = originalAccountNumber;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String documentName) {
		this.filename = documentName;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public int getBatchProfileJobId() {
		return batchProfileJobId;
	}

	public void setBatchProfileJobId(int batchProfileJobId) {
		this.batchProfileJobId = batchProfileJobId;
	}

}

