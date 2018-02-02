package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.ftl.AnimationObject;
import com.kartoflane.superluminal2.mvc.controllers.MountController;


public class MountView extends BaseView
{
	private Point weaponOffset = null;
	private Point frameSize = null;


	public MountView()
	{
		super();

		setBorderColor( null );
		setBackgroundColor( null );
		setBorderThickness( 2 );
	}

	private MountController getController()
	{
		return (MountController)controller;
	}

	@Override
	public void paintControl( PaintEvent e )
	{
		if ( alpha > 0 ) {
			paintImageCorner(
				e, image, cachedImageBounds, 0, 0, frameSize.x, frameSize.y,
				controller.getX() - weaponOffset.x,
				controller.getY() - weaponOffset.y,
				frameSize.x, frameSize.y, alpha
			);

			paintBorderSquare(
				e, controller.getX() - weaponOffset.x,
				controller.getY() - weaponOffset.y,
				frameSize.x, frameSize.y, borderColor, borderThickness, alpha
			);
		}
	}

	@Override
	public void updateView()
	{
		AnimationObject anim = getController().getWeapon().getAnimation();
		setImage( anim.getSheetPath() );
		weaponOffset = anim.getMountOffset();
		frameSize = anim.getFrameSize();

		if ( controller.isSelected() ) {
			setBorderColor( controller.isPinned() ? PIN_RGB : SELECT_RGB );
			setBackgroundColor( defaultBackground );
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
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}
}
