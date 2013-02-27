package com.cognifide.securecq;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpHelper {

	private final DefaultHttpClient client;

	public HttpHelper() {
		this.client = new DefaultHttpClient();
	}

	public boolean pathExists(String url) throws ClientProtocolException, IOException {
		HttpParams params = new BasicHttpParams();
		params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

		HttpUriRequest request = new HttpGet(url);
		request.setParams(params);
		HttpResponse response = client.execute(request);
		EntityUtils.consume(response.getEntity());
		int code = response.getStatusLine().getStatusCode();
		return code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_UNAUTHORIZED;
	}

	public boolean pageContainsString(String url, String stringToFind) throws ClientProtocolException,
			IOException {
		HttpParams params = new BasicHttpParams();
		params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

		HttpUriRequest request = new HttpGet(url);
		request.setParams(params);
		HttpResponse response = client.execute(request);
		String responseString = EntityUtils.toString(response.getEntity());
		int code = response.getStatusLine().getStatusCode();
		if (code == HttpURLConnection.HTTP_OK) {
			if (StringUtils.isBlank(stringToFind)) {
				return true;
			}
			return responseString.indexOf(stringToFind) != -1;
		} else {
			return false;
		}
	}

	public String getBasePath(String url, boolean removeExtension) throws ClientProtocolException,
			IOException {
		HttpGet getRequest = new HttpGet(url);
		HttpContext context = new BasicHttpContext();
		HttpResponse response = client.execute(getRequest, context);
		EntityUtils.consume(response.getEntity());
		HttpUriRequest request = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);

		URI uri = request.getURI();
		String baseUrl = context.getAttribute(ExecutionContext.HTTP_TARGET_HOST).toString() + uri.getPath();
		if (removeExtension && uri.getPath().contains(".")) {
			baseUrl = StringUtils.substringBeforeLast(baseUrl, ".");
		}
		if (!"/".equals(uri.getPath())) {
			baseUrl = StringUtils.removeEnd(baseUrl, "/");
		}
		return baseUrl;
	}

}
