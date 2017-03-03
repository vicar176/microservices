package com.mcmcg.dia.media.metadata.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.mcmcg.dia.media.metadata.annotation.Encrypt;
import com.mcmcg.dia.media.metadata.exception.EncryptionException;
import com.mcmcg.dia.media.metadata.model.BaseModel;
import com.mcmcg.dia.media.metadata.model.MediaMetadataModel.DataElement;
import com.mcmcg.dia.media.metadata.model.entity.MediaMetadataEntity;

/**
 * 
 * @author wporras
 *
 */
@Component
public class EncryptComponent {
	private final static Logger LOG = Logger.getLogger(EncryptComponent.class);

	@Value("${aws.kms.endpoint}")
	String awsKmsEndpoint;

	@Value("${application.env}")
	private String server;

	@Autowired
	AWSKMS kms;

	@Autowired
	String awsEncryptionKeyArn;

	@Autowired
	AWSCredentials awsCredentials;
	
	private String savedHour = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-"
			+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

	/**
	 * Decrypt an Object Entity
	 * 
	 * @param objectEntity
	 */
	public void decrypt(Object objectEntity) {
		LOG.info("Encryption Key ====> " + awsEncryptionKeyArn);
		if (objectEntity != null && objectEntity.getClass().getAnnotation(Encrypt.class) != null) {
			// Decrypt Entity
			for (String field : getEncryptableFields(objectEntity)) {
				decryptEntityField(objectEntity, field);
			}

			// Decrypt Data Elements
			if (objectEntity instanceof MediaMetadataEntity) {
				processDataElements((MediaMetadataEntity) objectEntity, false);
			}
		}
	}

	/**
	 * Encrypt an Object Entity
	 * 
	 * @param objectEntity
	 * @return Object
	 */
	public Object encrypt(BaseModel objectEntity) {
		LOG.info("Encryption Key ====> " + awsEncryptionKeyArn);
		if (objectEntity != null && objectEntity.getClass().getAnnotation(Encrypt.class) != null) {
			Object entity = SerializationUtils.clone(objectEntity);
			
			// Encrypt Entity
			for (String field : getEncryptableFields(objectEntity)) {
				encryptEntityField(entity, field);
			}

			// Encrypt Data Elements
			if (objectEntity instanceof MediaMetadataEntity) {
				processDataElements((MediaMetadataEntity) entity, true);
			}

			return entity;
		}
		return objectEntity;
	}

	/**
	 * Recursively encrypts an Entity Field
	 * 
	 * @param object
	 * @param field
	 * @throws EncryptionException
	 */
	public void encryptEntityField(Object objectEntity, String field) throws EncryptionException {

		String[] splittedField = field.split("\\.");

		try {
			if (splittedField.length == 1) {
				String text = (String) invokeGet(objectEntity, field);
				invokeSet(objectEntity, field, encryptText(text));
			} else {
				Object innerClass = invokeGet(objectEntity, splittedField[0]);
				encryptEntityField(innerClass, field.substring(field.indexOf(".") + 1));
			}
		} catch (ReflectiveOperationException e) {
			String message = "Error while encrypting the entity: " + objectEntity.getClass();
			LOG.error(message);
			throw new EncryptionException(message + " --> " + e.getMessage(), e);
		}
	}

	/**
	 * Recursively decrypts an Entity Field
	 * 
	 * @param object
	 * @param field
	 * @throws EncryptionException
	 */
	public void decryptEntityField(Object objectEntity, String field) throws EncryptionException {

		String[] splittedField = field.split("\\.");

		try {
			if (splittedField.length == 1) {
				String text = (String) invokeGet(objectEntity, field);
				invokeSet(objectEntity, field, decryptText(text));
			} else {
				Object innerClass = invokeGet(objectEntity, splittedField[0]);
				decryptEntityField(innerClass, field.substring(field.indexOf(".") + 1));
			}
		} catch (ReflectiveOperationException | SecurityException | EncryptionException e) {
			String message = String.format("Error while decrypting the field (%s) of %s", field,
					objectEntity.getClass());
			LOG.error(message);
			throw new EncryptionException(message + " --> " + e.getMessage(), e);
		}
	}

	/**
	 * Encrypts plaintext into ciphertext by using a master key
	 * 
	 * @param text
	 * @return cipherText
	 */
	public String encryptText(String text) throws EncryptionException {

		LOG.debug("Encrypt => " + text);
		ByteBuffer ciphertextBlob = null;
		String cipherText = text;

		if (StringUtils.isBlank(text)) {
			return StringUtils.EMPTY;
		}

		try {
			ByteBuffer plainText = ByteBuffer.wrap(text.getBytes());
			EncryptRequest request = new EncryptRequest().withKeyId(awsEncryptionKeyArn).withPlaintext(plainText);
			if (getKms() != null) {
				ciphertextBlob = getKms().encrypt(request).getCiphertextBlob();
			}

			if (ciphertextBlob != null) {
				cipherText = new String(Base64.encodeBase64(ciphertextBlob.array()));
			}
		} catch (Throwable e) {
			String message = "Error while encrypting the text " + text;
			LOG.error(message, e);
			throw new EncryptionException(message + " --> " + e.getMessage(), e);
		}

		LOG.debug("Encrypted to => " + cipherText);
		return cipherText;
	}

