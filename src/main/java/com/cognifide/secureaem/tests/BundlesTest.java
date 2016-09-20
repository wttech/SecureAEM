package com.cognifide.secureaem.tests;

import com.google.gson.Gson;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.UserHelper;
import com.cognifide.secureaem.markers.AuthorTest;
import com.cognifide.secureaem.markers.PublishTest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Check if the bundle is present
 *
 * Created by Mariusz Kubiś on 19.09.16
 */
public class BundlesTest extends AbstractTest implements AuthorTest, PublishTest {

	private static final Gson GSON = new Gson();

	public BundlesTest(Configuration config) {
		super(config);
	}

	@Override public boolean doTest(String url, String instanceName) throws Exception {
		String[] users = config.getStringList("users");
		if (ArrayUtils.isEmpty(users)) {
			throw new IllegalArgumentException("Invalid configuration");
		}
		String[] userInfo = UserHelper.splitUser(users[0]);
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userInfo[0], userInfo[1]);

		String agentUrl = url + "/system/console/bundles.json";
		HttpUriRequest request = new HttpGet(agentUrl);
		request.addHeader(new BasicScheme().authenticate(credentials, request, null));

		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(request);
		String body = EntityUtils.toString(response.getEntity());
		if (response.getStatusLine().getStatusCode() == 401) {
			throw new AuthenticationException("Cannot authenticate user " + credentials.getUserName());
		} else if (response.getStatusLine().getStatusCode() == 200) {
			@SuppressWarnings("unchecked") Map<String, List<Map<String, String>>> map = GSON
					.fromJson(body, Map.class);
			String[] bundlesConfig = config.getStringList("bundles");
			ArrayList<String> bundles = new ArrayList<>(Arrays.asList(bundlesConfig));
			for (Map<String, String> item : map.get("data")) {
				if (bundles.contains(item.get("name"))) {
					addErrorMessage("Bundle [%s] already exists on [%s]", item.get("name"), instanceName);
					bundles.remove(item.get("name"));
				}
			}
			for (String bundle : bundles) {
				addInfoMessage("Bundle [%s] doesn't exists on [%s]", bundle, instanceName);
			}
		}
		return true;
	}
}
