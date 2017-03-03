package com.mcmcg.media.workflow.configuration.aws;

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

	public CustomAWSCredentialsProvider() {
	}

	public CustomAWSCredentialsProvider(String accessKey, String secretKey) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
	}

	@Override
	public AWSCredentials getCredentials() {
		if (StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey)) {
			return new BasicAWSCredentials(accessKey, secretKey);
		}

		return null;
	}

	@Override
	public void refresh() {

	}

}
