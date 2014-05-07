package com.danielbchapman.control.dmx;

import com.lightassistant.utility.Utility;

import lombok.Data;

@Data
public class Channel implements Comparable<Channel>, Cloneable
{
  private int id;
  private String label;
  private Integer patch;
  private transient Fade activeFade;

  public Channel()
  {
    this(1, null, null);
  }

  public Channel(int channel)
  {
    this(channel, null, null);
  }
  
  public Channel(int channel, int patch)
  {
    this(channel, patch, null);
  }
  public Channel(int channel, Integer patch, String label)
  {
    this.id = channel;
    this.patch = patch;
    this.label = label;
  }

  @Override
  public int compareTo(Channel other)
  {
    return Utility.compareToNullSafe(this.id, other.id);
  }
  
  public Channel clone()
  {
    Channel copy = new Channel();
    copy.id = id;
    copy.label = label;
    copy.patch = patch; 
    
    return copy;
  }
  
  public boolean isPatched()
  {
    return patch != null;
  }
}
