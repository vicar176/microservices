package com.mcmcg.dia.batchmanager.dao;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AuroraRepository {

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Resource(name = "queriesMap")
	protected Map<String, String> queriesMap;

	
}
