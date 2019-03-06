package com.cognifide.secureaem.sling;

import com.adobe.granite.crypto.CryptoSupport;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.GlobalConfiguration;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

@Component
@Service(ConfigurationProvider.class)
public class ConfigurationProvider {

	@Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY,
			policy = ReferencePolicy.DYNAMIC,
			referenceInterface = SecureAemGlobalConfiguration.class,
			bind = "bind", unbind = "unbind")
	private SecureAemGlobalConfiguration globalConfiguration;

	@Reference
	private CryptoSupport cryptoSupport;

	public Configuration createConfiguration(SlingHttpServletRequest request) {
		ResourceTestConfiguration testConfiguration = new ResourceTestConfiguration(request);
		GlobalConfiguration globalConfig = globalConfiguration;
		if (globalConfig == null) {
			globalConfig = new ResourceGlobalConfiguration(request, cryptoSupport);
		}
		return new ConfigurationWrapper(globalConfig, testConfiguration);
	}

	public void bind(SecureAemGlobalConfiguration globalConfiguration) {
		this.globalConfiguration = globalConfiguration;
	}

	public void unbind(SecureAemGlobalConfiguration globalConfiguration) {
		this.globalConfiguration = null;
	}
}
