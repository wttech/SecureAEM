package com.cognifide.secureaem.tests;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.UserHelper;
import com.cognifide.secureaem.markers.AuthorTest;
import com.cognifide.secureaem.markers.PublishTest;

public class SlingJsHandlerTest extends AbstractTest implements AuthorTest, PublishTest, OsgiConfigurationTest {

	public SlingJsHandlerTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String[] user = AuthorTest.ENVIRONMENT_NAME.equals(instanceName) ? UserHelper.splitUser(config.getStringValue("authorUser", "admin:admin")) : UserHelper.splitUser(config.getStringValue("publishUser", "admin:admin"));
		String configurationEndpoint = url + "/system/console/configMgr/org.apache.sling.scripting.java.impl.JavaScriptEngineFactory.json";
		String jsonBody = getJsonBodyOfOsgiConfiguration(configurationEndpoint, user, instanceName);
		checkBooleanValue(getBooleanValueFromJson("java.classdebuginfo", jsonBody), false, "Generate Debug Info", instanceName);
		return getErrorMessages().isEmpty();
	}

}
