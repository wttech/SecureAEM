package com.cognifide.securecq.tests;

import com.cognifide.securecq.AbstractTest;
import com.cognifide.securecq.Configuration;
import com.cognifide.securecq.markers.DispatcherTest;

/**
 * Check if the CRX DE logs servlet is enabled.
 * 
 * @author trekawek
 * 
 */
public class CrxdeLogsTest extends AbstractTest implements DispatcherTest {

	public CrxdeLogsTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String testUrl = url + "/bin/crxde/logs?tail=100";
		if (httpHelper.pageContainsString(testUrl, "*INFO*")) {
			addErrorMessage("Instance logs available at `[%s]`", testUrl.replace("//", "//anonymous:@"));
			return false;
		} else {
			addInfoMessage("Instance logs restricted [%s]", testUrl);
			return true;
		}
	}
}
