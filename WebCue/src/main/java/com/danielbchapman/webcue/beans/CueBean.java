package com.danielbchapman.webcue.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import lombok.Getter;
import lombok.Setter;

import com.danielbchapman.code.Pair;
import com.danielbchapman.control.dmx.Channel;
import com.danielbchapman.control.dmx.Cue;
import com.danielbchapman.control.dmx.Level;
import com.danielbchapman.web.utility.Safe;
import com.danielbchapman.web.utility.WebUtil;

@SessionScoped
@ManagedBean(name = "cueBean")
public class CueBean implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Getter
  @Setter
  ArrayList<ArrayList<Pair<Channel, Double>>> editLevels;
  
  @Getter
  @Setter
  private boolean enableEdits;
  
  @Getter
  @Setter
  private boolean enableOut;
  
  @Getter
  @Setter
  private String addCueNumber;
  
  @Getter
  @Setter
  Cue edit;
  @Getter
  @Setter
  private BigDecimal gotoCueNumber;
  
  public void gotoCueZero(ActionEvent evt)
  {
    if(!enableOut)
      return;
    enableOut = false;
    CueStack.gotoCueZero();
  }
  public BigDecimal[] getCueNumbers()
  {  
    BigDecimal[] nums = new BigDecimal[CueStack.CUES.size()];
    for(int i = 0; i < CueStack.CUES.size(); i++)
      nums[i] = CueStack.CUES.get(i).getCueNumber();
    return nums;
  }
  
  public ArrayList<Pair<Channel, Integer>> getStatus()
  {
    return CueStack.getStatus();
  }
  
  public Cue getCue()
  {
    return CueStack.getCurrentCue();
  }
  
  public Cue getNextCue()
  {
    return CueStack.getNextCue();
  }
  
  public Cue getLastCue()
  {
    return CueStack.getLastCue();
  }
  
  public ArrayList<Cue> getStack()
  {
    return CueStack.CUES;
  }
  
  public void gotoCue(ActionEvent evt)
  {
    if(gotoCueNumber != null)
    {
      Cue c = CueStack.find(gotoCueNumber);
      if(c != null)
        CueStack.gotoCue(c);
    }
    gotoCueNumber = null;
  }
  
  private void editCue(Cue cue, String route)
  {
    if(cue != null)
    {
      edit = cue.clone();
      editLevels = createLevels(edit); 
      if(route != null)
        WebUtil.redirect(route);
    }
  }
  private void edit(ActionEvent evt, String route)
  {
    BigDecimal cueNumber = (BigDecimal) evt.getComponent().getAttributes().get("cueNumber");
    if(cueNumber != null)
    {
      Cue cue = CueStack.find(cueNumber);
      editCue(cue, route);
    }
  }
  
  public void editLive(ActionEvent evt)
  {
    edit(evt, "/live.xhtml");
  }
  
  public void editBlind(ActionEvent evt)
  {
    edit(evt, "/blind.xhtml");
  }
  
  public void go(ActionEvent evt)
  {
    CueStack.go();
  }
  
  public void back(ActionEvent evt)
  {
    CueStack.back();
  }
  
  public ArrayList<ArrayList<Pair<Channel, Double>>> createLevels(Cue cue)
  {
    //FIXME add "tracked level"
    editLevels = null;
    ArrayList<Pair<Channel, Integer>> active = new ArrayList<>();
    
    for(Channel c : CueStack.CHANNELS)
    {
      Level level = cue.getLevels().get(c);
      if(level == null)
        level = new Level();
      active.add(Pair.create(c.clone(), level.getLevel()));
    }
    
    return createLevels(active, 10);
  }
  
  public ArrayList<ArrayList<Pair<Channel, Double>>> createLevels(ArrayList<Pair<Channel, Integer>> status, int split)
  {
    editLevels = new ArrayList<>();
    int count = 0;
    
    for(Pair<Channel, Integer> p : status)
    {
      if(count % 10 == 0)
        editLevels.add(new ArrayList<Pair<Channel, Double>>());
      
      Double val = null;
      if(p.getTwo() != null)
        val = toPercent(p.getTwo());
      editLevels.get(editLevels.size() - 1).add(Pair.create(p.getOne().clone(), val));
      count++;
    }
    
    return editLevels;
  }
  
  public void clearLevels(ActionEvent evt)
  {
    editLevels = null;
  }
  
  public String getChannelOutput(ArrayList<Pair<Channel, Integer>> status)
  {
    StringBuilder out = new StringBuilder();
    
    out.append("<table>");
    out.append("<tbody>");
    out.append("<tr>");
    for(int i = 0; i < status.size(); i++)
    {
      Pair<Channel, Integer> el = status.get(i);
      out.append("<td>");
      out.append("<div class=\"channel-status\">");
      out.append("<label class=\"channel\">");
      out.append(el.getOne().getId());
      out.append("</label>");
      out.append("<label class=\"level\">");
      out.append(el.getTwo() == null ? "" : toPercent(el.getTwo()));
      out.append("</label>");
      out.append("</div>");
      out.append("</td>");
      if(((i + 1) % 10) == 0)
        out.append("</tr>\n<tr>");
    }
    out.append("</tr>");;
    out.append("</tbody>");
    out.append("</table>");
    return out.toString();
  }
  
  public void saveEdits(ActionEvent evt)
  {
    if(edit != null)
    {
      if(editLevels != null)
      {
        HashMap<Channel, Level> updated = new HashMap<>();
        for(ArrayList<Pair<Channel, Double>> list : editLevels)
          for(Pair<Channel, Double> p : list)
          {
            Level level = new Level();
            Number two = p.getTwo();
            level.setLevel(two == null ? null : toDmx(two.intValue()));
            updated.put(p.getOne().clone(), level);
          }
        
        edit.setLevels(updated);
      }
      
      CueStack.recordCue(edit);
      WebUtil.redirect("/cues.xhtml");
    }
  }
  //FIXME These are lossy---
  public static double toPercent(int value)
  {
    double x = new Double(value);
    return x / 255.0 * 100.0;
  }
  
  public static int toDmx(double percent)
  {
    
    double convert = percent / 100.00 * 255.00;
    int out = Double.valueOf(convert).intValue();
//    System.out.println("Input ->" + percent + " output ->" + out);
    return out;
  }
  
  public void addCue(ActionEvent evt)
  {
    BigDecimal decimal = Safe.parseBigDecimal(addCueNumber);
    
    if(decimal == null)
      return;
    
    Cue existing = CueStack.find(decimal);
    if(existing != null)
      return; //FIXME add errors here...
    
    Cue add = new Cue(decimal);
    CueStack.recordCue(add);
    editCue(add, "/live.xhtml");
  }
}
