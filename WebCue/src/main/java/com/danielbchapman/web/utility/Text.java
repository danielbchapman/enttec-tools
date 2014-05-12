package com.danielbchapman.web.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A simple set of static methods for dealing with strings and text.
 * 
 * @author Daniel B. Chapman
 * @since Mar 5, 2012
 * @copyright Copyright 2012 Daniel B. Chapman/Harrison Fortier. All rights
 *            reserved. PROPRIETARY/CONFIDENTAL. Use is subject to license
 *            terms.
 */
public class Text
{

  public static boolean areEqual(String one, String two)
  {
    return compare(one, two) == 0;
  }

  /**
   * @param one
   *            the first string to compare
   * @param two
   *            the second string to compare
   * @see @link {@link java.lang.Comparable}
   * @return an int conforming to the specification (1 is "greater")
   */
  public static int compare(String one, String two)
  {
    return Safe.compare(one, two);
  }

  /**
   * A simple static method that does a contains ignoring case.
   * 
   * @param one the element to search
   * @param two the string to search for.
   * @throws IllegalAccessException when the first argument is null, a second argument of null will return false
   * @return true if one contains two ignoring case...
   * 
   */
  public static boolean containsIgnoreCase(String one, String two)
  {
    if(one == null)
      throw new IllegalArgumentException("Value " + one + " can not be null");
    if(two == null)
      return false;

    if(one.toLowerCase().contains(two.toLowerCase()))
      return true;

    return false;
  }

  /**
   * @param string
   * @return <b>true</b> if the string is null or empty <b>false</b> if it
   *         contains data
   */
  public static boolean empty(String string)
  {
    if (string == null)
      return true;
    if (string.trim().length() == 0)
      return true;
    return false;
  }

  public static boolean maximum(String string, int length)
  {
    if (!empty(string))
      if (string.length() >= length)
        return true;
      else
        return false;

    return false;
  }

  /**
   * @param string
   *            the string to check
   * @param length
   *            the length to compare against
   * @return true if this has a minimum length of [length]
   */
  public static boolean minimum(String string, int length)
  {
    if (!empty(string))
      if (string.length() < length)
        return false;
      else
        return true;

    return false;
  }

  /**
   * Read an input stream into a text file.
   * @param stream
   * @return the contents of the file
   * @throws IOException <Return Description>
   * 
   */
  // FIXME Java Doc Needed
  public static String readStream(InputStream stream) throws IOException
  {
    if (stream == null)
      throw new IllegalArgumentException("The stream is null");

    String line = null;
    StringBuilder builder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
    {
      while ((line = reader.readLine()) != null)
      {
        builder.append(line);
        builder.append("\r\n");
      }
    }
    return builder.toString();
  }
}
