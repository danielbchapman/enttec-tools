package com.danielbchapman.web.utility;

import java.io.Serializable;

public interface Action<In> extends Serializable{
	public void call(In in);
}
