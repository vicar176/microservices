package com.mcmcg.dia.media.metadata.model.query;

import com.mcmcg.dia.media.metadata.model.MediaMetadataModel.DocumentType;
import com.mcmcg.dia.media.metadata.model.MediaMetadataModel.Seller;
import com.mcmcg.dia.media.metadata.model.domain.PagedResponse;

/**
 * ExtractionExceptionDetailDataByPortfolio
 * 
 * @author jaleman
 *
 */
public class ExtractionExceptionDetailDataByPortfolio {

	private final PagedResponse<TemplateFound> templates;

	private final PagedResponse<TemplateNotFound> templatesNotFound;

	public ExtractionExceptionDetailDataByPortfolio(PagedResponse<TemplateFound> templates,
			PagedResponse<TemplateNotFound> templatesNotFound) {

		this.templates = templates;
		this.templatesNotFound = templatesNotFound;

	}

	public PagedResponse<TemplateFound> getTemplates() {
		return templates;
	}

	public PagedResponse<TemplateNotFound> getTemplatesNotFound() {
		return templatesNotFound;
	}

	public static class TemplateFound {

		private final String templateId;
		private final String templateName;
		private final Long version;
		private final String updatedBy;
		private final String updateDate;
		private final String lastRun;
		private Integer documentsFailed;
		private boolean reprocess;

		public TemplateFound(String templateId, String name, Long version, String updatedBy, String updateDate,
				String lastRun, Integer documentsFailed, boolean reprocess) {
			this.templateId = templateId;
			this.templateName = name;
			this.version = version;
			this.updatedBy = updatedBy;
			this.updateDate = updateDate;
			this.lastRun = lastRun;
			this.documentsFailed = documentsFailed;
			this.reprocess = reprocess;
		}

		public String getTemplateId() {
			return templateId;
		}

		public String getTemplateName() {
			return templateName;
		}

		public Long getVersion() {
			return version;
		}

		public String getUpdatedBy() {
			return updatedBy;
		}

		public String getUpdateDate() {
			return updateDate;
		}

		public String getLastRun() {
			return lastRun;
		}

		public Integer getDocumentsFailed() {
			return documentsFailed;
		}

		public boolean isReprocess() {
			return reprocess;
		}

		public void setDocumentsFailed(Integer documentsFailed) {
			this.documentsFailed = documentsFailed;
		}

		public void setReprocess(boolean reprocess) {
			this.reprocess = reprocess;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof TemplateFound))
				return false;
			TemplateFound c = (TemplateFound) o;
			if (c.getTemplateId() != getTemplateId())
				return false;
			return true;
		}

	}

	public static class TemplateNotFound {

		private final String originalLender;
		private final Integer documentsFailed;
		private final Seller seller;
		private final DocumentType documentType;
		private boolean reprocess;

		public TemplateNotFound(String originalLender, Integer documentsFailed, Seller seller,
				DocumentType documentType, boolean reprocess) {
			super();
			this.originalLender = originalLender;
			this.documentsFailed = documentsFailed;
			this.seller = seller;
			this.documentType = documentType;
			this.reprocess = reprocess;
		}

		public boolean isReprocess() {
			return reprocess;
		}

		public void setReprocess(boolean reprocess) {
			this.reprocess = reprocess;
		}

		public String getOriginalLender() {
			return originalLender;
		}

		public Integer getDocumentsFailed() {
			return documentsFailed;
		}

		public Seller getSeller() {
			return seller;
		}

		public DocumentType getDocumentType() {
			return documentType;
		}

	}
}
