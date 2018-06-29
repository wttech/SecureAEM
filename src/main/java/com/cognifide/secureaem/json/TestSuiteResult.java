package com.cognifide.secureaem.json;

import java.util.ArrayList;
import java.util.List;

import com.cognifide.secureaem.TestResult;

public class TestSuiteResult {

	List<SingleTestResult> testResults = new ArrayList<>();

	int passed = 0;
	int failed = 0;

	int blocker = 0;
	int critical = 0;
	int major = 0;
	int minor = 0;
	int info = 0;

	public void addTestResult(SingleTestResult testResult) {
		testResults.add(testResult);
		appendResults(testResult);
	}

	private void appendResults(SingleTestResult testResult) {
		if (testResult.getTestResult() != TestResult.OK) {
			failed++;
			switch (testResult.getSeverity()) {
			case BLOCKER:
				blocker++;
				break;
			case CRITICAL:
				critical++;
				break;
			case MAJOR:
				major++;
				break;
			case MINOR:
				minor++;
				break;
			case INFO:
			default:
				info++;
				break;
			}
		} else {
			passed++;
		}
	}
}
