package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;


public class ImageView extends BaseView
{
	/** Width of the drawn image. Actual image is resized to fit this. */
	protected int w = -1;
	/** Height of the drawn image. Actual image is resized to fit this. */
	protected int h = -1;


	@Override
	public void paintControl( PaintEvent e )
	{
		if ( alpha > 0 ) {
			// Can't have negative values, so default to cached size if that's the case
			int tw = w < 0 ? cachedImageBounds.width : w;
			int th = h < 0 ? cachedImageBounds.height : h;
			paintImageResize(
				e, image, cachedImageBounds, controller.getX() - tw / 2, controller.getY() - th / 2,
				tw, th, alpha
			);
			paintBorderSquare( e, borderColor, borderThickness, 255 );
		}
	}

	public void setSize( int w, int h )
	{
		this.w = w;
		this.h = h;
	}

	public void setSize( Point p )
	{
		w = p.x;
		h = p.y;
	}

	public Point getSize()
	{
		return new Point( w, h );
	}

	@Override
	public void updateView()
	{
		setSize( controller.getSize() );

		if ( controller.isSelected() ) {
			setBorderColor( controller.isPinned() ? PIN_RGB : SELECT_RGB );
			setBackgroundColor( defaultBackground );
			setBorderThickness( 2 );
		}
		else if ( controller.isHighlighted() ) {
			setBorderColor( controller.isPinned() ? PIN_HIGHLIGHT_RGB : HIGHLIGHT_RGB );
			setBackgroundColor( defaultBackground );
			setBorderThickness( 3 );
		}
		else {
			setBorderColor( defaultBorder );
			setBackgroundColor( defaultBackground );
			setBorderThickness( 2 );
		}
	}
}
