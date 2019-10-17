package com.cognifide.secureaem.sling;

import com.cognifide.secureaem.TestConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;

public class ResourceTestConfiguration implements TestConfiguration {

	private final ValueMap valueMap;

	public ResourceTestConfiguration(SlingHttpServletRequest request) {
		this.valueMap = request.getResource().adaptTo(ValueMap.class);
	}

	@Override
	public String getStringValue(String name, String defaultValue) {
		return getLocalConfig(name, defaultValue);
	}

	@Override
	public String[] getStringList(String name) {
		return getLocalConfig(name, ArrayUtils.EMPTY_STRING_ARRAY);
	}

	private <T> T getLocalConfig(String name, T defaultValue) {
		return this.valueMap.get(name, defaultValue);
	}
}
