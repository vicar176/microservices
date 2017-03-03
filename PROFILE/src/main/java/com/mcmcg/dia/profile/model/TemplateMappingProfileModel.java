package com.mcmcg.dia.profile.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import com.mcmcg.dia.profile.model.OaldProfileModel.DocumentType;

public class TemplateMappingProfileModel extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	@Field
	private DocumentType documentType;
	@Field
	private Seller seller;
	@Field
	private Set<String> originalLenders;
	@Field
	private String name;
	@Field
	private String sampleFileName;
	@Field
	private int totalPages;
	@Field
	private Set<String> affinities;
	@Field
	private List<ReferenceArea> referenceAreas;
	@Field
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

	public Set<String> getOriginalLenders() {
		return originalLenders;
	}

	public void setOriginalLenders(Set<String> originalLenders) {
		this.originalLenders = originalLenders;
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

		private ZoneFieldDefinition fieldDefinition;
		private String fieldFormat;
		private List<Float> zoneArea;
		private int pageNumber;

		public ZoneMapping() {
			super();
		}

		public ZoneMapping(ZoneFieldDefinition zoneFieldDefinition, String fieldFormat, List<Float> zoneArea,
				int pageNumber) {
			super();
			this.fieldDefinition = zoneFieldDefinition;
			this.fieldFormat = fieldFormat;
			this.zoneArea = zoneArea;
			this.pageNumber = pageNumber;
		}

		public ZoneFieldDefinition getFieldDefinition() {
			return fieldDefinition;
		}

		public void setFieldDefinition(ZoneFieldDefinition zoneFieldDefinition) {
			this.fieldDefinition = zoneFieldDefinition;
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

		public static class ZoneFieldDefinition implements Serializable {
			private static final long serialVersionUID = 1L;

			private String fieldName;
			private String fieldDescription;
			private String fieldType;
			private boolean fieldRequired;
			//private boolean active;
			//private boolean encrypt;

			public ZoneFieldDefinition() {
				super();
			}

			public ZoneFieldDefinition(String fieldName, String fieldDescription, String fieldType,
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
			/*
			public boolean isActive() {
				return active;
			}

			public void setActive(boolean active) {
				this.active = active;
			}
			
			public boolean isEncrypt() {
				return encrypt;
			}

			public void setEncrypt(boolean encrypt) {
				this.encrypt = encrypt;
			}*/
		}
	}
}
