package com.mcmcg.dia.account.metadata.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.account.metadata.annotation.Diagnostics;
import com.mcmcg.dia.account.metadata.aop.DiagnosticsAspect;
import com.mcmcg.dia.account.metadata.dao.AccountOALDDAO;
import com.mcmcg.dia.account.metadata.exception.ServiceException;
import com.mcmcg.dia.account.metadata.model.AccountOALDModel.MediaOald;
import com.mcmcg.dia.account.metadata.model.domain.AccountOALD;
import com.mcmcg.dia.account.metadata.model.entity.AccountOALDEntity;
import com.mcmcg.dia.account.metadata.util.MediaMetadataUtil;
import com.mcmcg.dia.account.metadata.util.MetadataConvertUtil;

@Service
public class AccountOALDService {

	private static final Logger LOG = Logger.getLogger(AccountOALDService.class);

	@Autowired
	@Qualifier("accountOALDDAO")
	AccountOALDDAO accountOALDDAO;

	@Autowired
	private DiagnosticsAspect diagnosticsAspect;
	
	@Diagnostics(area = "Couchbase-Reads")
	public AccountOALD findByAccountNumber(Long accountNumber) throws ServiceException {
		LOG.info("findByAccountNumber AccountNumber start");
		AccountOALD accountOald = null;

		try {
			AccountOALDEntity entity = accountOALDDAO.findByAccountNumber(accountNumber);
			accountOald = MetadataConvertUtil.convertAccountOALDEntityToDomain(entity);
			LOG.debug("accountNumber = " + accountNumber + ", AccountOald = " + accountOald);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error(message);
			throw new ServiceException(message);
		}

		LOG.info("findByAccountNumber AccountNumber end");
		return accountOald;
	}

	public AccountOALD save(Long accountNumber, AccountOALD accountOald) throws ServiceException {

		return save(accountNumber, null, null, accountOald, false);
	}

	public AccountOALD save(Long accountNumber, String mediaOaldId, MediaOald mediaOald, AccountOALD accountOald,
			boolean isUpdate) throws ServiceException {

		AccountOALD toSave = null;
		AccountOALD current = null;
		boolean valid = true;
		boolean alreadyExist = false;
		String errorMessage = StringUtils.EMPTY;

		if (accountNumber != null) {
			current = findByAccountNumber(accountNumber);
			if (isUpdate) {
				if (mediaOaldId != null && mediaOald != null) {
					if (!StringUtils.equals(mediaOaldId, mediaOald.getId())) {
						String errorMsj = String.format("The provided Id %s does not match the MediaOald id %s",
								mediaOaldId, mediaOald.getId());
						ServiceException se = new ServiceException(errorMsj);
						LOG.error(errorMsj, se);
						throw se;
					}
				} else {
					errorMessage = "Parameters not valid, unable to update AccountOald";
					valid = false;
				}
			} else {
				if (accountOald != null) {
					if (!accountNumber.equals(accountOald.getAccountNumber())) {
						String message = "The accountNumber does not match with the accountNumber in the JSON object";
						ServiceException se = new ServiceException(message);
						LOG.error(message, se);
						throw se;
					}
				} else {
					errorMessage = "Invalid AccountOald, unable to Save";
					valid = false;
				}
			}
		}

		if (!valid) {
			ServiceException se = new ServiceException(errorMessage);
			LOG.error(errorMessage, se);
			throw se;
		}

		if (isUpdate) {
			if (current != null) {
				List<MediaOald> oalds = current.getOalds();
				if (oalds != null && !oalds.isEmpty()) {
					for (MediaOald oald : current.getOalds()) {
						if (oald.getId().equals(mediaOaldId)) {
							if (oald.getDocumentId().equals(mediaOald.getDocumentId())) {
								String dateNow = MediaMetadataUtil.formatDate(new Date());
								//oald.setCreateDate(mediaOald.getCreateDate());
								oald.setUpdateDate(dateNow);
								oald.setUpdatedBy(mediaOald.getUpdatedBy());
								oald.setReceivedDate(dateNow);
								oald.setOaldProfileVersion(mediaOald.getOaldProfileVersion());
								oald.setVersion(oald.getVersion() + 1);
								alreadyExist = true;
								break;
							} else {
								String errorMsj = String.format(
										"The existent documentId %s does not match the object documentId %s",
										oald.getDocumentId(), mediaOald.getDocumentId());
								ServiceException se = new ServiceException(errorMsj);
								LOG.error(errorMsj);
								throw se;
							}
						}
					}
					if (!alreadyExist) {
						mediaOald.setReceivedDate(MediaMetadataUtil.formatDate(new Date()));
						oalds.add(mediaOald);
					}
				} else {
					oalds = new ArrayList<MediaOald>();
					mediaOald.setReceivedDate(MediaMetadataUtil.formatDate(new Date()));
					oalds.add(mediaOald);
				}
				current.setOalds(oalds);
				toSave = current;
			} else {
				String errorMsj = String.format("No accountOald exists with accountNumber %s", accountNumber);
				ServiceException se = new ServiceException(errorMsj);
				LOG.error(errorMsj, se);
				throw se;
			}
		} else {
			if (current == null) {
				accountOald.setVersion(1L);
				List<MediaOald> oalds = accountOald.getOalds();

				if (oalds != null && !oalds.isEmpty()) {
					for (MediaOald oald : oalds) {
						if (oald.getVersion() == null) {
							oald.setVersion(1L);
						}
						String dateNow = MediaMetadataUtil.formatDate(new Date());
						oald.setCreateDate(dateNow);
						oald.setUpdateDate(dateNow);
						oald.setReceivedDate(dateNow);
						oald.setOaldValidated(true);
						oald.setUpdatedBy(accountOald.getUpdatedBy());
					}
				}
				toSave = accountOald;
			} else {
				String errorMsj = String.format("An AccountOald already exists with the accountNumber %s", accountNumber);
				ServiceException se = new ServiceException(errorMsj);
				LOG.error(errorMsj, se);
				throw se;
			}
		}

		try {
			saveInternal(toSave);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}

		return toSave;

	}

	public AccountOALD deleteAccOald(Long accountNumber, String mediaOaldId) throws ServiceException {
		AccountOALD current = null;
		current = findByAccountNumber(accountNumber);
		if (current != null) {
			Date curDate = new Date();
			List<MediaOald> oaldList = current.getOalds();
			for (MediaOald oald : oaldList) {
				if (oald.getDocumentId().equals(mediaOaldId)) {
					oaldList.remove(oald);
					break;
				}
			}
			current.setOalds(oaldList);
			current.setUpdateDate(MediaMetadataUtil.formatDate(curDate));
			save(accountNumber, current);
		} else {
			String message = "AccountMediaOALD not found for MediaOaldId";
			LOG.error(message);
			throw new ServiceException(message);
		}
		return current;
	}

	/**
	 * 
	 * @param accountOald
	 */
	private void saveInternal(AccountOALD accountOald) {
		
		long start = System.currentTimeMillis();
		
		if (StringUtils.isBlank(accountOald.getId())) {
			String id = UUID.randomUUID().toString();
			accountOald.setId(id);
		}
		
		accountOALDDAO.save(MetadataConvertUtil.convertAccountOaldToEntity(accountOald));

		long end = System.currentTimeMillis();
		
		diagnosticsAspect.log("saveAccountOald", new Object[] {accountOald.getId()}, "Couchbase-Writes", start, end);

	}

}
