package com.danielbchapman.webcue.beans;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.danielbchapman.code.Pair;
import com.danielbchapman.control.dmx.Channel;
import com.danielbchapman.control.dmx.Cue;
import com.danielbchapman.control.dmx.Level;
import com.danielbchapman.groups.Groups.GroupFormatException;
import com.danielbchapman.web.utility.Utility;
import com.danielbchapman.webcue.beans.CueBean;
import com.danielbchapman.webcue.beans.CueStack;
import com.lightassistant.utility.FileUtil;
import com.lightassistant.utility.Safe;


public class Hog2Import
{
  
  public static void main(String ... args) 
  {
    try
    {
      processHog2B();
    }
    catch (ParserConfigurationException | GroupFormatException | SAXException | IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
  public static void processHog2B() throws FileNotFoundException, ParserConfigurationException, GroupFormatException, SAXException, IOException
  {
    ArrayList<String> file = FileUtil.readLines("import.txt");
    String[][] lines = new String[file.size()][];
    for(int i = 0; i < file.size(); i++)
      lines[i] = file.get(i).split(",");
    
    int length = lines[0].length;
    Cue[] cues = new Cue[length];
    @SuppressWarnings("unchecked")
    HashMap<Integer, Integer>[] levels = new HashMap[length];
    for(int i = 0; i < length; i++)
    {
      BigDecimal cueNumber = Safe.parseBigDecimal(lines[0][i]);
      if(cueNumber != null)
      {
        cues[i] = new Cue(cueNumber);
        levels[i] = new HashMap<>();
        //Line 1 ? 
        //Line 2 = time
        if(i < lines[2].length)
        {
          BigDecimal follow = getTime(lines[2][i]);
          cues[i].setFollow(follow);
        }
      }
    }
    
    //SKIP TIME
    
    for(int i = 3; i < lines.length; i+=3)//Jump 3
    {
      Integer id = Safe.parseInteger(lines[i][1]);
      if(id == null)
        throw new IllegalArgumentException("The file at line " + i + " lacks channel identification");
      
      Channel channel = new Channel(id);
      //FIXME Normally check for attributes
      //FIXME Do channel specific timing here..
      for(int k = 3; k < lines[i].length; k++)
      {
        Integer level = getLevel(lines[i][k]);
        Pair<BigDecimal, BigDecimal> delay = getTimeSplit(lines[i + 1][k]);
        Pair<BigDecimal, BigDecimal> time = getTimeSplit(lines[i + 2][k]);
        
        if(level != null)
        {
          cues[k].getLevels().put(channel.clone(), new Level(level, 4900L));
          levels[k].put(id, level);
        }
        
        if(time.getOne() != null)
          cues[k].setTime(time.getOne());
        
        if(time.getTwo() != null)
          cues[k].setTime(time.getTwo());
        
        if(delay.getOne() != null)
          cues[k].setDelayUp(delay.getOne());
        
        if(delay.getTwo() != null)
            cues[k].setDelayDown(delay.getTwo());
      }
    }
    
    for(int i = 3; i < cues.length; i++)
    {
      System.out.println(cues[i]);
      System.out.println("\t" + printChannels(levels[i]));
    }
    
    ArrayList<Cue> save = new ArrayList<>();
    for(Cue c : cues)
      if(c != null)
        save.add(c);
    CueStack.load("current.xml");
    CueStack.save("backup-import.xml");
    CueStack.CUES = save;
    CueStack.save("import.xml");
  }
  
  public static String printChannels(Map<Integer, Integer> map)
  {
    
    StringBuilder b = new StringBuilder();
    ArrayList<Integer> obj = new ArrayList<>();
    for(Integer o : map.keySet())
      obj.add(o);
    
    Collections.sort(obj);
    
    for(Integer c : obj)
    {
      Double percent = CueBean.toPercent(map.get(c));
      DecimalFormat df = new DecimalFormat("#0");
      b.append("(" + c + ")@" +  df.format(percent) + "%\t");
    }
    
    return b.toString();
  }
  public static void processHog2() throws FileNotFoundException, ParserConfigurationException, GroupFormatException, SAXException, IOException
  {
    System.out.println("IMPORT TEST");
    ArrayList<String> file = FileUtil.readLines("import.txt");
    ArrayList<String[]> tokens = new ArrayList<String[]>();
    for(String s : file)
    {
      String[] token = s.split(",");
      tokens.add(token);
    }
    //Line 3 is Follows
    //Line 2 = ???
    //Line 1 = cues
    
    String[] cueNumbers = tokens.get(0);
    String[] cueTimes = tokens.get(0);
    String[] cueFollows = tokens.get(0);

    //these all have null fronts
    Cue[] cues = new Cue[cueNumbers.length];
    @SuppressWarnings("unchecked")
    HashMap<Integer, Integer>[] levels = new HashMap[cueNumbers.length];
    BigDecimal[] follows = new BigDecimal[cueNumbers.length];
    BigDecimal[] timeUp = new BigDecimal[cueNumbers.length];
    BigDecimal[] delayUp = new BigDecimal[cueNumbers.length];
    BigDecimal[] timeDown = new BigDecimal[cueNumbers.length];
    BigDecimal[] delayDown = new BigDecimal[cueNumbers.length];
    
    //This only imports Desk Channels
    int max = cueNumbers.length;
    int start = 3; //skip 3 blanks
    
    //row 3
    for(int i = start; i < max; i++)
    {
      cues[i] = new Cue(new BigDecimal(cueNumbers[i]));
      levels[i] = new HashMap<Integer, Integer>();
      BigDecimal follow = getTime(cueFollows[i]);
      //Column 2.. links?
    }
    
    //Start reading at tokens 4, cue 0
    for(int i = 3; i < tokens.size(); )
    {
      String[] intensities = tokens.get(i++);
      String[] delays = tokens.get(i++);
      String[] times = tokens.get(i++);
      
      Integer id = Integer.parseInt(intensities[1]);
      Channel c = new Channel(id);

      System.out.println("Processing cue intensities: " + intensities.length);
      //Parse channel for all cues
      for(int k = start; k < intensities.length - 1; k++)
      {
        Cue cue = cues[k];
        HashMap<Integer, Integer> cueLevels = levels[k];
        if(cueLevels == null)
        {
          cueLevels = new HashMap<>();
          levels[k] = cueLevels;
        }
//        System.out.println("Processing: " + k + " of " + intensities.length);
        Channel copy = c.clone();
        Integer level = getLevel(intensities[k]);
        Pair<BigDecimal, BigDecimal> t = getTimeSplit(times[k]);
        Pair<BigDecimal, BigDecimal> d = getTimeSplit(delays[k]);
        
        if(level != null)
        {
          cue.getLevels().put(copy,  new Level(level, 4900L));
          cueLevels.put(id, level);
        }
        
          
        if(t.getOne() != null)
          cue.setTime(t.getOne());
        
        if(t.getTwo() != null)
          cue.setDownTime(t.getTwo());
        
        if(d.getOne() != null)
          cue.setDelayUp(d.getOne());
        
        if(d.getTwo() != null)
          cue.setDelayDown(d.getTwo());
      }
    }
    
    for(int i = start; i < cues.length; i++)
    {
      System.out.println(cues[i]);
      System.out.print(i + ">>\t");
      Set<Integer> keys = levels[i].keySet();
      if(keys == null)
      {
        System.out.println("No entries...");
        continue;
      }
      ArrayList<Integer> keySort = new ArrayList<>();
      for(Integer x : keys)
        keySort.add(x);
      
      Collections.sort(keySort);
      DecimalFormat df = new DecimalFormat("#0");
      for(Integer ch : keySort)
        System.out.print("(" + ch + ")@ " + df.format(CueBean.toPercent(levels[i].get(ch))) + "%\t");
      System.out.println();
      
      if(levels[i].size() != cues[i].getLevels().size())
        System.out.println("\t-> SIZE MISMATCH");
    }
      
    
//    CueStack.load("current.xml");
//    CueStack.save("backup-import.xml");
//    CueStack.CUES = cues;
//    CueStack.save("import.xml");
  }
  
  public static Pair<BigDecimal, BigDecimal> getTimeSplit(String data)
  {
    String[] split = data.split("/");
    if(split.length == 0)
      return null;
    
    String one = split[0];
    String two = null;
    if(split.length == 2)
      two = split[1];
    
    
    return Pair.create(getTime(one), getTime(two));
  }
  
  public static BigDecimal getTime(String input)
  {
    if(Utility.isEmptyOrNull(input))
      return null;
    
    if(input.endsWith("s"))
    {
      return Safe.tolerantParseBigDecimal(input.replaceAll("s", ""));
    } 
    else if (input.endsWith("m"))
    {
      BigDecimal min = Safe.tolerantParseBigDecimal(input.replaceAll("m", ""));
      if(min == null)
        return null;
      
      return min.multiply(new BigDecimal(60));
        
    }
    else
      return Safe.tolerantParseBigDecimal(input);
  }
  
  public static Integer getLevel(String input)
  {
    if(Utility.isEmptyOrNull(input))
      return null;
    
    if("full".equals(input.toLowerCase()))
    {
      return 255;
    } 
    if(input.endsWith("%"))
    {
      input = input.replaceAll("%", "");
      BigDecimal doub = Safe.parseBigDecimal(input); 
      if(doub == null)
        return null;
      
      return CueBean.toDmx(doub.doubleValue());
    }
    
    return null; //empty (moves to zero are marked.
  }
}
