package com.cognifide.secureaem.tests;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.markers.AuthorTest;
import com.cognifide.secureaem.markers.PublishTest;
import com.google.gson.Gson;

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
		UsernamePasswordCredentials credentials = getUsernamePasswordCredentials(instanceName);

		String agentUrl = url + "/system/console/bundles.json";
		HttpUriRequest request = new HttpGet(agentUrl);
		request.addHeader(new BasicScheme().authenticate(credentials, request, null));

		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(request);
		String body = EntityUtils.toString(response.getEntity());
		if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
			throw new AuthenticationException("Cannot authenticate user " + credentials.getUserName());
		} else if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
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