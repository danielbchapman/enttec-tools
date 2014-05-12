package com.danielbchapman.web.utility;

public interface Function<In, Out>
{
	public Out call(In in);
}
