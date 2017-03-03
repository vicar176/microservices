package com.mcmcg.ingestion.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mcmcg.ingestion.domain.Account;
import com.mcmcg.ingestion.domain.DataElement;
import com.mcmcg.ingestion.domain.MediaDocument;
import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.Statement;
import com.mcmcg.ingestion.domain.StatementTranslation;
import com.mcmcg.ingestion.domain.StatementTranslation.TranslateAccount;
import com.mcmcg.ingestion.domain.StatementTranslation.TranslateDocument;

/**
 * 
 * @author wporras
 *
 */
public class StatementTranslationUtil {

	private static final Logger LOG = Logger.getLogger(IngestionUtils.class);

	public static final String FIELD_STATEMENT_BALANCE = "statementBalance";
	public static final String FIELD_STATEMENT_DATE = "statementDate";
	public static final String FIELD_STATEMENT_PAYMENT = "statementPayment";

	/**
	 * Builds a Statement Translation object for the Utility Statement
	 * Translation
	 * 
	 * @param mediaDocument
	 * @param account
	 * @param currentMetadata
	 * @param adjacentMetadata
	 * @return StatementTranslation
	 */
	public static StatementTranslation buildStatementTranslation(MediaDocument mediaDocument, Account account,
			MediaMetadataModel currentMetadata, MediaMetadataModel adjacentMetadata, String adjacentMetadataType) {

		StatementTranslation statementTranslation = new StatementTranslation();

		// Document List
		List<TranslateDocument> documentList = new ArrayList<TranslateDocument>();
		documentList.add(buildTranslateDocument(currentMetadata, "current"));
		if (adjacentMetadataType != null && adjacentMetadata != null && adjacentMetadata.getDataElements() != null) {
			documentList.add(buildTranslateDocument(adjacentMetadata, adjacentMetadataType));
		}
		statementTranslation.setDocumentList(documentList);

		// Translate Account
		TranslateAccount translateAccount = new TranslateAccount();
		Statement accountStatement = account.getStatement();
		translateAccount.setAccountNumber(mediaDocument.getAccountNumber().toString());
		translateAccount.setChargeOffAmount(accountStatement.getChargeOffAmount());
		translateAccount.setChargeOffDate(IngestionUtils.formatDateShort(accountStatement.getChargeOffDate()));
		// translateAccount.setLastActivityAmount(); - not used yet
		// translateAccount.setLastActivityDate(); - not used yet
		translateAccount.setLastBalanceTransferAmount(accountStatement.getLastBalanceTransferAmount());
		translateAccount.setLastBalanceTransferDate(
				IngestionUtils.formatDateShort(accountStatement.getLastBalanceTransferDate()));
		translateAccount.setLastCashAdvanceAmount(accountStatement.getLastCashAdvanceAmount());
		translateAccount
				.setLastCashAdvanceDate(IngestionUtils.formatDateShort(accountStatement.getLastCashAdvanceDate()));
		translateAccount.setLastPaymentAmount(accountStatement.getLastPaymentAmount());
		translateAccount.setLastPaymentDate(IngestionUtils.formatDateShort(accountStatement.getLastPaymentDate()));
		translateAccount.setLastPurchaseAmount(accountStatement.getLastPurchaseAmount());
		translateAccount.setLastPurchaseDate(IngestionUtils.formatDateShort(accountStatement.getLastPurchaseDate()));
		translateAccount.setSetForIssuer(accountStatement.isIssuerSet());

		statementTranslation.setTranslateAccount(translateAccount);

		// Debug
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			LOG.debug(String.format("Statement Translation Request %s ",
					mapper.writeValueAsString(statementTranslation)));
		} catch (JsonProcessingException e) {
			// nothing
		}

		return statementTranslation;
	}

	private static TranslateDocument buildTranslateDocument(MediaMetadataModel metadata, String type) {

		TranslateDocument translateDocument = new TranslateDocument();

		translateDocument.setType(type);
		translateDocument.setId(metadata.getId());

		List<DataElement> dataElements = metadata.getDataElements();
		for (DataElement dataElement : dataElements) {

			// STATEMENT BALANCE
			if (dataElement.getFieldDefinition().getFieldName().equalsIgnoreCase(FIELD_STATEMENT_BALANCE)) {
				translateDocument.setStatementBalanceAmount(Double.valueOf(dataElement.getValue()));
			}

			// STATEMENT DATE
			if (dataElement.getFieldDefinition().getFieldName().equalsIgnoreCase(FIELD_STATEMENT_DATE)) {
				translateDocument.setStatementDate(dataElement.getValue());
			}

			// STATEMENT PAYMENT (Optional)
			if (dataElement.getFieldDefinition().getFieldName().equalsIgnoreCase(FIELD_STATEMENT_PAYMENT)) {
				translateDocument.setStatementPaymentAmount(Double.valueOf(dataElement.getValue()));
			}
		}
		return translateDocument;
	}

}
