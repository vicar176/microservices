package com.mcmcg.dia.iwfm.domain;

import java.io.Serializable;

import com.mcmcg.dia.iwfm.domain.MediaMetadataModel.FieldDefinition;

/**
 * 
 * @author wPorras
 *
 */

public class DataElement implements Serializable {

	private static final long serialVersionUID = 1L;
	private FieldDefinition fieldDefinition;
	private String value;
	private String snipet;
	private boolean validated;
	private String type;

	public DataElement() {
	}

	public DataElement(FieldDefinition fieldDefinition, String value, String snipet, boolean validated, String type) {
		this.fieldDefinition = fieldDefinition;
		this.value = value;
		this.snipet = snipet;
		this.validated = validated;
		this.type = type;
	}

	public FieldDefinition getFieldDefinition() {
		return fieldDefinition;
	}

	public void setFieldDefinition(FieldDefinition fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSnipet() {
		return snipet;
	}

	public void setSnipet(String snipet) {
		this.snipet = snipet;
	}

	public boolean getValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}
