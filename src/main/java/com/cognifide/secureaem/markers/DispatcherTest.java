package com.cognifide.secureaem.markers;

/**
 * This test will be performed on the dispatcher.
 * 
 */
public interface DispatcherTest {

	String ENVIRONMENT_NAME = "dispatcher";

	boolean doTest(String url, String instanceName) throws Exception;
}
