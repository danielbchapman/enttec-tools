package com.danielbchapman.web.utility;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookies is a simple utility class for storing information in cookies.
 * 
 * @author Daniel B. Chapman
 * @since Sep 19, 2013
 * @copyright Copyright 2013 Daniel B. Chapman/Harrison Fortier. All rights reserved.
 *            PROPRIETARY/CONFIDENTAL. Use is subject to license terms.
 * 
 */
public class Cookies
{
	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge)
	{
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public static void addCookie(String name, String value, int maxAge)
	{
		FacesContext ctx = FacesContext.getCurrentInstance();
		if(ctx == null)
			throw new IllegalStateException("Faces context is null");

		addCookie((HttpServletResponse) ctx.getExternalContext().getResponse(), name, value, maxAge);
	}

	public static String getCookieValue(HttpServletRequest request, String name)
	{
		Cookie[] cookies = request.getCookies();
		if(cookies != null)
			for(Cookie cookie : cookies)
				if(name.equals(cookie.getName()))
					return cookie.getValue();
		return null;
	}

	public static String getCookieValue(String name)
	{
		FacesContext ctx = FacesContext.getCurrentInstance();
		if(ctx == null)
			throw new IllegalStateException("Faces context is null");

		return getCookieValue((HttpServletRequest) ctx.getExternalContext().getRequest(), name);
	}

	public static void removeCookie(HttpServletResponse response, String name)
	{
		addCookie(response, name, null, 0);
	}
}
