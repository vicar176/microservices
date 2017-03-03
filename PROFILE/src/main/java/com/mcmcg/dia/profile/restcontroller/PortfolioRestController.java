package com.mcmcg.dia.profile.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.profile.model.domain.PortfolioProfile;
import com.mcmcg.dia.profile.model.domain.Response;
import com.mcmcg.dia.profile.model.entity.PortfolioProfileEntity;
import com.mcmcg.dia.profile.service.ProfileService;

/**
 * 
 * @author wporras
 *
 */

@RestController
@RequestMapping(value = "/portfolio-profiles")
public class PortfolioRestController extends BaseRestController{

	@Autowired
	ProfileService profileService; // Service which will do all data
										// retrieval/manipulation work

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Response<PortfolioProfileEntity> getPortfolioById(@PathVariable("id") String id) {
		
		Response<PortfolioProfileEntity> response = null;
		
		try{
			response = profileService.getPortfolioById(id);
		}catch(Exception e){ // needs to be changed by checked exceptions
			
		}
		
		return response;
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public Response<PortfolioProfile> savePortfolio(@RequestParam("automatedOrderingPeriod") Long automatedOrderingPeriod,
			@RequestParam("sourceLocationProfile") String sourceLocationProfile){
		
		Response<PortfolioProfile> response = new Response<PortfolioProfile>();
		
//		try{
//			PortfolioProfileEntity portfolio = new PortfolioProfileEntity();
//			portfolio.setAutomatedOrderingPeriod(automatedOrderingPeriod);
//			portfolio.setSourceLocationProfile(sourceLocationProfile);
//			
//			response = profileService.savePortfolio(portfolio);
//		}catch(Exception e){
//			
//		}
		
		return response;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public Response<PortfolioProfile> deletePortfolio(@PathVariable("id") String id){
		Response<PortfolioProfile> response = new Response<PortfolioProfile>();
		
		try{
			response = profileService.deletePortfolio(id);
		}catch(Exception e){ // needs to be changed by checked exceptions
			
		}
		
		return response;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Response<PortfolioProfile> updatePortfolio(@PathVariable("id") String id, @RequestBody PortfolioProfile portfolioProfile){
		return null;
	}
	
}
