/**
 * 
 */
package com.mcmcg.ingestion.service;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mcmcg.ingestion.BaseApplicationTest;
import com.mcmcg.ingestion.domain.MediaDocument;

/**
 * @author jaleman
 *
 */
public class ExtractionServiceTest extends BaseApplicationTest {

	@Autowired
	public ExtractionService extractionService;
	
	/**
	 * 
	 */
	public ExtractionServiceTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Ignore
	@Test
	public void testExtractionService() throws Exception{
		
		MediaDocument mediaDocument = new MediaDocument();
		mediaDocument.setDocumentType("GBLTR");
		mediaDocument.setAccountNumber(8501190971L);  	
		mediaDocument.setDocumentId(44491140678L);
		
		extractionService.execute(mediaDocument);
		
	}

}
