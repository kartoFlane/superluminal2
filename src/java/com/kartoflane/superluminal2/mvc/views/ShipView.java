package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;


public class ShipView extends BaseView
{
	public ShipView()
	{
		super();

		setBorderColor( 0, 0, 0 );
		setBackgroundColor( 0, 255, 255 );
		setDefaultBorderColor( 0, 0, 0 );
		setDefaultBackgroundColor( 0, 255, 255 );
		setBorderThickness( 3 );
	}

	@Override
	public void setController( Controller controller )
	{
		this.controller = (ShipController)controller;
	}

	@Override
	public void paintControl( PaintEvent e )
	{
		if ( alpha > 0 ) {
			Color prevFgColor = e.gc.getForeground();

			paintBackgroundSquare( e, backgroundColor, alpha );
			paintBorderSquare( e, borderColor, getBorderThickness(), alpha );

			e.gc.setForeground( prevFgColor );
		}
	}

	@Override
	public void updateView()
	{
		setBackgroundColor( defaultBackground );
		if ( controller.isSelected() ) {
			setBorderColor( controller.isPinned() ? PIN_RGB : SELECT_RGB );
		}
		else if ( controller.isHighlighted() ) {
			setBorderColor( HIGHLIGHT_RGB );
		}
		else {
			setBorderColor( defaultBorder );
		}
	}
}
