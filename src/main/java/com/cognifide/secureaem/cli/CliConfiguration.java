package com.cognifide.secureaem.cli;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

public class CliConfiguration implements com.cognifide.secureaem.CliConfiguration {

	public static final String DEFAULT_USER = "admin";

	private final CommandLine cmdLine;

	public CliConfiguration(CommandLine cmdLine)
			throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
		this.cmdLine = cmdLine;
	}

	@Override public String getDispatcherUrl() {
		return makeUrl(cmdLine.getOptionValue("d"));
	}

	@Override public String getAuthor() {
		return makeUrl(cmdLine.getOptionValue("a"));
	}

	@Override public String getAuthorLogin() {
		return getCredentialsParameter("aCredentials", 0);
	}

	@Override public String getAuthorPassword() {
		return getCredentialsParameter("aCredentials", 1);
	}

	@Override public String getPublish() {
		return makeUrl(cmdLine.getOptionValue("p"));
	}

	@Override public String getPublishLogin() {
		return getCredentialsParameter("pCredentials", 0);
	}

	@Override public String getPublishPassword() {
		return getCredentialsParameter("pCredentials", 1);
	}

	private String getCredentialsParameter(String credentialName, int parameterIndex) {
		if (cmdLine.hasOption(credentialName)) {
			String[] parameters = cmdLine.getOptionValue(credentialName).split(":");
			if (parameters.length == 2) {
				return parameters[parameterIndex];
			}
		}
		return DEFAULT_USER;
	}

	private static String makeUrl(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		String result = StringUtils.removeEnd(url, "/");
		if (!result.startsWith("http://") && !result.startsWith("https://")) {
			result = StringUtils.removeStart(result, "/");
			result = "http://" + result;
		}
		return result;
	}

}
