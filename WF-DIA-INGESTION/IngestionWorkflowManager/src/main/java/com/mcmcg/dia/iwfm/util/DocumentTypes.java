package com.mcmcg.dia.iwfm.util;

/**
 * 
 * @author wporras
 *
 */
public enum DocumentTypes {
	
	STATEMENT("stmt"), 
	CHARGE_OFF("chrgoff"), 
	LAST_PAYMENT("lastpmt"), 
	LAST_BILL("lastbill"), 
	LAS_ACTIVITY("lastactivity");

	private final String type;

	private DocumentTypes(final String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}

	public String getType() {
		return type;
	}
}
