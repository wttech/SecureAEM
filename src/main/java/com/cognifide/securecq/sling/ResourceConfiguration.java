package com.cognifide.securecq.sling;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.cognifide.securecq.Configuration;

public class ResourceConfiguration implements Configuration {
	private static final String GLOBAL_CONFIG_PATH = "/etc/securecq/jcr:content/globalConfig";

	private final ValueMap globalConfig;

	private final SlingHttpServletRequest request;

	public ResourceConfiguration(SlingHttpServletRequest request) {
		Resource globalConfigRes = request.getResourceResolver().getResource(GLOBAL_CONFIG_PATH);
		if (globalConfigRes == null) {
			globalConfig = null;
		} else {
			globalConfig = globalConfigRes.adaptTo(ValueMap.class);
		}
		this.request = request;
	}

	@Override
	public String getDispatcherUrl() {
		return StringUtils.removeEnd(getGlobalConfig("dispatcher"), "/");
	}

	@Override
	public String getAuthor() {
		return StringUtils.removeEnd(getGlobalConfig("author"), "/");
	}

	@Override
	public String getPublish() {
		return StringUtils.removeEnd(getGlobalConfig("publish"), "/");
	}

	@Override
	public String getStringValue(String name, String defaultValue) {
		return getLocalConfig(name, defaultValue);
	}

	@Override
	public String[] getStringList(String name) {
		return getLocalConfig(name, ArrayUtils.EMPTY_STRING_ARRAY);
	}

	private String getGlobalConfig(String name) {
		if (globalConfig == null) {
			return null;
		}
		return globalConfig.get(name, String.class);
	}

	private <T> T getLocalConfig(String name, T defaultValue) {
		return request.getResource().adaptTo(ValueMap.class).get(name, defaultValue);
	}
}
