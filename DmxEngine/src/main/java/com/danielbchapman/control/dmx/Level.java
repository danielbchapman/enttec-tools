package com.danielbchapman.control.dmx;

import java.io.Serializable;
import java.text.DecimalFormat;

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
  
  public String percent()
  {
    if(level != null)
    {
      DecimalFormat df = new DecimalFormat("#0");
      double d = level.doubleValue();
      String out = df.format(d / 255D * 100D);
      return out + "%";
    }
    
    return "-";
  }
}
