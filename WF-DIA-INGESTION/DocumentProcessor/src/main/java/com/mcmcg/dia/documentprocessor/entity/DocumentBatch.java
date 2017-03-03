package com.mcmcg.dia.documentprocessor.entity;

public class DocumentBatch {

	private Long batchExecutionId;
	private String documentId;
	private String documentType;
	private String s3Folder;
	private String s3Bucket;
	private String awsS3Url;
	private String ingestionStatus;

	public DocumentBatch() {
	}

	public Long getBatchExecutionId() {
		return batchExecutionId;
	}

	public void setBatchExecutionId(Long batchExecutionId) {
		this.batchExecutionId = batchExecutionId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getS3Folder() {
		return s3Folder;
	}

	public void setS3Folder(String s3Forlder) {
		this.s3Folder = s3Forlder;
	}

	public String getAwsS3Url() {
		return awsS3Url;
	}

	public void setAwsS3Url(String awsS3Url) {
		this.awsS3Url = awsS3Url;
	}

	public String getIngestionStatus() {
		return ingestionStatus;
	}

	public void setIngestionStatus(String ingestionStatus) {
		this.ingestionStatus = ingestionStatus;
	}

	public String getS3Bucket() {
		return s3Bucket;
	}

	public void setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
	}

}
