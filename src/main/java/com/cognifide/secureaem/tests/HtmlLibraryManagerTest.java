package com.cognifide.secureaem.tests;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.UserHelper;
import com.cognifide.secureaem.markers.AuthorTest;
import com.cognifide.secureaem.markers.PublishTest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;

public class HtmlLibraryManagerTest extends AbstractTest implements AuthorTest, PublishTest {
	private static final JsonParser JSON_PARSER = new JsonParser();

	public HtmlLibraryManagerTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String[] user = AuthorTest.ENVIRONMENT_NAME.equals(instanceName) ? UserHelper.splitUser(config.getStringValue("authorUser", "admin:admin")) : UserHelper.splitUser(config.getStringValue("publishUser", "admin:admin"));
		String configurationEndpoint = url + "/system/console/configMgr/com.adobe.granite.ui.clientlibs.impl.HtmlLibraryManagerImpl.json";
		JsonObject configuration = getConfiguration(configurationEndpoint, user, instanceName);
		JsonObject properties = configuration.getAsJsonObject("properties");
		checkProperty(getBooleanValue(properties, "htmllibmanager.minify"), "Minify", true, instanceName);
		checkProperty(getBooleanValue(properties, "htmllibmanager.gzip"), "Gzip", true, instanceName);
		checkProperty(getBooleanValue(properties, "htmllibmanager.debug"), "Debug", false, instanceName);
		checkProperty(getBooleanValue(properties, "htmllibmanager.timing"), "Timing", false, instanceName);
		return getErrorMessages().isEmpty();
	}

	private void checkProperty(boolean propertyValue, String propertyName, boolean expectedValue, String instanceName) {
		if (propertyValue == expectedValue) {
			addInfoMessage("On %s instance %s property is %b", instanceName, propertyName, propertyValue);
		} else {
			addErrorMessage("On %s instance %s property is %b, but it should be %b", instanceName, propertyName, propertyValue, expectedValue);
		}
	}

	private boolean getBooleanValue(JsonObject properties, String propertyKey) {
		return properties.getAsJsonObject(propertyKey).getAsJsonPrimitive("value").getAsBoolean();
	}

	private JsonObject getConfiguration(String url, String[] credentials, String instanceName) throws AuthenticationException, IOException {
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(credentials[0], credentials[1]);
		DefaultHttpClient authorizedClient = new DefaultHttpClient();
		HttpUriRequest request = new HttpGet(url);
		request.addHeader(new BasicScheme().authenticate(creds, request, null));
		HttpResponse response = authorizedClient.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpURLConnection.HTTP_OK) {
			throw new AuthenticationException("Cannot authenticate user " + credentials[0] + " to " + instanceName + " instance");
		}
		String body = EntityUtils.toString(response.getEntity());
		return JSON_PARSER.parse(body).getAsJsonArray().get(0).getAsJsonObject();
	}
}
