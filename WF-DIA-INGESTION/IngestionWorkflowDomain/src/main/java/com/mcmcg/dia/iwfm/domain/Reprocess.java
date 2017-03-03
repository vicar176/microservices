package com.mcmcg.dia.iwfm.domain;

/**
 * 
 * @author wporras
 *
 */
public class Reprocess {

	private String bucketName;
	private String documentId;
	private String documentNameString;
	private Long batchProfileJobId;

	public Reprocess() {
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocumentNameString() {
		return documentNameString;
	}

	public void setDocumentNameString(String documentNameString) {
		this.documentNameString = documentNameString;
	}

	public Long getBatchProfileJobId() {
		return batchProfileJobId;
	}

	public void setBatchProfileJobId(Long batchProfileJobId) {
		this.batchProfileJobId = batchProfileJobId;
	}

}
