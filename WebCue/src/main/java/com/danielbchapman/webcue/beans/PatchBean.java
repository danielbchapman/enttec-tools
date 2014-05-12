package com.danielbchapman.webcue.beans;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

import com.danielbchapman.control.dmx.Channel;
import com.danielbchapman.web.utility.WebUtil;

@ManagedBean (name = "patchBean")
@ViewScoped
public class PatchBean implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private ArrayList<Channel> patch;
  
  public ArrayList<Channel> getPatch()
  {
    if(patch != null)
      return patch;
    
    patch = new ArrayList<>();
    for(Channel c : CueStack.CHANNELS)
      patch.add(c);
    
    return patch;
  }
  
  public void resetPatch(ActionEvent evt)
  {
    patch = null;
  }
  
  public void clearPatch(ActionEvent evnt)
  {
    patch = new ArrayList<>();
    CueStack.CHANNELS = patch;
  }
  
  public void addChannel(ActionEvent evt)
  {
    CueStack.CHANNELS.add(new Channel());
  }
  
  public void commitPatch(ActionEvent evt)
  {
    if(patch != null)
    {
      CueStack.CHANNELS = patch;
      WebUtil.redirect("/cues.xhtml");
    }
      
  }
  
  public void onCellEdit(CellEditEvent event) {
    Object oldValue = event.getOldValue();
    Object newValue = event.getNewValue();
     
    if(newValue != null && !newValue.equals(oldValue)) {
      System.out.println(newValue);
    }
  }
  public void onEdit(RowEditEvent event) {

  }
}
