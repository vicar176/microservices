package com.mcmcg.dia.media.metadata.model.query;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.domain.Sort.Direction;

public class CustomQuery {

	private String baseQuery;
	private final Map<String, Object> whereClauseMap;
	private final Integer limit ;
	private final Integer offset ;
	private final String sortBy;
	private final String groupBy;

	public CustomQuery(String baseQuery, Map<String, Object> whereClauseMap, String groupBy, int limit, 
					   int offset, String sortBy) {
		this.whereClauseMap = whereClauseMap;
		this.limit = limit;
		this.offset = offset;
		this.sortBy = sortBy;
		this.baseQuery = baseQuery;
		this.groupBy = groupBy;
	}

	public Integer getLimit() {
		return limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public String getSortBy() {
		return sortBy;
	}

	public Map<String, Object> getWhereClauseMap() {
		return whereClauseMap;
	}

	public String getBaseQuery() {
		return baseQuery;
	}
	
	public String getGroupBy() {
		return groupBy;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
