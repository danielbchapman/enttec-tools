package com.danielbchapman.webcue.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.danielbchapman.code.Pair;
import com.danielbchapman.control.dmx.Channel;
import com.danielbchapman.control.dmx.Cue;
import com.danielbchapman.control.dmx.Fade;
import com.danielbchapman.control.dmx.FadeEngine;
import com.danielbchapman.control.dmx.Level;
import com.danielbchapman.groups.Group;
import com.danielbchapman.groups.Groups;
import com.danielbchapman.groups.Groups.GroupFormatException;
import com.danielbchapman.groups.Item;
import com.danielbchapman.groups.JSON;
import com.danielbchapman.web.utility.WebUtil;
import com.lightassistant.utility.FileUtil;
import com.lightassistant.utility.Utility;
import com.s5g.util.Dmx;


/**
 * The Cue Bean is a statically scoped object with access to the hardware. Everything routes 
 * through this interface as there is a "single" output for the application.
 * 
 * Multiple instances will cause a hardware failure.
 *
 ***************************************************************************
 * @author Daniel B. Chapman 
 * <br /><i><b>Light Assistant</b></i> copyright Daniel B. Chapman
 * @since May 9, 2014
 * @version 2 Development
 * @link http://www.lightassistant.com
 ***************************************************************************
 */
public class CueStack
{
  public final static String GROUP_CUES = "cues";
  public final static String GROUP_CHANNELS = "channels";
  public static String rootDir = "C:/WebCue/";
  
  static FadeEngine FADE = new FadeEngine();
  static Dmx DMX;
  static ArrayList<Cue> CUES = new ArrayList<>();
  static ArrayList<Channel> CHANNELS = new ArrayList<Channel>();
  static int CURRENT_CUE = -1;
  public static long DEFAULT_TIME = 2000;
  