	/**
	 * Decrypts ciphertext. Ciphertext is plaintext that has been previously
	 * encrypted with an master key
	 * 
	 * @param cipherText
	 * @return decipherText
	 * @throws EncryptionException
	 */
	public String decryptText(String cipherText) throws EncryptionException {

		LOG.debug("Decrypt => " + cipherText);
		ByteBuffer plainTextBlob = null;
		String decipherText = cipherText;
		
		if (StringUtils.isBlank(cipherText)) {
			return cipherText;
		}

		try {
			ByteBuffer cipherPlaintext = ByteBuffer.wrap(Base64.decodeBase64(cipherText));
			DecryptRequest request = new DecryptRequest().withCiphertextBlob(cipherPlaintext);
			if (getKms() != null) {
				plainTextBlob = getKms().decrypt(request).getPlaintext();
			}
		} catch (Throwable e) {
			String message = "Error while decrypting the text " + cipherText;
			LOG.error(message, e);
			throw new EncryptionException(message + " --> " + e.getMessage(), e);
		}

		if (plainTextBlob != null) {
			decipherText = new String(plainTextBlob.array());
		}

		LOG.debug("Decrypted to => " + decipherText);

		return decipherText;
	}

	// ***************************************************************************************
	//
	// PRIVATE METHODS
	//
	// ***************************************************************************************

	private AWSKMS getKms() {
		String currentHour = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-"
				+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

		if (!StringUtils.equalsIgnoreCase("local", server)) {
			if (!savedHour.equals(currentHour) || kms == null) {

				savedHour = currentHour;

				if (awsCredentials == null) {
					kms = new AWSKMSClient(new InstanceProfileCredentialsProvider(true));
					kms.setEndpoint(awsKmsEndpoint);
				} else {
					kms = new AWSKMSClient(awsCredentials);
					kms.setEndpoint(awsKmsEndpoint);
				}
			}
		}

		return kms;
	}

	/**
	 * Get a list of encryptable fields from an entity
	 * 
	 * @param entity
	 * @return List<String>
	 */
	private List<String> getEncryptableFields(Object entity) {

		List<String> encryptableFields = new ArrayList<String>();

		if (entity != null) {
			Encrypt encryptAnnotation = entity.getClass().getAnnotation(Encrypt.class);
			if (encryptAnnotation != null) {
				encryptableFields = Arrays.asList(encryptAnnotation.fields());
			}
		}

		return encryptableFields;
	}

	/**
	 * Encrypt or Decrypt Data Elements
	 * 
	 * @param metadata
	 * @param encrypt
	 * @throws EncryptionException
	 */
	private void processDataElements(MediaMetadataEntity metadata, boolean encrypt) throws EncryptionException {

		List<DataElement> dataElements = metadata.getDataElements();
		if (CollectionUtils.isEmpty(dataElements)) {
			return;
		}
		List<String> stringDataElements = getEncryptableDataElements(metadata);

		for (DataElement element : dataElements) {
			for (String stringElememt : stringDataElements) {

				if (StringUtils.containsIgnoreCase(element.getFieldDefinition().getFieldName(), stringElememt)) {

					try {
						if (encrypt) {
							element.setValue(encryptText(element.getValue()));
						} else {
							element.setValue(decryptText(element.getValue()));
						}

					} catch (EncryptionException e) {
						String message = null;
						if (encrypt) {
							message = String.format("Error while encrypting the field (%s)",
									element.getFieldDefinition().getFieldName());
						} else {
							message = String.format("Error while decrypting the field (%s)",
									element.getFieldDefinition().getFieldName());
						}

						LOG.error(message);
						throw new EncryptionException(message + " --> " + e.getMessage(), e);
					}
					break;
				}
			}
		}
	}

	/**
	 * Get a list of encryptable fields from an entity
	 * 
	 * @param entity
	 * @return List<String>
	 */
	private List<String> getEncryptableDataElements(Object entity) {

		List<String> dataElements = new ArrayList<String>();

		if (entity != null) {
			Encrypt encryptAnnotation = entity.getClass().getAnnotation(Encrypt.class);
			if (encryptAnnotation != null) {
				dataElements = Arrays.asList(encryptAnnotation.dataElements());
			}
		}

		return dataElements;
	}

	private Object invokeGet(Object objectClass, String field)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		String methodName = "get" + StringUtils.capitalize(field);
		Method method = null;

		if (objectClass.getClass().isMemberClass()) {
			method = objectClass.getClass().getMethod(methodName);
		} else {
			method = objectClass.getClass().getSuperclass().getMethod(methodName);
		}

		return method.invoke(objectClass);
	}

	private void invokeSet(Object objectClass, String field, String text)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		String methodName = "set" + StringUtils.capitalize(field);
		Method method = null;

		if (objectClass.getClass().isMemberClass()) {
			method = objectClass.getClass().getMethod(methodName, String.class);
		} else {
			method = objectClass.getClass().getSuperclass().getMethod(methodName, String.class);
		}

		method.invoke(objectClass, new String(text));
	}

	/**
	 * Mock encrypt text method for testing purpose
	 * 
	 * @param text
	 * @return cipherText
	 */
	@Deprecated
	private String encryptTextMock(String text) {
		String cipherText = text + "lorem ipsu";
		LOG.debug(String.format("Encrypted text (%s) to (%s)", text, cipherText));
		return cipherText;
	}

	/**
	 * Mock decrypt text method for testing purpose
	 * 
	 * @param cipherText
	 * @return text
	 */
	@Deprecated
	private String decryptTextMock(String cipherText) {
		String text = cipherText.replace("lorem ipsu", "");
		LOG.debug(String.format("Dencrypted text (%s) to (%s)", cipherText, text));
		return text;
	}
}
