package com.cognifide.securecq.tests;

import java.io.IOException;
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
import com.cognifide.securecq.markers.DispatcherTest;
import com.cognifide.securecq.markers.PublishTest;

/**
 * Check if the CRX DE logs servlet is enabled.
 * 
 * @author trekawek
 * 
 */
public class CrxdeLogsTest extends AbstractTest implements DispatcherTest, PublishTest {

	public CrxdeLogsTest(Configuration config) {
		super(config);
	}

	@Override
	public boolean doTest(String url, String instanceName) throws Exception {
		String testUrl = url + "/bin/crxde/logs?tail=100";
		if (logsAvailable(testUrl)) {
			addErrorMessage("Instance logs available at `curl -u anonymous: %s`", testUrl);
			return false;
		} else {
			addInfoMessage("Instance logs restricted [%s]", testUrl);
			return true;
		}
	}

	@SuppressWarnings("deprecation")
	private boolean logsAvailable(String url) throws URISyntaxException, ClientProtocolException,
			IOException, AuthenticationException {
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials("anonymous", "");
		DefaultHttpClient authorizedClient = new DefaultHttpClient();
		HttpUriRequest request = new HttpGet(url);
		request.addHeader(new BasicScheme().authenticate(creds, request));
		HttpResponse response = authorizedClient.execute(request);
		String body = EntityUtils.toString(response.getEntity());
		return body.contains("*INFO*") || body.contains("*WARN*");
	}

}
