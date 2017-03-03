package com.mcmcg.dia.profile.model.query;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.domain.Sort.Direction;

public class CustomQuery {

	private String baseQuery;
	private final Map<String, Object> whereClauseMap;
	private final Integer limit ;
	private final Integer offset ;
	private final String sortByField;
	private final Direction sortDirection;
	private final String groupBy;

	public CustomQuery(String baseQuery, Map<String, Object> whereClauseMap, String groupBy, int limit, 
					   int offset, String sortByField, Direction sortDirection) {
		this.whereClauseMap = whereClauseMap;
		this.limit = limit;
		this.offset = offset;
		this.sortByField = sortByField;
		this.sortDirection = sortDirection;
		this.baseQuery = baseQuery;
		this.groupBy = groupBy;
	}

	public Integer getLimit() {
		return limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public String getSortByField() {
		return sortByField;
	}

	public Direction getSortDirection() {
		return sortDirection;
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
