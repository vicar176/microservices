package com.mcmcg.dia.profile.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmcg.dia.profile.aop.DiagnosticsAspect;
import com.mcmcg.dia.profile.dao.DocumentFieldDefinitionDAO;
import com.mcmcg.dia.profile.dao.N1qlQueryDAO;
import com.mcmcg.dia.profile.dao.TemplateMappingDAO;
import com.mcmcg.dia.profile.dao.TemplateMappingHistoryDAO;
import com.mcmcg.dia.profile.exception.PersistenceException;
import com.mcmcg.dia.profile.exception.ServiceException;
import com.mcmcg.dia.profile.model.DocumentFieldsDefinitionModel.DocumentFieldDefinition;
import com.mcmcg.dia.profile.model.FieldDefinitionModel;
import com.mcmcg.dia.profile.model.OaldProfileModel.DocumentType;
import com.mcmcg.dia.profile.model.TemplateMappingProfileModel.ReferenceArea;
import com.mcmcg.dia.profile.model.TemplateMappingProfileModel.Seller;
import com.mcmcg.dia.profile.model.TemplateMappingProfileModel.ZoneMapping;
import com.mcmcg.dia.profile.model.TemplateMappingProfileModel.ZoneMapping.ZoneFieldDefinition;
import com.mcmcg.dia.profile.model.domain.DocumentFieldsDefinition;
import com.mcmcg.dia.profile.model.domain.PagedResponse;
import com.mcmcg.dia.profile.model.domain.Response;
import com.mcmcg.dia.profile.model.domain.Response.Error;
import com.mcmcg.dia.profile.model.domain.TemplateMappingProfile;
import com.mcmcg.dia.profile.model.entity.TemplateMappingEntity;
import com.mcmcg.dia.profile.model.entity.TemplateMappingHistoryEntity;
import com.mcmcg.dia.profile.model.query.CustomQueryBuilder;
import com.mcmcg.dia.profile.util.EventCode;
import com.mcmcg.dia.profile.util.MediaProfileUtil;
import com.mcmcg.dia.profile.util.MetaDataUtil;

/**
 * 
 * @author wporras
 *
 */
@Service
public class TemplateMappingService extends BaseService {

	private static final Logger LOG = Logger.getLogger(TemplateMappingService.class);

	@Autowired
	@Qualifier("templateMappingDAO")
	TemplateMappingDAO templateMappingDAO;

	@Autowired
	@Qualifier("templateMappingHistoryDAO")
	TemplateMappingHistoryDAO templateMappingHistoryDAO;

	@Autowired
	DocumentFieldDefinitionDAO documentFieldDefinitionDAO;

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	private DiagnosticsAspect diagnosticsAspect;

	/**
	 * Save Template Mapping Profile
	 * 
	 * @param templateMapping
	 * @return Response<TemplateMappingProfile>
	 * @throws PersistenceException
	 * @throws ServiceException
	 */
	public TemplateMappingProfile saveTemplateMappingProfile(TemplateMappingProfile templateMapping)
			throws ServiceException, PersistenceException {

		return saveTemplateMappingProfile(templateMapping, null);
	}

