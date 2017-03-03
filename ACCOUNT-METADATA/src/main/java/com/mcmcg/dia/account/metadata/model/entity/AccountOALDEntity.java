package com.mcmcg.dia.account.metadata.model.entity;

import org.springframework.data.annotation.Version;

import com.mcmcg.dia.account.metadata.model.AccountOALDModel;
import com.mcmcg.dia.account.metadata.annotation.Encrypt;

@Encrypt(fields = { "originalAccountNumber" })
public class AccountOALDEntity extends AccountOALDModel {

	private static final long serialVersionUID = 1L;

	@Version
	private Long versionEntity;

	public Long getVersionEntity() {
		return versionEntity;
	}

	public void setVersionEntity(Long versionEntity) {
		this.versionEntity = versionEntity;
	}

	private String type = AccountOALDEntity.class.getSimpleName();

	public String getType() {
		return type;
	}

}
