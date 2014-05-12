package com.danielbchapman.web.utility;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.danielbchapman.code.Pair;


/**
 * A set of static methods for safe conversion when we don't really care about
 * the null or state of the data. This is often empty checks on data.
 * 
 * @author Daniel B. Chapman
 */
public class Safe
{
  public final static Date MODERN_TIMES;
  public static final String[] COMMON_DATE_FORMATS;

  static
  {
    COMMON_DATE_FORMATS = new String[]
        {
        "MM-dd-yyyy",//stupid Americans!
        "MM-dd-yy",
        "MM/dd/yyyy",
        "MM/dd/yy",
        "M/dd/yy",//really stupid Americans
        "yy-MM-dd",
        "yyyy-MM-dd",
        "yy-MM-dd HH:mm",
        "yyyy-MM-dd HH:mm",
        "yy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "EEE, dd MMM yyyy HH:mm:ss z",
        "yyyy-MM-dd'T'HH:mm:ssz",
        "YYYY-MM-DDThh:mmTZD"
        };

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, 1901);
    cal.set(Calendar.DAY_OF_YEAR, 1);
    cal.set(Calendar.HOUR_OF_DAY, 12); //and who cares about the rest;

    MODERN_TIMES = cal.getTime();
  }
  /**
   * @param one
   *            the first value to compare
   * @param two
   *            the second value to compare
   * @see @link {@link java.lang.Comparable}
   * @return an int conforming to the specification (1 is "greater")
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static <X extends Comparable> int compare(X one, X two)
  {
    if (one == null && two == null)
      return 0;

    if (one == null)
      return -1;
    if (two == null)
      return 1;

    return one.compareTo(two);
  }

  public static boolean numbersAreEqual(Number one, Number two, int places)
  {
    char[] format = new char[2 + places];
    format[0] = '0';
    format[1] = '.';
    for(int i = 2; i < format.length; i++)
      format[i] = '0';

    DecimalFormat fg = new DecimalFormat(new String(format));
    String a = fg.format(one);
    String b = fg.format(two);

    return a.equals(b);
  }

  public static BigDecimal parseBigDecimal(String input)
  {
    if (input == null)
      return null;
    try
    {
      return new BigDecimal(input);
    }
    catch (NumberFormatException e)
    {
      return null;
    }
  }

  public static Boolean parseBoolean(String input)
  {
    if (input == null)
      return null;

    return Boolean.parseBoolean(input);
  }

  public static Integer parseInteger(String input)
  {
    if (input == null)
      return null;

    try
    {
      return Integer.parseInt(input);
    }
    catch (NumberFormatException e)
    {
      return null;
    }
  }

  public static BigDecimal tolerantParseBigDecimal(String input)
  {
    return tolerantParseBigDecimal(input, null);
  }

  public static BigDecimal tolerantParseBigDecimal(String input, BigDecimal defaultValue)
  {
    return tolerantParseBigDecimalCheck(input, defaultValue).getOne();
  }

  /**
   * @param input
   * @return A pair of safeValue and hasError (false if no error)
   * 
   */
  public static Pair<BigDecimal, Boolean> tolerantParseBigDecimalCheck(String input, BigDecimal defaultValue)
  {
    String raw = input;
    if(input == null)
      return Pair.create(defaultValue, true);

    //PERCENT
    boolean percent = false;
    boolean negative = false;

    if(input.contains("%"))
      percent = true;

    if(input.contains("-"))
      negative = true;

    if(!negative && input.contains("(") && input.contains(")"))
      negative = true;

    input = input.replaceAll("[^0-9.]", "");//Strip all but 0 to 9 and '.'

    try
    {
      BigDecimal value = null;
      if(negative)
        value = new BigDecimal("-" + input);
      else
        value = new BigDecimal(input);

      if(value != null && percent && !value.equals(BigDecimal.ZERO))
        value = value.divide(new BigDecimal("100"));

      return Pair.create(value, false);
    }
    catch(Exception e)
    {
      System.out.println(MessageFormat.format("Could not convert {0} to a big decimal", raw));
      return Pair.create(defaultValue, false);
    }
  }

  public static Date tolerantParseDate(String input, String pattern, Date earliest)
  {
    return tolerantParseDateCheck(input, pattern, earliest).getOne();
  }

  public static Pair<Date, Boolean> tolerantParseDateCheck(String input, String pattern, Date earliest)
  {
    String raw = input;
    Date result = null;

    result = tryFormat(input, pattern, earliest);
    if(result != null)
      return Pair.create(result, true);

    String replace = "[^0-9]"; //Strip all words/symbols
    String replaceFormat = "[^GyYMwWDdFEuaHkKhmsSzZX]"; //legal symbols in a format except punctuation

    pattern = pattern.replaceAll(replaceFormat, "");
    input.replaceAll(replace, "");

    result = tryFormat(input, pattern, earliest);
    if(result != null)
      return Pair.create(result, false);

    //try common formats
    for(String format : COMMON_DATE_FORMATS)
    {
      result = tryFormat(raw, format, earliest);
      if(result != null)
        return Pair.create(result, false);

      result = tryFormat(input, format.replaceAll(replaceFormat, ""), earliest);
      if(result != null)
        return Pair.create(result, false);
    }

    return Pair.create(result, true);
  }

  private static Date tryFormat(String input, String pattern, Date earliest)
  {
    if(earliest == null)
      earliest = MODERN_TIMES;
    try
    {
      Date parse = new SimpleDateFormat(pattern).parse(input);
      if(parse.before(earliest))
        return null;

      return parse;
    }
    catch(ParseException e)
    {
      return null;//debug hook
    }
    catch(IllegalArgumentException e)
    {
      System.out.println(e.getMessage());
      return null;
    }
  }
}


