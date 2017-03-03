/**
 * 
 */
package com.mcmcg.ingestion.domain;

/**
 * @author jaleman
 *
 */
public class ProductGroup extends BaseDomain{

	private static final long serialVersionUID = 1L;
	
	private String code;
	
	private String name;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}
