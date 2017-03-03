package com.mcmcg.dia.profile.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmcg.dia.profile.annotation.Diagnostics;
import com.mcmcg.dia.profile.aop.DiagnosticsAspect;
import com.mcmcg.dia.profile.dao.N1qlQueryDAO;
import com.mcmcg.dia.profile.dao.OaldDAO;
import com.mcmcg.dia.profile.dao.OaldHistoryDAO;
import com.mcmcg.dia.profile.exception.PersistenceException;
import com.mcmcg.dia.profile.exception.ServiceException;
import com.mcmcg.dia.profile.model.OaldProfileModel;
import com.mcmcg.dia.profile.model.OaldProfileModel.DocumentType;
import com.mcmcg.dia.profile.model.OaldProfileModel.ProductGroup;
import com.mcmcg.dia.profile.model.domain.OaldProfile;
import com.mcmcg.dia.profile.model.domain.PagedResponse;
import com.mcmcg.dia.profile.model.domain.Response;
import com.mcmcg.dia.profile.model.entity.OaldProfileEntity;
import com.mcmcg.dia.profile.model.entity.OaldProfileHistoryEntity;
import com.mcmcg.dia.profile.model.query.CustomQueryBuilder;
import com.mcmcg.dia.profile.util.MediaProfileUtil;

/**
 * 
 * @author Victor Arias
 *
 */
@Service
public class OaldService extends BaseService {

	private static final Logger LOG = Logger.getLogger(OaldService.class);

	@Autowired
	@Qualifier("oaldDAO")
	private OaldDAO oaldDAO;

	@Autowired
	@Qualifier("oaldHistoryDAO")
	private OaldHistoryDAO oaldHistoryDAO;
	
	@Autowired
	private DiagnosticsAspect diagnosticsAspect;

	/**
	 * 
	 * @param oaldProfile
	 * @return
	 * @throws PersistenceException
	 * @throws Exception
	 */
	public OaldProfile saveOaldProfile(OaldProfile oaldProfile) throws ServiceException, PersistenceException {
		return saveOaldProfile(oaldProfile, null);
	}

