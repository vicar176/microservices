/**
 * 
 */
package com.mcmcg.dia.iwfm.entity;

/**
 * @author jaleman
 *
 */
public class ConfigurationParameters extends BaseEntity {

	private String key;
	private String value;
	
	/**
	 * 
	 */
	public ConfigurationParameters() {
		
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
