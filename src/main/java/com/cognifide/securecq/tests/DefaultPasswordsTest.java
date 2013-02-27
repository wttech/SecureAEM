package com.cognifide.securecq.tests;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.cognifide.securecq.AbstractTest;
import com.cognifide.securecq.Configuration;
import com.cognifide.securecq.markers.AuthorTest;
import com.cognifide.securecq.markers.PublishTest;

public class DefaultPasswordsTest extends AbstractTest implements AuthorTest, PublishTest {

	public DefaultPasswordsTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		boolean ok = true;
		String[] users = config.getStringList("users");
		for (String user : users) {
			String[] split = splitUser(user);
			if (split[1] != null && remoteUserExists(split, url)) {
				addErrorMessage("User %s exists on %s", user, instanceName);
				ok = false;
			} else {
				addInfoMessage("User %s doesn't exists on %s", user, instanceName);
			}
		}
		return ok;
	}

	private String[] splitUser(String user) {
		int colon = user.indexOf(':');
		String[] result = new String[2];
		if (colon == -1) {
			result[0] = user;
			result[1] = null;
		} else {
			result[0] = user.substring(0, colon);
			result[1] = user.substring(colon + 1);
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	private boolean remoteUserExists(String[] user, String url) throws URISyntaxException,
			ClientProtocolException, IOException, AuthenticationException {
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user[0], user[1]);
		DefaultHttpClient authorizedClient = new DefaultHttpClient();
		HttpUriRequest request = new HttpGet(url);
		request.addHeader(new BasicScheme().authenticate(creds, request));
		HttpResponse response = authorizedClient.execute(request);
		EntityUtils.consume(response.getEntity());
		int code = response.getStatusLine().getStatusCode();
		return code != HttpURLConnection.HTTP_UNAUTHORIZED;
	}

}