	/**
	 * 
	 * @param oaldProfile
	 * @param transaction
	 * @return
	 * @throws PersistenceException
	 * @throws Exception
	 */
	private OaldProfile saveOaldProfile(OaldProfile oaldProfile, String id)
			throws ServiceException, PersistenceException {// , OaldProfile
															// oaldProfileOld) {
		LOG.debug("Save OALD Started");

		boolean isValidPortfolio = oaldProfile.getPortfolio() != null;
		boolean isValidLender = oaldProfile.getOriginalLender() != null;
		boolean isValidGroupAndTypes = validProductGroupsAndDocumentTypes(oaldProfile.getProductGroup(),
				oaldProfile.getDocumentTypes());
		Long portfolioId = null;
		String originalLenderName = StringUtils.EMPTY;
		String productGroupCode = StringUtils.EMPTY;

		try {
			if (StringUtils.isBlank(oaldProfile.getUpdatedBy())) {
				String errorMsj = "Missing required field updatedBy";
				LOG.error(errorMsj);
				throw new ServiceException(errorMsj);
			}

			if (!isValidGroupAndTypes || isValidPortfolio != isValidLender) {
				String errorMsj = "OALD is not valid";
				ServiceException se = new ServiceException(errorMsj);
				LOG.error(errorMsj, se);
				throw se;
			} else {
				productGroupCode = oaldProfile.getProductGroup().getCode();
				if (isValidPortfolio) {
					portfolioId = oaldProfile.getPortfolio().getId();
				}
				if (isValidLender) {
					originalLenderName = oaldProfile.getOriginalLender().getName();
				}
				
			}
			OaldProfileEntity possibleExistent = null;
			OaldProfileEntity current = null;

			if (id != null) {
				if (StringUtils.equals(id, oaldProfile.getId())) {
					current = oaldDAO.findOne(id);
					if (current == null) {
						String errorMsj = String.format("The ID provided %s does not exists!", id);
						ServiceException se = new ServiceException(errorMsj);
						LOG.error(errorMsj, se);
						throw se;
					} else if(!validateExistentOald(current, oaldProfile)){
						String errorMsj = String.format(
								"Unable to match the combination of ProductGroup, PortfolioId and OriginalLender in Database with the Id provided for ProductGroup = %s, PortfolioId = %s and OriginalLender = %s",
								productGroupCode, portfolioId, originalLenderName);
						ServiceException se = new ServiceException(errorMsj);
						LOG.error(errorMsj, se);
						throw se;
					}
				} else {
					String errorMsj = String.format("The Id provided %s does not match object Id %s ", id,
							oaldProfile.getId());
					ServiceException se = new ServiceException(errorMsj);
					LOG.error(errorMsj, se);
					throw se;
				}
			}

			possibleExistent = internalFindByProductGroupPortfolioOriginalLender(productGroupCode, portfolioId,
					originalLenderName);
			boolean error = false;
			
			if (possibleExistent != null) {
				if (current != null) {
					if (StringUtils.equals(current.getId(), possibleExistent.getId())) {
						oaldProfile.setCreateDate(possibleExistent.getCreateDate());
						oaldProfile.setVersion(possibleExistent.getVersion() + 1);
					} else {
						error = true;
					}
				} else {
					error = true;
				}
				if (error) {
					String errorMsj = String.format(
							"An OALD profile already exists with ProductGroupCode = %s, PortfolioId = %s and OriginalLenderName = %s",
							productGroupCode, portfolioId, originalLenderName);
					ServiceException se = new ServiceException(errorMsj);
					LOG.error(errorMsj, se);
					throw se;
				}
			} else {
				oaldProfile.setVersion(1L);
			}

			save(oaldProfile);
			OaldProfileHistoryEntity oaldHistory = new OaldProfileHistoryEntity(oaldProfile);
			oaldHistoryDAO.save(oaldHistory);

		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (Throwable e) {
			String message = StringUtils.EMPTY;
			if (StringUtils.isBlank(e.getMessage())) {
				message = e.toString();
			} else {
				message = e.getMessage();
			}
			LOG.error(message, e);
			throw new PersistenceException(message, e);
		}
		LOG.debug("Save OALD Finished");
		return oaldProfile;
	}

	/**
	 * 
	 * @param id
	 * @param oaldProfile
	 * @return
	 * @throws PersistenceException
	 * @throws Exception
	 */
	public OaldProfile updateOaldProfile(String id, OaldProfile oaldProfile)
			throws ServiceException, PersistenceException {

		return saveOaldProfile(oaldProfile, id);
	}

	/**
	 * 
	 * @param productGroupCode
	 * @param portfolioId
	 * @param originalLenderName
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws PersistenceException
	 * @throws ServiceException
	 */
	public OaldProfile findByProductGroupPortfolioIdOriginalLender(String productGroupCode, Long portfolioId,
			String originalLenderName) throws PersistenceException, ServiceException {
		OaldProfile oaldProfile = null;
		try {
			OaldProfileEntity entity = internalFindByProductGroupPortfolioOriginalLender(productGroupCode, portfolioId,
					originalLenderName);

			oaldProfile = MediaProfileUtil.buildOaldDomainFromOaldEntity(entity);

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
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(), e);
		}
		return oaldProfile;
	}

	public OaldProfile findByVersion(String id, long version) throws PersistenceException, ServiceException {
		OaldProfile oaldProfile = null;
		try {
			OaldProfileHistoryEntity entity = oaldHistoryDAO.findOne(id + "_" + version);
			oaldProfile = MediaProfileUtil.buildOaldDomainFromOaldEntity(entity);
			oaldProfile.setId(id);
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
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(), e);
		}
		return oaldProfile;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Response<OaldProfile> deleteOaldProfile(String id) {
		Response<OaldProfile> response = new Response<OaldProfile>();
		return response;
	}

