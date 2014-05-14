package com.danielbchapman.dmx;

import java.util.Stack;

/**
 * This is the core class for the DMX interface, it has 
 * the core methods for maintaining the state of the system
 * and interpreting fades.
 * @author Daniel B. Chapman
 */
public class DmxUniverse {
	
	private Stack<Integer>[] universe;
	
	@SuppressWarnings("unchecked")
	public DmxUniverse()
	{
		universe = new Stack[512];
		for(int i = 0; i < universe.length; i++)
			universe[i] = new Stack<Integer>();
		
	}
	
	public int[] getUniverse()
	{
		int[] results = new int[universe.length];
		for(int i = 0; i < universe.length; i++)
		{
			Stack<Integer> stack = universe[i];

			if(stack.isEmpty())
				results[i] = 0;
			else 
			{
				//Fader algorithms here...
				results[i] = 128; //50%
			}
		}
		
		return results;
	}
}
