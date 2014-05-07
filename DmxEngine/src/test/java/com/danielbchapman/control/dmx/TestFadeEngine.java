package com.danielbchapman.control.dmx;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.danielbchapman.code.Pair;
import com.lightassistant.utility.Utility;

public class TestFadeEngine
{
  public static void main(String ... args) throws RecordException, InterruptedException
  {
    new TestFadeEngine().testListsAndFades();
    Thread.sleep(10000);
    System.exit(0);
  }
  @SuppressWarnings("unchecked")
  @Test
  public void testListsAndFades() throws RecordException
  {
    CueList main = new CueList();
    main.addCue(new BigDecimal(12));
    main.addCue(new BigDecimal(13));
    Cue a = main.find(new BigDecimal(12));
    Cue b = main.find(new BigDecimal(13));
    
    //Basic Tests
    Assert.assertTrue(a != null);
    Assert.assertTrue(b != null);
    Assert.assertTrue(main.find(new BigDecimal(11)) == null);
    
    ArrayList<Channel> channels = new ArrayList<Channel>();
    ArrayList<Pair<Channel, Level>> levels = new ArrayList<>();
    for(int i = 0; i < 256; i++)
    {
      Channel c = new Channel(i, 100+i);
      Level l = new Level();
      channels.add(c);
      levels.add(new Pair(c, l));
    }
    
    a.update(levels);
    System.out.println(a);    
    for(int i = 0; i < 10; i++)
    {
      levels.get(i).getTwo().setLevel(i * 10);
    }
    
    b.update(levels);
    Level levelb = b.getLevels().get(channels.get(2));
    Level levela = a.getLevels().get(channels.get(2));

    System.out.println(main);
    
    Assert.assertTrue(Integer.valueOf(20).equals(levelb.getLevel()));
    Assert.assertTrue(!Integer.valueOf(20).equals(levela.getLevel()));//Ref check
    
    //Test Fades (this just makes sure it doesn't fall on its face.
    FadeEngine engine = new FadeEngine();
    Fade[] go = engine.createFades(b);
    engine.prepareFade(go);
    int testIndex = -1;
    for(int i = 0; i < go[0].getLevels().length; i++)
    {
      if(Utility.compareToNullSafe(20, go[0].getLevels()[i].getLevel()) == 0)
      {
        testIndex = i;
        break;
      }
    }
    
    int calc = engine.calculateLevel(go[0], testIndex, 0, 2500, 5000);// ~ 10;
    Assert.assertTrue("Assuming 10 ->" + calc + " using index -> " + testIndex, Utility.compareToNullSafe(10, calc) == 0);
    
    //Fire fades test
    engine.go(go);
  }
}
