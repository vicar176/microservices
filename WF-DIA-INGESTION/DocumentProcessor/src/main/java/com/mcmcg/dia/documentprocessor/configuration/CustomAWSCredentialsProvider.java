package com.mcmcg.dia.documentprocessor.configuration;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

/**
 * @author jaleman
 *
 */
public class CustomAWSCredentialsProvider implements AWSCredentialsProvider {

	private String accessKey;

	private String secretKey;

	public CustomAWSCredentialsProvider(String accessKey, String secretKey) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
	}

	public CustomAWSCredentialsProvider() {
	}

	@Override
	public AWSCredentials getCredentials() {
		if (StringUtils.isNotBlank(accessKey)) {
			return new BasicAWSCredentials(accessKey, secretKey);
		}

		return null;
	}

	@Override
	public void refresh() {

	}

}