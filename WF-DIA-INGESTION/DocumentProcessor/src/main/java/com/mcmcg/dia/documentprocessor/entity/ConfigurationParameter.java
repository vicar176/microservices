/**
 * 
 */
package com.mcmcg.dia.documentprocessor.entity;

/**
 * @author jaleman
 *
 */
public class ConfigurationParameter extends BaseModel {

	private String key;
	private String value;
	
	/**
	 * 
	 */
	public ConfigurationParameter() {
		
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
