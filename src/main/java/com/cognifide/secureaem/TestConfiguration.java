package com.cognifide.secureaem;

/**
 * Configuration of particular test
 */
public interface TestConfiguration {

	String getStringValue(String name, String defaultValue);

	String[] getStringList(String name);
}
