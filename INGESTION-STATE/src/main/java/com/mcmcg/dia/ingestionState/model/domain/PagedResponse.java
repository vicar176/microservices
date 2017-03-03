package com.mcmcg.dia.ingestionState.model.domain;

import java.util.ArrayList;
import java.util.List;

import com.mcmcg.dia.ingestionState.model.BaseModel;


/**
 * 
 * @author jaleman
 *
 */
public final class PagedResponse<T> {

	private final int totalItems;
	private final int startIndex;
	private final int itemsPerPage;
	private final List<T> items;


	public PagedResponse(int totalItems, int startIndex, int itemsPerPage, List<T> items) {
		this.totalItems = totalItems;
		this.startIndex = startIndex;
		this.itemsPerPage = itemsPerPage;
		if (items != null && !items.isEmpty()){
			this.items = new ArrayList<T>(items);
		}else{
			this.items = new ArrayList<T>();
		}
	}

	public int getTotalItems() {
		return totalItems;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getItemsPerPage() {
		return itemsPerPage;
	}
	
	public List<T> getItems() {
		return new ArrayList<T>(items);
	}
}
