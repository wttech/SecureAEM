package com.cognifide.secureaem.cli;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import com.cognifide.secureaem.Configuration;

public class CliConfiguration implements Configuration {

	public static final String DEFAULT_USER = "admin";

	private final XmlConfigurationReader xmlConfigReader;

	private final CommandLine cmdLine;

	public CliConfiguration(XmlConfigurationReader xmlConfigReader, CommandLine cmdLine)
			throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
		this.xmlConfigReader = xmlConfigReader;
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

	@Override public String getStringValue(String name, String defaultValue) {
		return StringUtils.defaultIfEmpty(xmlConfigReader.getValue(name), defaultValue);
	}

	@Override public String[] getStringList(String name) {
		return xmlConfigReader.getValueList(name);
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
