package com.danielbchapman.dmx;
/**
 * This class represents a 2D Function (General algebraic curve) that
 * allows a transformation from a linear value to another value (mapped at 8Bit).
 * @author Daniel B. Chapman
 *
 */
public abstract class Linear8BitFunction {

	public Linear8BitFunction(){
		//Creates an instance
	}
	
	public abstract byte returnValue(byte value);
}
