package com.cognifide.secureaem.sling;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.cli.CliConfiguration;

public class ResourceConfiguration implements Configuration {
	private static final Logger LOG = LoggerFactory.getLogger(ResourceConfiguration.class);
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
		return getCredentialsParameter("authorCredentials", 0);
	}

	@Override public String getAuthorPassword() {
		return getCredentialsParameter("authorCredentials", 1);
	}

	@Override
	public String getPublish() {
		return StringUtils.removeEnd(getGlobalConfig("publish"), "/");
	}

	@Override public String getPublishLogin() {
		return getCredentialsParameter("publishCredentials", 0);
	}

	@Override public String getPublishPassword() {
		return getCredentialsParameter("publishCredentials", 1);
	}

	@Override
	public String getStringValue(String name, String defaultValue) {
		return getLocalConfig(name, defaultValue);
	}

	@Override
	public String[] getStringList(String name) {
		return getLocalConfig(name, ArrayUtils.EMPTY_STRING_ARRAY);
	}

	private String getCredentialsParameter(String credentialName, int parameterIndex) {
		String[] parameters = getGlobalConfig(credentialName).split(":");
		if(parameters.length == 2) {
			return parameters[parameterIndex];
		}
		return CliConfiguration.DEFAULT_USER;
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
