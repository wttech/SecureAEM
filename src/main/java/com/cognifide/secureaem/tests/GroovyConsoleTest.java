package com.cognifide.secureaem.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.markers.DispatcherTest;
import com.cognifide.secureaem.markers.PublishTest;
import com.google.gson.Gson;

/**
 * Check if there is a groovy console running.
 * 
 * @author trekawek
 * 
 */
public class GroovyConsoleTest extends AbstractTest implements PublishTest, DispatcherTest {

	private static final Gson GSON = new Gson();

	public GroovyConsoleTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String servletUrl = url + "/etc/groovyconsole/jcr:content.html";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(servletUrl);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("script", "print session.getUserID()"));
		request.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		HttpResponse response = client.execute(request);
		String body = EntityUtils.toString(response.getEntity());
		if (response.getStatusLine().getStatusCode() == 200) {
			if (StringUtils.contains(body, "executionResult")) {
				@SuppressWarnings("unchecked")
				Map<String, String> map = GSON.fromJson(body, Map.class);
				String user = map.get("outputText");
				addErrorMessage("Groovy servlet works at [%s] on behalf of " + user, servletUrl);
				return false;
			}
		}
		return true;
	}
}
