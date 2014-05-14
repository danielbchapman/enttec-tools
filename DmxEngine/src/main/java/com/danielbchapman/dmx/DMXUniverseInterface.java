package com.danielbchapman.dmx;

public interface DMXUniverseInterface {
	
	//Naming and Identifiers
	/**
	 * Sets the name (String) of the universe.
	 * @param s
	 */
	public void setName(String s);
	
	/**
	 * @return String (Name of Universe)
	 */
	public String getName();
	
	//Data Setting/Retrieving Methods
	/**
	 * Returns the number of channels in this universe (not necessarily 512).
	 * @return int (number of channels in this universe)
	 */
	public int getMaxChannels();

	/**
	 * Sets the max channels that this universe can hold (max 512) this 
	 * is via the standard DMX protocol.
	 */
	
	public void setMaxChannels(int i);
	/**
	 * Returns an array of all values in the universe at the 
	 * time of the method call.
	 * @return int[maxChannelValue] of the DMX values
	 */
	public int getAddress(int i);
	
	/**
	 * Sets the value of a specific address to the new value.
	 * USEAGE: setAddress(0, 255); Sets the the value of address 0 to 255 (full)
	 * 
	 * standard usage "Dimmer 1 @ FULL"
	 * 
	 * @param address
	 * @param value
	 */
	public void setAddress(int address, int value);
	
	

}
