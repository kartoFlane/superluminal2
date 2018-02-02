package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.Polygon;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.utils.Utils;


public class RoomView extends BaseView
{
	public static final int RESIZE_HANDLE_LENGTH = 19;
	public static final RGB WALL_RGB = new RGB( 0, 0, 0 );
	public static final RGB FLOOR_RGB = new RGB( 230, 225, 220 );

	private Color gridColor = null;
	private Color handleColor = null;
	private Polygon[] resizeHandles = null;


	public RoomView()
	{
		super();

		gridColor = Cache.checkOutColor( this, CellView.GRID_RGB );
		handleColor = Cache.checkOutColor( this, HIGHLIGHT_RGB );

		setBorderColor( WALL_RGB );
		setDefaultBorderColor( WALL_RGB );
		setBackgroundColor( FLOOR_RGB );
		setDefaultBackgroundColor( FLOOR_RGB );
		setBorderThickness( 2 );

		resizeHandles = new Polygon[4];
		Point[] points = new Point[] { new Point( 0, 0 ), new Point( 0, 0 ), new Point( 0, 0 ) };
		points[1].x += RESIZE_HANDLE_LENGTH;
		points[2].y += RESIZE_HANDLE_LENGTH;

		resizeHandles[0] = new Polygon( points );
		resizeHandles[1] = new Polygon( points );
		resizeHandles[2] = new Polygon( points );
		resizeHandles[3] = new Polygon( points );
		resizeHandles[1].rotate( (float)( Math.PI / 2 ), 0, 0 );
		resizeHandles[2].rotate( (float)( Math.PI / 2 ) * 3, 0, 0 );
		resizeHandles[3].rotate( (float)Math.PI, 0, 0 );
	}

	/**
	 * Visibility for the controller, to allow to check whether a triangle was clicked.
	 * 
	 * @return array of length 4, containing triangles representing the resize handles for the room.
	 */
	public Polygon[] getResizeHandles()
	{
		return resizeHandles;
	}

	@Override
	public void paintControl( PaintEvent e )
	{
		if ( alpha > 0 ) {
			paintBackgroundSquare( e, backgroundColor, alpha );

			// draw fake grid lines inside of rooms
			if ( controller.getW() > ShipContainer.CELL_SIZE || controller.getH() > ShipContainer.CELL_SIZE ) {
				Color prevFgColor = e.gc.getForeground();
				e.gc.setForeground( gridColor );
				paintFakeGridLines( e );
				e.gc.setForeground( prevFgColor );
			}

			paintBorderSquare( e, borderColor, borderThickness, alpha );

			if ( controller.isSelected() && !controller.isPinned() )
				paintResizeHandles( e, handleColor, alpha / 2 );
		}
	}

	private void paintFakeGridLines( PaintEvent e )
	{
		if ( controller.getW() > ShipContainer.CELL_SIZE )
			for ( int i = 1; i < controller.getW() / ShipContainer.CELL_SIZE; i++ )
			e.gc.drawLine(
				controller.getX() - controller.getW() / 2 + i * ShipContainer.CELL_SIZE,
				controller.getY() - controller.getH() / 2 + borderThickness,
				controller.getX() - controller.getW() / 2 + i * ShipContainer.CELL_SIZE,
				controller.getY() + controller.getH() / 2 - borderThickness
			);
		if ( controller.getH() > ShipContainer.CELL_SIZE )
			for ( int i = 1; i < controller.getH() / ShipContainer.CELL_SIZE; i++ )
			e.gc.drawLine(
				controller.getX() - controller.getW() / 2 + borderThickness,
				controller.getY() - controller.getH() / 2 + i * ShipContainer.CELL_SIZE,
				controller.getX() + controller.getW() / 2 - borderThickness,
				controller.getY() - controller.getH() / 2 + i * ShipContainer.CELL_SIZE
			);
	}

	private void paintResizeHandles( PaintEvent e, Color color, int alpha )
	{
		int prevAlpha = e.gc.getAlpha();
		Color prevColor = e.gc.getBackground();

		e.gc.setAlpha( alpha );
		e.gc.setBackground( color );

		for ( int i = 0; i < 4; i++ )
			resizeHandles[i].fill( e );

		e.gc.setAlpha( prevAlpha );
		e.gc.setBackground( prevColor );
	}

	@Override
	public void dispose()
	{
		super.dispose();
		Cache.checkInColor( this, CellView.GRID_RGB );
		Cache.checkInColor( this, HIGHLIGHT_RGB );
		handleColor = null;
		gridColor = null;
	}

	@Override
	public void updateView()
	{
		if ( controller.isSelected() ) {
			setBorderColor( controller.isPinned() ? PIN_RGB : SELECT_RGB );
			setBackgroundColor( Utils.tint( defaultBackground, borderColor.getRGB(), 0.33 ) );
			setBorderThickness( 2 );
		}
		else if ( controller.isHighlighted() ) {
			setBorderColor( HIGHLIGHT_RGB );
			setBackgroundColor( defaultBackground );
			setBorderThickness( 3 );
		}
		else {
			setBorderColor( defaultBorder );
			setBackgroundColor( defaultBackground );
			setBorderThickness( 2 );
		}

		updateResizeHandles();
	}

	private void updateResizeHandles()
	{
		Rectangle bounds = controller.getBounds();
		// redefine triangles for resize handles
		// top left corner
		int x = bounds.x + 1;
		int y = bounds.y + 1;
		resizeHandles[0].setLocation( x + RESIZE_HANDLE_LENGTH / 2, y + RESIZE_HANDLE_LENGTH / 2 );

		// top right corner
		x = bounds.x + controller.getW() + borderThickness - 1;
		resizeHandles[1].setLocation( x - RESIZE_HANDLE_LENGTH / 2, y + RESIZE_HANDLE_LENGTH / 2 );

		// bottom right corner
		y = bounds.y + controller.getH() + borderThickness - 1;
		resizeHandles[3].setLocation( x - RESIZE_HANDLE_LENGTH / 2, y - RESIZE_HANDLE_LENGTH / 2 );
		// Yes, this is supposed to stay out of order -- this way we can access the triangle
		// on the opposite corner via resizeHandles[3 - id]

		// bottom left corner
		x = bounds.x + 2;
		resizeHandles[2].setLocation( x + RESIZE_HANDLE_LENGTH / 2, y - RESIZE_HANDLE_LENGTH / 2 );
	}
}
