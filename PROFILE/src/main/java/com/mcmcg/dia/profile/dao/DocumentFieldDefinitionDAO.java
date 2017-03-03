package com.mcmcg.dia.profile.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.profile.model.entity.DocumentFieldsDefinitionEntity;

/**
 * 
 * @author Wagner Porras
 *
 */
@Repository("documentFieldDefinitionDAO")
public interface DocumentFieldDefinitionDAO extends PagingAndSortingRepository<DocumentFieldsDefinitionEntity, String>{

	DocumentFieldsDefinitionEntity findByDocumentTypeCode(String documentTypeCode);
	
	List<DocumentFieldsDefinitionEntity> findAll();

}
