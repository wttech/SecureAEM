package com.cognifide.secureaem.json;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public enum Severity {
	BLOCKER("BLOCKER"), CRITICAL("CRITICAL"), MAJOR("MAJOR"), MINOR("MINOR"), INFO("INFO");

	private final String value;

	Severity(String value) {
		this.value = value;
	}

	public static Severity of(String value) {
		return Arrays.stream(Severity.values())
			.filter(severity -> StringUtils.equals(severity.value, value))
			.findFirst().orElse(INFO);
	}
}
