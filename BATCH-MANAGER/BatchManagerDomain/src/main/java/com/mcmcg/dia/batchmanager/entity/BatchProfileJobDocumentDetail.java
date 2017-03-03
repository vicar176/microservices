package com.mcmcg.dia.batchmanager.entity;

public class BatchProfileJobDocumentDetail {
	
	private String bucketName;
	private Long documentId;
	private String documentNameString;
	private String step;
	private String type;
	private String description;
//	private String s3Bucket;
//	private String awsS3Url;
//	private String s3Folder;
	
		
//	public String getS3Bucket() {
//		return s3Bucket;
//	}
//	public void setS3Bucket(String s3Bucket) {
//		this.s3Bucket = s3Bucket;
//	}
//	public String getAwsS3Url() {
//		return awsS3Url;
//	}
//	public void setAwsS3Url(String awsS3Url) {
//		this.awsS3Url = awsS3Url;
//	}
//	public String getS3Folder() {
//		return s3Folder;
//	}
//	public void setS3Folder(String s3Folder) {
//		this.s3Folder = s3Folder;
//	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
	public String getDocumentNameString() {
		return documentNameString;
	}
	public void setDocumentNameString(String documentNameString) {
		this.documentNameString = documentNameString;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}

