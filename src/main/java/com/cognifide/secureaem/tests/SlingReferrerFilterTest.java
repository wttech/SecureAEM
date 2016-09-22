package com.cognifide.secureaem.tests;

import com.google.gson.Gson;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.markers.AuthorTest;
import com.cognifide.secureaem.markers.PublishTest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Check if Sling Referrer Filter is configured.
 *
 * Created by Mariusz Kubiś on 20.09.16
 */
public class SlingReferrerFilterTest extends AbstractTest implements AuthorTest, PublishTest {

	private static final Gson GSON = new Gson();

	private static final DefaultHttpClient client = new DefaultHttpClient();

	public SlingReferrerFilterTest(Configuration config) {
		super(config);
	}

	@Override public boolean doTest(String url, String instanceName) throws Exception {
		UsernamePasswordCredentials credentials = getUsernamePasswordCredentials(instanceName);
		String testedUrl =
				url + "/system/console/configMgr/org.apache.sling.security.impl.ReferrerFilter.json";
		HttpUriRequest request = new HttpGet(testedUrl);
		request.addHeader(new BasicScheme().authenticate(credentials, request, null));
		HttpResponse response = client.execute(request);
		String body = EntityUtils.toString(response.getEntity());
		if (response.getStatusLine().getStatusCode() == 200) {
			String hostsString = StringUtils
					.substringBetween(body, "\"allow.hosts\":", ",\"allow.hosts.regexp\"");
			@SuppressWarnings("unchecked") Map<String, ArrayList<String>> hostsMap = GSON
					.fromJson(hostsString, Map.class);
			String regexpString = StringUtils
					.substringBetween(body, "\"allow.hosts.regexp\":", ",\"filter.methods\"");
			@SuppressWarnings("unchecked") Map<String, ArrayList<String>> regexpMap = GSON
					.fromJson(regexpString, Map.class);
			List<String> hosts = hostsMap.get("values");
			List<String> regexps = regexpMap.get("values");
			hosts.removeAll(Collections.singletonList(""));
			regexps.removeAll(Collections.singletonList(""));
			if (hosts.isEmpty() && regexps.isEmpty()) {
				addErrorMessage("Sling Referrer Filter is not configured on [%s]", instanceName);
				return false;
			} else {
				addInfoMessage("Sling Referrer Filter is configured on [%s]", instanceName);
				return true;
			}
		} else {
			addErrorMessage("Cannot get Sling Referrer Filter configuration from [%s]", instanceName);
			return false;
		}
	}
}
