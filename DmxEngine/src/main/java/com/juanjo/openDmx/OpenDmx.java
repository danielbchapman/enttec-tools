package com.juanjo.openDmx;

public class OpenDmx {

	static {
		System.loadLibrary("opendmx");
	}
	
	public static final int OPENDMX_TX=0;
	public static final int OPENDMX_RX=1;
	
	native public static boolean connect(int _mode);
	native public static boolean disconnect();
	
	native public static void setValue(int _channel, int _data);
	native public static int getValue(int _channel);
}
