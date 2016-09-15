package com.cognifide.secureaem.sling;

import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import com.cognifide.secureaem.AbstractTest;
import com.cognifide.secureaem.Configuration;

public class TestInvoker extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1334083614379709964L;

	@Override
	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException,
			ServletException {
		String testClassName = getTestClassName(request);
		Configuration config = new ResourceConfiguration(request);
		AbstractTest test;
		try {
			Class<?> clazz = Class.forName(testClassName);
			Constructor<?> constructor = clazz.getConstructor(Configuration.class);
			test = (AbstractTest) constructor.newInstance(config);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		test.test();
		TestResultSerializer serializer = new TestResultSerializer(test);
		response.setContentType("application/json");
		response.getWriter().print(serializer.toString());
	}

	private String getTestClassName(SlingHttpServletRequest request) {
		String resourceType = request.getResource().getResourceType();
		Resource component = request.getResourceResolver().getResource(resourceType);
		ValueMap map = component.adaptTo(ValueMap.class);
		return map.get("testClass", String.class);
	}
}
