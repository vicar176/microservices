package com.mcmcg.utility.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import com.mcmcg.utility.annotation.Diagnostics;
import com.mcmcg.utility.exception.ServiceException;
import com.mcmcg.utility.file.FileVO;
import com.mcmcg.utility.file.IFileSystem;

/**
 * 
 * @author wporras
 *
 */
public class MetaDataUtil {

	private static final Logger LOG = Logger.getLogger(MetaDataUtil.class);

	public static final String DATE_FORMAT_LONG = "yyyy-MM-dd HH:mm:SSS";
	public static final String DATE_FORMAT_SHORT = "MM/dd/yyyy";

	public static enum VALIDATION_TYPES {
		number, alphanumeric, date
	}

	public static enum EXTENSION_TYPES {
		pdf// , tif, tiff, jpg
	};

	public static enum BUCKET_NAMES_ENUM {
		TEMPLATE, EXTRACTION, ORIGINAL
	};

	private static final float PIXELS_TO_POINTS_ESCALE = 0.24f;

	/**
	 * Convert a String with various formats into java.util.Date
	 * 
	 * @param input
	 * @return Date
	 */
	public static Date convertToDate(String input) {

		Date date = null;
		if (null == input) {
			LOG.info("convertToDate null -> null");
			return null;
		}

		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input);
		if (groups.size() == 1) {
			DateGroup group = groups.get(0);
			if (!group.isDateInferred()) {
				date = group.getDates().get(0);
				LOG.info(String.format("convertToDate - %s => %s", input, date.toString()));
			} else {
				LOG.info(String.format("convertToDate - %s => (Inferred) %s", input, group.getDates().get(0)));
			}
		}

