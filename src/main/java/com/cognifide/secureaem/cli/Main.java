package com.cognifide.secureaem.cli;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.cognifide.secureaem.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.StringUtils;

import com.cognifide.secureaem.json.SingleTestResult;
import com.cognifide.secureaem.json.TestSuiteResult;
import com.google.gson.Gson;

public class Main {

	private static final String TEST_JSON_PATH = "/test_suite.json";

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
		List<TestLoader> testLoaders = createTestLoaders();
		boolean result = true;
		for (TestLoader testLoader : testLoaders) {
			result = doTest(testLoader, cmdLine) && result;
		}
		printOutput();
		System.exit(result ? 0 : 1337);
	}

	private static List<TestLoader> createTestLoaders() throws ClassNotFoundException, UnsupportedEncodingException {
		List<TestLoader> testLoaders = new ArrayList<>();

		InputStream configStream = Main.class.getResourceAsStream(TEST_JSON_PATH);
		BufferedReader configReader = new BufferedReader(new InputStreamReader(configStream, "UTF-8"));
		JsonArray testsJson = (JsonArray) new JsonParser().parse(configReader);

		for(int i = 0; i < testsJson.size(); i++) {
			JsonObject jsonObject = (testsJson.get(i)).getAsJsonObject();

			if(jsonObject.get("class") != null && jsonObject.get("name") != null) {
				Class clazz = Class.forName(jsonObject.get("class").getAsString());
				String name = jsonObject.get("name").getAsString();
				testLoaders.add(new TestLoader(clazz, name));
			} else {
				printf("Tests in " + TEST_JSON_PATH + " should always have a class and name attribute");
				System.exit(1337);
			}
		}

		return testLoaders;
	}


	private static boolean doTest(TestLoader testLoader, CommandLine cmdLine) throws Exception {
		TestConfiguration testConfiguration = new TestConfiguration(testLoader.getComponentName());
		Configuration cliConfig = new CliConfiguration(cmdLine);
		AbstractTest test = testLoader.getTest(cliConfig, testConfiguration);
		test.test();
		if (test.getResult() == TestResult.DISABLED) {
			return true;
		}

		printf("### %s ###", testConfiguration.getName());
		printf("Environments: %s", StringUtils.join(test.getEnvironments(), " / "));
		if(testConfiguration.getDescription() != null){
			printf("Description: %s", testConfiguration.getDescription());
		}
		if(testConfiguration.getUrl() != null){
			printf("Url: %s", testConfiguration.getUrl());
		}
		if(testConfiguration.getUrlDescription() != null){
			printf("Url Description: %s", testConfiguration.getUrlDescription());
		}
		if(testConfiguration.getSeverity() != null){
			printf("Severity: %s", testConfiguration.getSeverity());
		}
		printf("Result: %s", test.getResult());
		if (!test.getErrorMessages().isEmpty()) {
			printf("");
			printf("Failed tests:");
			for (String message : test.getErrorMessages()) {
				printf(" * %s", message);
			}
		}
		if (!test.getInfoMessages().isEmpty()) {
			printf("");
			printf("Passed tests:");
			for (String message : test.getInfoMessages()) {
				printf(" * %s", message);
			}
		}
		printf("");
		testSuiteResult.addTestResult(
				new SingleTestResult(
						testConfiguration.getName(), test, testConfiguration.getSeverity()));
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
