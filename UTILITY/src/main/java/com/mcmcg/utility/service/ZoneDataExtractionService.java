package com.mcmcg.utility.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mcmcg.utility.aop.DiagnosticsAspect;
import com.mcmcg.utility.domain.DataElement;
import com.mcmcg.utility.domain.FieldDefinition;
import com.mcmcg.utility.domain.TemplateMappingProfileModel;
import com.mcmcg.utility.domain.TemplateMappingProfileModel.ReferenceArea;
import com.mcmcg.utility.domain.TemplateMappingProfileModel.ZoneMapping;
import com.mcmcg.utility.exception.S3Exception;
import com.mcmcg.utility.exception.ServiceException;
import com.mcmcg.utility.file.FileVO;
import com.mcmcg.utility.file.IFileSystem;
import com.mcmcg.utility.util.MetaDataUtil;
import com.mcmcg.utility.util.MetaDataUtil.BUCKET_NAMES_ENUM;
import com.mcmcg.utility.util.MetaDataUtil.VALIDATION_TYPES;

/**
 * 
 * @author wporras
 *
 */
@Service
public class ZoneDataExtractionService {

	private static final String ERROR_EXTRACTING_TEXT_OF_THE_PDF = "Error extracting text of the PDF";

	private static final Logger LOG = Logger.getLogger(ZoneDataExtractionService.class);

	@Value("${sample.document.source.dir}")
	private String SOURCE_DIR;

	@Value("${getImage.timeout.milliseconds}")
	private int timeout;

	@Autowired
	private IFileSystem fileSystem;

	@Autowired
	DiagnosticsAspect diagnosticsAspect;

	/**
	 * Extract text by Text or OCR
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param pageNumber
	 * @param sourceFile
	 * @param type
	 * @param validation
	 * @return String
	 * @throws ServiceException
	 */
	public String extractZoneTextByType(float x, float y, float width, float height, int pageNumber, String sourceFile,
			String type, String validation, String dateFormat) throws ServiceException, S3Exception {

		String extractedText = null;

		try {
			validateCoordinates(x, y, width, height);

			String fileExtension = FilenameUtils.getExtension(sourceFile);
			if (!MetaDataUtil.validateFileExtension(fileExtension)) {
				throw new ServiceException("Source format not valid " + fileExtension);
			}

			if (fileExtension.equalsIgnoreCase(MetaDataUtil.EXTENSION_TYPES.pdf.toString())) {
				extractedText = extractZoneTextPDFBox(x, y, width, height, pageNumber, sourceFile,
						BUCKET_NAMES_ENUM.TEMPLATE, null);
			}

			// Validate extracted text
			extractedText = MetaDataUtil.validateExtractedText(extractedText, validation, dateFormat);
			LOG.info("Extracted text (" + extractedText + ") by x=" + x + ", y=" + y + ", width=" + width + ", height="
					+ height + ", pageNumber=" + pageNumber + ", sourceFile=" + sourceFile + ", type=" + type
					+ " and validation=" + validation);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}

		return extractedText;

	}

	/**
	 * 
	 * @param location
	 * @param template
	 * @param params
	 * @return
	 * @throws ServiceException
	 */
	public List<DataElement> extractDocumentData(String location, TemplateMappingProfileModel template, String bucket)
			throws ServiceException {

		List<DataElement> dataElements = new ArrayList<DataElement>();

		try {
			LOG.info("Extracting data from document " + location);

			String fileExtension = FilenameUtils.getExtension(location);
			if (!MetaDataUtil.validateFileExtension(fileExtension)) {
				throw new ServiceException("Location file format not valid " + fileExtension);
			}

			// Validate template
			validateTemplate(template);

			// Extract text from the document based on the template
			Date date = new Date();
			dataElements = extractDocumentTexts(location, template, bucket);

			LOG.debug("TIMING - Extract data from the document: " + (new Date().getTime() - date.getTime()));
			LOG.info("Extracted data from document " + location);

		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}
		return dataElements;

	}

