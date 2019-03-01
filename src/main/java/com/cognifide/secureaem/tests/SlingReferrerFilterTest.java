package com.cognifide.secureaem.tests;

import com.google.gson.Gson;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.markers.AuthorTest;
import com.cognifide.secureaem.markers.PublishTest;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Check if Sling Referrer Filter is configured.
 *
 * Created by Mariusz Kubiś on 20.09.16
 */
public class SlingReferrerFilterTest extends AbstractTest implements AuthorTest, PublishTest {

	private static final Gson GSON = new Gson();

	private static final String FILTER_URL =
			"/system/console/configMgr/org.apache.sling.security.impl.ReferrerFilter.json";

	private static final DefaultHttpClient HTTP_CLIENT = new DefaultHttpClient();

	public SlingReferrerFilterTest(Configuration config) {
		super(config);
	}

	@Override public boolean doTest(String url, String instanceName) throws Exception {
		HttpResponse response = fetchRefererConfig(url, instanceName);
		if (response.getStatusLine().getStatusCode() == 200) {
			String body = EntityUtils.toString(response.getEntity());
			return validateResponseBody(instanceName, body);
		} else {
			addErrorMessage("Cannot get Sling Referrer Filter configuration from [%s]", instanceName);
			return false;
		}
	}

	private HttpResponse fetchRefererConfig(String url, String instanceName) throws AuthenticationException, IOException {
		UsernamePasswordCredentials credentials = getUsernamePasswordCredentials(instanceName);
		String testedUrl =
				url + FILTER_URL;
		HttpUriRequest request = new HttpGet(testedUrl);
		request.addHeader(new BasicScheme().authenticate(credentials, request, null));
		return HTTP_CLIENT.execute(request);
	}

	private boolean validateResponseBody(String instanceName, String body) {
		List<ReferrerFilterConfigModel> configModel = GSON.fromJson(body, new TypeToken<List<ReferrerFilterConfigModel>>(){}.getType());
		boolean validationResult = false;
		if(!configModel.isEmpty()) {
			validationResult = validateConfig(instanceName, configModel.get(0));
		} else {
			addErrorMessage("Sling Referrer Filter is not configured on [%s]", instanceName);
		}
		return validationResult;
	}

	private boolean validateConfig(String instanceName, ReferrerFilterConfigModel configModel) {
		boolean validationResult = false;
		if (configModel.getAllowedHosts().isEmpty() || configModel.getAllowedHostsRegeps().isEmpty()) {
			addErrorMessage("Sling Referrer Filter is not configured on [%s]", instanceName);
		} else {
			addInfoMessage("Sling Referrer Filter is configured on [%s]", instanceName);
			validationResult =  true;
		}
		return validationResult;
	}

	private static class ReferrerFilterConfigModel {

		private ReferrerFilterConfigProperties properties;

		public List<String> getAllowedHosts() {
			List<String> allowedHosts = properties.allowedHosts.getValues();
			return filterBlank(allowedHosts);
		}

		public List<String> getAllowedHostsRegeps() {
			List<String> allowedHosts = properties.allowedHostsRegexps.getValues();
			return filterBlank(allowedHosts);
		}

		private List<String> filterBlank(List<String> source) {
			return source.stream()
					.filter(StringUtils::isNotBlank)
					.collect(Collectors.toList());
		}
	}

	private static class ReferrerFilterConfigProperties {

		@SerializedName("allow.hosts")
		private MultipleValueConfig allowedHosts;

		@SerializedName("allow.hosts.regexp")
		private MultipleValueConfig allowedHostsRegexps;
	}

	private static class MultipleValueConfig {

		private String name;

		@SerializedName("is_set")
		private boolean isSet;

		private List<String> values;

		public List<String> getValues() {
			return Optional.ofNullable(values)
					.orElse(Collections.emptyList());
		}
	}
}
