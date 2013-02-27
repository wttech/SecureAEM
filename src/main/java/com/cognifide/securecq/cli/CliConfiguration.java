package com.cognifide.securecq.cli;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.cognifide.securecq.Configuration;

public class CliConfiguration implements Configuration {

	private final XmlConfigurationReader xmlConfigReader;

	private final CommandLine cmdLine;

	public CliConfiguration(XmlConfigurationReader xmlConfigReader, CommandLine cmdLine) throws IOException,
			ParserConfigurationException, SAXException, URISyntaxException {
		this.xmlConfigReader = xmlConfigReader;
		this.cmdLine = cmdLine;
	}

	@Override
	public String getDispatcherUrl() {
		return makeUrl(cmdLine.getOptionValue("d"));
	}

	@Override
	public String getAuthor() {
		return makeUrl(cmdLine.getOptionValue("a"));
	}

	@Override
	public String getPublish() {
		return makeUrl(cmdLine.getOptionValue("p"));
	}

	@Override
	public String getStringValue(String name, String defaultValue) {
		return StringUtils.defaultIfEmpty(xmlConfigReader.getValue(name), defaultValue);
	}

	@Override
	public String[] getStringList(String name) {
		return xmlConfigReader.getValueList(name);
	}

	public static String makeUrl(String url) {
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
