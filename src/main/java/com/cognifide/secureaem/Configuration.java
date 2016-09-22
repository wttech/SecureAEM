package com.cognifide.secureaem;

/**
 * Test configuration.
 * 
 */
public interface Configuration {
	String getDispatcherUrl();

	String getAuthor();

	String getAuthorLogin();

	String getAuthorPassword();

	String getPublish();

	String getPublishLogin();

	String getPublishPassword();

	String getStringValue(String name, String defaultValue);

	String[] getStringList(String name);
}
