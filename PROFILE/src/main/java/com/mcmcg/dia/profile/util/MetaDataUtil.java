package com.mcmcg.dia.profile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @author wporras
 *
 */
public class MetaDataUtil {

	private static final Logger LOG = Logger.getLogger(MetaDataUtil.class);

	public static enum VALIDATION_TYPES {
		number, alphanumeric, date
	}

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

	public static boolean validateDataType(String dataType) {
		if (dataType.equals(VALIDATION_TYPES.alphanumeric.toString())
				|| dataType.equals(VALIDATION_TYPES.number.toString())
				|| dataType.equals(VALIDATION_TYPES.date.toString())) {
			return true;
		}
		return false;
	}

	

}
