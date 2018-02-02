package com.kartoflane.ftl.layout.located;

import com.kartoflane.ftl.layout.DefaultFTLLayoutFactory;
import com.kartoflane.ftl.layout.DoorLayoutObject;
import com.kartoflane.ftl.layout.EllipseLayoutObject;
import com.kartoflane.ftl.layout.LOType;
import com.kartoflane.ftl.layout.RoomLayoutObject;
import com.kartoflane.ftl.layout.SingleValueLayoutObject;


/**
 * Extension of the default factory, which constructs located objects
 * when the data is sufficient.
 * 
 * @author kartoFlane
 *
 */
public class LocatedFTLLayoutFactory extends DefaultFTLLayoutFactory
{
	@Override
	public DoorLayoutObject door( int line, int x, int y, int lid, int rid, boolean h )
	{
		return new LocatedDoorLayoutObject( line, x, y, lid, rid, h ? 0 : 1 );
	}

	@Override
	public RoomLayoutObject room( int line, int id, int x, int y, int w, int h )
	{
		return new LocatedRoomLayoutObject( line, id, x, y, w, h );
	}

	@Override
	public EllipseLayoutObject ellipse( int line, int maj, int min, int x, int y )
	{
		return new LocatedEllipseLayoutObject( line, maj, min, x, y );
	}

	@Override
	public SingleValueLayoutObject xOffset( int line, int i )
	{
		return new LocatedSingleValueLayoutObject( LOType.X_OFFSET, line, i );
	}

	@Override
	public SingleValueLayoutObject yOffset( int line, int i )
	{
		return new LocatedSingleValueLayoutObject( LOType.Y_OFFSET, line, i );
	}

	@Override
	public SingleValueLayoutObject horizontal( int line, int i )
	{
		return new LocatedSingleValueLayoutObject( LOType.HORIZONTAL, line, i );
	}

	@Override
	public SingleValueLayoutObject vertical( int line, int i )
	{
		return new LocatedSingleValueLayoutObject( LOType.VERTICAL, line, i );
	}
}
