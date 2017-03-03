package com.mcmcg.dia.documentprocessor.entity;

/**
 * 
 * @author wporras
 *
 */
public class IngestionTrackerEntity extends BaseModel {

	private Long ingestionTrackerId;
	private String documentId;
	private Long batchExecutionId;
	private String documentStatusCode;

	public IngestionTrackerEntity() {
		super();
	}

	public IngestionTrackerEntity(String documentId, Long batchExecutionId, String documentStatusCode,
			String changedBy) {
		super(changedBy);
		this.documentId = documentId;
		this.batchExecutionId = batchExecutionId;
		this.documentStatusCode = documentStatusCode;
	}

	public Long getIngestionTrackerId() {
		return ingestionTrackerId;
	}

	public void setIngestionTrackerId(Long ingestionTrackerId) {
		this.ingestionTrackerId = ingestionTrackerId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public Long getBatchExecutionId() {
		return batchExecutionId;
	}

	public void setBatchExecutionId(Long batchExecutionId) {
		this.batchExecutionId = batchExecutionId;
	}

	public String getDocumentStatusCode() {
		return documentStatusCode;
	}

	public void setDocumentStatusCode(String documentStatusCode) {
		this.documentStatusCode = documentStatusCode;
	}

}
