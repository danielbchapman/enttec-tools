package com.danielbchapman.dmx;

public class FaderChannel {
	
	Universe universe;
	int patch = -1;
	int level = 0;
	Fader fader;
	
	boolean isFading = false;
	public FaderChannel(Universe u, int patch){
		universe = u;
		this.patch = patch;
	}
	
	public void fade(int level, long time){
		
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setLevel(int i){
		level = i;
	}
	
	//////////////////
	//THREADED FADE
	//////////////////
	private class Fader extends Thread{
		long time;
		int oldValue = 0;
		int newValue = 0;
		
		Fader(){
			
		}
		
		public void fade(int value, long time){
			isFading = false; //Stop Previous Fade
			this.time = time;
			newValue = value;
			
			this.start(); //Fade...
		}
		
		public void run(){
			//Calculate fade time
			int currentValue = oldValue;
			int numberOfSteps;
			
			if(oldValue > newValue){
				numberOfSteps = oldValue - newValue;
			} else {
				numberOfSteps = newValue - oldValue;
			}
			
			if(numberOfSteps != 0){
				long sleepTime = time/(long)numberOfSteps; //Step Time
				////////////
				//FADE LEVEL
				////////////
				isFading = true;
				while(isFading){
					if(currentValue < 0){
						//Edge Case
						setLevel(0);
						break;
					}
					if(currentValue > 255){
						//Edge Case
						setLevel(255);
						break;
					}
					setLevel(currentValue++);
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						setLevel(newValue);
						break;
					}
				}	
			} else {
				setLevel(newValue);
			}
			
			////////////////
			//COMPLETE FADE
			////////////////
			isFading = false;
			oldValue = currentValue;
			
		}
	}
}
