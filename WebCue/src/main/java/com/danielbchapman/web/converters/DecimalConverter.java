package com.danielbchapman.web.converters;

import javax.faces.convert.FacesConverter;

@FacesConverter("com.danielbchapman.web.converters.DecimalConverter")
public class DecimalConverter extends AbstractDecimalConverter
{
	@Override
	protected String getDecimalFormat()
	{
		return ",###.00";
	}
}
