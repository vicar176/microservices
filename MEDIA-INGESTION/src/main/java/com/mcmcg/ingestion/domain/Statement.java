package com.mcmcg.ingestion.domain;

import java.util.Date;

/**
 * 
 * @author wporras
 *
 */
public class Statement extends BaseDomain {

	private static final long serialVersionUID = 1L;

	private double chargeOffAmount;
	private Date chargeOffDate;
	private double lastPaymentAmount;
	private Date lastPaymentDate;
	private double lastPurchaseAmount;
	private Date lastPurchaseDate;
	private double lastBalanceTransferAmount;
	private Date lastBalanceTransferDate;
	private double lastCashAdvanceAmount;
	private Date lastCashAdvanceDate;
	private boolean isIssuerSet;

	public Statement() {

	}

	public double getChargeOffAmount() {
		return chargeOffAmount;
	}

	public void setChargeOffAmount(double chargeOffAmount) {
		this.chargeOffAmount = chargeOffAmount;
	}

	public Date getChargeOffDate() {
		return chargeOffDate;
	}

	public void setChargeOffDate(Date chargeOffDate) {
		this.chargeOffDate = chargeOffDate;
	}

	public double getLastPaymentAmount() {
		return lastPaymentAmount;
	}

	public void setLastPaymentAmount(double lastPaymentAmount) {
		this.lastPaymentAmount = lastPaymentAmount;
	}

	public Date getLastPaymentDate() {
		return lastPaymentDate;
	}

	public void setLastPaymentDate(Date lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}

	public double getLastPurchaseAmount() {
		return lastPurchaseAmount;
	}

	public void setLastPurchaseAmount(double lastPurchaseAmount) {
		this.lastPurchaseAmount = lastPurchaseAmount;
	}

	public Date getLastPurchaseDate() {
		return lastPurchaseDate;
	}

	public void setLastPurchaseDate(Date lastPurchaseDate) {
		this.lastPurchaseDate = lastPurchaseDate;
	}

	public double getLastBalanceTransferAmount() {
		return lastBalanceTransferAmount;
	}

	public void setLastBalanceTransferAmount(double lastBalanceTransferAmount) {
		this.lastBalanceTransferAmount = lastBalanceTransferAmount;
	}

	public Date getLastBalanceTransferDate() {
		return lastBalanceTransferDate;
	}

	public void setLastBalanceTransferDate(Date lastBalanceTransferDate) {
		this.lastBalanceTransferDate = lastBalanceTransferDate;
	}

	public double getLastCashAdvanceAmount() {
		return lastCashAdvanceAmount;
	}

	public void setLastCashAdvanceAmount(double lastCashAdvanceAmount) {
		this.lastCashAdvanceAmount = lastCashAdvanceAmount;
	}

	public Date getLastCashAdvanceDate() {
		return lastCashAdvanceDate;
	}

	public void setLastCashAdvanceDate(Date lastCashAdvanceDate) {
		this.lastCashAdvanceDate = lastCashAdvanceDate;
	}

	public boolean isIssuerSet() {
		return isIssuerSet;
	}

	public void setIssuerSet(boolean isIssuerSet) {
		this.isIssuerSet = isIssuerSet;
	}
}
