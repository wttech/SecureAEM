package com.cognifide.secureaem.cli;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XmlConfigurationReader {
	private static final String PATH = "/aem/jcr_root/etc/secureaem/%s/.content.xml";

	private final NamedNodeMap config;

	private final NamedNodeMap metadata;

	public XmlConfigurationReader(String componentName) throws IOException, ParserConfigurationException,
			SAXException, URISyntaxException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		String filename = String.format(PATH, componentName);
		InputStream is = this.getClass().getResourceAsStream(filename);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(is);

		config = document.getElementsByTagName("testComponent").item(0).getAttributes();
		metadata = document.getElementsByTagName("jcr:content").item(0).getAttributes();
	}

	public String getValue(String name) {
		Node node = config.getNamedItem(name);
		if (node != null) {
			return node.getNodeValue();
		}
		return null;
	}

	public String[] getValueList(String name) {
		String value = getValue(name);
		value = StringUtils.removeStart(value, "[");
		value = StringUtils.removeEnd(value, "]");
		return StringUtils.split(value, ',');
	}

	public String getMetadataValue(String name) {
		return metadata.getNamedItem(name).getNodeValue();
	}
}
