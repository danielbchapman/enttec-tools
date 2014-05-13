package com.danielbchapman.control.dmx;

import lombok.Data;

@Data
public class Fade
{
  private String name;
  private Channel[] channels;
  private Level[] levels;
  int[] fadeStart;
  private long time;
  private Direction direction = Direction.UP;
  private long initialDelay = 0;
  public Fade(Channel[] chs, Level[] lvls, long time)
  {
    this.channels = chs;
    this.levels = lvls;
    this.time = time;
  }
}
