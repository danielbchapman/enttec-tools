package com.danielbchapman.control.dmx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

import com.s5g.util.Dmx;


/**
 * The fade engine is the core class for the DMX interface. It represents a 
 * set of DMX universes (TBD).
 *
 ***************************************************************************
 * @author Daniel B. Chapman 
 * <br /><i><b>Light Assistant</b></i> copyright Daniel B. Chapman
 * @since May 6, 2014
 * @version 2 Development
 * @link http://www.lightassistant.com
 ***************************************************************************
 */

public class FadeEngine
{
  public static int MILLISECOND_REPEAT = 10;
  private ScheduledExecutorService timer =  Executors.newScheduledThreadPool(8);
  
  @Override
  protected void finalize() throws Throwable
  {
    timer.awaitTermination(1, TimeUnit.MINUTES);
  }
  @Setter
  @Getter
  private Dmx dmx;
  private int[] snapshot = new int[512];
  private Fade[] activeFades = new Fade[512];
  private boolean programmerActive = false;
  private Integer[] programmer = new Integer[512];

  public FadeEngine()
  {
    snapshot = new int[512];
    for(int i = 0; i < 512; i++)
    {
      snapshot[i] = 0;
      activeFades[i] = null;
    }

  }

  public void programmer(Integer[] levels)
  {
    programmerActive = true;
    commit(levels, null);
  }
  
  public void clearProgrammer()
  {
    programmerActive = false;
    programmer = new Integer[512];
  }
  
  public int[] getStatus()
  {
    int[] copy = new int[snapshot.length];
    for(int i = 0; i < copy.length; i++)
      copy[i] = snapshot[i];
    
    return copy;
  }
  public Integer[] getProgrammer()
  {
    Integer[] copy = new Integer[programmer.length];
    for(int i = 0; i < copy.length; i++)
      copy[i] = programmer[i];
    return copy;
  }
  
  /**
   * Write this array to the DMX, if a value is null it will be ignored
   * 
   * @param newLevels an array containing MOVES in the Data. Nulls will be ignored.  
   * 
   */
  public void commit(Integer[] newLevels, Fade fade) //the fade is to track for debugging..
  {
    Integer[] send = new Integer[snapshot.length];
    StringBuilder x = new StringBuilder();
    x.append("Committing array to DMX | FADE: " + fade.getName() +"\n");
    for(int i = 0; i < newLevels.length; i++)
      if(newLevels[i] != null)
      {
        snapshot[i] = newLevels[i];
        x.append("\t[" + i + "]@" + newLevels[i]);
      }

    for(int i = 0; i < snapshot.length; i++)
      send[i] = snapshot[i];
    
    if(dmx != null && dmx.isOpen())
    {
      dmx.setDmxArray(send);
    }
    else 
    {
      System.out.println(x.toString());  
    }
    
    //System.out.println(dmx.printData());
  }

  public Fade[] createGotoCue(List<Cue> stack, Collection<Channel> allChannels, long time)
  {
    //IF there is an unused channel this won't affect its level!
    HashMap<Channel, Level> current = new HashMap<>();
    
    //Zero out...
    for(Channel c : allChannels)
    {
      current.put(c, new Level(0, time));
    }
    
    for(Cue cue : stack)
    {
      for(Channel c : cue.getLevels().keySet())
      {
        Level to = cue.getLevels().get(c);
        if(to != null && to.getLevel() != null)
        {
          current.put(c, to);
        }
      }
    }
    
    ArrayList<Channel> chan = new ArrayList<>();
    ArrayList<Level> level = new ArrayList<>();

    for(Channel c : current.keySet())
    {
      chan.add(c);
      level.add(current.get(c));
    }

    if(chan.size() != level.size())
      throw new IllegalArgumentException("Channel and level size must match!");
    
    Channel[] channels = new Channel[chan.size()];
    Level[] levels = new Level[level.size()];

    for(int i = 0; i < chan.size(); i++)
    {
      channels[i] = chan.get(i);
      levels[i] = level.get(i);
    }
    
    //Use only cue time for now, eventually do multi-part fades   
    Fade fade = new Fade(channels, levels, time);
    fade.setName("Goto Cue #" + stack.get(stack.size() -1 ).getCueNumber());
    return new Fade[]{fade};
  }
  
