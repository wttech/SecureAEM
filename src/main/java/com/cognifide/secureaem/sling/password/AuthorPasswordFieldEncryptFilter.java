package com.cognifide.secureaem.sling.password;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

@Service
@Component
public class AuthorPasswordFieldEncryptFilter implements PasswordFiledEncryptFilter {
	@Override
	public boolean isSupported(String propertyPath) {
		return StringUtils.startsWith(propertyPath, "/etc/secureaem")
				&& StringUtils.endsWith(propertyPath, "authorPassword");
	}
}
