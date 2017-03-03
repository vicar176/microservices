package com.mcmcg.ingestion.domain;

/**
 * 
 * @author Jose Aleman
 *
 */
public class Account extends BaseDomain {

	private static final long serialVersionUID = 1L;

	private Long accountNumber;
	private String originalAccountNumber;
	private Long portfolioNumber;
	private String productType;
	private String originalLender;
	private String affinity;
	private String sellerId;
	private Statement statement;
	private String sellerName;

	public Account() {

	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getOriginalAccountNumber() {
		return originalAccountNumber;
	}

	public void setOriginalAccountNumber(String originalAccountNumber) {
		if (originalAccountNumber != null) {
			this.originalAccountNumber = originalAccountNumber.trim();
		}
	}

	public Long getPortfolioNumber() {
		return portfolioNumber;
	}

	public void setPortfolioNumber(Long portfolioNumber) {
		this.portfolioNumber = portfolioNumber;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		if (productType != null) {
			this.productType = productType.trim();
		}
	}

	public String getOriginalLender() {
		return originalLender;
	}

	public void setOriginalLender(String originalLender) {
		if (originalLender != null) {
			this.originalLender = originalLender.trim();
		}
	}

	public String getAffinity() {
		return affinity;
	}

	public void setAffinity(String affinity) {
		if (affinity != null) {
			this.affinity = affinity.trim();
		}
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		if (sellerId != null) {
			this.sellerId = sellerId.trim();
		}
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	/**
	 * @return the sellerName
	 */
	public String getSellerName() {
		return sellerName;
	}

	/**
	 * @param sellerName the sellerName to set
	 */
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

}
