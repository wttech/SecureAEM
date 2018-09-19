package com.cognifide.secureaem.cli;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.StringUtils;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.TestResult;
import com.cognifide.secureaem.TestRunParameters;
import com.cognifide.secureaem.json.Severity;
import com.cognifide.secureaem.json.SingleTestResult;
import com.cognifide.secureaem.json.TestSuiteResult;
import com.google.gson.Gson;

public class Main {

	private static final String DEFAULT_TEST_SUITE_PATH = "/test_suite.properties";

	private static final String CMD_SUITE_OPTION = "suite";
	
	private static boolean jsonMode;
	
	private static TestSuiteResult testSuiteResult = new TestSuiteResult();

	public static void main(String[] args) throws Exception {
		CommandLine cmdLine = createOptions(args);
		if (!cmdLine.hasOption('a') && !cmdLine.hasOption('p') && !cmdLine.hasOption('d')) {
			printf("Usage: ");
			printf("java -jar secure-aem.jar [-a AUTHOR_URL] [-p PUBLISH_URL] [-d DISPATCHER_URL] [-m MODE]");
			System.exit(1);
		}
		selectMode(cmdLine);
		List<TestLoader> testLoaders = createTestLoaders(cmdLine);
		boolean result = true;
		for (TestLoader testLoader : testLoaders) {
			result = doTest(testLoader, cmdLine) && result;
		}
		printOutput();
		System.exit(result ? 0 : -1);
	}

	private static List<TestLoader> createTestLoaders(CommandLine cmdLine)
			throws IOException, ClassNotFoundException {
		try (BufferedReader reader = getBufferedReader(cmdLine)) {
			List<TestLoader> testLoaders = new ArrayList<>();
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parameters = line.split(",");
				if (parameters.length >= 2) {
					Class clazz = Class.forName(parameters[0].trim());
					Severity severity = Severity.MAJOR;
					if (parameters.length == 3) {
						severity = Severity.of(parameters[2].trim());
					}
					testLoaders.add(new TestLoader(clazz, parameters[1].trim(), severity));
				}
			}
			return testLoaders;
		}
	}

	private static BufferedReader getBufferedReader(CommandLine cmdLine) throws FileNotFoundException {
		BufferedReader reader;
		if (cmdLine.hasOption(CMD_SUITE_OPTION)) {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(cmdLine.getOptionValue(CMD_SUITE_OPTION)),
							StandardCharsets.UTF_8));
		} else {
			InputStream is = Main.class.getClass().getResourceAsStream(DEFAULT_TEST_SUITE_PATH);
			reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
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
		if (!test.getInfoMessages().isEmpty() && !"true"
				.equals(config.getStringValue("hidePassed", "false"))) {
			printf("");
			printf("Passed tests:");
			for (String message : test.getInfoMessages()) {
				printf(" * %s", message);
			}
		}
		printf("");
		testSuiteResult.addTestResult(
				new SingleTestResult(
						xmlConfigReader.getMetadataValue("jcr:title"), test, testLoader.getSeverity()));
		return test.getResult() == TestResult.OK;
	}

	private static void printf(String format, Object... args) {
		if (!TestRunParameters.SILENT_MODE) {
			System.out.println(String.format(format, args));
		}
	}
	
	private static void selectMode(CommandLine cmdLine) {
		jsonMode = cmdLine.hasOption('m') && 
				StringUtils.equals(cmdLine.getOptionValue('m'), "json");
		TestRunParameters.SILENT_MODE = jsonMode;
	}

	private static void printOutput() {
		if (jsonMode) {
			System.out.println(new Gson().toJson(testSuiteResult));
		}
	}

	private static CommandLine createOptions(String[] args) throws ParseException {
		Options options = new Options();
		options.addOption("a", true, "author URL");
		options.addOption("p", true, "publish URL");
		options.addOption("d", true, "dispatcher URL");
		options.addOption(CMD_SUITE_OPTION, true, "test suite");
		options.addOption("aCredentials", true, "author credentials");
		options.addOption("pCredentials", true, "publish credentials");		
		options.addOption("m", true, "mode");
		
		CommandLineParser parser = new PosixParser();
		return parser.parse(options, args);
	}
	
}
