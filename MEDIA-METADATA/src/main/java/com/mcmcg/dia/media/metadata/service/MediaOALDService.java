package com.mcmcg.dia.media.metadata.service;

import java.text.ParseException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mcmcg.dia.media.metadata.annotation.Diagnostics;
import com.mcmcg.dia.media.metadata.aop.DiagnosticsAspect;
import com.mcmcg.dia.media.metadata.dao.MediaOaldDAO;
import com.mcmcg.dia.media.metadata.exception.ServiceException;
import com.mcmcg.dia.media.metadata.model.domain.MediaOald;
import com.mcmcg.dia.media.metadata.model.entity.MediaOALDEntity;
import com.mcmcg.dia.media.metadata.util.MediaMetadataUtil;
import com.mcmcg.dia.media.metadata.util.OaldConvertUtil;

/**
 * 
 * @author Victor Arias
 *
 */
@Service
public class MediaOALDService {

	private static final Logger LOG = Logger.getLogger(MediaOALDService.class);
	@Autowired
	@Qualifier("mediaOaldDAO")
	MediaOaldDAO mediaOaldDAO;

	@Autowired
	DiagnosticsAspect diagnosticsAspect;
	
	private enum DocumentTypes {
		stmt
	};

	@Diagnostics(area = "Couchbase-Reads")
	public MediaOald findMediaOaldByDocumentId(Long documentId) throws ServiceException {
		LOG.info("findByDocumentId MediaOald start");
		MediaOald mediaOald = null;

		try {
			MediaOALDEntity entity = mediaOaldDAO.findByDocumentId(documentId);
			mediaOald = OaldConvertUtil.convertMediaOaldEntityToDomain(entity);
			LOG.debug("documentId = " + documentId + ", MediaOald = " + mediaOald);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error(message);
			throw new ServiceException(message);
		}

		LOG.info("findByDocumentId MediaOald end");
		return mediaOald;
	}

	public MediaOald save(MediaOald mediaOald, Long documentId) throws ServiceException {

		LOG.info("Save MediaOald start");
		try {
			if (mediaOald != null) {

				if (!(documentId.equals(mediaOald.getDocumentId()))) {
					String message = "The documentId does not match with the documentId in the JSON object";
					ServiceException se = new ServiceException(message);
					LOG.error(message, se);
					throw se;
				}

				if (StringUtils.equals(mediaOald.getOriginalDocumentType(), DocumentTypes.stmt.toString())) {
					if (StringUtils.isBlank(mediaOald.getDocumentDate())) {
						String message = "The documentDate should not be null or empty";
						ServiceException se = new ServiceException(message);
						LOG.error(message, se);
						throw se;
					}
				}

				MediaOald current = findMediaOaldByDocumentId(documentId);

				if (current != null) {
					mediaOald.setId(current.getId());
					mediaOald.setCreateDate(current.getCreateDate());
					mediaOald.setVersion(current.getVersion() + 1);
				} else {
					mediaOald.setVersion(1L);
				}

				if (!StringUtils.isBlank(mediaOald.getDocumentDate())
						&& !MediaMetadataUtil.isValidFormat(mediaOald.getDocumentDate())) {
					String message = "The documentDate " + mediaOald.getDocumentDate()
							+ " is using a bad date format, it should be " + MediaMetadataUtil.DATE_FORMAT_SHORT;
					LOG.error(message);
					throw new ServiceException(message);
				}

				LOG.info("documentId -" + documentId + "----mediaOald.getDocumentId -" + mediaOald.getDocumentId());

				// if (!documentId.compareTo(mediaOald.getDocumentId())) {

				saveInternal(mediaOald);

			} else {
				String message = "MediaOald not valid";
				LOG.error(message);
				throw new ServiceException(message);
			}
		} catch (

		ServiceException e)

		{
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		} catch (

		Throwable t)

		{
			LOG.error(t.getMessage(), t);
			throw new ServiceException(t.getMessage());
		}

		LOG.info("Save MediaOald end");
		return mediaOald;

	}

	@Diagnostics(area = "Couchbase-Writes")
	public void saveInternal(MediaOald mediaOald) throws JsonProcessingException, ParseException {
		if (StringUtils.isBlank(mediaOald.getId())) {
			String id = UUID.randomUUID().toString();
			mediaOald.setId(id);
		}
		long start = System.currentTimeMillis();
		mediaOaldDAO.save(OaldConvertUtil.convertMediaOaldToEntity(mediaOald));
		long end = System.currentTimeMillis();

		diagnosticsAspect.log("saveInternal", new Object[]{mediaOald.getDocumentId()}, "Couchbase-Writes", start, end);

	}

};