	/**
	 * 
	 * @param location
	 * @param template
	 * @return
	 */
	public boolean createSnippets(String location, TemplateMappingProfileModel template, String bucket)
			throws ServiceException {
		boolean createSnippets = true;

		try {
			LOG.info("Extracting data from document " + location);

			String fileExtension = FilenameUtils.getExtension(location);
			if (!MetaDataUtil.validateFileExtension(fileExtension)) {
				throw new ServiceException("Location file format not valid " + fileExtension);
			}

			// Get the original file
			FileVO fileVO = null;
			Date date = new Date();
			try {
				InputStream is = fileSystem.getInputStream(location, BUCKET_NAMES_ENUM.ORIGINAL, bucket);
				fileVO = new FileVO(is, location);
				fileVO.setPath(FilenameUtils.removeExtension(location) + "/");
			} catch (Exception e) {
				throw new ServiceException("Failed to read the file: " + location);
			}
			LOG.debug("TIMING - Get original file: " + (new Date().getTime() - date.getTime()));

			// Create Folder
			fileSystem.createDirectory(SOURCE_DIR + fileVO.getPath());

			// 1 - Create page images in local
			date = new Date();
			Integer totalImages = MetaDataUtil.convertPdfToImages(fileVO, fileSystem, BUCKET_NAMES_ENUM.EXTRACTION,
					false);
			if (totalImages == null || totalImages == 0) {
				throw new ServiceException("Failed to create the page images for the file " + location);
			}
			LOG.debug("TIMING - Create page images in local: " + (new Date().getTime() - date.getTime()));

			// 3 - Create Snippets
			date = new Date();
			createSnippets(location, template, BUCKET_NAMES_ENUM.EXTRACTION);
			LOG.debug("TIMING - Create Snippets: " + (new Date().getTime() - date.getTime()));

			// 4 - Upload snippets to S3
			date = new Date();

			try {
				fileSystem.uploadSnippetsFromLocal(location);
			} catch (Exception e) {
				throw new ServiceException(
						"An error occurred while trying to upload the snippets to S3. Location = " + location);
			}

			LOG.debug("TIMING - Upload snippets to S3: " + (new Date().getTime() - date.getTime()));

			// 5 - Delete all local files created in the previous steps
			fileSystem.deleteDirectory(SOURCE_DIR + fileVO.getPath());

			LOG.info("Extracted data from document " + location);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}

		return createSnippets;

	}

	/**
	 * Extract text by PDF reading using PDFBox
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param pageNumber
	 * @param sourceFile
	 * @param validation
	 * @return String
	 * @throws ServiceException
	 */
	private String extractZoneTextPDFBox(float x, float y, float width, float height, int pageNumber, String sourceFile,
			BUCKET_NAMES_ENUM bucketEnum, String bucket) throws ServiceException, S3Exception {

		String extractedText;
		PDDocument document = null;
		PDFTextStripperByArea stripper = null;

		try {
			LOG.info("Extracting text by PDF x=" + x + ", y=" + y + ", width=" + width + ", height=" + height
					+ ", pageNumber=" + pageNumber + ", and sourceFile=" + sourceFile);
			try {
				InputStream is = fileSystem.getInputStream(sourceFile, bucketEnum, bucket);
				document = PDDocument.load(is);
				is.close();
				stripper = new PDFTextStripperByArea();
			} catch (Exception e) {

				throw new S3Exception("Error trying to access the document: " + sourceFile + " => " + e.getMessage(),
						e);

			}

			// Define an equal or smaller region of interest on the image
			PDPage page = (PDPage) document.getDocumentCatalog().getPages().get(pageNumber - 1);
			stripper.setSortByPosition(true);
			java.awt.Rectangle rect = MetaDataUtil.getRectangleFromPixeltoPDFBox(x, y, width, height);
			stripper.addRegion("zone", rect);

			// Extract text
			try {
				stripper.extractRegions(page);
				stripper.setLineSeparator(" ");
				extractedText = stripper.getTextForRegion("zone");
				extractedText = extractedText.replaceAll("\\\n", "");
				extractedText = extractedText.replaceAll("\\\r", "");
				extractedText = extractedText.trim();
			} catch (IOException e) {
				throw new ServiceException(ERROR_EXTRACTING_TEXT_OF_THE_PDF, e);
			}
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}

		return extractedText;
	}

