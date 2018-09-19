package com.cognifide.secureaem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.secureaem.markers.AuthorTest;
import com.cognifide.secureaem.markers.DispatcherTest;
import com.cognifide.secureaem.markers.PublishTest;

public abstract class AbstractTest {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractTest.class);

	protected final HttpHelper httpHelper;

	protected final Configuration config;

	private List<String> infoMessages;

	private List<String> errorMessages;

	private Set<String> environments;

	private TestResult result;

	public AbstractTest(Configuration config) {
		this.httpHelper = new HttpHelper();
		this.config = config;
	}

	public void test() throws IOException {
		result = null;
		appendEnvironmentInfo();
		if ("true".equals(config.getStringValue("enabled", "false"))) {
			try {
				result = doTest();
			} catch (Exception e) {
				if (!TestRunParameters.SILENT_MODE) {
					LOG.error("Error during test", e);
				}
				if (!(e instanceof InvalidConfigurationException)) {
					addErrorMessage("Exception occured: " + e.toString());
				}
				
				result = TestResult.EXCEPTION;
			}
		} else {
			result = TestResult.DISABLED;
		}
	}

	private void appendEnvironmentInfo() {
		environments = new HashSet<>();
		if (this instanceof AuthorTest) {
			environments.add(AuthorTest.ENVIRONMENT_NAME);
		}
		if (this instanceof PublishTest) {
			environments.add(PublishTest.ENVIRONMENT_NAME);
		}
		if (this instanceof DispatcherTest) {
			environments.add(DispatcherTest.ENVIRONMENT_NAME);
		}
	}

	/**
	 * Perform test.
	 *
	 * @param url          URL of the instance to test.
	 * @param instanceName Name of the instance (eg. author, publish or dispatcher).
	 * @return true if the test succeeded
	 * @throws Exception If you throw an exception, test result will be set to "Exception". You may throw
	 *                   special {@link AbstractTest.InvalidConfigurationException} with message if the test
	 *                   configuration isn't set correctly.
	 */
	protected abstract boolean doTest(String url, String instanceName) throws Exception;

	private TestResult doTest() throws Exception {
		boolean success = true;
		boolean testDone = false;
		infoMessages = new ArrayList<>();
		errorMessages = new ArrayList<>();

		if (this instanceof AuthorTest && StringUtils.isNotBlank(config.getAuthor())) {
			success = doTest(config.getAuthor(), AuthorTest.ENVIRONMENT_NAME);
			testDone = true;
		}

		if (this instanceof PublishTest && StringUtils.isNotBlank(config.getPublish())) {
			success = doTest(config.getPublish(), PublishTest.ENVIRONMENT_NAME) && success;
			testDone = true;
		}

		if (this instanceof DispatcherTest && StringUtils.isNotBlank(config.getDispatcherUrl())) {
			success = doTest(config.getDispatcherUrl(), DispatcherTest.ENVIRONMENT_NAME) && success;
			testDone = true;
		}

		if (testDone) {
			return success ? TestResult.OK : TestResult.FAIL;
		} else {
			return TestResult.DISABLED;
		}
	}

	/**
	 * Add information message, it'll be shown to the user.
	 *
	 * @param message Message can contain standard {@code String.format()} placeholders
	 * @param params  Values to fill the placeholders.
	 */
	protected void addInfoMessage(String message, Object... params) {
		String formatted = String.format(message, params);
		infoMessages.add(formatted);
	}

	/**
	 * Add error message, it'll be shown to the user.
	 *
	 * @param message Message can contain standard {@code String.format()} placeholders
	 * @param params  Values to fill the placeholders.
	 */
	protected void addErrorMessage(String message, Object... params) {
		String formatted = String.format(message, params);
		errorMessages.add(formatted);
	}

	/**
	 * Creates {@code UsernamePasswordCredentials} instance from configuration.
	 * @param instance - instance name
	 * @return UsernamePasswordCredentials
	 */
	protected UsernamePasswordCredentials getUsernamePasswordCredentials(String instance) {
		UsernamePasswordCredentials credentials = null;
		if (AuthorTest.ENVIRONMENT_NAME.equals(instance)) {
			credentials = new UsernamePasswordCredentials(config.getAuthorLogin(),
					config.getAuthorPassword());
		} else if (PublishTest.ENVIRONMENT_NAME.equals(instance)) {
			credentials = new UsernamePasswordCredentials(config.getPublishLogin(),
					config.getPublishPassword());
		}
		return credentials;
	}

	/**
	 * Check boolean property. If different than expected error message will be added to error list, if same as expected info message will be added to info list
	 *
	 * @param actualValue Actual value of boolean property
	 * @param expectedValue Expected value of boolean property
	 * @param propertyName Property name used in info/error messages
	 * @param instanceName Instance name used in info/error messages
	 */
	protected void checkBooleanValue(boolean actualValue, boolean expectedValue, String propertyName, String instanceName) {
		if (actualValue == expectedValue) {
			addInfoMessage("On %s instance %s property is %b", instanceName, propertyName, actualValue);
		} else {
			addErrorMessage("On %s instance %s property is %b, but it should be %b", instanceName, propertyName, actualValue, expectedValue);
		}
	}

	public List<String> getInfoMessages() {
		return infoMessages;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public Set<String> getEnvironments() {
		return environments;
	}

	public TestResult getResult() {
		return result;
	}

	private static class InvalidConfigurationException extends Exception {
		private static final long serialVersionUID = -5814780508801961467L;
	}
}
