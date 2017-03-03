package com.mcmcg.dia.profile.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mcmcg.dia.profile.model.TemplateMappingProfileModel;

@JsonInclude(Include.NON_NULL)
public class TemplateMappingProfile extends TemplateMappingProfileModel {

	private static final long serialVersionUID = 1L;

}
