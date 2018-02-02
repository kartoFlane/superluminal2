package com.kartoflane.superluminal2.mvc.views.props;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

import com.kartoflane.superluminal2.mvc.controllers.props.ArcPropController;


public class ArcPropView extends PropView
{
	@Override
	public void paintControl( PaintEvent e )
	{
		if ( alpha > 0 ) {
			ArcPropController apc = (ArcPropController)getController();
			if ( apc.getPaintRim() ) {
				drawArc( e, apc.getX(), apc.getY(), apc.getW(), apc.getH(), apc.getStartAngle(), apc.getArcSpan(), backgroundColor, alpha );
			}
			else {
				paintArc( e, apc.getX(), apc.getY(), apc.getW(), apc.getH(), apc.getStartAngle(), apc.getArcSpan(), backgroundColor, alpha );
			}
		}
	}

	protected void paintArc( PaintEvent e, int x, int y, int w, int h, int startAngle, int span, Color backgroundColor, int alpha )
	{
		if ( backgroundColor != null ) {
			Color prevBgColor = e.gc.getBackground();
			int prevAlpha = e.gc.getAlpha();

			e.gc.setBackground( backgroundColor );
			e.gc.setAlpha( alpha );

			e.gc.fillArc( x - w / 2, y - h / 2, w, h, startAngle, span );

			e.gc.setBackground( prevBgColor );
			e.gc.setAlpha( prevAlpha );
		}
	}

	protected void drawArc( PaintEvent e, int x, int y, int w, int h, int startAngle, int span, Color borderColor, int alpha )
	{
		if ( borderColor != null ) {
			Color prevFgColor = e.gc.getForeground();
			int prevAlpha = e.gc.getAlpha();

			e.gc.setForeground( backgroundColor );
			e.gc.setAlpha( alpha );

			e.gc.drawArc( x - w / 2, y - h / 2, w, h, startAngle, span );

			e.gc.setForeground( prevFgColor );
			e.gc.setAlpha( prevAlpha );
		}
	}
}