	/**
	 * Save Template Mapping Profile with transaction
	 * 
	 * @param templateMapping
	 * @param transaction
	 * @return Response<TemplateMappingProfile>
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	private TemplateMappingProfile saveTemplateMappingProfile(TemplateMappingProfile templateMapping, String id)
			throws ServiceException, PersistenceException {

		LOG.debug("Save Template Mapping started");
		Response<TemplateMappingProfile> response = new Response<TemplateMappingProfile>();
		TemplateMappingHistoryEntity templateHistory = null;
		Error status = new Error();
		
		try {
			if (!StringUtils.isBlank(id)) {
				if (!id.equals(templateMapping.getId())) {
					String message = String.format("The path ID = %s and the payload Id = %s are not equals ", id,
							templateMapping.getId());
					throw new ServiceException(message);
				}

				TemplateMappingEntity templateMappingEntity = templateMappingDAO.findOne(id);
				if (templateMappingEntity == null) {
					String message = String.format("Template Mapping with ID %s not found", id);
					throw new ServiceException(message);
				}

				templateMapping.setVersion(templateMappingEntity.getVersion() + 1);
				templateMapping.setCreateDate(templateMappingEntity.getCreateDate());
			} else {
				templateMapping.setVersion(1L);
			}

			// Validate Template Mapping
			validateTemplateMapping(templateMapping);

			// Find template mapping by documentType, Seller and OriginalLenders
			boolean hasAffinities = false;
			if (templateMapping.getAffinities() != null) {
				hasAffinities = true;
			}

			String documentTypeCode = templateMapping.getDocumentType().getCode();
			Long sellerId = templateMapping.getSeller().getId();
			Set<String> originalLenderList = templateMapping.getOriginalLenders(); 

			List<TemplateMappingEntity> templateMappingList = internalFindByDocumentTypeAndSellerAndOriginalLenderList(
					documentTypeCode, sellerId, originalLenderList, hasAffinities);

			// Is it an update?
			if (StringUtils.isNotBlank(templateMapping.getId())) {

				if (templateMappingList == null) {
					throw new ServiceException(String.format(
							"Not Template Mapping found for Media Type Code = %s, Seller ID = %s and Lenders = %s",
							templateMapping.getDocumentType().getCode(), templateMapping.getSeller().getId(),
							MediaProfileUtil.arrayToTruncateString(originalLenderList, 100)));
				}

				validateLendersAndAffinities(templateMapping, templateMappingList, hasAffinities, true);

			} else if (templateMappingList != null) { // It is save
				validateLendersAndAffinities(templateMapping, templateMappingList, hasAffinities, false);
			}
			
			save(templateMapping);
			
			templateHistory = new TemplateMappingHistoryEntity(templateMapping);
			templateMappingHistoryDAO.save(templateHistory);
			
			status.setCode(EventCode.OBJECT_CREATED.getCode());
			status.setMessage("Operation save Template Mapping Profile executed succesfully for ID " + templateMapping.getId());
			response.setData(templateMapping);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException("Failed to run operation save Template Mapping Profile", e);
		}

		LOG.debug("Save Template Mapping finished");
		return templateMapping;

	}

	/**
	 * Update Template Mapping Profile
	 * 
	 * @param id
	 * @param templateMapping
	 * @return Response<TemplateMappingProfile>
	 * @throws Exception
	 */
	public TemplateMappingProfile updateTemplateMappingProfile(String id, TemplateMappingProfile templateMapping)
			throws PersistenceException, ServiceException {

		LOG.debug("Update Template Mapping started for ID " + id);
		TemplateMappingProfile domain = null;

		try {
			// Save new TemplateMappingDomain
			domain = saveTemplateMappingProfile(templateMapping, id);
		} catch (PersistenceException e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException("Failed to run operation update Template Mapping Profile for ID " + id, e);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}

		LOG.debug("Update Template Mapping finished for ID " + id);
		return domain;
	}
	
