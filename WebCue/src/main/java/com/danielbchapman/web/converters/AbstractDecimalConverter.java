package com.danielbchapman.web.converters;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.danielbchapman.web.utility.Internationalization;

public abstract class AbstractDecimalConverter implements Converter
{

	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value)
	{
		if(value == null)
			return null;

		BigDecimal result = com.danielbchapman.web.utility.Safe.tolerantParseBigDecimal(value);

		if(result == null) //failed to convert
		{
			String error = Internationalization.getMessage(AbstractDecimalConverter.class, "error.key");
			String message = Internationalization.getMessage(AbstractDecimalConverter.class, "error.message", value);
			FacesMessage msg = new FacesMessage(error, message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			//throw new ConverterException(msg);
			System.out.println("Conversion aborted for " + value);
			
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value)
	{
		if(value == null)
			return null;
		if(value instanceof Number)
			return new DecimalFormat(getDecimalFormat()).format(value);
		else
			return value.toString();
	}

	/**
	 * @return a simple decimal format
	 */
	protected abstract String getDecimalFormat();

}
