package com.cognifide.secureaem.tests;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.markers.AuthorTest;
import com.cognifide.secureaem.markers.PublishTest;

public class SlingJspScriptHandlerTest extends AbstractTest
		implements AuthorTest, PublishTest, OsgiConfigurationTest {

	public SlingJspScriptHandlerTest(Configuration config) {
		super(config);
	}

	@Override public boolean doTest(String url, String instanceName) throws Exception {
		String configurationEndpoint = url
				+ "/system/console/configMgr/org.apache.sling.scripting.jsp.JspScriptEngineFactory.json";
		String body = getJsonBodyOfOsgiConfiguration(configurationEndpoint, getUsernamePasswordCredentials(instanceName), instanceName);
		checkBooleanValue(getBooleanValueFromJson("jasper.classdebuginfo", body), false,
				"Generate Debug Info", instanceName);
		checkBooleanValue(getBooleanValueFromJson("jasper.mappedfile", body), false, "Mapped Content",
				instanceName);
		return getErrorMessages().isEmpty();
	}

}
