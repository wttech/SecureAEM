package com.cognifide.secureaem.markers;

/**
 * This test will be performed on the author instance.
 * 
 */
public interface AuthorTest {

	String ENVIRONMENT_NAME = "author";

	boolean doTest(String url, String instanceName) throws Exception;

}
