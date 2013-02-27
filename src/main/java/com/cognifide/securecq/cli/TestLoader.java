package com.cognifide.securecq.cli;

import java.lang.reflect.Constructor;

import com.cognifide.securecq.AbstractTest;
import com.cognifide.securecq.Configuration;

public class TestLoader {

	private final Class<? extends AbstractTest> clazz;

	private final String componentName;

	public TestLoader(Class<? extends AbstractTest> clazz, String componentName) {
		this.clazz = clazz;
		this.componentName = componentName;
	}

	public AbstractTest getTest(Configuration config) throws Exception {
		Constructor<?> constructor = clazz.getConstructor(Configuration.class);
		return (AbstractTest) constructor.newInstance(config);
	}

	String getComponentName() {
		return componentName;
	}

}
