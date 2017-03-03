package com.mcmcg.dia.batchmanager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

/**
 * 
 * @author Victor Arias
 *
 */
public class IngestionStateUtil {

	private static final Logger LOG = Logger.getLogger(IngestionStateUtil.class);

	public static final String DATE_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String DATE_FORMAT_SHORT = "MM/dd/yyyy";

	/**
	 * Convert a String into a Date using a given format
	 * 
	 * @param input
	 * @param format
	 * @return Date
	 * @throws ParseException
	 */

	public static Date convertToDate(String input, String dateFormat) {

		Date date = null;
		if (input == null || dateFormat == null) {
			LOG.info("convertToDate null -> null");
			return null;
		}
		try {
			date = new SimpleDateFormat(dateFormat).parse(input);
		} catch (ParseException e) {
			LOG.info(String.format("convertToDate - ParseException (input = %s and dateFormat = %s)", input,
					dateFormat));
			return null;
		}

		LOG.info(String.format("convertToDate - (input = %s and dateFormat = %s) -> %s", input, dateFormat,
				date.toString()));
		return date;
	}

	public static String formatDate(Date date) {
		return getFormater(false).format(date);
	}

	public static String formatDateShort(Date date) {
		return getFormater(true).format(date);
	}

	public static Date parseDate(String date) throws ParseException {
		return getFormater(false).parse(date);
	}

	private static SimpleDateFormat getFormater(boolean isShort) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat formater;
		if (isShort) {
			String dateLong = DATE_FORMAT_SHORT;
			formater = new SimpleDateFormat(dateLong);
		} else {
			formater = new SimpleDateFormat(DATE_FORMAT_LONG);
		}
		formater.setTimeZone(tz);
		return formater;
	}

}
