package com.danielbchapman.control.dmx;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
  private BigDecimal delayUp = BigDecimal.ZERO;
  @Getter
  @Setter
  private BigDecimal delayDown = BigDecimal.ZERO;;
  /**
   * The follow property is symbolic and the engine (which does not maintain the stack)
   * is unable to use this, it is for the user to execute and schedule auto-follows. 
   */
  @Getter
  private BigDecimal follow;
  public void setFollow(BigDecimal dec)
  {
    follow = dec;
  }
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
  private BigDecimal time = new BigDecimal(4.9);
  @Getter
  @Setter
  private BigDecimal downTime = null;
  
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
    build.append("Cue number [" + getCueNumber() + "] TIME: " + time + "s\n");
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
    DecimalFormat nf = new DecimalFormat("##0.##");
    if(downTime != null)
      return nf.format(getTime()) + "/" + getTimeDownSeconds();
    else
      return nf.format(getTime());
  }
  
  public String getHumanDelay()
  {
    DecimalFormat nf = new DecimalFormat("##0.##");
    if(downTime != null)
      return nf.format(getDelayUp()) + "/" + nf.format(getDelayDown());
    else
      return nf.format(getDelayUp());
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
  
  public String getTimeDownSeconds()
  {
    if(downTime == null)
      return null;
    return downTime.toString();
  }
  
  public void setTimeDownSeconds(String time) //allow nulls JSF Conversion bug
  {

    BigDecimal parse = Safe.parseBigDecimal(time);
    if(parse == null)
    {
      downTime = null;
      return;
    }
  
    downTime = parse;
  }
  
  public long getDelayUpMils()
  {
    return getDelayUp().multiply(new BigDecimal(1000)).longValue();
  }
  
  public long getDelayDownMils()
  {

    return getDelayDown().multiply(new BigDecimal(1000)).longValue();
  }
  
  public long getTimeMils()
  {
    return getTime().multiply(new BigDecimal(1000)).longValue();
  }
  
  public long getTimeDownMils()
  {
    if(downTime == null)
      return getTimeMils();
    else
      return getDownTime().multiply(new BigDecimal(1000)).longValue();
  }
}
