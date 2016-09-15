package com.cognifide.secureaem.markers;

/**
 * This test will be performed on the publish instance.
 * 
 */
public interface PublishTest {

	String ENVIRONMENT_NAME = "publish";

	boolean doTest(String url, String instanceName) throws Exception;
}
