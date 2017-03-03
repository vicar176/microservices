package com.mcmcg.dia.media.metadata.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.media.metadata.model.entity.MediaMetadataEntity;

@Repository("mediaMetadataDAO")
public interface MediaMetadataDAO extends CrudRepository<MediaMetadataEntity, String> {

	MediaMetadataEntity findByAccountNumberAndOriginalDocumentTypeCodeAndDocumentDate(Long accountNumber, String originalDocumentType,
			String documentDate);
	
	MediaMetadataEntity findByDocumentId(Long documentId);
}
