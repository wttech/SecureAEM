package com.cognifide.secureaem.tests;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.markers.PublishTest;

/**
 * Check if the WCM filter is disabled on publish instance.
 *
 * Created by Mariusz Kubiś on 21.09.16
 */
public class WcmFilterTest extends AbstractTest implements PublishTest {

	public WcmFilterTest(Configuration config) {
		super(config);
	}

	@Override public boolean doTest(String url, String instanceName) throws Exception {
		String testUrl = httpHelper.getBasePath(url, false) + "?wcmmode=edit";
		if (httpHelper.pageContainsString(testUrl, "CQ.WCM.launchSidekick")) {
			addErrorMessage("WCM filter is not disabled on [%s]", instanceName);
			return false;
		} else {
			addInfoMessage("WCM filter is disabled on [%s]", instanceName);
			return true;
		}
	}
}
