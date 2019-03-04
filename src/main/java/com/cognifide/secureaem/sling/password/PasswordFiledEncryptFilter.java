package com.cognifide.secureaem.sling.password;

public interface PasswordFiledEncryptFilter {

	/**
	 * Checks whether property given by <code>propertyPath</code> parameter should be encrypted
	 * @param propertyPath absolute path to property
	 * @return <strong>true</strong> if property should be encrypted, <strong>false</strong> otherwise
	 */
	boolean isSupported(String propertyPath);

}
