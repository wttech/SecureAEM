package com.cognifide.secureaem;

/**
 * Created by Mariusz Kubiś on 19.09.16
 */
public final class UserHelper {

	private UserHelper() {
		// To prevent initialization
	}

	public static String[] splitUser(String user) {
		int colon = user.indexOf(':');
		String[] result = new String[2];
		if (colon == -1) {
			result[0] = user;
			result[1] = null;
		} else {
			result[0] = user.substring(0, colon);
			result[1] = user.substring(colon + 1);
		}
		return result;
	}
}