  static {
    Item info = new Item();
    info.setValue("version", "0.0.1 - Cymbeline");
    Groups.getGroup("version-info").put(info);
    
    File current = new File("C:/WebCue/current.xml");
    if(current.exists()){
      try
      {
        load("C:/WebCue/current.xml");
      }
      catch (ParserConfigurationException | GroupFormatException | SAXException | IOException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    if(CUES.size() < 1)
    {
      CUES.add(new Cue(new BigDecimal(1)));
      CUES.add(new Cue(new BigDecimal(2)));
      CUES.add(new Cue(new BigDecimal(3)));
    }
    
    if(CHANNELS.size() < 1)
    {
      for(int i = 0; i < 512; i++)
        CHANNELS.add(new Channel(i + 1));
    }
  }
  
  public static boolean connect()
  {
    if(DMX != null && DMX.isOpen())
      return true; //connected
   
    if(DMX == null)
    {
      DMX = new Dmx();
      FADE.setDmx(DMX);
    }
      
    if(!DMX.isOpen())
    {
      //Scan for ports
      String[] ports = Dmx.getPorts();
      for(String s : ports)
      {
        if(DMX.open(s))
          break;
      }
      
      return DMX.isOpen();
    }
    
    return true;
  }
  
  private static synchronized void goIndex(int index)
  {
    //Fade here
    if(index >= CUES.size())
      index = CUES.size() -1;
    
    
    if(index <= 0)
      index = 0;
    
    CURRENT_CUE = index;
    
    
  }
  
  public static Cue addCue()
  {
    return null;
  }
  
  public static Cue deleteCue(Cue cue)
  {
    return null;
  }
  
  public static void gotoCueZero()
  {
    Cue zero = new Cue(BigDecimal.ZERO);
    HashMap<Channel, Level> out = new HashMap<>();
    for(Channel c : CHANNELS)
    {
      out.put(c, new Level(0, 2000L)); 
    }
    
    zero.setLevels(out);
    zero.setTime(DEFAULT_TIME);
    Fade[] none = FADE.createFades(zero);
    FADE.go(none);
    
  }
  public static void go()
  {
    goIndex(++CURRENT_CUE);
    Fade[] go = FADE.createFades(CUES.get(CURRENT_CUE));
    FADE.go(go);
  }
  
  public static void back()
  {
    goIndex(--CURRENT_CUE);
    //FIXME implement...
  }
  public static void gotoCue(Cue cue)
  {
    ArrayList<Cue> stack = new ArrayList<Cue>();
    int index = CUES.indexOf(cue);
    
    if(index < 0)
    {
      sendError("THE CUE " + cue.getCueNumber() + " DOES NOT EXIST.");
      return;
    }
    
    for(int i = 0; i < index + 1; i++)
    {
        stack.add(CUES.get(i));
    }
    
    CURRENT_CUE = index;
    Fade[] go = FADE.createGotoCue(stack, CHANNELS, DEFAULT_TIME);
    FADE.go(go);
  }
  public static synchronized void save(String name)
  {
    if(name == null)
      name = "C:/WebCue/current.xml";
    else
    {
      name = name.replaceAll("\\s+", "_");
    }
      name = "C:/WebCue/" + name + ".xml";
      
    Group cues = Groups.getGroup(GROUP_CUES);
    Group channels = Groups.getGroup(GROUP_CHANNELS);
    ArrayList<Item> backupCues = cues.all();
    ArrayList<Item> backupChannels = channels.all();
   
    try
    {
      cues.remove(cues.all());
      channels.remove(channels.all());
      
      //Serialize here...
      for(Cue c : CUES)
      {
        Item cue = new Item();
        cue.setValue("NUMBER", c.getCueNumber());
        cue.setValue("UP", c.getCueNumber());
        cue.setValue("DOWN", c.getCueNumber());
        
        for(Channel ch: c.getLevels().keySet())
        {
          Level level = c.getLevels().get(ch);
          if(level != null && level.getLevel() != null)
            cue.setValue("CH" + Integer.toString(ch.getId()), level.getLevel());
          
        }
          
        cues.put(cue);
      }
      
      for(Channel c : CHANNELS)
      {
        Item channel = new Item();
        channel.setValue("CHANNEL", c.getId());
        channel.setValue("PATCH", c.getPatch());
        channel.setValue("LABEL", c.getLabel());
        
        channels.put(channel);
      }
      
      FileUtil.makeDirs(new File("C:/WebCue/"));
      Groups.toXml(new FileOutputStream(new File(name)), "groups", "web-cue files", Groups.getKnownGroups());
      Groups.toXml(new FileOutputStream(new File("C:/WebCue/current.xml")), "groups", "web-cue files", Groups.getKnownGroups());
    }
    catch (FileNotFoundException | ParserConfigurationException | TransformerException e)
    {
      for(Item i : backupChannels)
        channels.put(i);
      
      for(Item i : backupCues)
        cues.put(i);
      
      sendError(e.getMessage());
      e.printStackTrace();
    }  
  }
  
  public static synchronized void load(String file) throws FileNotFoundException, ParserConfigurationException, GroupFormatException, SAXException, IOException
  {
    File f = new File(file);
    if(f.exists() && f.isFile())
    {
      Groups.fromXml(new FileInputStream(f));
    }
    else
      return;
    
    Group cues = Groups.getGroup(GROUP_CUES);
    Group channels = Groups.getGroup(GROUP_CHANNELS);
    
    //Serialize here...
    ArrayList<Channel> tmpChannel = new ArrayList<>();
    
    HashMap<String, Channel> lookup = new HashMap<>();
    
    for(int i = 0; i < 512; i++)
    {
      Channel defaultChannel = new Channel();
      defaultChannel.setId(i);
      lookup.put(Integer.toString(i+1), defaultChannel);
    }
    
    for(Item i : channels.sort("CHANNEL"))
    {
      Channel ch = new Channel();
      ch.setId(i.integer("CHANNEL"));
      ch.setPatch(i.integer("PATCH"));
      ch.setLabel(i.string("LABEL"));
      
      System.out.println("Adding Channel -> " + i.toString());
      lookup.put(Integer.toString(ch.getId()), ch);
    }
    
    for(String key : lookup.keySet())
      tmpChannel.add(lookup.get(key));
    
    Collections.sort(tmpChannel);
    
    CHANNELS = tmpChannel;
    
    ArrayList<Cue> tmpCues = new ArrayList<>();
    
    for(Item i : cues.sort("NUMBER"))
    {
      Cue cue = new Cue(new BigDecimal(i.string("NUMBER")));
      cue.setDownTime(Long.valueOf(i.integer("DOWN")));
      cue.setTime(Long.valueOf(i.integer("UP")));
      HashMap<Channel, Level> levels = new HashMap<>();
      Map<String, JSON> fields = i.getValues();
      for(String x : fields.keySet())
      {
        
        if(!x.startsWith("CH"))
          continue;
        
        String chan = x.replaceAll("CH", "");
        Channel c = lookup.get(chan);
        if(c == null)
          continue; //This is a field like Channel etc...;
        Level level = new Level();
        level.setLevel(fields.get(x).getInteger());
        levels.put(c.clone(),  level);
      }
      
      cue.setLevels(levels);
      tmpCues.add(cue);
    }
    
    CUES = tmpCues;
  }
  
  public static String[] getShows()
  {
    FileUtil.makeDirs(new File("shows"));
    return new File("C:/WebCue/").list();
  }
  
  public static boolean isOpen()
  {
    if(DMX == null)
      return false;
    
    return DMX.isOpen();
  }
  public static void main(String ... args)
  {
    System.err.println("This would be the hard-console...");
  }
  
  public static void sendMessage(String message)
  {
    System.out.println(message);
  }
  
  public static void sendError(String error)
  {
    System.out.println(error);
  }
  
  public static Cue getCue(int index)
  {
    if(CUES.size() == 0)
    {
      Cue c = new Cue(new BigDecimal(1));
      CUES.add(c);
    }
    
    if(index < 0)
      return null;
    
    if(index >= CUES.size())
      return null;
    
    return CUES.get(index);
  }
  
  public static Cue getCurrentCue()
  {
    return getCue(CURRENT_CUE);
  }
  
  public static Cue getNextCue()
  {
    return getCue(CURRENT_CUE + 1);
  }
  
  public static Cue getLastCue()
  {
    return getCue(CURRENT_CUE - 1);
  }

  public static Cue find(BigDecimal gotoCueNumber)
  {
    for(Cue c : CUES)
    {
      if(Utility.compareToNullSafe(c.getCueNumber(), gotoCueNumber) == 0)
        return c;
    }
    
    return null;
  }
  
  public static ArrayList<Pair<Channel, Integer>> getStatus()
  {
    int[] out = FADE.getStatus();
    ArrayList<Pair<Channel, Integer>> channels = new ArrayList<>();
    
    for(Channel c : CHANNELS)
    {
      Channel copy = c.clone();
      Integer level = copy.isPatched() ? out[copy.getPatch()] : null;
      channels.add(Pair.create(copy, level));
    }
    
    return channels;
  }
  
  public static void recordCue(Cue cue)
  {
    Cue inList = find(cue.getCueNumber());
    
    if(inList != null)
    {
      CUES.remove(inList);  
    }
    
    CUES.add(cue);
    Collections.sort(CUES);
  }
  
  public static void updateCue(Cue cue, HashMap<Channel, Level> levels)
  {
    throw new RuntimeException("Not implemented!!!....");
  }
}