  public Fade[] createFades(Cue cue)
  {
    cue.getLevels();
    ArrayList<Channel> chanUp = new ArrayList<>();
    ArrayList<Level> levelUp = new ArrayList<>();
    ArrayList<Channel> chanDown = new ArrayList<>();
    ArrayList<Level> levelDown = new ArrayList<>();
    
    for(Channel c : cue.getLevels().keySet())
    {
      if(c.isPatched())
      {
        int current = snapshot[c.getPatch()];
        Level level = cue.getLevels().get(c);
        if(level != null && level.getLevel() != null)
        {
          if(current < level.getLevel())
          {
            chanUp.add(c);
            levelUp.add(level);
          }
          else
          {
            chanDown.add(c);
            levelDown.add(level);
          }
        }
      }
    }
//
//    if(chan.size() != level.size())
//      throw new IllegalArgumentException("Channel and level size must match!");

    Channel[] channelsUp = new Channel[chanUp.size()];
    Channel[] channelsDown = new Channel[chanDown.size()];
    Level[] levelsUp = new Level[levelUp.size()];
    Level[] levelsDown = new Level[levelDown.size()];

    for(int i = 0; i < chanUp.size(); i++)
    {
      channelsUp[i] = chanUp.get(i);
      levelsUp[i] = levelUp.get(i);
    }
    
    for(int i = 0; i < chanDown.size(); i++)
    {
      channelsDown[i] = chanDown.get(i);
      levelsDown[i] = levelDown.get(i);
    }
    //Use only cue time for now, eventually do multi-part fades   
    Fade up = new Fade(channelsUp, levelsUp, cue.getTimeMils());
    up.setName("Cue #" + cue.getCueNumber() + " [up]");
    up.setInitialDelay(cue.getDelayUpMils());
    
    Fade down = new Fade(channelsDown, levelsDown, cue.getTimeDownMils());
    down.setName("Cue #" + cue.getCueNumber() + " [down]");
    down.setInitialDelay(cue.getDelayDownMils());
    return new Fade[]{up, down};
  }

  //Separated for testing purposes...
  public void prepareFade(Fade[] fades)
  {
    //Set fades to match channels
    for(Fade f : fades)
    {
      for(int i = 0; i < f.getChannels().length; i++)
      {
        if(f.getLevels()[i].getLevel() == null || !f.getChannels()[i].isPatched())
          continue; //no level

        Channel ch = f.getChannels()[i];
        Integer index = ch.getPatch();
        activeFades[index] = f;
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void executeFades(Fade[] fades)
  {
    prepareFade(fades);

    for(int i = 0; i < fades.length; i++)
    {
      ScheduledFuture<FadeThread> future = null;
      FadeThread fader = new FadeThread();
      fader.engine = this;
      fader.fade = fades[i];
      future = (ScheduledFuture<FadeThread>) timer.scheduleAtFixedRate(fader, 0, MILLISECOND_REPEAT, TimeUnit.MILLISECONDS);
      fader.end = future;  
    }


  }
  ///Returns -1 on ignore.
  public int calculateLevel(Fade fade, int index, int original, long elapsed, long fadeTime)
  {
    Channel channel = fade.getChannels()[index];
    Level level = fade.getLevels()[index];
    if(level.getLevel() == null || !channel.isPatched() || activeFades[channel.getPatch()] != fade) //Is this fade active?
    {
      return -1;
    }
    //%complete
    double ratio = (double) elapsed / fadeTime;

    int levelTarget = level.getLevel();
    int levelNow = snapshot[channel.getPatch()];
    int adjustment = (int) ((levelTarget - original) * ratio);

    return original + adjustment;
  }

  public class FadeThread implements Runnable
  {
    ScheduledFuture<FadeThread> end;
    int[] original; //Maps to channel index => snapshot[patch]
    Integer[] patch; //Maps channel index => dmx address (snapshot[address])
    Integer[] targets; //Maps channel => target level
    Channel[] channels;
    Level[] levels;
    Integer[] update;
    FadeEngine engine;
    Fade fade;
    long startTime = -1;
    int count = 0;
    @Override
    public void run()
    { 
      count++;
      if(startTime < 0)
      {
        channels = fade.getChannels();
        levels = fade.getLevels();
        update = new Integer[snapshot.length];
        startTime = System.currentTimeMillis();

        original = new int[channels.length];
        patch = new Integer[channels.length];
        targets = new Integer[channels.length];

        for(int i = 0; i < channels.length; i++)
        {
          patch[i] = channels[i].getPatch();
          targets[i]= levels[i].getLevel();
          if(patch[i] != null)
            original[i] = engine.snapshot[patch[i]];
        }
      }
      
      long elapsed = System.currentTimeMillis() - startTime;
      if(elapsed >= fade.getTime())
      {
        //Force snapshot;
        end.cancel(true);
        
        for(int i = 0; i < channels.length; i++)
        {
          if(patch[i] == null)
            continue;
          if(activeFades[patch[i]] != fade)
          {
            update[patch[i]] = null;
            continue;
          }
          update[patch[i]] = targets[i];//engine.calculateLevel(fade, i, original[i], elapsed, fade.getTime());
        }
        
        commit(update, fade);
        double stepsPerSecond = count/(double)((double) fade.getTime()/ (double)1000);
        System.out.println("[FORCE FADE " + fade.getName() + "] There were " + count + " steps for a rate of " + stepsPerSecond + "Hz refresh | Cue time was: " + fade.getTime());
      }
      else
      {
        for(int i = 0; i < channels.length; i++)
        {
          if(patch[i] == null)
            continue;
          if(activeFades[patch[i]] != fade)
          {
            update[patch[i]] = null;
            continue;
          }
          update[patch[i]] = engine.calculateLevel(fade, i, original[i], elapsed, fade.getTime());
        }
        
        commit(update, fade);
      }
    }
  }
}
