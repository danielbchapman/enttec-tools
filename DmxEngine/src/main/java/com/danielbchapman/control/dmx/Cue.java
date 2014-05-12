package com.danielbchapman.control.dmx;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

import com.danielbchapman.code.Pair;
import com.lightassistant.utility.Safe;

public class Cue implements Comparable<Cue>, Serializable, Cloneable
{
  private static final long serialVersionUID = 1L;
  @Getter
  @Setter
  private String label;
  @Getter
  @Setter
  private BigDecimal cueNumber;
  @Getter
  @Setter
  private HashMap<Channel, Level> levels = new HashMap<Channel, Level>();
  @Getter
  @Setter
  private Long time = Long.valueOf(4900);
  @Getter
  @Setter
  private Long downTime;
  
  public Cue(BigDecimal id)
  {
    this.cueNumber = id;
  }

  @Override
  public int compareTo(Cue other)
  {
    //FIXME add utility.compareTo;
    return cueNumber.compareTo(other.cueNumber);
  }

  public void update(Collection<Pair<Channel, Level>> set)
  {
    if(set == null)
      return;
    
    for(Pair<Channel, Level> p : set)
    {
      if(p == null)
        continue;
      
      Channel c = p.getOne();
      Level l = p.getTwo();
      
      if(c != null)
        levels.put(c.clone(),  l.clone());
    }
  }
  
  @Override
  public String toString()
  {
    StringBuilder build = new StringBuilder();
    build.append("Cue number [" + getCueNumber() + "] TIME: " + (double) time/1000 + "s\n");
    ArrayList<Channel> keys = new ArrayList<>();
    for(Channel c : levels.keySet())
    {
      keys.add(c);
    }
    
    Collections.sort(keys);
    
    for(Channel c : keys)
    {
      Level l = levels.get(c);
      if(l == null)
        build.append("\t(" + c.getId() + ")@????");
      else
        build.append("\t(" + c.getId() + ")@" + l.getLevel());
      
    }
    
    return build.toString();
  }
  
  public String getHumanTime()
  {
    if(downTime != null)
      return time + "/" + downTime;
    else
      return time.toString();
  }
  
  public Cue clone()
  {
    Cue copy = new Cue(cueNumber);
    copy.label = label;
    copy.downTime = downTime;
    copy.time = time;
    
    HashMap<Channel, Level> tmp = new HashMap<>();
    for(Channel c : levels.keySet())
      tmp.put(c,  levels.get(c));
    
    copy.levels = tmp;
    return copy;
  }
  
  public double getTimeSeconds()
  {
    return Double.valueOf(time) / 1000.0D;
  }
  
  public void setTimeSeconds(double x)
  {
    if(x < 0)
      x = 0;
    time = Double.valueOf(x * 1000.0D).longValue();
  }
  
  public String getTimeDownSeconds()
  {
    if(downTime == null)
      return null;
    return Double.valueOf(downTime / 1000.0D).toString();
  }
  
  public void setTimeDownSeconds(String time)
  {

    BigDecimal parse = Safe.parseBigDecimal(time);
    if(parse == null)
    {
      downTime = null;
      return;
    }
  
    downTime = Double.valueOf(parse.doubleValue() * 1000D).longValue();
  }
}
