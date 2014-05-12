package com.danielbchapman.web;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.danielbchapman.web.utility.WebUtil;

@FacesConverter("com.danielbchapman.web.CurrencyConverter")
public class CurrencyConverter implements Converter, Serializable
{
	private static final long serialVersionUID = 1L;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
	{
		if(Utility.isEmptyOrNull(arg2))
			return null;

		arg2 = arg2.replaceAll("[^0-9.]", "");

		try
		{
			return new BigDecimal(arg2);
		}
		catch(NumberFormatException e)
		{
			throw new ConverterException("Could not convert " + arg2 + " to a BigDecimal", e);
		}
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
	{
		NumberFormat formatter = NumberFormat.getCurrencyInstance(WebUtil.getCurrentLocale());

		if(arg2 == null)
			return formatter.format(0.00);

		if(arg2 instanceof Number)
			return formatter.format(((Number) arg2).longValue());

		return formatter.format(0.00);
	}

}
