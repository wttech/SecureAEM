package com.cognifide.secureaem.sling;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.cognifide.secureaem.GlobalConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service(SecureAemGlobalConfiguration.class)
@Component(label = "Global Configuration for Secure Aem",
	description = "This configuration is used instead of the global configuration provided in the content. To use values configured in content please disable this " +
			"Component or remove configuration file.",
	policy = ConfigurationPolicy.REQUIRE,
	metatype = true,
	immediate = true)
public class SecureAemGlobalConfiguration implements GlobalConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecureAemGlobalConfiguration.class);

	@Reference
	private CryptoSupport cryptoSupport;

	@Property(label = "Dispatcher url")
	private static final String DISPATCHER_URL = "dispatcher.url";

	@Property(label = "Author url", value = "http://localhost:4502")
	private static final String AUTHOR_URL = "author.url";

	@Property(label = "Author login", value = "admin")
	private static final String AUTHOR_LOGIN = "author.login";

	@Property(label = "Author password", value = "admin",
			description = "Should be encrypted with tool available through /system/console/crypto. Plain text supported but not recommended.")
	private static final String AUTHOR_PASSOWRD = "author.password";

	@Property(label = "Publish url", value = "http://localhost:4503")
	private static final String PUBLISH_URL = "publish.url";

	@Property(label = "Publish login", value = "admin")
	private static final String PUBLISH_LOGIN = "publish.login";

	@Property(label = "Publish password", value = "admin",
			description = "Should be encrypted with tool available through /system/console/crypto. Plain text supported but not recommended.")
	private static final String PUBLISH_PASSOWRD = "publish.password";

	private String dispatcherUrl;

	private String authorUrl;

	private String authorLogin;

	private String authorPassowrd;

	private String publishUrl;

	private String publishLogin;

	private String publishPassword;

	@Activate
	protected void activate(Map<String, Object> properties) {
		LOGGER.info("Activating service.");
		dispatcherUrl = PropertiesUtil.toString(properties.get(DISPATCHER_URL), "");
		authorUrl = PropertiesUtil.toString(properties.get(AUTHOR_URL), "");
		authorLogin = PropertiesUtil.toString(properties.get(AUTHOR_LOGIN), "");
		authorPassowrd = PropertiesUtil.toString(properties.get(AUTHOR_PASSOWRD), "");
		publishUrl = PropertiesUtil.toString(properties.get(PUBLISH_URL), "");
		publishLogin = PropertiesUtil.toString(properties.get(PUBLISH_LOGIN), "");
		publishPassword = PropertiesUtil.toString(properties.get(PUBLISH_PASSOWRD), "");
	}

	@Override
	public String getDispatcherUrl() {
		return dispatcherUrl;
	}

	@Override
	public String getAuthor() {
		return authorUrl;
	}

	@Override
	public String getAuthorLogin() {
		return authorLogin;
	}

	@Override
	public String getAuthorPassword() {
		return getPassword(authorPassowrd);
	}

	@Override
	public String getPublish() {
		return publishUrl;
	}

	@Override
	public String getPublishLogin() {
		return publishLogin;
	}

	@Override
	public String getPublishPassword() {
		return getPassword(publishPassword);
	}


	private String getPassword(String passwordToDecrypt) {
		String password = StringUtils.defaultString(passwordToDecrypt);
		if (cryptoSupport.isProtected(password)) {
			try {
				password = cryptoSupport.unprotect(password);
			} catch (CryptoException e) {
				LOGGER.error("Failed to decrypt password", e);
			}
		}
		return password;
	}
}
