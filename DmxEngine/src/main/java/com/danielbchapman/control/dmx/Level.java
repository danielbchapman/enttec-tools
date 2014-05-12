package com.danielbchapman.control.dmx;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Level implements Cloneable, Serializable
{
  private Integer level;
  private Long fadeTime;
 
  public Level clone()
  {
    return new Level(level, fadeTime);
  }
}
