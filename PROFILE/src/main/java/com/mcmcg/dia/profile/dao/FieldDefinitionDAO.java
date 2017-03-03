package com.mcmcg.dia.profile.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.profile.model.entity.FieldDefinitionEntity;

/**
 * 
 * @author Victor Arias
 *
 */
@Repository("fieldDefinitionDAO")
public interface FieldDefinitionDAO extends PagingAndSortingRepository<FieldDefinitionEntity, String>{

	FieldDefinitionEntity findByFieldName(String fieldName);
	List<FieldDefinitionEntity> findAll();
}
