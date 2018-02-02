package com.kartoflane.ftl.layout;

/**
 * Factory interface for creation of FTL layout objects.
 * 
 * @author kartoFlane
 */
public interface FTLLayoutFactory
{
	public DoorLayoutObject door( int x, int y, int lid, int rid, boolean horizontal );

	public RoomLayoutObject room( int id, int x, int y, int w, int h );

	public EllipseLayoutObject ellipse( int maj, int min, int x, int y );

	public SingleValueLayoutObject xOffset( int i );

	public SingleValueLayoutObject yOffset( int i );

	public SingleValueLayoutObject horizontal( int i );

	public SingleValueLayoutObject vertical( int i );

	public DoorLayoutObject door( int line, int x, int y, int lid, int rid, boolean h );

	public RoomLayoutObject room( int line, int id, int x, int y, int w, int h );

	public EllipseLayoutObject ellipse( int line, int x, int y, int maj, int min );

	public SingleValueLayoutObject xOffset( int line, int i );

	public SingleValueLayoutObject yOffset( int line, int i );

	public SingleValueLayoutObject horizontal( int line, int i );

	public SingleValueLayoutObject vertical( int line, int i );
}