		return date;
	}

	/**
	 * Convert a String into a Date using a given format
	 * 
	 * @param input
	 * @param format
	 * @return Date
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

	/**
	 * Clean and validate extracted text depending on validation type
	 * 
	 * @param extractedText
	 * @param validation
	 * @return String
	 * @throws ServiceException
	 */
	public static String validateExtractedText(String extractedText, String validation, String dateFormat)
			throws ServiceException {
		String result = extractedText;

		if (result == null || result.isEmpty()) {
			throw new ServiceException("No text extracted");
		}

		// Clean up and validate the extracted text
		if (validation.equals(VALIDATION_TYPES.number.toString())) {
			result = result.replaceAll("[^\\d.]", "");
		} else if (validation.equals(VALIDATION_TYPES.alphanumeric.toString())) {
			result = result.trim();
		} else if (validation.equals(VALIDATION_TYPES.date.toString())) {
			result = result.trim();
			result = result.replaceAll("\\s+","");
			Date date = null;
			if (StringUtils.isNotBlank(dateFormat)) {
				date = MetaDataUtil.convertToDate(result, dateFormat);
			} else {
				date = MetaDataUtil.convertToDate(result);
			}
			if (date == null) {
				throw new ServiceException("Not valid date");
			}
			result = formatDateShort(date);

		}

		if (result.isEmpty()) {
			throw new ServiceException("No text extracted");
		}

		return result;
	}

	public static Integer convertPdfToImages(FileVO fileVO, IFileSystem fileSystem, BUCKET_NAMES_ENUM bucketEnum,
			boolean upload) {
		Integer totalImages = null;
		PDDocument document = null;
		try {
			LOG.info("Start convert PDF to Images process");
			document = PDDocument.load(fileVO.getInputStream());
			int numPages = document.getNumberOfPages();
			for (int i = 0; i < numPages; i++) {
				fileSystem.createImageAndUpload(fileVO, i, bucketEnum, upload);
			}
			totalImages = numPages;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			if (document != null) {
				try {
					document.close();
					LOG.info("End convert PDF to Images process");
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
		return totalImages;
	}

	public static Integer convertTiffToImages(FileVO fileVO, IFileSystem fileSystem) {
		int numPages = 0;
		try {
			LOG.info("Start convert TIF to Images process");
			ImageInputStream is = ImageIO.createImageInputStream(fileVO.getInputStream());
			if (is == null || is.length() == 0) {
				throw new ServiceException("Unnable to read TIF file");
			}
			Iterator<ImageReader> iterator = ImageIO.getImageReaders(is);
			if (iterator == null || !iterator.hasNext()) {
				throw new IOException("Image file format not supported by ImageIO: " + fileVO.getFileName());
			}
			ImageReader reader = (ImageReader) iterator.next();
			iterator = null;
			reader.setInput(is);
			numPages = reader.getNumImages(true);
			for (int i = 0; i < numPages; i++) {
				long initTime = new Date().getTime();
				fileSystem.createImage(reader.read(i), i, fileVO.getPath(), BUCKET_NAMES_ENUM.TEMPLATE);
				long endTime = new Date().getTime();
				LOG.info("Saved image page_" + String.format("%03d", i + 1) + ".jpg, total time "
						+ (endTime - initTime));
			}
			reader.dispose();
		} catch (IOException ioe) {
			LOG.error(ioe.getMessage(), ioe);
		} catch (ServiceException se) {
			LOG.error(se.getMessage(), se);
		}
		LOG.info("End convert TIF to Images process");
		return numPages;
	}

	/**
	 * Validate if a String is equal to one of the allowed data types
	 * 
	 * @param dataType
	 * @return
	 */
	public static boolean validateDataType(String dataType) {
		if (dataType.equals(VALIDATION_TYPES.alphanumeric.toString())
				|| dataType.equals(VALIDATION_TYPES.number.toString())
				|| dataType.equals(VALIDATION_TYPES.date.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * Set a java.awt.Rectangle from Pixels
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param pageHeight
	 * @return java.awt.Rectangle
	 */
	public static java.awt.Rectangle getRectangleFromPixeltoPDFBox(float x, float y, float width, float height) {
		float scale = PIXELS_TO_POINTS_ESCALE;

		int newWidth = Math.round(width * scale);
		int newHeight = Math.round(height * scale);
		int upperLeftX = Math.round(x * scale);
		int lowerLeftY = Math.round(y * scale);

		java.awt.Rectangle result = new java.awt.Rectangle(upperLeftX, lowerLeftY, newWidth, newHeight);

		LOG.debug("Rectangle =" + result.toString());
		return result;
	}

	public static String getImagePath(String fileName, int pageNumber, boolean isNew, BUCKET_NAMES_ENUM bucketEnum) {
		String dir = StringUtils.EMPTY;

		if (isNew) {
			dir = fileName + "/page_" + String.format("%03d", pageNumber + 1) + ".jpg";
		} else {
			dir = getFilePath(fileName, bucketEnum) + "page_" + String.format("%03d", pageNumber) + ".jpg";
		}

		return dir;
	}

	/**
	 * 
	 * @param fileName
	 * @param bucketEnum
	 * @return String
	 */
	public static String getFilePath(String fileName, BUCKET_NAMES_ENUM bucketEnum) {
		String path = null;

		switch (bucketEnum) {
		case TEMPLATE:
			// i.e.=>gbltr/gbltr_602_DISCOVER_daDDFSHP/gbltr_602_DISCOVER_daDDFSHP.pdf
			// get=>gbltr_602_DISCOVER_daDDFSHP.pdf
			// return=>gbltr/gbltr_602_DISCOVER_daDDFSHP/
			path = fileName.substring(0, fileName.indexOf('_')) + "/" + FilenameUtils.removeExtension(fileName) + "/";
			break;
		case EXTRACTION:
			// i.e.=>2016/04/docid=44491140678_account=8501190971_DocType=judgment_DocDate=12-12-2005/consumerName.jpg
			// get=>2016/04/docid=44491140678_account=8501190971_DocType=judgment_DocDate=12-12-2005.pdf
			// return=>2016/04/docid=44491140678_account=8501190971_DocType=judgment_DocDate=12-12-2005/
			path = FilenameUtils.removeExtension(fileName) + "/";
			break;
		case ORIGINAL:
			// i.e.=>2016/04/docid=44491140678_account=8501190971_DocType=judgment_DocDate=12-12-2005.pdf
			// get=>2016/04/docid=44491140678_account=8501190971_DocType=judgment_DocDate=12-12-2005.pdf
			// return=>""
			path = "";
			break;
		default:
			path = "";
			break;
		}

		return path;
	}

	/**
	 * Validate if a extension is supported by the business or not
	 * 
	 * @param extension
	 * @return boolean
	 */
	public static boolean validateFileExtension(String fileExtension) {
		if (fileExtension == null) {
			return false;
		}

		for (EXTENSION_TYPES extensionType : EXTENSION_TYPES.values()) {
			if (StringUtils.equalsIgnoreCase(extensionType.toString(), fileExtension)) {
				return true;
			}
		}
		return false;
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
