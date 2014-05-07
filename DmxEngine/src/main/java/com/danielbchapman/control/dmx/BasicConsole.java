package com.danielbchapman.control.dmx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.s5g.util.Dmx;

public class BasicConsole extends JFrame
{
  private static Dmx DMX;
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static void main(String... args)
  {
    new BasicConsole();
  }

  public BasicConsole()
  {
    setLayout(new BorderLayout());
    JButton off = new JButton("OFF");
    off.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        for (int i = 2; i < 511; i++)
          DMX.setDMXChannel(i, 0, false);
        DMX.setDMXChannel(511, 0, true);
      }
    });

    JButton connect = new JButton("Connect");
    connect.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        if (DMX == null)
        {
          DMX = new Dmx();
          if (DMX.open("COM3"))
          {
            DMX.getSerialNumber();
            DMX.getParameters();
            DMX.setDMXChannel(0, 255, false);
            DMX.setDMXChannel(1, 255, false);
            DMX.setDMXChannel(2, 50, false);
            DMX.setDMXChannel(4, 255, true);
          }
          else
          {
            System.out.println("Failure in opening com4");
          }
        }
      }
    });
    JButton print = new JButton("Print");
    print.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        System.out.println("DATA IS:\n" + DMX.printData());
      }});
    
    JButton on = new JButton("ON");
    on.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        for (int i = 2; i < 511; i++)
          DMX.setDMXChannel(i, 255, false);
        DMX.setDMXChannel(511, 255, true);
      }
    });

    add(off, BorderLayout.NORTH);
    add(on, BorderLayout.SOUTH);
    add(connect, BorderLayout.CENTER);
    add(print, BorderLayout.EAST);
    add(create("BLUE", 0, 255, 1, 255, 2, 0, 3, 0, 4, 255), BorderLayout.WEST);
    
    setSize(800,600);
    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
  
  public JButton create(String name, final int ... values)
  {
    JButton ret = new JButton(name);
    ret.addActionListener(new ActionListener()
    {
      
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        for(int i = 0; i < values.length; i+=2)
          DMX.setDMXChannel(values[i], values[i+1], true);      }
    }
    );
    
    return ret;
  }

}
