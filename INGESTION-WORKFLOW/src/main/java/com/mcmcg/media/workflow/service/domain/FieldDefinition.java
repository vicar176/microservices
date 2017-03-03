package com.mcmcg.media.workflow.service.domain;

import java.io.Serializable;

public class FieldDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	private String fieldName;
	private String fieldDescription;
	private String fieldType;
	private boolean fieldRequired;

	public FieldDefinition() {
		super();
	}

	public FieldDefinition(String fieldName, String fieldDescription, String fieldType, boolean fieldRequired) {
		super();
		this.fieldName = fieldName;
		this.fieldDescription = fieldDescription;
		this.fieldType = fieldType;
		this.fieldRequired = fieldRequired;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldDescription() {
		return fieldDescription;
	}

	public void setFieldDescription(String fieldDescription) {
		this.fieldDescription = fieldDescription;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public boolean isFieldRequired() {
		return fieldRequired;
	}

	public void setFieldRequired(boolean fieldRequired) {
		this.fieldRequired = fieldRequired;
	}
}