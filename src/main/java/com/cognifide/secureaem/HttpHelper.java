package com.cognifide.secureaem;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
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

	HttpHelper() {
		this.client = new DefaultHttpClient();
	}

	/**
	 * Check if given URL exists
	 * 
	 * @param url request url
	 * @return true if given url exists
	 * @throws IOException in case of a problem or the connection was aborted
	 */
	public boolean pathExists(String url) throws IOException {
		HttpParams params = new BasicHttpParams();
		params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

		HttpUriRequest request = new HttpGet(url);
		request.setParams(params);
		HttpResponse response = client.execute(request);
		EntityUtils.consume(response.getEntity());
		int code = response.getStatusLine().getStatusCode();
		return code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_UNAUTHORIZED;
	}

	/**
	 * Check if page with URL exists and it's body contains given string.
	 * 
	 * @param url request url
	 * @param stringToFind expected string in response
	 * @return true if given url exists and page contains given string
	 * @throws IOException in case of a problem or the connection was aborted
	 */
	public boolean pageContainsString(String url, String stringToFind) throws IOException {
		HttpParams params = new BasicHttpParams();
		params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

		HttpUriRequest request = new HttpGet(url);
		request.setParams(params);
		HttpResponse response = client.execute(request);
		String responseString = EntityUtils.toString(response.getEntity());
		int code = response.getStatusLine().getStatusCode();
		if (code == HttpURLConnection.HTTP_OK && StringUtils.isBlank(stringToFind)) {
			return true;
		} else {
			return responseString.contains(stringToFind);
		}
	}

	public String getBasePath(String url, boolean removeExtension) throws IOException {
		HttpGet getRequest = new HttpGet(url);
		HttpContext context = new BasicHttpContext();
		HttpResponse response = client.execute(getRequest, context);
		EntityUtils.consume(response.getEntity());
		HttpUriRequest request = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);

		String  uriPath = request.getURI().getPath();
		String baseUrl = context.getAttribute(ExecutionContext.HTTP_TARGET_HOST).toString() + uriPath;
		if (removeExtension && uriPath.contains(".")) {
			baseUrl = StringUtils.substringBeforeLast(baseUrl, ".");
		}
		if (!"/".equals(uriPath)) {
			baseUrl = StringUtils.removeEnd(baseUrl, "/");
		}
		return baseUrl;
	}

}
