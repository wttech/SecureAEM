package com.cognifide.securecq.tests;

import com.cognifide.securecq.AbstractTest;
import com.cognifide.securecq.Configuration;
import com.cognifide.securecq.markers.DispatcherTest;

/**
 * Check if there is a page under given path and if this page contains given string.
 * 
 * @author trekawek
 *
 */
public class PageContentTest extends AbstractTest implements DispatcherTest {

	public PageContentTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String[] paths = config.getStringList("paths");
		String[] content = config.getStringList("content");
		if (paths.length != content.length) {
			throw new IllegalArgumentException("Invalid configuration");
		}
		boolean ok = true;
		for (int i = 0; i < paths.length; i++) {
			String testUrl = url + paths[i];
			if (httpHelper.pageContainsString(testUrl, content[i])) {
				addErrorMessage("[%s] is not restricted", testUrl);
				ok = false;
			} else {
				addInfoMessage("[%s] is restricted", testUrl);
			}
		}
		return ok;
	}
}