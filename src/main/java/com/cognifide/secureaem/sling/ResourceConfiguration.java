package com.cognifide.secureaem.sling;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.secureaem.Configuration;

public class ResourceConfiguration implements Configuration {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultConfigurationProvider.class);
	private final ValueMap globalConfig;

	private final SlingHttpServletRequest request;

	public ResourceConfiguration(SlingHttpServletRequest request) {
		LOG.info(System.getProperty("javax.net.ssl.trustStore"));
		Resource globalConfigRes = findGlobalConfig(request);
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

	@Override public String getAuthorLogin() {
		String[] parameters = getGlobalConfig("authorCredentials").split(":");
		if(parameters.length == 2) {
			return parameters[0];
		}
		return StringUtils.EMPTY;
	}

	@Override public String getAuthorPassword() {
		String[] parameters = getGlobalConfig("authorCredentials").split(":");
		if(parameters.length == 2) {
			return parameters[1];
		}
		return StringUtils.EMPTY;
	}

	@Override
	public String getPublish() {
		return StringUtils.removeEnd(getGlobalConfig("publish"), "/");
	}

	@Override public String getPublishLogin() {
		String[] parameters = getGlobalConfig("publishCredentials").split(":");
		if(parameters.length == 2) {
			return parameters[0];
		}
		return StringUtils.EMPTY;
	}

	@Override public String getPublishPassword() {
		String[] parameters = getGlobalConfig("publishCredentials").split(":");
		if(parameters.length == 2) {
			return parameters[1];
		}
		return StringUtils.EMPTY;
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

	private Resource findGlobalConfig(SlingHttpServletRequest request) {
		Resource resource = request.getResource();
		while (resource != null) {
			if (resource.isResourceType("cq:Page")) {
				Resource content = resource.getChild("jcr:content");
				String resourceType = content.adaptTo(ValueMap.class).get("sling:resourceType", String.class);
				if ("cognifide/secureaem/renderers/mainRenderer".equals(resourceType)) {
					return content.getChild("globalConfig");
				}
			}
			resource = resource.getParent();
		}
		return null;
	}
}
