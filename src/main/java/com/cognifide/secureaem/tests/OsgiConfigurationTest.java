package com.cognifide.secureaem.tests;

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

interface OsgiConfigurationTest {

	JsonParser JSON_PARSER = new JsonParser();

	default String getJsonBodyOfOsgiConfiguration(String osgiConfigurationUrl, String[] credentials, String instanceName) throws AuthenticationException, IOException {
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(credentials[0], credentials[1]);
		DefaultHttpClient authorizedClient = new DefaultHttpClient();
		HttpUriRequest request = new HttpGet(osgiConfigurationUrl);
		request.addHeader(new BasicScheme().authenticate(creds, request, null));
		HttpResponse response = authorizedClient.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpURLConnection.HTTP_OK) {
			throw new AuthenticationException("Cannot authenticate user " + credentials[0] + " to " + instanceName + " instance");
		}
		return EntityUtils.toString(response.getEntity());
	}

	default boolean getBooleanValueFromJson(String propertyKey, String osgiConfigurationJson) throws IOException, AuthenticationException {
		JsonObject configuration = JSON_PARSER.parse(osgiConfigurationJson).getAsJsonArray().get(0).getAsJsonObject();
		JsonObject properties =  configuration.getAsJsonObject("properties");
		return properties.getAsJsonObject(propertyKey).getAsJsonPrimitive("value").getAsBoolean();
	}
}
