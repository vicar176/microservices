package com.mcmcg.dia.profile.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.profile.model.entity.FieldDefinitionHistoryEntity;

/**
 * 
 * @author Victor Arias
 *
 */
@Repository("fieldDefinitionHistoryDAO")
public interface FieldDefinitionHistoryDAO extends PagingAndSortingRepository<FieldDefinitionHistoryEntity, String>{

}
