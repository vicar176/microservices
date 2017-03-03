package com.mcmcg.dia.profile.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.profile.model.entity.TemplateMappingEntity;

@Repository("templateMappingDAO")
public interface TemplateMappingDAO extends PagingAndSortingRepository<TemplateMappingEntity, String>{

}
