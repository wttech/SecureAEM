package com.cognifide.secureaem.tests;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
 */
public class DefaultPasswordsTest extends AbstractTest implements AuthorTest, PublishTest {

	private static final String LOGIN_PATH = "/libs/granite/core/content/login.html/j_security_check";
	private static final String USERNAME_FORM_PARAM_NAME = "j_username";
	private static final String PASSWORD_FORM_PARAM_NAME = "j_password";
	private static final String IS_VALIDATE_FORM_PARAM_NAME = "j_validate";

	public DefaultPasswordsTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String loginUrl = url + LOGIN_PATH;
		boolean ok = true;
		String[] users = config.getStringList("users");
		for (String user : users) {
			String[] split = UserHelper.splitUser(user);
			if (split[1] != null && remoteUserExists(split, loginUrl)) {
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
		DefaultHttpClient authorizedClient = new DefaultHttpClient();

		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = getPostParamsList(user);
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		HttpResponse response = authorizedClient.execute(httpPost);
		EntityUtils.consume(response.getEntity());
		int code = response.getStatusLine().getStatusCode();
		return code != HttpURLConnection.HTTP_FORBIDDEN;
	}

	private List<NameValuePair> getPostParamsList(String[] user) {
		List<NameValuePair> params = new ArrayList<>();

		params.add(new BasicNameValuePair(USERNAME_FORM_PARAM_NAME, user[0]));
		params.add(new BasicNameValuePair(PASSWORD_FORM_PARAM_NAME, user[1]));
		params.add(new BasicNameValuePair(IS_VALIDATE_FORM_PARAM_NAME, "true"));

		return params;
	}

}
