package com.danielbchapman.control.dmx;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

import com.danielbchapman.code.Pair;

public class Cue implements Comparable<Cue>
{
  @Getter
  @Setter
  private BigDecimal cueNumber;
  @Getter
  private HashMap<Channel, Level> levels = new HashMap<Channel, Level>();
  @Getter
  @Setter
  private long time = 4900;
  
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
}
