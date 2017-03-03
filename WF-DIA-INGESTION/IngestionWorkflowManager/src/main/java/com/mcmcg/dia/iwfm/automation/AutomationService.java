package com.mcmcg.dia.iwfm.automation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.iwfm.dao.ConfigurationParametersDAO;

/**
 * 
 * @author wporras
 *
 */
@Service
public class AutomationService {

	private static final Logger LOG = Logger.getLogger(AutomationService.class);

	@Autowired
	private AuroraDAO auroraDAO;

	@Autowired
	private ConfigurationParametersDAO configurationParametersDAO;

	@Autowired
	CouchBaseDAO couchBaseDAO;

	public IngestionTest prepareIngestionTest(IngestionTest ingestionTest)
			throws ServiceException, PersistenceException {

		LOG.info("Start prepareIngestionTest");

		try {
			// Turn off previous tests
			// turnOffIngestion(ingestionTest);

			// Set Workflow threadCount
			configurationParametersDAO.updateParameter("iwfm.threadcount",
					ingestionTest.getWorkFlowThreads().toString(), ingestionTest.getUser());

			// Set Document Processor cron
			setDocumentProcessorCron(ingestionTest);

			// Set create Snippets flag
			configurationParametersDAO.updateParameter("iwfm.createSnippets",
					String.valueOf(ingestionTest.isCreateSnippets()), ingestionTest.getUser());

			// Set pdfTagging flag
			configurationParametersDAO.updateParameter("iwfm.pdfTagging", String.valueOf(ingestionTest.isPdfTagging()),
					ingestionTest.getUser());

			// Clean Aurora tables
			auroraDAO.cleanAurotaTables();

			// Clean CouchBase Tables
			couchBaseDAO.cleanCouchBaseTables();

			// Set batchSize
			configurationParametersDAO.updateParameter("documentprocessor.batchSize",
					ingestionTest.getBatchSize().toString(), ingestionTest.getUser());

			// Prepare the documents to be ingested
			auroraDAO.prepareDocuments(ingestionTest.getTotalDocuments());

			// Turn on Document Processor
			configurationParametersDAO.updateParameter("documentprocessor.shutdown", "false", ingestionTest.getUser());

			// Turn On Workflow
			auroraDAO.changeWfShutdownState(ingestionTest.getUser(), false);

			// save configuration to DB
			auroraDAO.save(ingestionTest);

		} catch (PersistenceException e) {
			turnOffIngestion(ingestionTest);
			throw e;
		} catch (Throwable t) {
			turnOffIngestion(ingestionTest);
			String message = "Error preparing the ingestion test";
			LOG.error(message, t);
			throw new ServiceException(message, t);
		}

		return ingestionTest;
	}

	/****************************************************************************
	 * 
	 * PRIVATE METHODS
	 * 
	 * @throws ServiceException
	 * 
	 ****************************************************************************/

	private void setDocumentProcessorCron(IngestionTest ingestionTest) throws ServiceException {

		String CRON = " * * * ?";
		String newCron = "";
		Integer seconds = ingestionTest.getDocumentProcessorCron();

		if (seconds < 60 && seconds >= 1) {
			newCron = "0/" + seconds + " *" + CRON;
		} else if (seconds > 59 && seconds < 3540) {
			Integer minutes = seconds / 60;
			newCron = " 0/" + minutes + CRON;
			if ((seconds - (minutes * 60)) >= 1) {
				newCron = (seconds - (minutes * 60)) + newCron;
			} else {
				newCron = "0" + newCron;
			}
		} else {
			throw new ServiceException("The number of seconds for the CRON job is invalid");
		}

		configurationParametersDAO.updateParameter("documentprocessor.cron", newCron, ingestionTest.getUser());
	}

	private void turnOffIngestion(IngestionTest ingestionTest) {
		try {
			// a - Turn off Document Processor
			configurationParametersDAO.updateParameter("documentprocessor.shutdown", "true", ingestionTest.getUser());

			// b - Turn off Workflow
			auroraDAO.changeWfShutdownState(ingestionTest.getUser(), true);

		} catch (PersistenceException e) {
			String message = "Error on rolling back";
			LOG.error(message, e);
			// nothing to do
		}
	}

}
