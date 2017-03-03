package com.mcmcg.dia.iwfm.util;

public final class ConversionRulesUtil {

	public static final String REPROCESS = "REPROCESS";
	public static final String DISCARDED = "DISCARDED";
	public static final String TEMPLATE_NOT_FOUND = "TEMPLATENOTFOUND";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILED = "FAILED";

	/**
	 * 
	 * 
	 * @param metadataDocumentStatus
	 * @return
	 */
	public static String getHighLevelStatusFrom(String metadataDocumentStatus){
		
		String newStatus =  DocumentImagesStatusCode.SUCCESS.toString();
		
		switch (metadataDocumentStatus.toUpperCase()){
		case TEMPLATE_NOT_FOUND :
		case DISCARDED :
			newStatus = DocumentImagesStatusCode.SKIPPED.toString();
			break;
		case REPROCESS :
			newStatus = DocumentImagesStatusCode.REPROCESS.toString();
			break;
		case FAILED :
			newStatus = DocumentImagesStatusCode.FAILURE.toString();
			break;
		}
		
		return newStatus;
	}

}
