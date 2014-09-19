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

/**
 * Check if there is anonymous access to logs on author instance
 * 
 * @author mlejba
 *
 */
public class AnonymousLogsTest extends AbstractTest implements AuthorTest {

	public AnonymousLogsTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws ClientProtocolException, IOException,
			AuthenticationException {
		String logsUrl = url + "/bin/crxde/logs";
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials("anonymous", "");
		DefaultHttpClient authorizedClient = new DefaultHttpClient();
		HttpUriRequest request = new HttpGet(logsUrl);
		request.addHeader(new BasicScheme().authenticate(creds, request));
		HttpResponse response = authorizedClient.execute(request);
		EntityUtils.consume(response.getEntity());
		if (response.getStatusLine().getStatusCode() == 200) {
			addErrorMessage("CQ author logs are available at: " + logsUrl);
			return false;
		} else {
			return true;
		}
	}

}