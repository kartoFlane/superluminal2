package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;

import com.kartoflane.superluminal2.ftl.GlowObject;
import com.kartoflane.superluminal2.ftl.GlowSet.Glows;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;


public class GlowView extends BaseView
{
	public GlowView()
	{
		super();

		setImage( "cpath:/assets/station_glow.png" );
		setAlpha( 255 );
		setBorderColor( null );
		setBackgroundColor( null );
	}

	private ObjectModel getModel()
	{
		return (ObjectModel)model;
	}

	private GlowObject getGameObject()
	{
		return (GlowObject)getModel().getGameObject();
	}

	@Override
	public void paintControl( PaintEvent e )
	{
		if ( alpha > 0 ) {
			paintImage( e, image, cachedImageBounds, alpha );
			paintBorderSquare( e, borderColor, getBorderThickness(), alpha );
		}
	}

	@Override
	public void updateView()
	{
		if ( controller.isSelected() ) {
			setBorderColor( controller.isPinned() ? PIN_RGB : SELECT_RGB );
			setBackgroundColor( HIGHLIGHT_RGB );
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

		String path = getGameObject().getGlowSet().getImage( Glows.BLUE );
		if ( path == null )
			path = "cpath:/assets/station_glow.png";
		setImage( path );
	}
}
