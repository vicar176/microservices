package com.mcmcg.utility.domain;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * 
 * @author Victor Arias
 *
 */

public class MediaMetadataModel extends BaseDomain {

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String documentId;
	
	private Long  batchProfileJobId;
	
	private long accountNumber;
	
	private long portfolioNumber;
	
	private Seller seller;
	
	private String originalLenderName;
	
	private String documentStatus;
	
	private DocumentType originalDocumentType;
	
	private String documentDate;
	
	private ManualAccountVerification manualAccountVerification;
	
	private AccountVerification accountVerification;
	
	private Receive receive;
	
	private PdfTagging pdfTagging;
	
	private Extraction extraction;
	
	private AutoValidation autoValidation;
	
	private ManualValidation manualValidation;
	
	private Document document;
	
	private List<DataElement> dataElements;

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public long getPortfolioNumber() {
		return portfolioNumber;
	}

	public void setPortfolioNumber(long portfolioNumber) {
		this.portfolioNumber = portfolioNumber;
	}

	public String getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(String documentStatus) {
		this.documentStatus = documentStatus;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public Extraction getExtraction() {
		return extraction;
	}

	public void setExtraction(Extraction extraction) {
		this.extraction = extraction;
	}

	public AutoValidation getAutoValidation() {
		return autoValidation;
	}

	public void setAutoValidation(AutoValidation autoValidation) {
		this.autoValidation = autoValidation;
	}

	public ManualValidation getManualValidation() {
		return manualValidation;
	}

	public void setManualValidation(ManualValidation manualValidation) {
		this.manualValidation = manualValidation;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public List<DataElement> getDataElements() {
		return dataElements;
	}

	public void setDataElements(List<DataElement> dataElements) {
		this.dataElements = dataElements;
	}

	public PdfTagging getPdfTagging() {
		return pdfTagging;
	}

	public void setPdfTagging(PdfTagging pdfTagging) {
		this.pdfTagging = pdfTagging;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public String getOriginalLenderName() {
		return originalLenderName;
	}

	public void setOriginalLenderName(String originalLenderName) {
		this.originalLenderName = originalLenderName;
	}

	public DocumentType getOriginalDocumentType() {
		return originalDocumentType;
	}

	public void setOriginalDocumentType(DocumentType originalDocumentType) {
		this.originalDocumentType = originalDocumentType;
	}

	public ManualAccountVerification getManualAccountVerification() {
		return manualAccountVerification;
	}

	public void setManualAccountVerification(ManualAccountVerification manualAccountVerification) {
		this.manualAccountVerification = manualAccountVerification;
	}

	public AccountVerification getAccountVerification() {
		return accountVerification;
	}

	public void setAccountVerification(AccountVerification accountVerification) {
		this.accountVerification = accountVerification;
	}

	public Receive getReceive() {
		return receive;
	}

	public void setReceive(Receive receive) {
		this.receive = receive;
	}

	public Long getBatchProfileJobId() {
		return batchProfileJobId;
	}

	public void setBatchProfileJobId(Long batchProfileJobId) {
		this.batchProfileJobId = batchProfileJobId;
	}

	public static class ManualValidation implements Serializable {

		private static final long serialVersionUID = 1L;

		private boolean manulValidated;
		private String manualValidatedDate;
		private String manualValidatedBy;

		public ManualValidation() {
		}

		public ManualValidation(boolean manualValidated, String manualValidatedDate, String manualValidatedBy) {
			this.manulValidated = manualValidated;
			this.manualValidatedDate = manualValidatedDate;
			this.manualValidatedBy = manualValidatedBy;
		}

		public boolean getManulValidated() {
			return manulValidated;
		}

		public void setManulValidated(boolean manulValidated) {
			this.manulValidated = manulValidated;
		}

		public String getManualValidatedDate() {
			return manualValidatedDate;
		}

		public void setManualValidatedDate(String manualValidatedDate) {
			this.manualValidatedDate = manualValidatedDate;
		}

		public String getManualValidatedBy() {
			return manualValidatedBy;
		}

		public void setManualValidatedBy(String manualValidatedBy) {
			this.manualValidatedBy = manualValidatedBy;
		}

	}

	public static class FieldDefinition implements Serializable {

		private static final long serialVersionUID = 1L;

		private String fieldName;
		private String fieldType;
		private String fieldDescription;
		private boolean fieldRequired;

		public FieldDefinition() {
		}

		public FieldDefinition(String fieldName, String fieldType, boolean fieldRequired) {
			this.fieldName = fieldName;
			this.fieldType = fieldType;
			this.fieldRequired = fieldRequired;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		public String getFieldDescription() {
			return fieldDescription;
		}

		public void setFieldDescription(String fieldDescription) {
			this.fieldDescription = fieldDescription;
		}

		public boolean isFieldRequired() {
			return fieldRequired;
		}

		public void setFieldRequired(boolean fieldRequired) {
			this.fieldRequired = fieldRequired;
		}

	}

	public static class Extraction implements Serializable {

		private static final long serialVersionUID = 1L;

		private TemplateMappingProfileModel templateMappingProfile;
		private String extractedDate;

		public Extraction() {
		}

		public Extraction(TemplateMappingProfileModel templateMappingProfile, String extractedDate) {
			this.templateMappingProfile = templateMappingProfile;
			this.extractedDate = extractedDate;
		}

		public TemplateMappingProfileModel getTemplateMappingProfile() {
			return templateMappingProfile;
		}

		public void setTemplateMappingProfile(TemplateMappingProfileModel templateMappingProfile) {
			this.templateMappingProfile = templateMappingProfile;
		}

		public String getExtractedDate() {
			return extractedDate;
		}

		public void setExtractedDate(String extractedDate) {
			this.extractedDate = extractedDate;
		}
	}

	public static class DocumentName implements Serializable {

		private static final long serialVersionUID = 1L;

		private String documentId;
		private String documentType;
		private String originalAccountNumber;

		public DocumentName() {
		}

		public DocumentName(String documentId, String documentType, String originalAccountNumber) {
			this.documentId = documentId;
			this.documentType = documentType;
			this.originalAccountNumber = originalAccountNumber;
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

		public String getOriginalAccountNumber() {
			return originalAccountNumber;
		}

		public void setOriginalAccountNumber(String originalAccountNumber) {
			this.originalAccountNumber = originalAccountNumber;
		}

	}

	public static class Document implements Serializable {

		private static final long serialVersionUID = 1L;

		private String originalDocumentType;
		private String documentType;
		private String translatedDocumentType;
		private String documentNameString;
		private String documentDate;
		private DocumentName documentName;
		
		private String bucketName;

		public Document() {
		}

		public Document(String originalDocumentType, String documentType, String translatedDocumentType,
				String documentNameString, String documentDate, DocumentName documentName) {
			this.originalDocumentType = originalDocumentType;
			this.documentType = documentType;
			this.translatedDocumentType = translatedDocumentType;
			this.documentNameString = documentNameString;
			this.documentDate = documentDate;
			this.documentName = documentName;
		}

		public String getOriginalDocumentType() {
			return originalDocumentType;
		}

		public void setOriginalDocumentType(String originalDocumentType) {
			this.originalDocumentType = originalDocumentType;
		}

		public String getDocumentType() {
			return documentType;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}

		public String getTranslatedDocumentType() {
			return translatedDocumentType;
		}

		public void setTranslatedDocumentType(String translatedDocumentType) {
			this.translatedDocumentType = translatedDocumentType;
		}

		public String getDocumentNameString() {
			return documentNameString;
		}

		public void setDocumentNameString(String documentNameString) {
			this.documentNameString = documentNameString;
		}

		public String getDocumentDate() {
			return documentDate;
		}

		public void setDocumentDate(String documentDate) {
			this.documentDate = documentDate;
		}

		public DocumentName getDocumentName() {
			return documentName;
		}

		public void setDocumentName(DocumentName documentName) {
			this.documentName = documentName;
		}

		public void setBucketName(String bucketName) {
			this.bucketName = bucketName;
		}
		
		public String getBucketName() {
			return bucketName;
		}
	}

	public static class AutoValidation implements Serializable {

		private static final long serialVersionUID = 1L;

		private Boolean autoValidated;
		private String validatedDate;

		public AutoValidation() {
		}

		public AutoValidation(Boolean autoValidated, String validatedDate) {
			this.autoValidated = autoValidated;
			this.validatedDate = validatedDate;
		}

		public Boolean getAutoValidated() {
			return autoValidated;
		}

		public void setAutoValidated(Boolean autoValidated) {
			this.autoValidated = autoValidated;
		}

		public String getValidatedDate() {
			return validatedDate;
		}

		public void setValidatedDate(String validatedDate) {
			this.validatedDate = validatedDate;
		}

	}

	public static class PdfTagging implements Serializable {

		private static final long serialVersionUID = 1L;

		private boolean tagging;
		private String taggingDate;

		public PdfTagging() {
		}

		public PdfTagging(boolean tagging, String taggingDate) {
			this.tagging = tagging;
			this.taggingDate = taggingDate;
		}

		public boolean isTagging() {
			return tagging;
		}

		public void setTagging(boolean tagging) {
			this.tagging = tagging;
		}

		public String getTaggingDate() {
			return taggingDate;
		}

		public void setTaggingDate(String taggingDate) {
			this.taggingDate = taggingDate;
		}

	}

	public static class ManualAccountVerification extends ManualValidation {

		private static final long serialVersionUID = 1L;

	}

	public static class AccountVerification implements Serializable {

		private static final long serialVersionUID = 1L;

		private Boolean verification;
		private String verificationDate;

		public AccountVerification() {
		}

		public AccountVerification(Boolean verification, String verificationDate) {
			this.verification = verification;
			this.verificationDate = verificationDate;
		}

		public Boolean getVerification() {
			return verification;
		}

		public void setVerification(Boolean verification) {
			this.verification = verification;
		}

		public String getVerificationDate() {
			return verificationDate;
		}

		public void setVerificationDate(String verificationDate) {
			this.verificationDate = verificationDate;
		}

	}

	public static class Receive implements Serializable {

		private static final long serialVersionUID = 1L;

		private Boolean received;
		private String receivedDate;

		public Receive() {
		}

		public Receive(Boolean received, String receivedDate) {
			this.received = received;
			this.receivedDate = receivedDate;
		}

		public Boolean getReceived() {
			return received;
		}

		public void setReceived(Boolean received) {
			this.received = received;
		}

		public String getReceivedDate() {
			return receivedDate;
		}

		public void setReceivedDate(String receivedDate) {
			this.receivedDate = receivedDate;
		}

	}
	
	public static class DocumentType implements Serializable {
		private static final long serialVersionUID = 1L;

		private Long id;
		private String code;

		public DocumentType() {
			super();
		}

		public DocumentType(Long id, String code) {
			super();
			this.id = id;
			this.code = code;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (other == this)
				return true;
			if (!(other instanceof DocumentType))
				return false;
			DocumentType documentType = (DocumentType) other;
			if (StringUtils.equals(this.getCode(), documentType.getCode()) && this.getId().equals(documentType.getId()))
				return true;
			else return false;
		}
	}
	
	public static class Seller implements Serializable {
		private static final long serialVersionUID = 1L;

		private Long id;
		private String name;

		public Seller() {
			super();
		}

		public Seller(Long id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String code) {
			this.name = code;
		}
	}

}
