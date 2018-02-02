package com.kartoflane.superluminal2.mvc.controllers.props;

import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.events.SLDisposeEvent;
import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.models.BaseModel;
import com.kartoflane.superluminal2.mvc.views.props.TracerPropView;


public class TracerPropController extends PropController
{
	private AbstractController origin = null;
	private AbstractController target = null;
	private Rectangle cachedBounds = new Rectangle( 0, 0, 0, 0 );


	public TracerPropController( AbstractController origin, AbstractController target, String id )
	{
		super( origin, new BaseModel(), new TracerPropView(), id );

		setParent( null );
		setOrigin( origin );
		setTarget( target );
	}

	/**
	 * Tracer doesn't have a size of its own, instead uses the position of the origin and target
	 * to determine its size.<br>
	 * <br>
	 * This method will throw an {@link UnsupportedOperationException} when called.
	 */
	@Override
	public boolean setSize( int x, int y )
	{
		throw new UnsupportedOperationException( "Tracers don't have a size of their own." );
	}

	public void setOrigin( AbstractController ac )
	{
		if ( origin == ac )
			return;

		if ( origin != null ) {
			origin.removeListener( SLEvent.MOVE, this );
			origin.removeListener( SLEvent.DISPOSE, this );
		}

		if ( ac != null ) {
			ac.addListener( SLEvent.MOVE, this );
			ac.addListener( SLEvent.DISPOSE, this );
		}

		this.origin = ac;
	}

	public AbstractController getOrigin()
	{
		return origin;
	}

	public void setTarget( AbstractController ac )
	{
		if ( target == ac )
			return;

		if ( target != null ) {
			target.removeListener( SLEvent.MOVE, this );
			target.removeListener( SLEvent.DISPOSE, this );
		}

		if ( ac != null ) {
			ac.addListener( SLEvent.MOVE, this );
			ac.addListener( SLEvent.DISPOSE, this );
		}

		this.target = ac;
	}

	public AbstractController getTarget()
	{
		return target;
	}

	@Override
	public Rectangle getBounds()
	{
		Rectangle b = new Rectangle( 0, 0, 0, 0 );
		if ( origin == null || target == null )
			return b;

		b.x = Math.min( origin.getX(), target.getX() ) - getBorderThickness();
		b.y = Math.min( origin.getY(), target.getY() ) - getBorderThickness();
		b.width = Math.abs( target.getX() - origin.getX() ) + 2 * getBorderThickness();
		b.height = Math.abs( target.getY() - origin.getY() ) + 2 * getBorderThickness();

		return b;
	}

	@Override
	public void notifyLocationChanged( int x, int y )
	{
		redraw( cachedBounds );
		updateView();
		setVisible( origin != null && target != null );
		cachedBounds = getBounds();
	}

	@Override
	public void handleEvent( SLEvent e )
	{
		if ( e instanceof SLDisposeEvent )
			setVisible( false );
		super.handleEvent( e );
	}
}
