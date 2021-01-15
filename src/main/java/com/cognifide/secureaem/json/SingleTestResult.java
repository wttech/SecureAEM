package com.cognifide.secureaem.json;

import java.util.List;
import java.util.Set;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.TestResult;

@SuppressWarnings("unused") //serialized
public class SingleTestResult {
	
	private final String name;
	
	private final TestResult testResult;
	
	private final List<String> errorMessages;

	private final List<String> infoMessages;

	private final Set<String> environments;
	
	private final Severity severity;
	
	public SingleTestResult(String name, AbstractTest abstractTest, Severity severity) {
		this.name = name;
		testResult = abstractTest.getResult();
		errorMessages = abstractTest.getErrorMessages();
		infoMessages = abstractTest.getInfoMessages();
		environments = abstractTest.getEnvironments();
		this.severity = severity;
	}
	
	public TestResult getTestResult() {
		return testResult;
	}
	
	public Severity getSeverity() {
		return severity;
	}
	
}
