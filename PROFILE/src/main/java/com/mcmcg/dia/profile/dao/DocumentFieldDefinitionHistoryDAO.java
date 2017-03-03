package com.mcmcg.dia.profile.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.profile.model.entity.DocumentFieldsDefinitionHistoryEntity;

/**
 * 
 * @author Wagner Porras
 *
 */
@Repository("documentFieldDefinitionHistoryDAO")
public interface DocumentFieldDefinitionHistoryDAO extends PagingAndSortingRepository<DocumentFieldsDefinitionHistoryEntity, String>{
	
}
