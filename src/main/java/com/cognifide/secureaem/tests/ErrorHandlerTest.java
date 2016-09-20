package com.cognifide.secureaem.tests;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.UserHelper;
import com.cognifide.secureaem.markers.PublishTest;
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
import java.net.URISyntaxException;

public class ErrorHandlerTest extends AbstractTest implements PublishTest {

	public ErrorHandlerTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String user = config.getStringValue("user", "admin:admin");
		String[] credentials = UserHelper.splitUser(user);
		checkErrorHandlers(credentials, url);
		return getErrorMessages().isEmpty();
	}

	private void checkErrorHandlers(String[] credentials, String url) throws URISyntaxException, IOException, AuthenticationException {
		String notFoundHandlerUrl = url + "/apps/sling/servlet/errorhandler/404.jsp";
		String serverErrorHandlerUrl = url + "/apps/sling/servlet/errorhandler/Throwable.jsp";
		checkIfErrorHandlersExists(credentials, serverErrorHandlerUrl, "500");
		checkIfErrorHandlersExists(credentials, notFoundHandlerUrl, "404");
	}

	private void checkIfErrorHandlersExists(String[] credentials, String notFoundHandlerUrl, String handlerName) throws URISyntaxException, IOException, AuthenticationException {
		if (getErrorHandlerResponseCode(credentials, notFoundHandlerUrl) != HttpURLConnection.HTTP_OK) {
			addErrorMessage("Custom %s error handler doesn't exists on publish instance", handlerName);
		} else {
			addInfoMessage("Custom %s error handler exists on publish instance", handlerName);
		}
	}

	private int getErrorHandlerResponseCode(String[] credentials, String url) throws URISyntaxException,
			IOException, AuthenticationException {
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(credentials[0], credentials[1]);
		DefaultHttpClient authorizedClient = new DefaultHttpClient();
		HttpUriRequest request = new HttpGet(url);
		request.addHeader(new BasicScheme().authenticate(creds, request, null));
		HttpResponse response = authorizedClient.execute(request);
		EntityUtils.consume(response.getEntity());
		return response.getStatusLine().getStatusCode();
	}

}
