/**
 * 
 */
package com.mcmcg.dia.profile.model.query;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Sort.Direction;

/**
 * @author jaleman
 *
 */
public class CustomQueryBuilder{
	
	private String baseQuery;
	private String filter;
	private int pageNumber;
	private int pageSize;
	private String sort;
	private String groupBy;
	
	public CustomQueryBuilder() {
		
	}
	
	public CustomQueryBuilder setFilter(String filter) {
		this.filter = filter;
		return this;
	}
	
	public CustomQueryBuilder setPageNumber(int pageNumber) {
		
		if (pageNumber < 1 ){
			this.pageNumber = 1;
		}else{
			this.pageNumber = pageNumber;
		}
		
		return this;
	}

	public CustomQueryBuilder setPageSize(int pageSize) {
		if (pageSize < 1 ){
			this.pageSize = 1;
		}else{
			this.pageSize = pageSize;
		}
		this.pageSize = pageSize;
		
		return this;
	}

	public CustomQueryBuilder setSort(String sortByField) {
		this.sort = sortByField;
		return this;
	}

	public CustomQueryBuilder setBaseQuery(String baseQuery) {
		this.baseQuery = baseQuery;
		return this;
	}
	
	public CustomQueryBuilder setGroupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}

	/**
	 * 
	 * @return CustomQuery
	 */
	public CustomQuery build(){

		return new CustomQuery(baseQuery, buildFieldMap(filter), groupBy, pageSize, 
							   calculateStartIndex(pageNumber, pageSize), evalSort(sort), evalSortDirection(sort));
	}

	/**
	 * 
	 * @param filter
	 * @return
	 */
	private Map<String, Object> buildFieldMap(String filterString){
		
		Map<String, Object> fieldMap = new HashMap<String, Object>();
		
		if (StringUtils.isNotBlank(filterString) && StringUtils.contains(filterString, "|")){
			String filters[] = filter.split("[|]");
			
			for (String filter : filters){
				String field[] = filter.split("=");
				
				fieldMap.put(field[0], eval(field[1]));
			}
		}else if (StringUtils.isNotBlank(filterString) && StringUtils.contains(filterString, "=")){
			String field[] = filter.split("=");
			
			fieldMap.put(field[0], eval(field[1]));
		}

		return fieldMap;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private Object eval(String value){
		
		if (value.contains("\"") || value.contains(",") || value.contains("IS") || value.contains("[")){ 
			return value;
		}
		
		if (StringUtils.equals(Boolean.TRUE.toString().toLowerCase(), value.toLowerCase()) || 
			StringUtils.equals(Boolean.FALSE.toString().toLowerCase(), value.toLowerCase())){
			
			return Boolean.parseBoolean(value);
		}
		
		if (NumberUtils.isNumber(value)){
			return NumberUtils.toLong(value);
		}
		
		return Double.parseDouble(value);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private String evalSort(String value){
	
		if (StringUtils.isBlank(value)){
			return StringUtils.EMPTY;
		}
		if (!StringUtils.startsWith(value, "-")){
			return value;
		}
		
		return value.substring(1);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private Direction evalSortDirection(String value){

		if (StringUtils.isBlank(value)){
			return null;
		}

		if (StringUtils.startsWith(value, "-")){
			return Direction.DESC;
		}
		
		return Direction.ASC;
	}
	
	/**
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	public static int calculateStartIndex(int page, int size){
		//case 1
		if (page > 0 || size > 0){
			return (page * size) - size;
		}
		
		return 0;
	}
}