	/**
	 * Find Template Mapping Profiles by DocumentType, Seller and OriginalLenders
	 * List. Filter by affinity level
	 * 
	 * @param documentTypeCode
	 * @param sellerId
	 * @param originalLenders
	 * @param hasAffinities
	 * @return List<TemplateMappingProfile>
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public List<TemplateMappingProfile> findByDocumentTypeAndSellerAndOriginalLenderList(String documentTypeCode,
			Long sellerId, Set<String> originalLenders, boolean hasAffinities)
			throws ServiceException, PersistenceException {

		LOG.debug("FindBy Template Mapping started");
		List<TemplateMappingProfile> templateMappingList = null;

		try {
			// find entities
			List<TemplateMappingEntity> entitiesList = internalFindByDocumentTypeAndSellerAndOriginalLenderList(
					documentTypeCode, sellerId, originalLenders, hasAffinities);
			if (entitiesList != null && !entitiesList.isEmpty()) {
				templateMappingList = new ArrayList<TemplateMappingProfile>();
				for (TemplateMappingEntity entity : entitiesList) {
					templateMappingList
							.add(MediaProfileUtil.buildTemplateMappingDomainFromTemplateMappingEntity(entity));
				}
			}

		} catch (JsonParseException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(), e);
		}
		LOG.debug("FindBy Template Mapping finished");
		return templateMappingList;

	}

	public TemplateMappingProfile retrieveByVersion(String id, Long version)
			throws ServiceException, PersistenceException {

		LOG.debug("FindByVersion Template Mapping started");
		TemplateMappingProfile templateMappingProfile = null;
		try {
			TemplateMappingHistoryEntity historyEntity = templateMappingHistoryDAO.findOne(id + "_" + version);
			templateMappingProfile = MediaProfileUtil
					.buildTemplateMappingDomainFromTemplateMappingEntity(historyEntity);
			templateMappingProfile.setId(id);
		} catch (JsonParseException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException();
		}
		LOG.debug("FindByVersion Template Mapping finished");
		return templateMappingProfile;
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public List<Long> retrieveVersionsById(String id)
			throws ServiceException, PersistenceException {

		LOG.debug("retrieveVersionsById Template Mapping started");
		List<Long> versionList = null;
		try {
			TemplateMappingEntity entity = templateMappingDAO.findOne(id);
			if (entity != null){
				Long version = entity.getVersion();			
				versionList = fillVersionsIntoList(versionList, version);
			}	
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException();
		}
		LOG.debug("retrieveVersionsById Template Mapping finished");
		
		return versionList;
	}


	/**
	 * Delete Template Mapping by Id
	 * 
	 * @param id
	 * @return Response<TemplateMappingProfile>
	 */
	public Response<TemplateMappingProfile> deleteTemplateMappingProfile(String id) {

		LOG.info("Delete Template Mapping started for ID " + id);
		Response<TemplateMappingProfile> response = new Response<TemplateMappingProfile>();
		return response;
	}

	/**
	 * 
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 */
	public PagedResponse<Object> retrieveTemplateMappings(String filter, String sort, int page, int size)
			throws ServiceException, PersistenceException {
		// Get Count
		int total = getTotalCount(N1qlQueryDAO.GET_COUNT_TEMPLATEMAPPING, filter, null);

		// Execute the query
		List<JsonObject> results = filter(N1qlQueryDAO.GET_ALL_TEMPLATEMAPPING, filter, null, sort, page, size);

		List<Object> parseResult = parseJsonObjectToObject(results);

		return new PagedResponse<Object>(total, CustomQueryBuilder.calculateStartIndex(page, size), size, parseResult);
	}

	// ***************************************************************************************************
	//
	// PRIVATE METHODS
	//
	// ***************************************************************************************************

	/**
	 * Generates an UUID if required and then saves it
	 * 
	 * @param transaction
	 * @param templateMappingProfile
	 * @throws JsonProcessingException
	 * @throws ParseException
	 * @throws PersistenceException
	 */
	public void save(TemplateMappingProfile templateMappingProfile) throws JsonProcessingException, ParseException {
		long start = System.currentTimeMillis();
		
		if (StringUtils.isBlank(templateMappingProfile.getId())) {
			String id = UUID.randomUUID().toString();
			templateMappingProfile.setId(id);
		}
		templateMappingDAO
				.save(MediaProfileUtil.buildTemplateMappingEntityFromTemplateMappingDomain(templateMappingProfile));
		
		long end = System.currentTimeMillis();
		
		diagnosticsAspect.log("saveTemplateMappingProfile", new Object[] {templateMappingProfile.getId()}, "Couchbase-Writes", start, end);
	}
	
	private List<TemplateMappingEntity> internalFindByDocumentTypeAndSellerAndOriginalLenderList(String documentTypeCode,
			Long sellerId, Set<String> originalLenders, boolean hasAffinities) throws PersistenceException,
					ServiceException, JsonParseException, JsonMappingException, IOException {

		List<TemplateMappingEntity> templateMappingList = null;
		String affinities = "missing";
		if (hasAffinities) {
			affinities = "array";
		}

		// Make lenders IN clause string and replace "\"
		StringBuilder lenders = new StringBuilder();
        for (String lender : originalLenders) {
        	if(lenders.length() == 0 ){
        		lenders.append("'").append(lender.replaceAll("\"", "\\\\\"")).append("'");
        	}else{        		
        		lenders.append(",").append("'").append(lender.replaceAll("\"", "\\\\\"")).append("'");
        	}
        }
		
		String filter = String.format("documentType.code=\"%s\"|seller.id=%s|TYPE(affinities) = \"%s\"",
				documentTypeCode, sellerId, affinities);

		List<JsonObject> results = filter(N1qlQueryDAO.GET_ALL_TEMPLATEMAPPING_FOR_AFFINITIES, filter, null, null, 0, 0, lenders.toString());

		if (results != null && !results.isEmpty()) {
			templateMappingList = new ArrayList<TemplateMappingEntity>();
			ObjectMapper mapper = new ObjectMapper();
			for (JsonObject json : results) {

				TemplateMappingEntity entity = mapper.readValue(json.toString(), TemplateMappingEntity.class);
				templateMappingList.add(entity);
			}
		}

		return templateMappingList;

	}

