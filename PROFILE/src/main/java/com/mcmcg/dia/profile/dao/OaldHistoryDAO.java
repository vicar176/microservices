package com.mcmcg.dia.profile.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.profile.model.entity.OaldProfileHistoryEntity;

/**
 * 
 * @author Victor Arias
 *
 */
@Repository("oaldHistoryDAO")
public interface OaldHistoryDAO extends PagingAndSortingRepository<OaldProfileHistoryEntity, String> {

}
