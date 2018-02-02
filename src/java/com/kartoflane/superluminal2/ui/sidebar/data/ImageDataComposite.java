package com.kartoflane.superluminal2.ui.sidebar.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.components.enums.OS;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.ImageController;
import com.kartoflane.superluminal2.utils.UIUtils;
import com.kartoflane.superluminal2.utils.Utils;


public class ImageDataComposite extends Composite implements DataComposite
{
	private ImageController controller = null;
	private Label label = null;
	private Button btnFollowHull;
	private Label lblFollowHelp;


	public ImageDataComposite( Composite parent, ImageController control )
	{
		super( parent, SWT.NONE );
		setLayout( new GridLayout( 2, false ) );

		controller = control;

		label = new Label( this, SWT.NONE );
		label.setAlignment( SWT.CENTER );
		label.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
		String alias = control.getAlias();
		label.setText( "Image" + ( alias == null ? "" : " (" + alias + ")" ) );

		Label separator = new Label( this, SWT.SEPARATOR | SWT.HORIZONTAL );
		separator.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );

		btnFollowHull = new Button( this, SWT.CHECK );
		btnFollowHull.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, true, false, 1, 1 ) );
		btnFollowHull.setText( "Follow Hull" );

		lblFollowHelp = new Label( this, SWT.NONE );
		lblFollowHelp.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
		lblFollowHelp.setImage( Cache.checkOutImage( this, "cpath:/assets/help.png" ) );
		String msg = "When checked, this object will follow the hull image, so that " +
			"when hull is moved, this object is moved as well.";
		UIUtils.addTooltip( lblFollowHelp, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

		btnFollowHull.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					if ( btnFollowHull.getSelection() ) {
						controller.setParent( Manager.getCurrentShip().getImageController( Images.HULL ) );
					}
					else {
						controller.setParent( Manager.getCurrentShip().getShipController() );
					}
					controller.updateFollowOffset();
				}
			}
		);

		updateData();
	}

	@Override
	public void updateData()
	{
		String alias = controller.getAlias();
		label.setText( "Image" + ( alias == null ? "" : " (" + alias + ")" ) );

		ImageController hullController = Manager.getCurrentShip().getImageController( Images.HULL );
		btnFollowHull.setVisible( controller != hullController );
		btnFollowHull.setSelection( controller.getParent() == hullController );
		lblFollowHelp.setVisible( controller != hullController );
	}

	@Override
	public void setController( AbstractController controller )
	{
		this.controller = (ImageController)controller;
	}

	public void reloadController()
	{
	}

	@Override
	public void dispose()
	{
		Cache.checkInImage( this, "cpath:/assets/help.png" );
		super.dispose();
	}
}
