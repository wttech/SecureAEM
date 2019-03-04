package com.cognifide.secureaem.sling;

import com.adobe.granite.crypto.CryptoSupport;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.GlobalConfiguration;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Optional;

@Component
@Service(ConfigurationProvider.class)
public class ConfigurationProvider {

	@Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY)
	private GlobalConfiguration globalConfiguration;

	@Reference
	private CryptoSupport cryptoSupport;

	public Configuration createConfiguration(SlingHttpServletRequest request) {
		ResourceTestConfiguration testConfiguration = new ResourceTestConfiguration(request);
		GlobalConfiguration globalConfig = Optional.ofNullable(this.globalConfiguration)
				.orElseGet(() -> new ResourceGlobalConfiguration(request, cryptoSupport));
		return new ConfigurationWrapper(globalConfig, testConfiguration);
	}

}
