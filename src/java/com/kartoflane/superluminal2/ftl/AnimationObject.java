package com.kartoflane.superluminal2.ftl;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.interfaces.Identifiable;
import com.kartoflane.superluminal2.utils.Utils;


public class AnimationObject implements Comparable<AnimationObject>, Identifiable
{
	private final String animName;

	private String sheetPath;
	private Point sheetSize = new Point( 0, 0 );
	private Point frameSize = new Point( 0, 0 );
	private Point mountOffset = new Point( 0, 0 );


	/**
	 * Creates a default animation object.
	 */
	public AnimationObject()
	{
		animName = "Default Animation";
		sheetPath = "cpath:/assets/weapon.png";
		sheetSize.x = frameSize.x = 16;
		sheetSize.y = frameSize.y = 47;
		mountOffset.x = 2;
		mountOffset.y = 36;
	}

	public AnimationObject( String name )
	{
		animName = name;
	}

	@Override
	public String getIdentifier()
	{
		return animName;
	}

	public String getAnimName()
	{
		return animName;
	}

	public Point getSheetSize()
	{
		return Utils.copy( sheetSize );
	}

	public void setSheetSize( int x, int y )
	{
		sheetSize.x = x;
		sheetSize.y = y;
	}

	public void setSheetSize( Point p )
	{
		setSheetSize( p.x, p.y );
	}

	public Point getFrameSize()
	{
		return Utils.copy( frameSize );
	}

	public void setFrameSize( int x, int y )
	{
		frameSize.x = x;
		frameSize.y = y;
	}

	public void setFrameSize( Point p )
	{
		setFrameSize( p.x, p.y );
	}

	public Point getMountOffset()
	{
		return Utils.copy( mountOffset );
	}

	public void setMountOffset( int x, int y )
	{
		mountOffset.x = x;
		mountOffset.y = y;
	}

	public void setMountOffset( Point p )
	{
		setMountOffset( p.x, p.y );
	}

	public String getSheetPath()
	{
		return sheetPath;
	}

	public void setSheetPath( String path )
	{
		if ( path == null )
			throw new IllegalArgumentException( "Path must not be null." );
		sheetPath = path;
	}

	@Override
	public int compareTo( AnimationObject o )
	{
		return animName.compareTo( o.animName );
	}

	@Override
	public boolean equals( Object o )
	{
		if ( o instanceof AnimationObject ) {
			AnimationObject other = (AnimationObject)o;
			return animName.equals( other.animName );
		}
		else
			return false;
	}
}
