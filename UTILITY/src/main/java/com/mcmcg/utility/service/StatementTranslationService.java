
package com.mcmcg.utility.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.ReadableInstant;
import org.springframework.stereotype.Service;

import com.mcmcg.utility.domain.StatementTranslation;
import com.mcmcg.utility.domain.StatementTranslation.TranslateDocument;
import com.mcmcg.utility.domain.StatementType;
import com.mcmcg.utility.exception.ServiceException;
import com.mcmcg.utility.util.MetaDataUtil;

@Service
public class StatementTranslationService {
	
	private static final Logger LOG = Logger.getLogger(StatementTranslationService.class);

	public StatementTranslation statementTranslation(StatementTranslation statementTranslation) throws ServiceException {
		
		StatementTranslation newStatementTranslation = new StatementTranslation();
		List<TranslateDocument> stmtType = null;
		
		try {
			stmtType = getStatementTranslated(statementTranslation);
			newStatementTranslation.setDocumentList(stmtType);
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}
		return newStatementTranslation;
	}

	private List<TranslateDocument> getStatementTranslated(StatementTranslation statementTranslation) throws ServiceException {
		Date statementDate = null;
		double statementBalanceAmount = 0.0;
		double statementPaymentAmount = 0.0;
		//String stmt_priorType = "";
		String typeTemp = null ;
		TranslateDocument documentCurrent = null;
		double statementBalanceAmountTemp = 0;
		TranslateDocument documentTemp = null;
		ArrayList<TranslateDocument> documentTranslatedList = new ArrayList<TranslateDocument>();
		
		List<TranslateDocument> doclist = statementTranslation.getDocumentList();
		for (TranslateDocument document : doclist) {
			if (document.getType() == null || document.getType().isEmpty()) {
				LOG.info("The list is empty");
				break;
			}
			if (document.getType().equals("current")) {
				documentCurrent = document;
				statementDate = MetaDataUtil.convertToDate((String)documentCurrent.getStatementDate());
				statementBalanceAmount = documentCurrent.getStatementBalanceAmount();
				statementPaymentAmount = documentCurrent.getStatementPaymentAmount();
			}
			if (document.getType().equals("prior") || document.getType().equals("following"))
			{
				documentTemp = document;
				statementBalanceAmountTemp = documentTemp.getStatementBalanceAmount();
				typeTemp = documentTemp.getType();
			}
		}
		
		double chargeOffAmount = statementTranslation.getTranslateAccount().getChargeOffAmount();
		Date chargeOffDate = MetaDataUtil.convertToDate(statementTranslation.getTranslateAccount().getChargeOffDate());
		double lastPaymentAmount = statementTranslation.getTranslateAccount().getLastPaymentAmount();
		Date lastPaymentDate = MetaDataUtil.convertToDate(statementTranslation.getTranslateAccount().getLastPaymentDate());
		boolean isIssuerSet = statementTranslation.getTranslateAccount().isSetForIssuer();
		Date lastPurchaseDate = MetaDataUtil.convertToDate(statementTranslation.getTranslateAccount().getLastPurchaseDate());
		//double lastActivityAmount = statementTranslation.getTranslateAccount().getLastActivityAmount();
		//Date lastActivityDate = MetaDataUtil.convertToDate(statementTranslation.getTranslateAccount().getLastActivityDate());

		if (!isDateWithinOneMonth(statementDate, chargeOffDate)) {
			if(isDateWithinOneMonth(statementDate, lastPaymentDate) ){
				if (isLastPayment(statementDate, lastPaymentDate, statementPaymentAmount, lastPaymentAmount)) {
					LOG.info("The Statement will be translated to LASTPAYMENT ");
					documentCurrent.setTranslatedType(StatementType.LASTPAYMENT.toString());
				}
			} else if (isLastAcitivity(statementDate, lastPurchaseDate)) {
					LOG.info("The Statement will be translated to LASTACTIVITY");
					documentCurrent.setTranslatedType(StatementType.LASTACTIVITY.toString());
				}
		} else if (!isChargeOff(chargeOffAmount, statementBalanceAmount)) {
			if (isLastBill(statementDate, chargeOffDate, statementBalanceAmount, isIssuerSet)) {
				if (isStatementPrior(typeTemp, statementBalanceAmountTemp, chargeOffAmount)) {
					LOG.info("The Statements will be translated to CHARGEOFF and LASTBILL ");
					documentCurrent.setTranslatedType(StatementType.CHARGEOFF.toString());
					documentTemp.setTranslatedType(StatementType.LASTBILL.toString());
					documentTranslatedList.add(documentTemp);
				} else if (isStatementFollowing(typeTemp, statementBalanceAmountTemp, chargeOffAmount)) {
					documentCurrent.setTranslatedType(StatementType.LASTBILL.toString());
					documentTemp.setTranslatedType(StatementType.CHARGEOFF.toString());
					documentTranslatedList.add(documentTemp);
				}
			}

		} else {
			LOG.info("The Statements will be translated to CHARGEOFF");
			documentCurrent.setTranslatedType(StatementType.CHARGEOFF.toString());
		}
		
		if (documentCurrent.getTranslatedType() != null) {
			documentTranslatedList.add(documentCurrent);
		}
		return documentTranslatedList;
	}

