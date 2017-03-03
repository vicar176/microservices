/**
 * 
 */
package com.mcmcg.dia.profile.model;

import java.io.Serializable;
import java.util.List;

import com.couchbase.client.java.repository.annotation.Id;

/**
 * @author jaleman
 *
 */
public class FieldDefinitionModel extends BaseModel {

	private static final long serialVersionUID = 1L;

	private String fieldDescription;
	private String fieldName;
	private String fieldType;
	private List<DatabaseMapping> databaseMapping;
	private List<Operator> operators;
	private boolean active;
	//private boolean encrypt;

	public String getFieldDescription() {
		return fieldDescription;
	}

	public void setFieldDescription(String fieldDescription) {
		this.fieldDescription = fieldDescription;
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

	public List<DatabaseMapping> getDatabaseMapping() {
		return databaseMapping;
	}

	public void setDatabaseMapping(List<DatabaseMapping> databaseMapping) {
		this.databaseMapping = databaseMapping;
	}

	public List<Operator> getOperators() {
		return operators;
	}

	public void setOperators(List<Operator> operators) {
		this.operators = operators;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
/*
	public boolean isEncrypt() {
		return encrypt;
	}

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}*/

	public static class DatabaseMapping implements Serializable {

		private static final long serialVersionUID = 1L;

		private String schema;
		private String table;
		private String field;

		public DatabaseMapping() {

		}

		public DatabaseMapping(String schema, String table, String field) {
			this.schema = schema;
			this.table = table;
			this.field = field;
		}

		public String getSchema() {
			return schema;
		}

		public void setSchema(String schema) {
			this.schema = schema;
		}

		public String getTable() {
			return table;
		}

		public void setTable(String table) {
			this.table = table;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public static long getSerialversionuid() {
			return serialVersionUID;
		}

	}

	public static class Operator implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String symbol;
		private String name;

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Operator() {

		}

		public Operator(String symbol, String name) {
			this.symbol = symbol;
			this.name = name;

		}

	}

}
