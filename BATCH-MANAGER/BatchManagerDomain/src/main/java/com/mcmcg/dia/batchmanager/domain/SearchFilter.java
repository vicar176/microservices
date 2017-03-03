/**
 * 
 */
package com.mcmcg.dia.batchmanager.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author pshankar
 *
 */
public class SearchFilter {

	private Long filterId;
	private String filterCode;
	private String filterDescription;
	private Long filterTypeId;

	/**
	 * @return the filterId
	 */

	/**
	 * @return the filterCode
	 */
	public String getFilterCode() {
		return filterCode;
	}

	/**
	 * @return the filterId
	 */
	public Long getFilterId() {
		return filterId;
	}

	/**
	 * @param filterId the filterId to set
	 */
	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}

	/**
	 * @param filterCode
	 *            the filterCode to set
	 */
	public void setFilterCode(String filterCode) {
		this.filterCode = filterCode;
	}

	/**
	 * @return the filterDescription
	 */
	public String getFilterDescription() {
		return filterDescription;
	}

	/**
	 * @param filterDescription
	 *            the filterDescription to set
	 */
	public void setFilterDescription(String filterDescription) {
		this.filterDescription = filterDescription;
	}

	/**
	 * @return the filterTypeId
	 */
	public Long getFilterTypeId() {
		return filterTypeId;
	}

	/**
	 * @param filterTypeId
	 *            the filterTypeId to set
	 */
	public void setFilterTypeId(Long filterTypeId) {
		this.filterTypeId = filterTypeId;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof SearchFilter)) {
			return false;
		}

		SearchFilter searchFilter = (SearchFilter) o;

		return new EqualsBuilder().append(filterId, searchFilter.filterId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(filterId).toHashCode();
	}

	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
