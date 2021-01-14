package com.cognifide.secureaem.tests;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.TestConfiguration;
import com.cognifide.secureaem.markers.DispatcherTest;

/**
 * Check if it's possible to request content with given extension.
 * 
 * @author trekawek
 *
 */
public class ExtensionsTest extends AbstractTest implements DispatcherTest {
	public ExtensionsTest(Configuration config, TestConfiguration testConfiguration) {
		super(config, testConfiguration);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String[] extensions = testConfiguration.getExtensions();
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
