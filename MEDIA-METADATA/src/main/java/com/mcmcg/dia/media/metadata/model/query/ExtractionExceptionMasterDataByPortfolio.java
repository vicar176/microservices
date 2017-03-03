/**
 * 
 */
package com.mcmcg.dia.media.metadata.model.query;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author jaleman
 *
 */
public class ExtractionExceptionMasterDataByPortfolio  {

	private Long portfolioNumber;
	
	private String originalSeller;
	
	private Integer documentsFailed;
	
	private Integer templateNotFoundCount;

	private Integer templateFoundCount;
	
	private Integer noTextLayer;
	
	public ExtractionExceptionMasterDataByPortfolio(Long portfolioNumber, String originalSeller, 
											        Integer documentsFailed, Integer templateFoundCount, Integer noTextLayer) {
		this.portfolioNumber = portfolioNumber;
		this.originalSeller = originalSeller;
		this.documentsFailed = documentsFailed;
		this.templateFoundCount = templateFoundCount - noTextLayer;
		this.templateNotFoundCount = documentsFailed - templateFoundCount;
		this.noTextLayer = noTextLayer;
	}

	public Long getPortfolioNumber() {
		return portfolioNumber;
	}

	public String getOriginalSeller() {
		return originalSeller;
	}

	public Integer getDocumentsFailed() {
		return documentsFailed;
	}
	
	public Integer getTemplateFoundCount() {
		return templateFoundCount;
	}
	
	public Integer getTemplateNotFoundCount() {
		return templateNotFoundCount;
	}
	
	public Integer getNoTextLayer() {
		return noTextLayer;
	}
	
	public void setNoTextLayer(Integer noTextLayer) {
		this.noTextLayer = noTextLayer;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