	/**
	 * Validate a list of coordinates
	 * 
	 * @param coordinates
	 * @throws ServiceException
	 */
	private void validateCoordinates(Float... coordinates) throws ServiceException {

		for (Float coordinate : coordinates) {
			if (coordinate == null || coordinate < 10) {
				throw new ServiceException(String.format("The zone coordinate (%s) is not valid", coordinate));
			}
		}
	}

	/**
	 * Extract from an Original File all Field Definitions text defined in a
	 * template
	 * 
	 * @param location
	 * @param template
	 * @return List<DataElement>
	 * @throws ServiceException
	 */

	private List<DataElement> extractDocumentTexts(String location, TemplateMappingProfileModel template, String bucket)
			throws ServiceException {

		long start = System.currentTimeMillis();

		List<DataElement> dataElements = new ArrayList<DataElement>();

		boolean failedScenario2 = false;
		try {
			LOG.info("Start extracting document texts from " + location);
			int count = 0;
			
			// Reference Areas
			int pageNumber = 0;
			String referenceValue = null;
			String extractedValue = null;
			for (ReferenceArea referenceArea : template.getReferenceAreas()) {

				// 2.1 - Verify template alignment by reference area
				List<Float> zoneReferenceArea = referenceArea.getZoneArea();
				String extractedText = StringUtils.EMPTY;
				try {
					extractedText = extractZoneTextPDFBox(zoneReferenceArea.get(0), zoneReferenceArea.get(1),
							zoneReferenceArea.get(2), zoneReferenceArea.get(3), referenceArea.getPageNumber(), location,
							BUCKET_NAMES_ENUM.ORIGINAL, bucket);

					if (!StringUtils.equalsIgnoreCase(referenceArea.getValue(), extractedText.trim())) {
						/**
							Scenario 2: The document does have a text layer, but the reference area is located over a logo or image.
								Extraction exception -> Template found (Reference area not aligned)
						 */
						failedScenario2 = true;

						pageNumber = referenceArea.getPageNumber();
						extractedValue = extractedText;
						referenceValue = referenceArea.getValue();
						
						//Mark reference are as
						dataElements.add(new DataElement(new FieldDefinition("referenceArea", "", VALIDATION_TYPES.alphanumeric.toString(), false), "", null, false, "OCR"));
						count++;
					}
					
				} catch (ServiceException e) {
					LOG.error(location + "-->" + e.getMessage(), e);
					throw e;
				}

				// 2.2 - Extract data from a page
				
				for (ZoneMapping zoneMapping : template.getZoneMappings()) {
					if (zoneMapping.getPageNumber() == referenceArea.getPageNumber()) {

						// Extract text from original file
						List<Float> zoneArea = zoneMapping.getZoneArea();
						try {
							extractedText = extractZoneTextPDFBox(zoneArea.get(0), zoneArea.get(1), zoneArea.get(2),
									zoneArea.get(3), referenceArea.getPageNumber(), location,
									BUCKET_NAMES_ENUM.ORIGINAL, bucket);

							if (StringUtils.isBlank(extractedText)){
								dataElements.add(new DataElement(zoneMapping.getFieldDefinition(), extractedText,
										zoneMapping.getFieldDefinition().getFieldName() + ".jpg", false, "OCR"));
								count++;;
							}else{
								extractedText = MetaDataUtil.validateExtractedText(extractedText,
										zoneMapping.getFieldDefinition().getFieldType(), "");
								
								dataElements.add(new DataElement(zoneMapping.getFieldDefinition(), extractedText,
										zoneMapping.getFieldDefinition().getFieldName() + ".jpg", true, "textLayer"));
							}
							
						} catch (ServiceException e) {
							if (StringUtils.equalsIgnoreCase(e.getMessage(), "No text extracted")) {
								LOG.warn("No text extracted, possible absence of Text Layer for Data Element ", e);
								dataElements.add(new DataElement(zoneMapping.getFieldDefinition(), extractedText,
										zoneMapping.getFieldDefinition().getFieldName() + ".jpg", false, "OCR"));
							} else {
								LOG.warn("Text extracted might not follow the field definition convention " + 
										  zoneMapping.getFieldDefinition().getFieldType() + " : " + extractedText, e);
								dataElements.add(new DataElement(zoneMapping.getFieldDefinition(), extractedText,
										zoneMapping.getFieldDefinition().getFieldName() + ".jpg", false, "textLayer"));
							}
						}
						
					}
				}
				

			}
			
			//Check Scenario 2: The document does have a text layer, but the reference area is located over a logo or image.
			//  Extraction exception -> Template found (Reference area not aligned)
			
			int totalMappings = template.getZoneMappings() != null ? template.getZoneMappings().size() +  template.getReferenceAreas().size() : 0; 
			
			 if (failedScenario2 && count < totalMappings){
				
				throw new ServiceException(String.format(
						"The Document %s does not align to the Template %s. The Reference Area for "
								+ "the page %s has (%s) instead of (%s)",
						location, template.getId(), pageNumber, extractedValue,	referenceValue));
			}
			 
			LOG.info("End extract document texts from " + location);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOG.error(e);
			throw new ServiceException("An error occurred while trying extract text from " + location, e);
		}

		long end = System.currentTimeMillis();

		diagnosticsAspect.log("extractAllZonesFromPdf", new Object[] { template.getId() }, "PDF-Extraction", start,
				end);

		return dataElements;
	}

