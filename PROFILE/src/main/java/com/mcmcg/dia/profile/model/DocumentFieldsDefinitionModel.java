/**
 * 
 */
package com.mcmcg.dia.profile.model;

import java.util.List;

import com.couchbase.client.java.repository.annotation.Id;
import com.mcmcg.dia.profile.model.OaldProfileModel.DocumentType;
import com.mcmcg.dia.profile.model.domain.FieldDefinition;


/**
 * @author jaleman
 *
 */
public class DocumentFieldsDefinitionModel extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
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
		private FieldDefinition fieldDefinition;

		public DocumentFieldDefinition(boolean required, boolean verification, FieldDefinition fieldDefinition) {
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

		public FieldDefinition getFieldDefinition() {
			return fieldDefinition;
		}

		public void setFieldDefinition(FieldDefinition fieldDefinition) {
			this.fieldDefinition = fieldDefinition;
		}

		public static class DocumentType {

			private String id;
			private String code;

			public String getId() {
				return id;
			}

			public void setId(String id) {
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