	/**
	 * 
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 */
	public PagedResponse<Object> retrieveOalds(String filter, String sort, int page, int size)
			throws ServiceException, PersistenceException {
		// Get Count
		int total = getTotalCount(N1qlQueryDAO.GET_COUNT_OALD, filter, null);

		// Execute the query
		List<JsonObject> results = filter(N1qlQueryDAO.GET_ALL_OALD, filter, null, sort, page, size);

		List<Object> parseResult = parseJsonObjectToObject(results);

		return new PagedResponse<Object>(total, CustomQueryBuilder.calculateStartIndex(page, size), size, parseResult);
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public List<Long> retrieveVersionsById(String id) throws ServiceException, PersistenceException {

		LOG.debug("retrieveVersionsById Template Mapping started");
		List<Long> versionList = null;
		try {
			OaldProfileEntity entity = oaldDAO.findOne(id);
			if (entity != null) {
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
	 * 
	 * @param id
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	@Diagnostics(area = "Couchbase-Reads")
	public OaldProfile retrieveById(String id) throws ServiceException, PersistenceException {
		OaldProfile oaldProfile = null;
		try {
			OaldProfileEntity entity = oaldDAO.findOne(id);
			oaldProfile = MediaProfileUtil.buildOaldDomainFromOaldEntity(entity);

		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException();
		}

		return oaldProfile;
	}

	// ***************************************************************************************
	//
	// PRIVATE METHODS
	//
	// ***************************************************************************************

	private void save(OaldProfile oaldProfile) throws PersistenceException, JsonProcessingException, ParseException {
		long start = System.currentTimeMillis();

		if (StringUtils.isBlank(oaldProfile.getId())) {
			String id = UUID.randomUUID().toString();
			oaldProfile.setId(id);
		}
		oaldDAO.save(MediaProfileUtil.buildOaldEntityFromOaldDomain(oaldProfile));
		
		long end = System.currentTimeMillis();
		
		diagnosticsAspect.log("saveOaldProfile", new Object[] {oaldProfile.getId()}, "Couchbase-Writes", start, end);
	}

	private OaldProfileEntity internalFindByProductGroupPortfolioOriginalLender(String productGroupCode,
			Long portfolioId, String originalLenderName) throws PersistenceException, ServiceException,
					JsonParseException, JsonMappingException, IOException {
		long start = System.currentTimeMillis();

		OaldProfileEntity entity = null;

		String filterPortfolioAndLender = "|portfolio.id=%s|originalLender.name=\"%s\"";
		if (portfolioId == null && StringUtils.isBlank(originalLenderName)) {
			filterPortfolioAndLender = "|TYPE(portfolio) = \"missing\"|TYPE(originalLender) = \"missing\"";
		}
		String filter = String.format("productGroup.code=\"%s\"" + filterPortfolioAndLender, productGroupCode,
				portfolioId, originalLenderName, originalLenderName);
		List<JsonObject> results = filter(N1qlQueryDAO.GET_OALD_WITH_FILTER, filter, null, null, 0, 0);
		if (results != null && !results.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			entity = mapper.readValue(results.get(0).toString(), OaldProfileEntity.class);
		}

		long end = System.currentTimeMillis();
		
		diagnosticsAspect.log("retrieveOaldProfile", new Object[] {productGroupCode, portfolioId, originalLenderName}, 
							  "Couchbase-Reads", start, end);
		
		return entity;
	}

	/**
	 * 
	 * @param productGroup
	 * @return
	 */
	private boolean validProductGroupsAndDocumentTypes(ProductGroup productGroup, List<DocumentType> documentTypes) {
		// validate that the product group is set
		return (StringUtils.isNotBlank(productGroup.getCode()) && StringUtils.isNotBlank(productGroup.getName())
				&& validDocumentTypes(documentTypes));
	}

	private boolean validDocumentTypes(List<DocumentType> documentTypes) {
		boolean valid = true;
		if (documentTypes != null) {
			if (documentTypes.size() > 0) {
				for (DocumentType mt : documentTypes) {
					if (StringUtils.isBlank(mt.getCode()) || mt.getId() == null) {
						valid = false;
					}
				}
			} else {
				valid = false;
			}
		} else {
			valid = false;
		}
		return valid;
	}
	
	private boolean validateExistentOald(OaldProfileModel current, OaldProfileModel profile){
		boolean validated = false;
		if(current.equals(profile)){
			validated = true;
		}
		return validated;
	}

}
