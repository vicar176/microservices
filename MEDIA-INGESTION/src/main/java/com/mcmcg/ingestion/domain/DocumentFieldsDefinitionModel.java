/**
 * 
 */
package com.mcmcg.ingestion.domain;

import java.util.List;

import com.mcmcg.ingestion.domain.DocumentFieldsDefinitionModel.DocumentFieldDefinition.DocumentType;



/**
 * @author jaleman
 *
 */
public class DocumentFieldsDefinitionModel extends BaseDomain {

	private static final long serialVersionUID = 1L;

	private String id;
	private boolean accountVerification;
	private boolean active;
	private List<DocumentFieldDefinition> fieldDefinitions;
	private DocumentType documentType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isAccountVerification() {
		return accountVerification;
	}

	public void setAccountVerification(boolean accountVerification) {
		this.accountVerification = accountVerification;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setFieldDefinitions(List<DocumentFieldDefinition> fieldDefinitions) {
		this.fieldDefinitions = fieldDefinitions;
	}

	public List<DocumentFieldDefinition> getFieldDefinitions() {
		return fieldDefinitions;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public static class DocumentFieldDefinition {

		private boolean required;
		private boolean verification;
		private FieldDefinitionModel fieldDefinition;

		public DocumentFieldDefinition(boolean required, boolean verification, FieldDefinitionModel fieldDefinition) {
			this.required = required;
			this.verification = verification;
			this.fieldDefinition = fieldDefinition;
		}

		public DocumentFieldDefinition() {

		}

		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			this.required = required;
		}

		public boolean isVerification() {
			return verification;
		}

		public void setVerification(boolean verification) {
			this.verification = verification;
		}

		public FieldDefinitionModel getFieldDefinition() {
			return fieldDefinition;
		}

		public void setFieldDefinition(FieldDefinitionModel fieldDefinition) {
			this.fieldDefinition = fieldDefinition;
		}

		public static class DocumentType {

			private Long id;
			private String code;

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

		}
	}

}
