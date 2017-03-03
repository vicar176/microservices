package com.mcmcg.dia.media.metadata.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

/**
 * @author Victor Arias
 *
 */
public class MediaMetadataUtil {
	
	private static final Logger LOG = Logger.getLogger(MediaMetadataUtil.class);
	
	public static final String DATE_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String DATE_FORMAT_SHORT = "MM/dd/yyyy";

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
            SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_SHORT); 
            date = sdf1.parse(value);
            if (!value.equals(sdf1.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
        	LOG.error(ex.getMessage());
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
			double number = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public static boolean isValidDate(String formatedDate) {
		boolean isValid = false;
		
		DateFormat dateFormat = getFormater(true);
		
		try {
			dateFormat.setLenient(false);
			Date date = dateFormat.parse(formatedDate);
			LOG.info(date);
			isValid = true;
		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
		}
		
		return isValid;
	}
}