	/**
	 * Create snippets of the Reference Areas and Field Definitions of a
	 * original file. I crops the local already generated page images into the
	 * defined zones in the template.
	 * 
	 * @param location
	 * @param template
	 * @param bucketEnum
	 * @throws ServiceException
	 */
	private void createSnippets(String location, TemplateMappingProfileModel template, BUCKET_NAMES_ENUM bucketEnum)
			throws ServiceException {
		try {
			LOG.info("Start create Snippets for: " + location);
			// Reference Areas
			for (ReferenceArea referenceArea : template.getReferenceAreas()) {
				BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(
						fileSystem.getImage(location, referenceArea.getPageNumber(), bucketEnum, false)));

				// Define an smaller region of interest on the image
				List<Float> zoneArea = referenceArea.getZoneArea();
				BufferedImage biSnippet = bImage.getSubimage(zoneArea.get(0).intValue(), zoneArea.get(1).intValue(),
						zoneArea.get(2).intValue(), zoneArea.get(3).intValue());
				fileSystem.createSnippet(biSnippet, location, "referenceArea_page_" + referenceArea.getPageNumber());

				// Zone Mappings of page x
				for (ZoneMapping zoneMapping : template.getZoneMappings()) {
					if (zoneMapping.getPageNumber() == referenceArea.getPageNumber()) {
						// Define an smaller region of interest on the image
						zoneArea = zoneMapping.getZoneArea();
						biSnippet = bImage.getSubimage(zoneArea.get(0).intValue(), zoneArea.get(1).intValue(),
								zoneArea.get(2).intValue(), zoneArea.get(3).intValue());
						fileSystem.createSnippet(biSnippet, location, zoneMapping.getFieldDefinition().getFieldName());
					}
				}
			}
			LOG.info("End create Snippets for: " + location);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(
					"An error occurred while trying to create the snippets for " + location + " " + e.getMessage(), e);
		}
	}

	/**
	 * Validate a template of a Template Mapping Profile
	 * 
	 * @param template
	 * @throws ServiceException
	 */
	private void validateTemplate(TemplateMappingProfileModel template) throws ServiceException {

		int totalPages = template.getTotalPages();


		if (template.getZoneMappings() == null || template.getZoneMappings().isEmpty()) {
			throw new ServiceException("The zone mappings are required");
		}

		// Validated attributes
		if (totalPages < 1) {
			throw new ServiceException("The total pages is not valid");
		}

		Set<Integer> referenceAreaPages = null ;
		// Validate required objects
		if (!(template.getReferenceAreas() == null) ) 
			// Validate Reference Areas
			 referenceAreaPages = validateReferenceAreas(template.getReferenceAreas(), totalPages);
	

		// Validate Zone Mappings
		validateZoneMappings(template.getZoneMappings(), referenceAreaPages, totalPages);
	}

	/**
	 * Validate the Reference Areas of a Template. It returns the list of pages
	 * with a reference area
	 * 
	 * @param referenceAreas
	 * @return boolean
	 * @throws ServiceException
	 */
	private Set<Integer> validateReferenceAreas(List<ReferenceArea> referenceAreas, int totalPages)
			throws ServiceException {

		Set<Integer> referenceAreaPages = new HashSet<Integer>();

		for (ReferenceArea referenceArea : referenceAreas) {
			// Validate required objects
			if (StringUtils.isBlank(referenceArea.getValue()) || StringUtils.isBlank(referenceArea.getFieldType())
					|| referenceArea.getPageNumber() < 1 || referenceArea.getZoneArea() == null) {
				throw new ServiceException("A Reference Area is not valid");
			}

			// Validate page number are not repeated
			if (Collections.frequency(referenceAreaPages, referenceArea.getPageNumber()) > 0) {
				throw new ServiceException(String.format("There are more than one Reference Area for page %s",
						referenceArea.getPageNumber()));
			}
			referenceAreaPages.add(referenceArea.getPageNumber());

			// Validate page number
			if (referenceArea.getPageNumber() > totalPages) {
				throw new ServiceException(
						String.format("The Reference Area with page number %s is bigger than Total Pages",
								referenceArea.getPageNumber()));
			}

			// validate ZoneArea
			List<Float> zoneArea = referenceArea.getZoneArea();
			if (zoneArea.isEmpty() || zoneArea.size() != 4 || zoneArea.contains(null)) {
				throw new ServiceException(String.format("The Reference Area for the page %s has an invalid Zone Area",
						referenceArea.getPageNumber()));
			}
		}
		return referenceAreaPages;
	}

	/**
	 * Validate the Zone Mappings of a Template
	 * 
	 * @param zoneMappings
	 * @return boolean
	 * @throws ServiceException
	 */
	private void validateZoneMappings(List<ZoneMapping> zoneMappings, Set<Integer> referenceAreaPages, int totalPages)
			throws ServiceException {

		Set<String> zoneMappingNames = new HashSet<String>();

		for (ZoneMapping zoneMapping : zoneMappings) {
			if (zoneMapping.getFieldDefinition() == null || zoneMapping.getZoneArea() == null
					|| zoneMapping.getPageNumber() < 1) {
				throw new ServiceException("A Zone Mapping is not valid");
			}

			// Validate Data Element
			FieldDefinition fieldDefinition = zoneMapping.getFieldDefinition();
			if (StringUtils.isBlank(fieldDefinition.getFieldType())
					|| StringUtils.isBlank(fieldDefinition.getFieldDescription())
					|| StringUtils.isBlank(fieldDefinition.getFieldName())) {
				throw new ServiceException("A Field Definition is not valid");
			}

			// Validate Zone Mapping are not repeated
			if (Collections.frequency(zoneMappingNames, fieldDefinition.getFieldName()) > 0) {
				throw new ServiceException(
						"There are more than one Zone Mapping for " + fieldDefinition.getFieldName());
			}
			zoneMappingNames.add(fieldDefinition.getFieldName());

			// Validate page number
			if (zoneMapping.getPageNumber() > totalPages) {
				throw new ServiceException(
						String.format("The Zone Mapping %s has a greater page number (%s) than Total Pages (%s)",
								fieldDefinition.getFieldName(), zoneMapping.getPageNumber(), totalPages));
			}

//			// Validate this zone's page has a reference area
//			if (Collections.frequency(referenceAreaPages, zoneMapping.getPageNumber()) == 0) {
//				throw new ServiceException(String.format("The Zone Mapping %s needs a Reference Area for page %s",
//						fieldDefinition.getFieldName(), zoneMapping.getPageNumber()));
//			}

			// Validate ZoneArea
			List<Float> zoneArea = zoneMapping.getZoneArea();
			if (zoneArea.isEmpty() || zoneArea.size() != 4 || zoneArea.contains(null)) {
				throw new ServiceException(
						String.format("The Zone Mapping %s has an invalid Zone Area", fieldDefinition.getFieldName()));
			}
		}
	}
}
