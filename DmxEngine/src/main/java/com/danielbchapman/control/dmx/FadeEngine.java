package com.danielbchapman.control.dmx;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
  private Dmx dmx;
  private int[] snapshot = new int[512];
  private Fade[] activeFades = new Fade[512];

  public FadeEngine()
  {
    snapshot = new int[512];
    for(int i = 0; i < 512; i++)
    {
      snapshot[i] = 0;
      activeFades[i] = null;
    }

  }

  /**
   * Write this array to the DMX, if a value is null it will be ignored
   * 
   * @param newLevels an array containing MOVES in the Data. Nulls will be ignored.  
   * 
   */
  public void commit(Integer[] newLevels)
  {
    StringBuilder x = new StringBuilder();
    x.append("Committing array to DMX\n");
    for(int i = 0; i < newLevels.length; i++)
      if(newLevels[i] != null)
        x.append("\t[" + i + "]@" + newLevels[i]);
    
    System.out.println(x.toString());
  }

  public Fade[] createFades(Cue cue)
  {
    cue.getLevels();
    ArrayList<Channel> chan = new ArrayList<>();
    ArrayList<Level> level = new ArrayList<>();

    for(Channel c : cue.getLevels().keySet())
    {
      chan.add(c);
      level.add(cue.getLevels().get(c));
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
    Fade fade = new Fade(channels, levels, cue.getTime());
    return new Fade[]{fade};
  }

  //Separated for testing purposes...
  public void prepareFade(Fade[] fades)
  {
    //Set fades to match channels
    for(Fade f : fades)
    {
      for(int i = 0; i < f.getChannels().length; i++)
      {
        if(f.getLevels()[i].getLevel() == null && f.getChannels()[i].isPatched())
          continue; //no level

        activeFades[f.getChannels()[i].getPatch()] = f;
      }
    }
  }

  public void go(Fade[] fades)
  {
    prepareFade(fades);

    for(int i = 0; i < fades.length; i++)
    {
      ScheduledFuture<FadeThread> future = null;
      FadeThread fader = new FadeThread();
      fader.engine = this;
      fader.fade = fades[i];
      ScheduledExecutorService timer =  Executors.newScheduledThreadPool(1);

      future = (ScheduledFuture<FadeThread>) timer.scheduleAtFixedRate(fader, 0, 10, TimeUnit.MILLISECONDS);
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
        //Force snapshop
        
        end.cancel(true);
        double stepsPerSecond = count/(double)((double) fade.getTime()/ (double)1000);
        System.out.println("[FORCE FADE] There were " + count + " steps for a rate of " + stepsPerSecond + "Hz refresh | Cue time was: " + fade.getTime());
      }
      else
      {
        for(int i = 0; i < channels.length; i++)
        {
          if(patch[i] == null)
            continue;
          update[patch[i]] = engine.calculateLevel(fade, i, original[i], elapsed, fade.getTime());
        }
        
        commit(update);
      }
    }
  }
}
