package com.danielbchapman.web.utility;

import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * A placeholder class to intercept strings for internationalization. At the moment it is simple
 * method signatures. I think a database might actually be the smart method, but a static bean might
 * be equally valid as an implementation.
 * 
 * @author Daniel B. Chapman
 * @since Mar 16, 2012
 * @copyright Copyright 2012 Daniel B. Chapman/Harrison Fortier. All rights reserved.
 *            PROPRIETARY/CONFIDENTAL. Use is subject to license terms.
 * 
 */
public class Internationalization
{
	private static InternationalizationImplementation FALLBACK = new InternationalizationImplementation();
	/**
	 * Return an internationalized string for this key based on the class provided
	 * 
	 * @param clazz the class associated with this call.
	 * @param key the key used to find this message
	 * 
	 * @return the string associated with this call.
	 */
	public static String getMessage(Class<?> clazz, String key)
	{
		return getMessage(clazz, key, (Object[]) null);
	}

	/**
	 * Return an internationalized string for this key based on the class provided
	 * and the parameters sent.
	 * 
	 * @param clazz the class associated with this call.
	 * @param key the key used to find this message
	 * @param params the parameters
	 * @return the string associated with this call.
	 */
	public static String getMessage(Class<?> clazz, String key, Object... params)
	{
		if(WebUtil.getSession() == null)
			return FALLBACK.getMessage(clazz, key, params);
		else
			return FALLBACK.getMessage(clazz, key, params);
	}

	public static class InternationalizationImplementation
	{
		private HashMap<Class<?>, ResourceBundle> resources = new HashMap<>();

		public String getMessage(Class<?> clazz, String key, Object... params)
		{
			if(key == null)
				throw new IllegalArgumentException("Keys can not be null");

			if(params == null || params.length < 1)
				return "*" + key;

			StringBuilder ret = new StringBuilder();
			ret.append("?");
			ret.append(key);
			ret.append("?");
			for(int i = 0; i < params.length; i++)
			{
				ret.append(" {");
				ret.append(i);
				ret.append("}");
				ret.append(params[i]);
			}

			return ret.toString();
		}
		private void writeMissingKey(Class<?> clazz, String key, Object ... params)
		{

		}
	}
}
