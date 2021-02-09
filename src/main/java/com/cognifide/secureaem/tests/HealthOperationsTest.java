package com.cognifide.secureaem.tests;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.CliConfiguration;
import com.cognifide.secureaem.TestConfiguration;
import com.cognifide.secureaem.markers.AuthorTest;
import com.cognifide.secureaem.markers.PublishTest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Check the Operations Dashboard Security Health Checks
 *
 */
public class HealthOperationsTest extends AbstractTest implements AuthorTest, PublishTest {

	private static final DefaultHttpClient client = new DefaultHttpClient();

	public HealthOperationsTest(CliConfiguration config, TestConfiguration testConfiguration) {
		super(config, testConfiguration);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		UsernamePasswordCredentials credentials = getUsernamePasswordCredentials(instanceName);
		String testedUrl = url
				+ "/system/sling/monitoring/mbeans/org/apache/sling/healthcheck/HealthCheck/securitychecks.json";
		HttpUriRequest request = new HttpGet(testedUrl);
		request.addHeader(new BasicScheme().authenticate(credentials, request, null));
		HttpResponse response = client.execute(request);
		String body = EntityUtils.toString(response.getEntity());
		if (response.getStatusLine().getStatusCode() == 200) {
			JsonObject securityChecksJson = new Gson().fromJson(body, JsonObject.class);
			String securityStatus = securityChecksJson.get("status").getAsString();
			if (!securityStatus.equals("OK")) {
				addErrorMessage("Security health checks have status %s on [%s]", securityStatus, instanceName);
				return false;
			} else {
				addInfoMessage("Security health checks have status %s on [%s]", securityStatus, instanceName);
				return true;
			}
		} else {
			addErrorMessage("Cannot get security health checks information from [%s]", testedUrl);
			return false;
		}
	}
}
