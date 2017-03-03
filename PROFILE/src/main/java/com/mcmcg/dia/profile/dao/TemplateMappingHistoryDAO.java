package com.mcmcg.dia.profile.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.profile.model.entity.TemplateMappingHistoryEntity;

@Repository("templateMappingHistoryDAO")
public interface TemplateMappingHistoryDAO extends PagingAndSortingRepository<TemplateMappingHistoryEntity, String>{

}
