package com.mcmcg.dia.media.metadata.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.media.metadata.model.domain.MediaOald;
import com.mcmcg.dia.media.metadata.model.entity.MediaOALDEntity;

/**
 * 
 * @author Victor Arias
 *
 */
@Repository("mediaOaldDAO")
public interface MediaOaldDAO extends CrudRepository<MediaOALDEntity, String>{
	
	MediaOALDEntity findByDocumentId(Long documentId);
	
}
