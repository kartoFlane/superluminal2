package com.kartoflane.superluminal2.mvc.controllers.props;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.enums.Shapes;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.PropDataComposite;


public class OffsetPropController extends PropController
{
	public OffsetPropController( AbstractController parent, String id )
	{
		super( parent, id );

		setInheritVisibility( true );
		setBounded( true );
		setSelectable( true );

		setBorderColor( 0, 0, 0 );
		setImage( null );
		setBorderThickness( 3 );
		setAlpha( 255 );

		setShape( Shapes.POLYGON );
		setSize( 3 * ShipContainer.CELL_SIZE / 2, 3 * ShipContainer.CELL_SIZE / 2 );
	}

	@Override
	public boolean setLocation( int x, int y )
	{
		boolean result = super.setLocation( x, y );
		polygon.setLocation( getX(), getY() );
		return result;
	}

	@Override
	public boolean translate( int dx, int dy )
	{
		boolean result = super.translate( dx, dy );
		polygon.setLocation( getX(), getY() );
		return result;
	}

	/**
	 * @param rad
	 *            angle in radians, 0 = north
	 */
	@Override
	public void setRotation( float rad )
	{
		polygon.rotate( rad );
	}

	/**
	 * @param rad
	 *            angle in radians, 0 = north
	 */
	@Override
	public void rotate( float rad )
	{
		polygon.rotate( rad );
	}

	@Override
	public Point getPresentedLocation()
	{
		return new Point( getX() / getPresentedFactor(), getY() / getPresentedFactor() );
	}

	@Override
	public boolean contains( int x, int y )
	{
		return polygon.contains( x, y );
	}

	@Override
	public void select()
	{
		super.select();
		setBounded( true );
	}

	@Override
	public void deselect()
	{
		super.deselect();
		setBounded( false );
		ShipContainer container = Manager.getCurrentShip();
		container.updateBoundingArea();
		container.updateChildBoundingAreas();
	}

	@Override
	public DataComposite getDataComposite( Composite parent )
	{
		return new PropDataComposite( parent, this );
	}
}
