package com.kartoflane.ftl.layout;

/**
 * The default implementation of the FTLLayoutFactory interface.
 * 
 * @author kartoFlane
 *
 */
public class DefaultFTLLayoutFactory
	implements FTLLayoutFactory
{
	@Override
	public DoorLayoutObject door( int x, int y, int lid, int rid, boolean horizontal )
	{
		return new DoorLayoutObject( x, y, lid, rid, horizontal ? 0 : 1 );
	}

	@Override
	public RoomLayoutObject room( int id, int x, int y, int w, int h )
	{
		return new RoomLayoutObject( id, x, y, w, h );
	}

	@Override
	public EllipseLayoutObject ellipse( int maj, int min, int x, int y )
	{
		return new EllipseLayoutObject( maj, min, x, y );
	}

	@Override
	public SingleValueLayoutObject xOffset( int i )
	{
		return new SingleValueLayoutObject( LOType.X_OFFSET, i );
	}

	@Override
	public SingleValueLayoutObject yOffset( int i )
	{
		return new SingleValueLayoutObject( LOType.Y_OFFSET, i );
	}

	@Override
	public SingleValueLayoutObject horizontal( int i )
	{
		return new SingleValueLayoutObject( LOType.HORIZONTAL, i );
	}

	@Override
	public SingleValueLayoutObject vertical( int i )
	{
		return new SingleValueLayoutObject( LOType.VERTICAL, i );
	}

	@Override
	public DoorLayoutObject door( int line, int x, int y, int lid, int rid, boolean h )
	{
		return door( x, y, lid, rid, h );
	}

	@Override
	public RoomLayoutObject room( int line, int id, int x, int y, int w, int h )
	{
		return room( id, x, y, w, h );
	}

	@Override
	public EllipseLayoutObject ellipse( int line, int maj, int min, int x, int y )
	{
		return ellipse( maj, min, x, y );
	}

	@Override
	public SingleValueLayoutObject xOffset( int line, int i )
	{
		return xOffset( i );
	}

	@Override
	public SingleValueLayoutObject yOffset( int line, int i )
	{
		return yOffset( i );
	}

	@Override
	public SingleValueLayoutObject horizontal( int line, int i )
	{
		return horizontal( i );
	}

	@Override
	public SingleValueLayoutObject vertical( int line, int i )
	{
		return vertical( i );
	}
}
