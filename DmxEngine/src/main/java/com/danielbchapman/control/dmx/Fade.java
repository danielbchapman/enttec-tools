package com.danielbchapman.control.dmx;

import lombok.Data;

@Data
public class Fade
{
  private Channel[] channels;
  private Level[] levels;
  int[] fadeStart;
  private long time;
  
  public Fade(Channel[] chs, Level[] lvls, long time)
  {
    this.channels = chs;
    this.levels = lvls;
    this.time = time;
  }
}
