package com.mcmcg.ingestion.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jaleman
 *
 */
public class IngestionUtils {

	private static final Logger LOG = Logger.getLogger(IngestionUtils.class);

	public static final String DATE_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String DATE_FORMAT_SHORT = "MM/dd/yyyy";
	public static final SimpleDateFormat formater = new SimpleDateFormat(DATE_FORMAT_LONG);

	public static final String EXTRACTED = "extracted";
	public static final String VALIDATED = "validated";
	public static final String MANUAL_VALIDATED = "manual-validated";
	public static final String TRANSLATED = "translated";
	public static final String TAGGED = "tagged";
	public static final String RECEIVED = "received";
	public static final String REPROCESS = "reprocess";

	public static final String DISCARDED = "discarded";
	public static final String TEMPLATE_NOT_FOUND = "templateNotFound";
	public static final String NO_TEXT_LAYER = "noTextLayer";

	protected IngestionUtils() {
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		return formater.format(date);
	}

	public static String formatDateShort(Date date) {
		if (date != null) {
			return new SimpleDateFormat(DATE_FORMAT_SHORT).format(date);
		} else
			return null;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateString(String date) {

		String dateString = formatDate(new Date());

		if (date != null) {
			String tokens[] = date.split("/");

			if (tokens.length == 3) {
				dateString = tokens[2] + "-" + tokens[0] + "-" + tokens[1];
			}
		}

		return dateString;
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	public static String getJsonObject(Object object) {

		ObjectMapper mapper = new ObjectMapper();
		String value = new String();
		try {
			value = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			//
		}

		return value;
	}

	/**
	 * Convert a String into a Date using a given format
	 * 
	 * @param date
	 * @param format
	 * @return Date
	 */
	public static Date convertToDate(String date, String dateFormat) {

		Date newDate = null;
		if (date == null || dateFormat == null) {
			LOG.info("convertToDate null -> null");
			return null;
		}
		try {
			newDate = new SimpleDateFormat(dateFormat).parse(date);
		} catch (ParseException e) {
			LOG.info(
					String.format("convertToDate - ParseException (input = %s and dateFormat = %s)", date, dateFormat));
			return null;
		}

		LOG.debug(String.format("convertToDate - (dateString = %s and dateFormat = %s) -> %s", date, dateFormat,
				newDate.toString()));
		return newDate;
	}

	/**
	 * Adds a number of months to a date returning a new object. The original
	 * {@code Date} is unchanged.
	 * 
	 * @param date
	 * @param amount
	 *            the amount to add, may be negative
	 * @return newDate
	 */
	public static String addMonths(String date, final int amount) {

		Date newDate = convertToDate(date, DATE_FORMAT_SHORT);
		newDate = DateUtils.addMonths(newDate, amount);

		SimpleDateFormat formaterMetadata = new SimpleDateFormat(DATE_FORMAT_SHORT);

		return formaterMetadata.format(newDate);

	}
}
