package com.mcmcg.ingestion.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 
 * @author Victor Arias
 *
 */

public class OaldProfile extends BaseDomain {
	private static final long serialVersionUID = 1L;

	private String id;
	private ProductGroup productGroup;
	private Portfolio portfolio;
	private OriginalLender originalLender;
	private List<DocumentType> documentTypes = new ArrayList<DocumentType>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public OriginalLender getOriginalLender() {
		return originalLender;
	}

	public void setOriginalLender(OriginalLender originalLender) {
		this.originalLender = originalLender;
	}

	public List<DocumentType> getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(List<DocumentType> documentTypes) {
		this.documentTypes = documentTypes;
	}

	public static class Portfolio implements Serializable {
		private static final long serialVersionUID = 1L;

		private Long id;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (other == this)
				return true;
			if (!(other instanceof Portfolio))
				return false;
			Portfolio portfolio = (Portfolio) other;
			if (this.getId().equals(portfolio.getId()))
				return true;
			else return false;
		}
		
	}

	public static class OriginalLender implements Serializable {
		private static final long serialVersionUID = 1L;

		private String name;

		public OriginalLender() {
			super();
		}

		public OriginalLender(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (other == this)
				return true;
			if (!(other instanceof OriginalLender))
				return false;
			OriginalLender originalLender = (OriginalLender) other;
			if (StringUtils.equals(this.getName(), originalLender.getName()))
				return true;
			else return false;
		}
		
	}

	public static class ProductGroup implements Serializable {
		private static final long serialVersionUID = 1L;

		private String code;
		private String name;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (other == this)
				return true;
			if (!(other instanceof ProductGroup))
				return false;
			ProductGroup productGroup = (ProductGroup) other;
			if (StringUtils.equals(this.getCode(), productGroup.getCode()) && StringUtils.equals(this.getName(), productGroup.getName()))
				return true;
			else return false;
		}
		
	}

	public static class DocumentType implements Serializable {
		private static final long serialVersionUID = 1L;

		private Long id;
		private String code;

		public DocumentType() {
			super();
		}

		public DocumentType(Long id, String code) {
			super();
			this.id = id;
			this.code = code;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (other == this)
				return true;
			if (!(other instanceof DocumentType))
				return false;
			DocumentType documentType = (DocumentType) other;
			if (StringUtils.equals(this.getCode(), documentType.getCode()) && this.getId().equals(documentType.getId()))
				return true;
			else return false;
		}
		
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof OaldProfile))
			return false;
		OaldProfile oaldProfile = (OaldProfile) other;
		if (this.getProductGroup().equals(oaldProfile.getProductGroup()) && 
				((this.getPortfolio() == null && this.getOriginalLender() == null) ||
				(this.getPortfolio().equals(oaldProfile.getPortfolio()) &&
				this.getOriginalLender().equals(oaldProfile.getOriginalLender()))))
			return true;
		else return false;
	}

}
