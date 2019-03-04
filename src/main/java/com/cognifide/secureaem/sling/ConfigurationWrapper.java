package com.cognifide.secureaem.sling;

import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.GlobalConfiguration;
import com.cognifide.secureaem.TestConfiguration;

public class ConfigurationWrapper implements Configuration {

	private GlobalConfiguration globalConfiguration;

	private TestConfiguration testConfiguration;

	public ConfigurationWrapper(GlobalConfiguration globalConfiguration, TestConfiguration testConfiguration) {
		this.globalConfiguration = globalConfiguration;
		this.testConfiguration = testConfiguration;
	}

	@Override
	public String getDispatcherUrl() {
		return globalConfiguration.getDispatcherUrl();
	}

	@Override
	public String getAuthor() {
		return globalConfiguration.getAuthor();
	}

	@Override
	public String getAuthorLogin() {
		return globalConfiguration.getAuthorLogin();
	}

	@Override
	public String getAuthorPassword() {
		return globalConfiguration.getAuthorPassword();
	}

	@Override
	public String getPublish() {
		return globalConfiguration.getPublish();
	}

	@Override
	public String getPublishLogin() {
		return globalConfiguration.getPublishLogin();
	}

	@Override
	public String getPublishPassword() {
		return globalConfiguration.getPublishPassword();
	}

	@Override
	public String getStringValue(String name, String defaultValue) {
		return testConfiguration.getStringValue(name, defaultValue);
	}

	@Override
	public String[] getStringList(String name) {
		return testConfiguration.getStringList(name);
	}
}
