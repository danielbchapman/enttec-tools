package com.danielbchapman.web.converters;

import javax.faces.convert.FacesConverter;

@FacesConverter("com.danielbchapman.web.converters.BigDecimalConverter")
public class BigDecimalConverter extends AbstractDecimalConverter
{

  @Override
  protected String getDecimalFormat()
  {
    return "##0.##";
  }

}
