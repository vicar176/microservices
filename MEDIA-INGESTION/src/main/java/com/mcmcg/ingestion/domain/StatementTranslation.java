package com.mcmcg.ingestion.domain;

import java.io.Serializable;
import java.util.List;

public class StatementTranslation implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<TranslateDocument> documentList;
	private TranslateAccount translateAccount;

	public List<TranslateDocument> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<TranslateDocument> documentList) {
		this.documentList = documentList;
	}	

	public TranslateAccount getTranslateAccount() {
		return translateAccount;
	}

	public void setTranslateAccount(TranslateAccount translateAccount) {
		this.translateAccount = translateAccount;
	}

	public static class TranslateAccount implements Serializable {

		private static final long serialVersionUID = 1L;

		public String accountNumber;

		public double chargeOffAmount;
		public String chargeOffDate;
		//
		public double lastPaymentAmount;
		public String lastPaymentDate;
		//
		public double lastPurchaseAmount;
		public String lastPurchaseDate;
		//
		public double lastBalanceTransferAmount;
		public String lastBalanceTransferDate;

		public double lastCashAdvanceAmount;
		public String lastCashAdvanceDate;

		public double lastActivityAmount;
		public String lastActivityDate;
		public boolean isSetForIssuer;

		public TranslateAccount() {
		}

		public String getAccountNumber() {
			return accountNumber;
		}

		public void setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
		}

		public double getChargeOffAmount() {
			return chargeOffAmount;
		}

		public void setChargeOffAmount(double chargeOffAmount) {
			this.chargeOffAmount = chargeOffAmount;
		}

		public String getChargeOffDate() {
			return chargeOffDate;
		}

		public void setChargeOffDate(String chargeOffDate) {
			this.chargeOffDate = chargeOffDate;
		}

		public double getLastPaymentAmount() {
			return lastPaymentAmount;
		}

		public void setLastPaymentAmount(double lastPaymentAmount) {
			this.lastPaymentAmount = lastPaymentAmount;
		}

		public String getLastPaymentDate() {
			return lastPaymentDate;
		}

		public void setLastPaymentDate(String lastPaymentDate) {
			this.lastPaymentDate = lastPaymentDate;
		}

		public double getLastPurchaseAmount() {
			return lastPurchaseAmount;
		}

		public void setLastPurchaseAmount(double lastPurchaseAmount) {
			this.lastPurchaseAmount = lastPurchaseAmount;
		}

		public String getLastPurchaseDate() {
			return lastPurchaseDate;
		}

		public void setLastPurchaseDate(String lastPurchaseDate) {
			this.lastPurchaseDate = lastPurchaseDate;
		}

		public double getLastBalanceTransferAmount() {
			return lastBalanceTransferAmount;
		}

		public void setLastBalanceTransferAmount(double lastBalanceTransferAmount) {
			this.lastBalanceTransferAmount = lastBalanceTransferAmount;
		}

		public String getLastBalanceTransferDate() {
			return lastBalanceTransferDate;
		}

		public void setLastBalanceTransferDate(String lastBalanceTransferDate) {
			this.lastBalanceTransferDate = lastBalanceTransferDate;
		}

		public double getLastCashAdvanceAmount() {
			return lastCashAdvanceAmount;
		}

		public void setLastCashAdvanceAmount(double lastCashAdvanceAmount) {
			this.lastCashAdvanceAmount = lastCashAdvanceAmount;
		}

		public String getLastCashAdvanceDate() {
			return lastCashAdvanceDate;
		}

		public void setLastCashAdvanceDate(String lastCashAdvanceDate) {
			this.lastCashAdvanceDate = lastCashAdvanceDate;
		}

		public double getLastActivityAmount() {
			return lastActivityAmount;
		}

		public void setLastActivityAmount(double lastActivityAmount) {
			this.lastActivityAmount = lastActivityAmount;
		}

		public String getLastActivityDate() {
			return lastActivityDate;
		}

		public void setLastActivityDate(String lastActivityDate) {
			this.lastActivityDate = lastActivityDate;
		}

		public boolean isSetForIssuer() {
			return isSetForIssuer;
		}

		public void setSetForIssuer(boolean isSetForIssuer) {
			this.isSetForIssuer = isSetForIssuer;
		}

	}

	public static class TranslateDocument implements Serializable {

		private static final long serialVersionUID = 1L;

		public String id;
		public String type;
		public String translatedType;
		public double statementBalanceAmount;
		public double statementPaymentAmount;
		public String statementDate;

		public TranslateDocument() {

		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getTranslatedType() {
			return translatedType;
		}

		public void setTranslatedType(String translatedType) {
			this.translatedType = translatedType;
		}

		public double getStatementBalanceAmount() {
			return statementBalanceAmount;
		}

		public void setStatementBalanceAmount(double statementBalanceAmount) {
			this.statementBalanceAmount = statementBalanceAmount;
		}

		public double getStatementPaymentAmount() {
			return statementPaymentAmount;
		}

		public void setStatementPaymentAmount(double statementPaymentAmount) {
			this.statementPaymentAmount = statementPaymentAmount;
		}

		public String getStatementDate() {
			return statementDate;
		}

		public void setStatementDate(String statementDate) {
			this.statementDate = statementDate;
		}

	}

}
