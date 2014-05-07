package com.danielbchapman.control.dmx;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.danielbchapman.code.Pair;

public class CueList
{
  private SortedMap<BigDecimal, Cue> cues = new TreeMap<>();
  private String label;

  public CueList(String label)
  {
    this.label = label;
  }
  
  public CueList()
  {
    this("default");
  }
  
  public void updateCue(BigDecimal number, Collection<Pair<Channel, Level>> levels) throws RecordException
  {
    Cue x = find(number);
    if(x == null)
      throw new RecordException("Cue " + number + " does not exist");
    
    x.update(levels);
  }
  public void addCue(BigDecimal number) throws RecordException
  {
    Cue existing = cues.get(number);
    if(existing != null)
      throw new RecordException("Cue " + number + " already exists");
    
    cues.put(number, new Cue(number));
  }
  
  /**
   * Capture the active levels and record as a new cue.
   * @param number
   * @param engine <Return Description>  
   * 
   */
  public void recordCue(BigDecimal number, FadeEngine engine) throws RecordException
  {
    //FIXME use the engine to get programmer levels
    addCue(number);
  }
  
  
  public Cue find(BigDecimal number)
  {
    return cues.get(number);
  }
  
  /**
   * Deletes a cue from the list, throws a RecordException if it is not found.
   * @param number
   * @throws RecordException <Return Description>  
   */
  public void deleteCue(BigDecimal number) throws RecordException
  {
    Cue toRemove = cues.remove(number);
    if(toRemove == null)
      throw new RecordException("Cue " + number + " does not exist");  
  }
  @Override
  public String toString()
  {
    StringBuilder x = new StringBuilder();
    x.append("CUE LIST: " + label + "\n");
    for(BigDecimal c : cues.keySet())
    {
      x.append(cues.get(c));
      x.append("\n");
    }
    
    return x.toString();
  }
}
