package com.mcmcg.dia.media.metadata.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class TemplateMappingProfileModel extends BaseModel {

	private static final long serialVersionUID = 1L;

	private String id;
	private DocumentType documentType;
	private Seller seller;
	private OriginalLender originalLender;
	private String name;
	private String sampleFileName;
	private int totalPages;
	private Set<String> affinities;
	private List<ReferenceArea> referenceAreas;
	private List<ZoneMapping> zoneMappings;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public OriginalLender getOriginalLender() {
		return originalLender;
	}

	public void setOriginalLender(OriginalLender originalLender) {
		this.originalLender = originalLender;
	}

	public Set<String> getAffinities() {
		return affinities;
	}

	public void setAffinities(Set<String> affinities) {
		this.affinities = affinities;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSampleFileName() {
		return sampleFileName;
	}

	public void setSampleFileName(String sampleFileName) {
		this.sampleFileName = sampleFileName;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public List<ReferenceArea> getReferenceAreas() {
		return referenceAreas;
	}

	public void setReferenceAreas(List<ReferenceArea> referenceAreas) {
		this.referenceAreas = referenceAreas;
	}

	public List<ZoneMapping> getZoneMappings() {
		return zoneMappings;
	}

	public void setZoneMappings(List<ZoneMapping> zoneMappings) {
		this.zoneMappings = zoneMappings;
	}

	public static class Seller implements Serializable {
		private static final long serialVersionUID = 1L;

		private Long id;
		private String name;

		public Seller() {
			super();
		}

		public Seller(Long id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String code) {
			this.name = code;
		}
	}
	
	public static class ReferenceArea implements Serializable {
		private static final long serialVersionUID = 1L;

		private String value;
		private List<Float> zoneArea;
		private String fieldType;
		private int pageNumber;

		public ReferenceArea() {
			super();
		}

		public ReferenceArea(String value, List<Float> zoneArea, String fieldType, int pageNumber) {
			super();
			this.value = value;
			this.zoneArea = zoneArea;
			this.fieldType = fieldType;
			this.pageNumber = pageNumber;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public List<Float> getZoneArea() {
			return zoneArea;
		}

		public void setZoneArea(List<Float> zoneArea) {
			this.zoneArea = zoneArea;
		}

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}
	}

	public static class ZoneMapping implements Serializable {
		private static final long serialVersionUID = 1L;

		private FieldDefinition fieldDefinition;
		private String fieldFormat;
		private List<Float> zoneArea;
		private int pageNumber;

		public ZoneMapping() {
			super();
		}

		public ZoneMapping(FieldDefinition fieldDefinition, String fieldFormat, List<Float> zoneArea,
				int pageNumber) {
			super();
			this.fieldDefinition = fieldDefinition;
			this.fieldFormat = fieldFormat;
			this.zoneArea = zoneArea;
			this.pageNumber = pageNumber;
		}

		public FieldDefinition getFieldDefinition() {
			return fieldDefinition;
		}

		public void setFieldDefinition(FieldDefinition fieldDefinition) {
			this.fieldDefinition = fieldDefinition;
		}

		public List<Float> getZoneArea() {
			return zoneArea;
		}

		public void setZoneArea(List<Float> zoneArea) {
			this.zoneArea = zoneArea;
		}

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}

		public String getFieldFormat() {
			return fieldFormat;
		}

		public void setFieldFormat(String fieldFormat) {
			this.fieldFormat = fieldFormat;
		}

		public static class FieldDefinition implements Serializable {
			private static final long serialVersionUID = 1L;

			private String fieldName;
			private String fieldDescription;
			private String fieldType;
			private boolean fieldRequired;

			public FieldDefinition() {
				super();
			}

			public FieldDefinition(String fieldName, String fieldDescription, String fieldType,
					boolean fieldRequired) {
				super();
				this.fieldName = fieldName;
				this.fieldDescription = fieldDescription;
				this.fieldType = fieldType;
				this.fieldRequired = fieldRequired;
			}

			public String getFieldName() {
				return fieldName;
			}

			public void setFieldName(String fieldName) {
				this.fieldName = fieldName;
			}

			public String getFieldDescription() {
				return fieldDescription;
			}

			public void setFieldDescription(String fieldDescription) {
				this.fieldDescription = fieldDescription;
			}

			public String getFieldType() {
				return fieldType;
			}

			public void setFieldType(String fieldType) {
				this.fieldType = fieldType;
			}

			public boolean isFieldRequired() {
				return fieldRequired;
			}

			public void setFieldRequired(boolean fieldRequired) {
				this.fieldRequired = fieldRequired;
			}
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
}
