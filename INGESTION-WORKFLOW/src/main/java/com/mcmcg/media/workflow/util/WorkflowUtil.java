/**
 * 
 */
package com.mcmcg.media.workflow.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmcg.media.workflow.service.domain.MediaDocument;

/**
 * @author jaleman
 *
 */
public class WorkflowUtil {
	
	private static final Logger LOG = Logger.getLogger(WorkflowUtil.class);
	
	public static final String DATE_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String DATE_FORMAT_SHORT = "MM/dd/yyyy";
	public static final String PDF_EXTENSION = ".pdf";
	public static final String PDF_DOCID = "docid";
	public static final String PDF_BOXID = "boxid";
	public static final String PDF_ACCOUNT = "account";
	public static final String PDF_DOC_TYPE = "doctype";
	public static final String PDF_DOC_DATE = "docdate";

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
	
	public static void main (String[] args){
		/*	
		System.out.println(buildMediaDocument("docid=43877169741_account=8568634804_DocType=stmt_DocDate=05-17-2014.pdf|s3bucket=mcm-s3-media-stage"));
		System.out.println(buildMediaDocument("docid=43877169741_account=8568634804_DocType=stmt_DocDate=05-17-2014.pdf|s3bucket="));
		System.out.println(buildMediaDocument("Box/851/000/8510008000/BoxID=11081299615_account=8510008000_DocType=mcmbillingst_DocDate=08182005_AxacoreID=2060712002843946857.pdf|s3bucket=mcm-s3-media-stage-box-852"));
		*/
	    System.out.println(buildMediaDocument("2016/05/docid=43877169741_account=8568634804_DocType=chrgoff_DocDate=05-17-2014.pdf|s3bucket=mcm-s3-media-stage"));
		
	}
	
	/***
	 * 
	 * @param filename
	 * @return
	 */
	public static MediaDocument buildMediaDocument(String filename){
		MediaDocument mediaDocument = null;

		ObjectMapper mapper = new ObjectMapper();

		try {
			mediaDocument = mapper.readValue(filename, MediaDocument.class);
		} catch (JsonProcessingException e) {
			//
		} catch (IOException e) {
			// 
		}

		return mediaDocument;
		
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String convertDateFormatToYYYYMMDD(String date){
		
		//Verifies if null
		if (date == null){
			return getFormater(true).format(new Date());
		}
		
		//See if an extension is included in the String value
		String newValue = date;
		if (date.contains(".")){
			newValue = date.substring(0, date.lastIndexOf("."));
		}
		
		//Split String in tokens
		String tokens[] = newValue.split("-");
		
		if (tokens.length == 3){
			newValue = tokens[0].trim() + "/" + tokens[1].trim() + "/" + tokens[2].trim();
		}else{
			newValue = getFormater(true).format(new Date());
		}
		
		return newValue;
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

}
