package com.cognifide.secureaem.sling;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.cognifide.secureaem.GlobalConfiguration;
import com.cognifide.secureaem.cli.CliConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceGlobalConfiguration implements GlobalConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(ResourceConfiguration.class);

	private ValueMap globalConfig;

	private CryptoSupport cryptoSupport;

	public ResourceGlobalConfiguration(SlingHttpServletRequest request, CryptoSupport cryptoSupport) {
		this.cryptoSupport = cryptoSupport;
		Resource globalConfigRes = findGlobalConfig(request);
		if (globalConfigRes != null) {
			globalConfig = globalConfigRes.adaptTo(ValueMap.class);
		}
	}

	@Override
	public String getDispatcherUrl() {
		return StringUtils.removeEnd(getGlobalConfigParam("dispatcher"), "/");
	}

	@Override
	public String getAuthor() {
		return StringUtils.removeEnd(getGlobalConfigParam("author"), "/");
	}

	@Override public String getAuthorLogin() {
		return getGlobalConfigParam("authorLogin");
	}

	@Override public String getAuthorPassword() {
		return getPassword("authorPassword");
	}

	@Override
	public String getPublish() {
		return StringUtils.removeEnd(getGlobalConfigParam("publish"), "/");
	}

	@Override public String getPublishLogin() {
		return getGlobalConfigParam("publishLogin");
	}

	@Override public String getPublishPassword() {
		return getPassword("publishPassword");
	}

	private String getPassword(String paramName) {
		String password = StringUtils.defaultString(getGlobalConfigParam(paramName));
		String decryptedPassword = password;
		if (cryptoSupport.isProtected(password)) {
			try {
				decryptedPassword = cryptoSupport.unprotect(password);
			} catch (CryptoException e) {
				LOG.error("Failed to decrypt password {}", paramName, e);
			}
		}
		return decryptedPassword;
	}


	private String getGlobalConfigParam(String name) {
		if (globalConfig == null) {
			return null;
		}
		return globalConfig.get(name, String.class);
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
