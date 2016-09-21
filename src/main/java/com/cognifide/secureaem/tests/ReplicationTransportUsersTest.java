package com.cognifide.secureaem.tests;

import com.google.gson.Gson;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.markers.AuthorTest;
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
import java.util.Map;

/**
 * Check if replication transport user is different then admin.
 *
 * Created by Mariusz Kubiś on 19.09.16
 */
public class ReplicationTransportUsersTest extends AbstractTest implements AuthorTest, PublishTest {

	private static final Gson GSON = new Gson();

	private static final DefaultHttpClient client = new DefaultHttpClient();

	public ReplicationTransportUsersTest(Configuration config) {
		super(config);
	}

	@Override public boolean doTest(String url, String instanceName) throws Exception {
		UsernamePasswordCredentials credentials = getUsernamePasswordCredentials(instanceName);

		boolean result = testReplicationUser(credentials, instanceName, url);
		return testReversReplicationUser(credentials, instanceName, url) && result;
	}

	private boolean testReplicationUser(UsernamePasswordCredentials credentials, String instanceName,
			String url) throws IOException, AuthenticationException {
		String agentUrl = url + "/etc/replication/agents.author/publish/jcr:content.json";
		HttpUriRequest request = new HttpGet(agentUrl);
		request.addHeader(new BasicScheme().authenticate(credentials, request, null));

		return invokeTest(request, credentials.getUserName(), instanceName, "Replication");
	}

	private boolean testReversReplicationUser(UsernamePasswordCredentials credentials, String instanceName,
			String url) throws AuthenticationException, IOException {
		String agentUrl = url + "/etc/replication/agents.author/publish_reverse/jcr:content.json";
		HttpUriRequest request = new HttpGet(agentUrl);
		request.addHeader(new BasicScheme().authenticate(credentials, request, null));

		return invokeTest(request, credentials.getUserName(), instanceName, "Reverse Replication");
	}

	private boolean invokeTest(HttpUriRequest request, String username, String instanceName,
			String replicationType) throws IOException, AuthenticationException {
		HttpResponse response = client.execute(request);
		String body = EntityUtils.toString(response.getEntity());
		if (response.getStatusLine().getStatusCode() == 401) {
			throw new AuthenticationException("Cannot authenticate user " + username);
		} else if (response.getStatusLine().getStatusCode() == 200) {
			@SuppressWarnings("unchecked") Map<String, String> map = GSON.fromJson(body, Map.class);
			String user = map.get("transportUser");
			if ("admin".equals(user)) {
				addErrorMessage(replicationType + " transport user on [%s] is [%s]", instanceName, user);
				return false;
			}
		}
		return true;
	}
}
