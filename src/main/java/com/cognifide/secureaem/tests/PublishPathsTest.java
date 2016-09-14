package com.cognifide.secureaem.tests;

import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.markers.PublishTest;

/**
 * Check if there is a page under given path.
 * 
 * @author trekawek
 * 
 */
public class PublishPathsTest extends PathsTest implements PublishTest {

	public PublishPathsTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String[] paths = config.getStringList("paths");
		boolean ok = true;
		for (String path : paths) {
			String testUrl = url + path;
			if (httpHelper.pathExists(testUrl)) {
				addErrorMessage("[%s] is not restricted", testUrl);
				ok = false;
			} else {
				addInfoMessage("[%s] is restricted", testUrl);
			}
		}
		return ok;
	}
}
