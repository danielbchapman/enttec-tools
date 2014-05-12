package com.danielbchapman.web;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class Utility
{
	public static <T extends Comparable<T>> int compare(T one, T two)
	{
		if(one == null && two == null)
			return 0;

		if(one == null)
			return -1;

		if(two == null)
			return 1;

		return one.compareTo(two);
	}

	public static boolean contains(String search, String forThis)
	{
		if(isEmptyOrNull(search))
			return false;

		if(isEmptyOrNull(forThis))
			return false;

		return search.trim().contains(forThis);
	}

	public static <T> boolean contains(T[] collection, T value)
	{
		if(value == null)
		{
			for(T t : collection)
				if(t == null)
					return true;
		}
		else
			for(T t : collection)
				if(value.equals(t))
					return true;

		return false;
	}

	public static boolean containsIgnoreCase(String search, String forThis)
	{
		if(isEmptyOrNull(search))
			return false;

		if(isEmptyOrNull(forThis))
			return false;

		return search.toLowerCase().contains(forThis.toLowerCase());
	}

	public static HttpSession getSession()
	{
		ExternalContext server = FacesContext.getCurrentInstance().getExternalContext();
		return (HttpSession) server.getSession(true);
	}

	public static boolean isEmptyOrNull(String string)
	{
		if(string == null)
			return true;

		if(string.trim().isEmpty())
			return true;

		return false;
	}

	public static void raiseError(String message, String detail)
	{
		raiseFacesMessage(FacesMessage.SEVERITY_ERROR, message, detail);
	}

	public static void raiseFacesMessage(FacesMessage.Severity severity, String message, String detail)
	{
		raiseFacesMessage(null, severity, message, detail);
	}

	public static void raiseFacesMessage(String id, FacesMessage.Severity severity, String message, String detail)
	{
		FacesContext ctx = FacesContext.getCurrentInstance();
		FacesMessage target = new FacesMessage(severity, message, detail);
		ctx.addMessage(id, target);

	}

	public static void raiseFatal(String message, String detail)
	{
		raiseFacesMessage(FacesMessage.SEVERITY_FATAL, message, detail);
	}

	public static String safe(String input)
	{
		if(input == null)
			return "";

		return input;
	}
}
