package com.cognifide.secureaem.cli;

import java.lang.reflect.Constructor;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.json.Severity;

public class TestLoader {

	private final Class<? extends AbstractTest> clazz;

	private final String componentName;

	private Severity severity;

	public TestLoader(Class<? extends AbstractTest> clazz, String componentName, Severity severity) {
		this.clazz = clazz;
		this.componentName = componentName;
		this.severity = severity;
	}

	public AbstractTest getTest(Configuration config) throws Exception {
		Constructor<?> constructor = clazz.getConstructor(Configuration.class);
		return (AbstractTest) constructor.newInstance(config);
	}

	String getComponentName() {
		return componentName;
	}
	
	Severity getSeverity() {
		return severity;
	}

}
