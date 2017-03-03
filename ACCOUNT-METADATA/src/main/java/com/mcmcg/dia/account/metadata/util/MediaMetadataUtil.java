package com.mcmcg.dia.account.metadata.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Chandra
 *
 */
public class MediaMetadataUtil {
	public static final String DATE_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";

	public static String formatDate(Date date) {
		return getFormater(false).format(date);
	}

	public static String formatDateShort(Date date) {
		return getFormater(true).format(date);
	}

	public static Date parseDate(String date) throws ParseException {
		return getFormater(false).parse(date);
	}
	
	public static boolean isValidFormat(String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_SHORT);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }
	
	/********************************************
	 * 											*
	 * 			PRIVATE METHODS					*
	 * 											*
	 ********************************************/
	
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
	
	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
