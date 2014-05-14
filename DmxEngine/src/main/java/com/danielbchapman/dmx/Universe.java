package com.danielbchapman.dmx;

import java.util.Arrays;

/**
 ***************************************************************************** 
 * A class that represents a 512 byte array of DMX values. Each value is written
 * as an unsigned byte that can be individually altered. It is contains methods
 * to write out a packet of DMX at any point in its stack and works with 
 * controls to provide cross fading and other funtionality.
 ***************************************************************************** 
 * Copyright Daniel B. Chapman 2008 | @author Daniel B. Chapman 
 *****************************************************************************
 */
public class Universe implements DMXUniverseInterface{
	//---------------------GLOBAL VARIABLES---------------------//
	//---------------------LOCAL VARIABLES---------------------//
	String name;
	int[] addressValues = new int[512];
	private int highestActiveAddress = 512;
	//---------------------CONSTRUCTOR---------------------//
	public Universe(String name, int highestActiveAddress ){
		this.name = name;
		highestActiveAddress = 0;
		
		for(int i = 0; i < DMX.MAX_VALUES; i++){
			addressValues[i] = 0;
		}
		
	}	
	public String toString(){
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < addressValues.length; i++){
			b.append(i);
			b.append("@");
			b.append(addressValues[i]);
			b.append("\t");
		}
		return b.toString();
	}
	@Override
	public int getAddress(int i) {
		if(i < 0 || i > highestActiveAddress){
			return -1;
		} else {
			int ret = addressValues[i];
			return ret;
		}
	}
	@Override
	public int getMaxChannels() {
		int ret = highestActiveAddress;
		return ret;
	}
	
	@Override
	public String getName() {
		String ret = name;
		return ret;
	}
	
	@Override
	public void setAddress(int address, int value) {
		if(address > -1 && address < highestActiveAddress){
			if(value > -1 && value < 255){
				addressValues[address] = value;
			}
		}
	}
	
	@Override
	public void setMaxChannels(int i) {
		if(i > -1 && i < 512){
			highestActiveAddress = i;
			int[] newArray = Arrays.copyOf(addressValues, highestActiveAddress);
			addressValues = newArray;
		}
	}
	
	@Override
	public void setName(String s) {
		if(s != null){
			name = s;
		}
	}
	
}
