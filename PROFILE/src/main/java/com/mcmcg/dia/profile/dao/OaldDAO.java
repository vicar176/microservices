package com.mcmcg.dia.profile.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.profile.model.entity.OaldProfileEntity;

/**
 * 
 * @author Victor Arias
 *
 */
@Repository("oaldDAO")
public interface OaldDAO extends PagingAndSortingRepository<OaldProfileEntity, String> {

}
