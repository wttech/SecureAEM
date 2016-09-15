package com.cognifide.secureaem.sling;

import java.util.HashMap;
import java.util.Map;

import com.cognifide.secureaem.AbstractTest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestResultSerializer {
	private final Map<String, Object> testResult;

	public TestResultSerializer(AbstractTest abstractTest) {
		testResult = new HashMap<String, Object>();
		testResult.put("testResult", abstractTest.getResult().name().toLowerCase());
		testResult.put("errorMessages", abstractTest.getErrorMessages());
		testResult.put("infoMessages", abstractTest.getInfoMessages());
		testResult.put("environments", abstractTest.getEnvironments());
	}

	@Override
	public String toString() {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.setPrettyPrinting().create();
		return gson.toJson(testResult);
	}
}