	/**
	 * Validate Template Mapping Profile
	 * 
	 * @param templateMapping
	 * @throws ServiceException
	 * @throws PersistenceException
	 * @throws ParseException 
	 */
	private void validateTemplateMapping(TemplateMappingProfile templateMapping)
			throws ServiceException, PersistenceException, ParseException {

		// Validate required objects
		if (templateMapping == null || templateMapping.getDocumentType() == null
				|| templateMapping.getOriginalLenders() == null || templateMapping.getSeller() == null) {
			throw new ServiceException("Template Mapping Profile not valid");
		}

		// Validate objects
		DocumentType documentType = templateMapping.getDocumentType();
		Set<String> originalLenders = templateMapping.getOriginalLenders();
		Seller seller = templateMapping.getSeller();

		if (documentType.getId() == null || StringUtils.isBlank(documentType.getCode())) {
			throw new ServiceException("Media type not valid");
		}

		if (originalLenders.isEmpty() || originalLenders.contains(null) || originalLenders.contains("")){
			throw new ServiceException("Original Lender not valid");
		}

		if (seller.getId() == null || StringUtils.isBlank(seller.getName())) {
			throw new ServiceException("Original Lender not valid");
		}

		if (StringUtils.isBlank(templateMapping.getUpdatedBy())) {
			throw new ServiceException("The user name is required");
		}

		// Validate template
		validateTemplate(templateMapping);

		// Validate all required FieldDefinitions
		validateRequiredFieldDefinitions(templateMapping.getZoneMappings(),
				templateMapping.getDocumentType().getCode());
	}

	/**
	 * Validate all required FieldDefinitions according to the selected
	 * documentType are defined
	 * 
	 * @param zoneMappings
	 * @param documentTypeCode
	 * @throws PersistenceException
	 * @throws ServiceException
	 * @throws ParseException 
	 */
	private void validateRequiredFieldDefinitions(List<ZoneMapping> zoneMappings, String documentTypeCode)
			throws PersistenceException, ServiceException, ParseException {

		DocumentFieldsDefinition documentfieldDefinition = null;
		List<ZoneFieldDefinition> zoneFieldDefinitions = new ArrayList<ZoneFieldDefinition>();
		List<ZoneFieldDefinition> zoneFieldDefinitionsCopy = null;

		// Find the Field Definitions for this media type
		documentfieldDefinition = MediaProfileUtil.buildDocumentFieldDefinitionFromEntity(
				documentFieldDefinitionDAO.findByDocumentTypeCode(documentTypeCode));

		if (documentfieldDefinition == null) {
			throw new ServiceException("Not Field Definitions found for Media Type Code = " + documentTypeCode);
		}

		// Get a list of FieldDefinitions to be validated
		for (ZoneMapping zoneMapping : zoneMappings) {
			zoneFieldDefinitions.add(zoneMapping.getFieldDefinition());
		}
		zoneFieldDefinitionsCopy = new ArrayList<ZoneFieldDefinition>(zoneFieldDefinitions);

		// Validated all required Field Definitions
		for (DocumentFieldDefinition documentField : documentfieldDefinition.getFieldDefinitions()) {
			FieldDefinitionModel fieldDefinitionDB = documentField.getFieldDefinition();
			boolean isDefined = false;

			for (ZoneFieldDefinition zoneFieldDefinition : zoneFieldDefinitions) {
				if (fieldDefinitionDB.getFieldName().equals(zoneFieldDefinition.getFieldName())) {
					// Validate it is properly defined
					if (!fieldDefinitionDB.getFieldDescription().equals(zoneFieldDefinition.getFieldDescription())) {
						throw new ServiceException(String.format(
								"The Field Definition %s has an incorrect Field Description value (%s) for "
										+ "the Media Type Code %s. It must be (%s)",
								fieldDefinitionDB.getFieldName(), zoneFieldDefinition.getFieldDescription(),
								documentTypeCode, fieldDefinitionDB.getFieldDescription()));
					} else if (!fieldDefinitionDB.getFieldType().equals(zoneFieldDefinition.getFieldType())) {
						throw new ServiceException(String.format(
								"The Field Definition %s has an incorrect Field Type value (%s) for "
										+ "the Media Type Code %s. It must be (%s)",
								fieldDefinitionDB.getFieldName(), zoneFieldDefinition.getFieldType(), documentTypeCode,
								fieldDefinitionDB.getFieldType()));
					} else if (documentField.isRequired() != zoneFieldDefinition.isFieldRequired()) {
						throw new ServiceException(String.format(
								"The Field Definition %s has an incorrect Field Required value (%s) for "
										+ "the Media Type Code %s. It must be (%s)",
								fieldDefinitionDB.getFieldName(), zoneFieldDefinition.isFieldRequired(), documentTypeCode,
								documentField.isRequired()));
					}

					isDefined = true;
					zoneFieldDefinitionsCopy.remove(zoneFieldDefinition);
					break;
				}
			}
			if ((documentField.isRequired() && documentField.getFieldDefinition().isActive()) && !isDefined) {
				throw new ServiceException(String.format("The Field Definition %s is required for Media Type Code %s",
						fieldDefinitionDB.getFieldName(), documentTypeCode));
			}
		}

		// Validate not extra Field Definitions
		if (zoneFieldDefinitionsCopy.size() > 0) {
			throw new ServiceException(String.format("The Field Definition %s is not defined for Media Type Code %s",
					zoneFieldDefinitionsCopy.get(0).getFieldName(), documentTypeCode));
		}
	}

