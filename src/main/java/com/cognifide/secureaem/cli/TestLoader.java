package com.cognifide.secureaem.cli;

import java.lang.reflect.Constructor;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;
import com.cognifide.secureaem.TestConfiguration;
import com.cognifide.secureaem.json.Severity;

public class TestLoader {

	private final Class<? extends AbstractTest> clazz;

	private final String componentName;

	public TestLoader(Class<? extends AbstractTest> clazz, String componentName) {
		this.clazz = clazz;
		this.componentName = componentName;
	}

	public AbstractTest getTest(Configuration config, TestConfiguration testConfiguration) throws Exception {
		Constructor<?> constructor = clazz.getConstructor(Configuration.class, TestConfiguration.class);
		return (AbstractTest) constructor.newInstance(config, testConfiguration);
	}

	String getComponentName() {
		return componentName;
	}

}
