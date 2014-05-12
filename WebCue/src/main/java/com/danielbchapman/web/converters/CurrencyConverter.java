package com.danielbchapman.web.converters;

import javax.faces.convert.FacesConverter;

@FacesConverter("com.danielbchapman.web.converters.CurrencyConverter")
public class CurrencyConverter extends AbstractDecimalConverter
{

	@Override
	protected String getDecimalFormat()
	{
		return "$,###.00";
	}

}