	/**
	 * Validate a template of a Template Mapping Profile
	 * 
	 * @param template
	 * @throws ServiceException
	 */
	private void validateTemplate(TemplateMappingProfile template) throws ServiceException {

		int totalPages = template.getTotalPages();
		// Validate required objects
		if (template.getReferenceAreas() == null || template.getReferenceAreas().isEmpty()) {
			throw new ServiceException("The reference areas are required");
		}

		if (template.getZoneMappings() == null || template.getZoneMappings().isEmpty()) {
			throw new ServiceException("The zone mappings are required");
		}

		// Validated attributes
		if (StringUtils.isBlank(template.getName())) {
			throw new ServiceException("The template mapping name is required");
		}
		if (StringUtils.isBlank(template.getSampleFileName())) {
			throw new ServiceException("The sample file name is required");
		}
		if (totalPages < 1) {
			throw new ServiceException("The total pages is not valid");
		}

		// validate Affinities
		if (template.getAffinities() != null) {
			Set<String> affinities = template.getAffinities();
			if (affinities.isEmpty()) {
				throw new ServiceException("Affinity list not valid");
			}
			for (String affinity : affinities) {
				if (StringUtils.isBlank(affinity)) {
					throw new ServiceException("Affinity list not valid");
				}
			}
		}

		// Validate Reference Areas
		Set<Integer> referenceAreaPages = validateReferenceAreas(template.getReferenceAreas(), totalPages);

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
			} else {
				for (Float zone : zoneArea) {
					if (zone < 10) {
						throw new ServiceException(
								String.format("The Reference Area for the page %s has an invalid Zone Area",
										referenceArea.getPageNumber()));
					}
				}
			}

