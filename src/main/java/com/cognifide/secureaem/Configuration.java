package com.cognifide.secureaem;

/**
 * Test configuration.
 * 
 */
public interface Configuration {
	String getDispatcherUrl();

	String getAuthor();

	String getPublish();

	String getStringValue(String name, String defaultValue);

	String[] getStringList(String name);
}