	private boolean isLastBill(Date statementDate, Date chargeOffDate, double statementBalanceAmount, boolean isIssuerSet) throws ServiceException {
		if (isIssuerSet) {
			boolean samedate = DateUtils.isSameDay(statementDate, chargeOffDate);  
			if (samedate && this.isAmountEqual(statementBalanceAmount, 0.0)) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	private boolean isStatementFollowing(String typeTemp, double statementBalanceAmountTemp, double chargeOffAmount)
			throws ServiceException {

		if (StringUtils.equalsIgnoreCase(typeTemp, "following")
				&& isAmountEqual(statementBalanceAmountTemp, chargeOffAmount))
			return true;
		else
			return false;
	}

	private boolean isStatementPrior(String typeTemp, double statementBalanceAmountTemp, double chargeOffAmount)
			throws ServiceException {

		if (StringUtils.equalsIgnoreCase(typeTemp, "prior")
				&& isAmountEqual(statementBalanceAmountTemp, chargeOffAmount))
			return true;
		else
			return false;
	}


	private boolean isChargeOff(double chargeOffAmount, double statementBalanceAmount) throws ServiceException {
		if (this.isAmountEqual(chargeOffAmount, statementBalanceAmount)) {
			return true;
		}
		return false;
	}

	/**
	 * Statement Date have to be max one month after last purchase date
	 * 
	 * @param statementDate
	 * @param lastPurchaseDate
	 * @return boolean
	 */
	private boolean isLastAcitivity(Date statementDate, Date lastPurchaseDate) {
		if (lastPurchaseDate != null && statementDate.after(lastPurchaseDate)) {

			DateTime dateTime = new DateTime(lastPurchaseDate).plusDays(30);
			if (statementDate.before(dateTime.toDate())) {
				return true;
			}
		}
		return false;
	}

	private boolean isLastPayment(Date statementDate, Date lastPaymentDate, double statementPaymentAmount, double lastPaymentAmount) {
		if (this.isDateWithinOneMonth(statementDate, lastPaymentDate) && isAmountEqual(statementPaymentAmount, lastPaymentAmount)) {
			return true;
		}
		return false;
	}
	
	private boolean isDateWithinOneMonth(Date dateA, Date dateB) {

		Days d = Days.daysBetween((ReadableInstant) new DateTime(dateA),
				(ReadableInstant) new DateTime(dateB));
		
		int days = Math.abs(d.getDays());
		if (days <= 30) {
			return true;
		}
		return false;
	}

	private boolean isAmountEqual(double firstAmount, double secondAmount) {
		int value = Double.compare(firstAmount, secondAmount);
		if (value > 0) {
			return false;
		}
		if (value < 0) {
			return false;
		}
		return true;
	}
	
}