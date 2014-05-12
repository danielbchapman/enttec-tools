package com.danielbchapman.webcue.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

import com.danielbchapman.web.utility.WebUtil;

@SessionScoped
@ManagedBean(name = "sessionBean")
public class SessionBean implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Getter
  @Setter
  private String password;
  
  public boolean isAuthenticated()
  {
    return "lightsup".equals(password);
  }
  
  public String contextUrl(String url)
  {
    return WebUtil.contextUrl(url);
  }
  
  public String getCopyYear()
  {
    return "Copyright Daniel B. Chapman " + new SimpleDateFormat("yyyy").format(new Date());
  }
}
