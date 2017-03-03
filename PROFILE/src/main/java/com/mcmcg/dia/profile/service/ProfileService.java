package com.mcmcg.dia.profile.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.profile.dao.PortfolioDAO;
import com.mcmcg.dia.profile.model.domain.PortfolioProfile;
import com.mcmcg.dia.profile.model.domain.Response;
import com.mcmcg.dia.profile.model.domain.Response.Error;
import com.mcmcg.dia.profile.model.entity.PortfolioProfileEntity;

/**
 * 
 * @author varias
 *
 */
@Service
public class ProfileService {

	@Autowired
	PortfolioDAO portfolioDao;

	public Response<PortfolioProfileEntity> getPortfolioById(String id) {

		//PortfolioProfileEntity portfolio = null;
		Response<PortfolioProfileEntity> response = new Response<PortfolioProfileEntity>();
		
		Error status = new Error();

//		try {
//			portfolio = portfolioDao.findPortfolioById(id);
//
//			if (portfolio != null) {
//				status.setCode(0);
//				status.setMessage("Operation was executed");
//				response.setData(portfolio);
//			}
//		} catch (Exception e) {
//			status.setCode(1);
//		}
		
		response.setError(status);
		return response;
	}

	public Response<PortfolioProfile> savePortfolio(PortfolioProfileEntity profile) {

		Response<PortfolioProfile> response = new Response<PortfolioProfile>();
		Error status = new Error();
		try {
			//portfolioDao.savePortfolio(profile);
			status.setCode(0);
			status.setMessage("portfolio profile create executed");
		} catch (Exception e) {
			status.setCode(1);
		}
		
		response.setError(status);
		return response;
	}
	
	public Response<PortfolioProfile> deletePortfolio(String id){
		
//		PortfolioProfileEntity portfolio = null;
		Response<PortfolioProfile> response = new Response<PortfolioProfile>();
		Error status = new Error();
//		try{
//			portfolio = portfolioDao.findPortfolioById(id);
//			if(portfolio != null){
//				portfolioDao.deletePortfolio(portfolio);
//				status.setCode(0);
//				status.setMessage("portfolio profile delete executed");
//			} 
//		}catch(Exception e){
//			status.setCode(1);
//		}
		
		response.setError(status);
		return response;
	}
	
	public Response<PortfolioProfile> updatePortfolio(String id){
		return null;
	}

}
