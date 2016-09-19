package com.cognifide.secureaem.tests;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

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
import com.cognifide.secureaem.UserHelper;
import com.cognifide.secureaem.markers.AuthorTest;
import com.cognifide.secureaem.markers.PublishTest;

/**
 * Check if user with given login and password exists on given instance.
 * 
 * @author trekawek
 *
 */
public class DefaultPasswordsTest extends AbstractTest implements AuthorTest, PublishTest {

	public DefaultPasswordsTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		boolean ok = true;
		String[] users = config.getStringList("users");
		for (String user : users) {
			String[] split = UserHelper.splitUser(user);
			if (split[1] != null && remoteUserExists(split, url)) {
				addErrorMessage("User %s exists on %s", user, instanceName);
				ok = false;
			} else {
				addInfoMessage("User %s doesn't exists on %s", user, instanceName);
			}
		}
		return ok;
	}

	private boolean remoteUserExists(String[] user, String url) throws URISyntaxException,
			IOException, AuthenticationException {
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user[0], user[1]);
		DefaultHttpClient authorizedClient = new DefaultHttpClient();
		HttpUriRequest request = new HttpGet(url);
		request.addHeader(new BasicScheme().authenticate(creds, request, null));
		HttpResponse response = authorizedClient.execute(request);
		EntityUtils.consume(response.getEntity());
		int code = response.getStatusLine().getStatusCode();
		return code != HttpURLConnection.HTTP_UNAUTHORIZED;
	}

}
