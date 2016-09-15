package com.cognifide.secureaem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

	private TestResult result;

	public AbstractTest(Configuration config) {
		this.httpHelper = new HttpHelper();
		this.config = config;
	}

	public void test() throws IOException {
		result = null;

		if ("true".equals(config.getStringValue("enabled", "false"))) {
			try {
				result = doTest();
			} catch (Exception e) {
				LOG.error("Error during test", e);
				if (!(e instanceof InvalidConfigurationException)) {
					addErrorMessage("Exception occured: " + e.toString());
				}
				result = TestResult.EXCEPTION;
			}
		} else {
			result = TestResult.DISABLED;
		}
	}

	/**
	 * Perform test.
	 * 
	 * @param url URL of the instance to test.
	 * @param instanceName Name of the instance (eg. author, publish or dispatcher).
	 * @return true if the test succeeded
	 * @throws Exception If you throw an exception, test result will be set to "Exception". You may throw
	 * special {@link AbstractTest.InvalidConfigurationException} with message if the test configuration isn't set
	 * correctly.
	 */
	protected abstract boolean doTest(String url, String instanceName) throws Exception;

	private TestResult doTest() throws Exception {
		boolean success = true;
		boolean testDone = false;
		infoMessages = new ArrayList<String>();
		errorMessages = new ArrayList<String>();

		if (this instanceof AuthorTest && StringUtils.isNotBlank(config.getAuthor())) {
			success = doTest(config.getAuthor(), "author") && success;
			testDone = true;
		}

		if (this instanceof PublishTest && StringUtils.isNotBlank(config.getPublish())) {
			success = doTest(config.getPublish(), "publish") && success;
			testDone = true;
		}

		if (this instanceof DispatcherTest && StringUtils.isNotBlank(config.getDispatcherUrl())) {
			success = doTest(config.getDispatcherUrl(), "dispatcher") && success;
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
	 * @param params Values to fill the placeholders.
	 */
	protected void addInfoMessage(String message, Object... params) {
		String formatted = String.format(message, params);
		infoMessages.add(formatted);
	}

	/**
	 * Add error message, it'll be shown to the user.
	 * 
	 * @param message Message can contain standard {@code String.format()} placeholders
	 * @param params Values to fill the placeholders.
	 */
	protected void addErrorMessage(String message, Object... params) {
		String formatted = String.format(message, params);
		errorMessages.add(formatted);
	}

	public List<String> getInfoMessages() {
		return infoMessages;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public TestResult getResult() {
		return result;
	}

	private static class InvalidConfigurationException extends Exception {
		private static final long serialVersionUID = -5814780508801961467L;
	}
}
