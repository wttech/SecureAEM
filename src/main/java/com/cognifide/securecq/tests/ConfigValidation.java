package com.cognifide.securecq.tests;

import com.cognifide.securecq.AbstractTest;
import com.cognifide.securecq.Configuration;
import com.cognifide.securecq.markers.AuthorTest;
import com.cognifide.securecq.markers.DispatcherTest;
import com.cognifide.securecq.markers.PublishTest;

/**
 * This class checks if the SecureCQ configuration looks sane.
 * 
 * @author trekawek
 *
 */
public class ConfigValidation extends AbstractTest implements AuthorTest, DispatcherTest, PublishTest {

	public ConfigValidation(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		if (safePathExists(url)) {
			addInfoMessage("URL [%s] for instance %s looks OK", url, instanceName);
		} else {
			addErrorMessage("URL [%s] for instance %s doesn't look right", url, instanceName);
			return false;
		}
		return true;
	}

	private boolean safePathExists(String url) {
		try {
			httpHelper.pathExists(url);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
