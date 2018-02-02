package com.kartoflane.ftl.layout.located;

import com.kartoflane.ftl.layout.LOType;
import com.kartoflane.ftl.layout.SingleValueLayoutObject;


public class LocatedSingleValueLayoutObject extends SingleValueLayoutObject
	implements LocatedLayoutObject
{
	private final int line;


	public LocatedSingleValueLayoutObject( LOType type, int line, int i )
	{
		super( type, i );
		this.line = line;
	}

	@Override
	public int getLine()
	{
		return line;
	}
}
