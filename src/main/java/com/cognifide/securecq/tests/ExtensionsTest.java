package com.cognifide.securecq.tests;

import com.cognifide.securecq.AbstractTest;
import com.cognifide.securecq.Configuration;
import com.cognifide.securecq.markers.DispatcherTest;

/**
 * Check if it's possible to request content with given extension.
 * 
 * @author trekawek
 *
 */
public class ExtensionsTest extends AbstractTest implements DispatcherTest {
	public ExtensionsTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String[] extensions = config.getStringList("extensions");
		String basePath = httpHelper.getBasePath(url, true);
		boolean ok = true;
		for (String extension : extensions) {
			String testUrl = basePath + extension;
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
