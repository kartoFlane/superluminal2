package com.kartoflane.ftl.layout.located;

import com.kartoflane.ftl.layout.DoorLayoutObject;


public class LocatedDoorLayoutObject extends DoorLayoutObject
	implements LocatedLayoutObject
{
	private final int line;


	public LocatedDoorLayoutObject( int line, int x, int y, int lid, int rid, int h )
	{
		super( x, y, lid, rid, h );
		this.line = line;
	}

	@Override
	public int getLine()
	{
		return line;
	}
}
