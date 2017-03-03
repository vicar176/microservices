package com.mcmcg.media.workflow.service.domain;

import java.util.List;

public class AccountOALDModel extends BaseDomain {
	
	
	private static final long serialVersionUID = 1L;

	
	private String id;
	private long accountNumber;
	private String originalAccountNumber;
	private long portfolioNumber;
	private List<MediaOald> oalds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getOriginalAccountNumber() {
		return originalAccountNumber;
	}

	public void setOriginalAccountNumber(String originalAccountNumber) {
		this.originalAccountNumber = originalAccountNumber;
	}

	public long getPortfolioNumber() {
		return portfolioNumber;
	}

	public void setPortfolioNumber(long portfolioNumber) {
		this.portfolioNumber = portfolioNumber;
	}

	public List<MediaOald> getOalds() {
		return oalds;
	}

	public void setOalds(List<MediaOald> oalds) {
		this.oalds = oalds;
	}

	public static class MediaOald extends BaseDomain {

		private static final long serialVersionUID = 1L;

		private String id;
		private Long documentId;
		private String receivedDate;
		private String oaldProfileId;
		private String documentNameString;
		private String originalDocumentType;
		private String translatedDocumentType;
		private String documentDate;
		private long oaldProfileVersion;
		private boolean oaldValidated;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getReceivedDate() {
			return receivedDate;
		}

		public void setReceivedDate(String receivedDate) {
			this.receivedDate = receivedDate;
		}

		public String getOaldProfileId() {
			return oaldProfileId;
		}

		public void setOaldProfileId(String oaldProfileId) {
			this.oaldProfileId = oaldProfileId;
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

		public String getOriginalDocumentType() {
			return originalDocumentType;
		}

		public void setOriginalDocumentType(String originalDocumentType) {
			this.originalDocumentType = originalDocumentType;
		}

		public String getTranslatedDocumentType() {
			return translatedDocumentType;
		}

		public void setTranslatedDocumentType(String translatedDocumentType) {
			this.translatedDocumentType = translatedDocumentType;
		}

		public String getDocumentDate() {
			return documentDate;
		}

		public void setDocumentDate(String documentDate) {
			this.documentDate = documentDate;
		}

		public long getOaldProfileVersion() {
			return oaldProfileVersion;
		}

		public void setOaldProfileVersion(long oaldProfileVersion) {
			this.oaldProfileVersion = oaldProfileVersion;
		}

		public boolean isOaldValidated() {
			return oaldValidated;
		}

		public void setOaldValidated(boolean oaldValidated) {
			this.oaldValidated = oaldValidated;
		}

	}
	}
