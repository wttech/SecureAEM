package com.cognifide.secureaem.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.TestResult;

public class Main {

	private static final String DEFAULT_TEST_SUITE_PATH = "/test_suite.properties";

	public static void main(String[] args) throws Exception {
		CommandLine cmdLine = createOptions(args);
		if (!cmdLine.hasOption('a') && !cmdLine.hasOption('p') && !cmdLine.hasOption('d')) {
			printf("Usage: ");
			printf("java -jar secure-aem.jar [-a AUTHOR_URL] [-p PUBLISH_URL] [-d DISPATCHER_URL] ");
			System.exit(1);
		}
		List<TestLoader> testLoaders =  createTestLoaders(cmdLine);
		boolean result = true;
		for (TestLoader testLoader : testLoaders) {
			result = doTest(testLoader, cmdLine) && result;
		}
		System.exit(result ? 0 : -1);
	}

	private static List<TestLoader> createTestLoaders(CommandLine cmdLine) throws IOException, ClassNotFoundException {
		try (BufferedReader reader = getBufferedReader(cmdLine)) {
			List<TestLoader> testLoaders = new ArrayList<>();
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parameters = line.split(", ");
				if (parameters.length == 2) {
					Class clazz = Class.forName(parameters[0]);
					testLoaders.add(new TestLoader(clazz, parameters[1]));
				}
			}
			return testLoaders;
		}
	}

	private static BufferedReader getBufferedReader(CommandLine cmdLine) throws FileNotFoundException {
		BufferedReader reader;
		if (cmdLine.hasOption("suite")) {
			reader = new BufferedReader(new FileReader(cmdLine.getOptionValue("suite")));
		} else {
			InputStream is = Main.class.getClass().getResourceAsStream(DEFAULT_TEST_SUITE_PATH);
			reader = new BufferedReader(new InputStreamReader(is));
		}
		return reader;
	}

	private static boolean doTest(TestLoader testLoader, CommandLine cmdLine) throws Exception {
		XmlConfigurationReader xmlConfigReader = new XmlConfigurationReader(testLoader.getComponentName());
		Configuration config = new CliConfiguration(xmlConfigReader, cmdLine);
		AbstractTest test = testLoader.getTest(config);
		test.test();
		if (test.getResult() == TestResult.DISABLED) {
			return true;
		}

		printf("### %s ###", xmlConfigReader.getMetadataValue("jcr:title"));
		printf("Environments: %s", StringUtils.join(test.getEnvironments(), " / "));
		printf("Result: %s", test.getResult());
		if (!test.getErrorMessages().isEmpty()) {
			printf("");
			printf("Failed tests:");
			for (String message : test.getErrorMessages()) {
				printf(" * %s", message);
			}
		}
		if (!test.getInfoMessages().isEmpty() && !"true".equals(config.getStringValue("hidePassed", "false"))) {
			printf("");
			printf("Passed tests:");
			for (String message : test.getInfoMessages()) {
				printf(" * %s", message);
			}
		}
		printf("");
		return test.getResult() == TestResult.OK;
	}

	private static void printf(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

	private static CommandLine createOptions(String args[]) throws ParseException {
		Options options = new Options();
		options.addOption("a", true, "author URL");
		options.addOption("p", true, "publish URL");
		options.addOption("d", true, "dispatcher URL");
		options.addOption("suite", true, "test suite");

		CommandLineParser parser = new PosixParser();
		return parser.parse(options, args);
	}
}
