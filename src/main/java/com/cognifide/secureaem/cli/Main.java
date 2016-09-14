package com.cognifide.secureaem.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.TestResult;
import com.cognifide.secureaem.tests.ConfigValidation;
import com.cognifide.secureaem.tests.CrxdeLogsTest;
import com.cognifide.secureaem.tests.DefaultPasswordsTest;
import com.cognifide.secureaem.tests.ExtensionsTest;
import com.cognifide.secureaem.tests.GroovyConsoleTest;
import com.cognifide.secureaem.tests.PageContentTest;
import com.cognifide.secureaem.tests.PathsTest;
import com.cognifide.secureaem.tests.PublishPathsTest;
import com.cognifide.secureaem.tests.WcmDebugTest;
import com.cognifide.secureaem.tests.WebDavTest;

public class Main {

	private static final TestLoader[] TESTS = new TestLoader[] {
			new TestLoader(ConfigValidation.class, "config-validation"),
			new TestLoader(DefaultPasswordsTest.class, "default-passwords"),
			new TestLoader(CrxdeLogsTest.class, "crxde-logs"),
			new TestLoader(PageContentTest.class, "dispatcher-access"),
			new TestLoader(PageContentTest.class, "shindig-proxy"),
			new TestLoader(PublishPathsTest.class, "third-party"),
			new TestLoader(GroovyConsoleTest.class, "groovy-console"),
			new TestLoader(PageContentTest.class, "etc-tools"),
			new TestLoader(ExtensionsTest.class, "content-grabbing"),
			new TestLoader(ExtensionsTest.class, "feed-selector"),
			new TestLoader(WcmDebugTest.class, "wcm-debug"),
			new TestLoader(WebDavTest.class, "webdav"),
			new TestLoader(PathsTest.class, "felix-console"),
			new TestLoader(PageContentTest.class, "geometrixx"),
			new TestLoader(ExtensionsTest.class, "redundant-selectors"),
	};

	public static void main(String[] args) throws Exception {
		new Main(args);
	}

	private Main(String[] args) throws Exception {
		CommandLine cmdLine = createOptions(args);
		if (!cmdLine.hasOption('a') && !cmdLine.hasOption('p') && !cmdLine.hasOption('d')) {
			printf("Usage: ");
			printf("java -jar secure-aem.jar [-a AUTHOR_URL] [-p PUBLISH_URL] [-d DISPATCHER_URL] ");
			System.exit(1);
		}
		boolean result = true;
		for (TestLoader testLoader : TESTS) {
			result = doTest(testLoader, cmdLine) && result;
		}
		System.exit(result ? 0 : -1);
	}

	private boolean doTest(TestLoader testLoader, CommandLine cmdLine) throws Exception {
		XmlConfigurationReader xmlConfigReader = new XmlConfigurationReader(testLoader.getComponentName());
		Configuration config = new CliConfiguration(xmlConfigReader, cmdLine);
		AbstractTest test = testLoader.getTest(config);
		test.test();
		if (test.getResult() == TestResult.DISABLED) {
			return true;
		}

		printf("### %s ###", xmlConfigReader.getMetadataValue("jcr:title"));
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

	private CommandLine createOptions(String args[]) throws ParseException {
		Options options = new Options();
		options.addOption("a", true, "author URL");
		options.addOption("p", true, "publish URL");
		options.addOption("d", true, "dispatcher URL");

		CommandLineParser parser = new PosixParser();
		return parser.parse(options, args);
	}
}
