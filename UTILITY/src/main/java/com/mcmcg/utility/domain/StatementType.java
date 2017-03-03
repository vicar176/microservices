package com.mcmcg.utility.domain;

public enum StatementType {

	LASTPAYMENT("lastpmt"),
	LASTACTIVITY("lastactivity"),
	CHARGEOFF("chrgoff"),
	LASTBILL("lastbillstmt"),
	STATEMENT("stmt");
	
	private final String type;
	
	private StatementType(final String type) {
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
