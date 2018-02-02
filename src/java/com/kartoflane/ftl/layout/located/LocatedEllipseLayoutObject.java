package com.kartoflane.ftl.layout.located;

import com.kartoflane.ftl.layout.EllipseLayoutObject;


public class LocatedEllipseLayoutObject extends EllipseLayoutObject
	implements LocatedLayoutObject
{
	private final int line;


	public LocatedEllipseLayoutObject( int line, int maj, int min, int x, int y )
	{
		super( maj, min, x, y );
		this.line = line;
	}

	@Override
	public int getLine()
	{
		return line;
	}
}
