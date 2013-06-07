package com.cognifide.securecq.markers;

/**
 * This test will be performed on the publish instance.
 * 
 */
public interface PublishTest {
	boolean doTest(String url, String instanceName) throws Exception;
}
