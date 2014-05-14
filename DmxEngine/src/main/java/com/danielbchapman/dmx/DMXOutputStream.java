package com.danielbchapman.dmx;


/**
 ***************************************************************************** 
 * A thread that writes DMX from a specific universe at the maximum possible 
 * baud rate available through the DMX Protocol. 
 ***************************************************************************** 
 * Copyright Daniel B. Chapman 2008 | @author Daniel B. Chapman 
 *****************************************************************************
 */
public abstract class DMXOutputStream extends Thread {
	Universe universe;
	private boolean isRunning = false;
	private long refreshRateInMiliSeconds = 46; //Max DMX Refresh rate..
	
	DMXOutputStream(Universe u){
		universe = u;
		startDmx();
	}
	
	/**
	 * @return the refreshRateInMiliSeconds
	 */
	public long getRefreshRateInMiliSeconds() {
		return refreshRateInMiliSeconds;
	}
	
	public Universe getUniverse(){
		return universe;
	}
	
	public void run(){
		//Write the DMX
		while(isRunning){
			int iterator = universe.getMaxChannels();
			for(int i = 0; i < iterator; i++){
				//Write the DMX
				writeChannel(i, universe.getAddress(i));
			}
			
			//Sleep @ refresh rate
			try {
				Thread.sleep(refreshRateInMiliSeconds);
			} catch (InterruptedException e) {
				//Doesn't really matter
			}
		}
	}
	/**
	 * @param refreshRateInMiliSeconds the refreshRateInMiliSeconds to set
	 */
	public void setRefreshRateInMiliSeconds(long refreshRateInMiliSeconds) {
		this.refreshRateInMiliSeconds = refreshRateInMiliSeconds;
	}
	
	public void setUniverse(Universe u){
		if(isRunning){
			this.stopDmx();
			this.universe = u;
			startDmx();
		} else {
			this.universe = u;
		}
	}
	
	public void startDmx(){
		isRunning = true;
		start();
		
	}

	public void stopDmx(){
		isRunning = false;
	}

	public abstract void writeChannel(int channel, int value);
}