			// validate dataType
			if (!MetaDataUtil.validateDataType(referenceArea.getFieldType())) {
				throw new ServiceException(String.format("The Reference Area for the page %s has an invalid field Type",
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
//			if (zoneMapping.getFieldDefinition() == null || zoneMapping.getZoneArea() == null
//					|| zoneMapping.getPageNumber() < 1) {
//				throw new ServiceException("A Zone Mapping is not valid");
//			}

			// Validate Data Element
			ZoneFieldDefinition zoneFieldDefinition = zoneMapping.getFieldDefinition();
			if (StringUtils.isBlank(zoneFieldDefinition.getFieldType())
					|| StringUtils.isBlank(zoneFieldDefinition.getFieldDescription())
					|| StringUtils.isBlank(zoneFieldDefinition.getFieldName())) {
				throw new ServiceException("A Field Definition is not valid");
			}

			// Validate Zone Mapping are not repeated
			if (Collections.frequency(zoneMappingNames, zoneFieldDefinition.getFieldName()) > 0) {
				throw new ServiceException(
						"There are more than one Zone Mapping for " + zoneFieldDefinition.getFieldName());
			}
			zoneMappingNames.add(zoneFieldDefinition.getFieldName());

			// Validate page number
			if (zoneMapping.getPageNumber() > totalPages) {
				throw new ServiceException(
						String.format("The Zone Mapping %s has a greater page number (%s) than Total Pages (%s)",
								zoneFieldDefinition.getFieldName(), zoneMapping.getPageNumber(), totalPages));
			}

			// Validate this zone's page has a reference area
//			if (Collections.frequency(referenceAreaPages, zoneMapping.getPageNumber()) == 0) {
//				throw new ServiceException(String.format("The Zone Mapping %s needs a Reference Area for page %s",
//						zoneFieldDefinition.getFieldName(), zoneMapping.getPageNumber()));
//			}

			// Validate fieldType
			if (!MetaDataUtil.validateDataType(zoneFieldDefinition.getFieldType())) {
				throw new ServiceException(String.format("The Zone Mapping %s has an invalid Data Type (%s)",
						zoneFieldDefinition.getFieldName(), zoneFieldDefinition.getFieldType()));
			}

			// Validate ZoneArea
			List<Float> zoneArea = zoneMapping.getZoneArea();
			if (zoneArea.isEmpty() || zoneArea.size() != 4 || zoneArea.contains(null)) {
				throw new ServiceException(
						String.format("The Zone Mapping %s has an invalid Zone Area", zoneFieldDefinition.getFieldName()));
			} else {
				for (Float zone : zoneArea) {
					if (zone < 10) {
						throw new ServiceException(String.format(
								"The Zone Mapping %s has a very small Zone Area (A coordinate is lower than 10)",
								zoneFieldDefinition.getFieldName()));
					}
				}
			}
		}
	}
	
	/**
	 * An affinity can only be associated to only one instance of a Template
	 * Mapping for a Document type/seller/lender. Also there can be only one template for a seller/lender which has no affinities.
	 * 
	 * @param templateMapping
	 * @param templateMappingList
	 * @param hasAffinities 
	 * @throws ServiceException
	 */
	private void validateLendersAndAffinities(TemplateMappingProfile templateMapping,
			List<TemplateMappingEntity> templateMappingList, boolean hasAffinities, boolean isUpdate)
			throws ServiceException {

		if (hasAffinities) {
			for (TemplateMappingEntity templateResponse : templateMappingList) {

				if (isUpdate && StringUtils.equals(templateMapping.getId(), templateResponse.getId())) {
					continue;
				}

				for (String affinity : templateResponse.getAffinities()) {
					if (templateMapping.getAffinities().contains(affinity) && CollectionUtils
							.containsAny(templateMapping.getOriginalLenders(), templateResponse.getOriginalLenders())) {

						throw new ServiceException(String.format(
								"A Template Mapping for the affinity %s already exists for "
										+ "Media Type Code = %s, Seller ID = %s and Lenders = %s",
								affinity, templateMapping.getDocumentType().getCode(),
								templateMapping.getSeller().getId(),
								MediaProfileUtil.arrayToTruncateString(templateMapping.getOriginalLenders(), 100)));
					}
				}
			}
		} else {			
			for (TemplateMappingEntity templateResponse : templateMappingList) {
				if (!StringUtils.equals(templateMapping.getId(), templateResponse.getId())) {

					throw new ServiceException(String.format(
							"A Template Mapping without affinities already exists for "
									+ "Media Type Code = %s, Seller ID = %s and Lenders = %s",
							templateMapping.getDocumentType().getCode(), templateMapping.getSeller().getId(),
							MediaProfileUtil.arrayToTruncateString(templateResponse.getOriginalLenders(), 100)));
				}
			}
		}
	}
}