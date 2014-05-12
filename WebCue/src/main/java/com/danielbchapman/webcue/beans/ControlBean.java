package com.danielbchapman.webcue.beans;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.xml.parsers.ParserConfigurationException;

import lombok.Getter;
import lombok.Setter;

import org.xml.sax.SAXException;

import com.danielbchapman.groups.Groups.GroupFormatException;
import com.danielbchapman.web.utility.Utility;
import com.danielbchapman.web.utility.WebUtil;

@ManagedBean(name = "controlBean")
@ViewScoped
public class ControlBean implements Serializable
{
  @Getter
  @Setter
  private String showName;

  public String[] getShows()
  {
    return CueStack.getShows();
  }
  
  public void saveShow(ActionEvent evt)
  {
    if(Utility.isEmptyOrNull(showName))
      return;
    
    CueStack.save(showName);
    WebUtil.redirect("/cues.xhtml");
  }
  
  public void loadShow(ActionEvent evt) throws FileNotFoundException, ParserConfigurationException, GroupFormatException, SAXException, IOException
  {
    String showFile = (String) evt.getComponent().getAttributes().get("showFile");
    
    if(!Utility.isEmptyOrNull(showFile))
    {
      System.out.println("Loading file...");
      CueStack.load(CueStack.rootDir + showFile);
      WebUtil.redirect("/cues.xhtml");
    }
  }
  
  public boolean isConnected()
  {
    return CueStack.isOpen();
  }
  
  public void connect(ActionEvent evt)
  {
    if(!CueStack.connect())
      throw new IllegalStateException("Could not connect to Enttec device");
  }
}
