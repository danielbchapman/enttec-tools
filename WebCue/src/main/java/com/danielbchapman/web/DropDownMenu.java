package com.danielbchapman.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.danielbchapman.code.Pair;

/**
 * A simple strongly typed binding for select-items so that the objects
 * can be accessed directly instead of relying on events.
 * 
 ***************************************************************************
 * @author Daniel B. Chapman <br />
 *         <i><b>Light Assistant</b></i> copyright Daniel B. Chapman
 * @since Apr 25, 2013
 * @version 2 Development
 * @link http://www.lightassistant.com
 ***************************************************************************
 */
public abstract class DropDownMenu<T> implements Serializable
{
	private static final long serialVersionUID = 1L;

	// @Getter
	// public HtmlSelectOneMenu selectOneMenu;

	// @Setter
	private T value;
	private List<SelectItem> items;

	// public void setSelectOneMenu(HtmlSelectOneMenu menu)
	// {
	// this.selectOneMenu = menu;
	// }

	public void setValue(T value)
	{
		this.value = value;
	}

	public T getValue()
	{
		return value;
		// if(value != null)
		// return value;
		//
		// if(items.size() > 0)
		// return (T) items.get(0).getValue();
		//
		// return null;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getItems()
	{
		if(items == null)
		{
			items = new ArrayList<SelectItem>();

			for(com.danielbchapman.code.Pair<T, String> value : createItems())
				items.add(new SelectItem(value.getOne(), value.getTwo()));

			if(items.size() > 0)
				value = (T) items.get(0).getValue();
		}

		return items;
	}

	/**
	 * @return A list of SelectItems that is of Pair&lt;VALUE, LABEL&gt;
	 */
	public abstract List<Pair<T, String>> createItems();

	/**
	 * @return the class this container will use to enforce type saftey
	 * 
	 */
	// public abstract Class<T> getTypeClass();

	/**
	 * @param value the value to convert
	 * @return an object of Type from the string
	 * 
	 */
	// public abstract T convertFrom(String value);

	/**
	 * <JavaDoc>
	 * 
	 * @param value
	 * @return the string value to write to the HTML, this will be used by the
	 *         pair @see {@link #convertFrom(String)} to recreate this object.
	 * @throws ConverterException
	 * 
	 */
	// public abstract String convertTo(T value) throws ConverterException;

	/*
	 * @SuppressWarnings("unchecked")
	 * public void onValueChange(ValueChangeEvent evt)
	 * {
	 * Object newValue = evt;
	 * if(newValue == null)
	 * return;
	 * if(newValue instanceof String)
	 * if(getTypeClass() == String.class)
	 * value = (T)newValue;
	 * else
	 * try
	 * {
	 * value = convertFrom((String)newValue);
	 * }
	 * catch(ConverterException e)
	 * {
	 * throw new RuntimeException("NEED TO PIPE TO COMPONENT | " + e.getMessage(), e);
	 * }
	 * else
	 * if(newValue.getClass().isAssignableFrom(getTypeClass()))
	 * value = (T) newValue;
	 * else
	 * throw new RuntimeException("Could not convert element " + newValue + " " + newValue.getClass() + " to " +
	 * getTypeClass());
	 * }
	 */
}
