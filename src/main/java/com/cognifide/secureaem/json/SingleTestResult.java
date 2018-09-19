package com.cognifide.secureaem.json;

import java.util.List;
import java.util.Set;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.TestResult;

@SuppressWarnings("unused") //serialized
public class SingleTestResult {
	
	private String name;
	
	private TestResult testResult;
	
	private List<String> errorMessages;

	private List<String> infoMessages;

	private Set<String> environments;
	
	private Severity severity;
	
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
