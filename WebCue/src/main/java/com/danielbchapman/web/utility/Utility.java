package com.danielbchapman.web.utility;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.UUID;

/**
 * A simple utility class for trimming/empty/nulls etc...
 * 
 * @author Daniel B. Chapman
 * @since Sep 19, 2013
 * @version 2 Development
 * @link http://www.lightassistant.com
 *
 */
public class Utility
{
  /**
   * Returns a random UUID--our system might hash/salt these
   * @return a random UUID
   * 
   */
  public static UUID getSystemUUID()
  {
    return UUID.randomUUID();
  }

  /**
   * Return true if the string is empty or null
   * 
   *
   */
  public static boolean isEmptyOrNull(String x)
  {
    if (x == null)
      return true;

    if (x.trim().isEmpty())
      return true;

    return false;
  }

  @SuppressWarnings("unchecked")
  public <T> T[] toArray(Collection<T> collection, Class<T> clazz)
  {
    T[] objs = (T[]) Array.newInstance(clazz, 0);
    return collection.toArray(objs);
  }

}
