package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.common.swt.ui.SquareColorPickerDialog;
import com.kartoflane.ftl.floorgen.FloorImageFactory;
import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.enums.OS;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.utils.UIUtils;
import com.kartoflane.superluminal2.utils.Utils;


public class FloorgenDialog
{
	private static FloorgenDialog instance = null;
	private static RGB currentBorder = null;
	private static RGB currentFloor = null;
	private static FloorImageFactory lastResult = new FloorImageFactory();
	private static Point lastPosition = null;

	private final Shell shell;
	private final Scale sclBorder;
	private final Scale sclMargin;
	private final Scale sclCorner;
	private final Label lblColorBorder;
	private final Label lblColorFloor;

	private FloorImageFactory result = lastResult;
	private boolean initd = false;


	public FloorgenDialog( final Shell parent )
	{
		if ( instance != null )
			throw new IllegalStateException( "Previous instance has not been disposed!" );
		instance = this;

		shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL );
		shell.setText( Superluminal.APP_NAME + " - Floor Image Generator" );
		GridLayout gl_shell = new GridLayout( 3, false );
		gl_shell.verticalSpacing = 15;
		shell.setLayout( gl_shell );

		Image imgInfo = Cache.checkOutImage( this, "cpath:/assets/help.png" );

		Composite composite = new Composite( shell, SWT.NONE );
		GridLayout gl_composite = new GridLayout( 5, false );
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout( gl_composite );
		composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 3, 1 ) );

		Label lblBorder = new Label( composite, SWT.NONE );
		lblBorder.setText( "Border Width" );

		sclBorder = new Scale( composite, SWT.NONE );
		sclBorder.setPageIncrement( 1 );
		sclBorder.setMaximum( 10 );
		sclBorder.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, true, false, 2, 1 ) );

		Label lblBorderInfo = new Label( composite, SWT.NONE );
		lblBorderInfo.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 2, 1 ) );
		lblBorderInfo.setImage( imgInfo );
		String msg = "This specifies the width of the border, in pixels.";
		UIUtils.addTooltip( lblBorderInfo, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

		Label lblMargin = new Label( composite, SWT.NONE );
		lblMargin.setText( "Border Margin" );

		sclMargin = new Scale( composite, SWT.NONE );
		sclMargin.setPageIncrement( 1 );
		sclMargin.setMaximum( 10 );
		sclMargin.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, true, false, 2, 1 ) );

		Label lblMarginInfo = new Label( composite, SWT.NONE );
		lblMarginInfo.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 2, 1 ) );
		lblMarginInfo.setImage( imgInfo );
		msg = "This specifies the distance from room walls to the border, in pixels. Basically, how much of the floor image should be visible.";
		UIUtils.addTooltip( lblMarginInfo, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

		Label lblCornerSize = new Label( composite, SWT.NONE );
		lblCornerSize.setText( "Corner Size" );

		sclCorner = new Scale( composite, SWT.NONE );
		sclCorner.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 2, 1 ) );
		sclCorner.setPageIncrement( 1 );
		sclCorner.setMaximum( Math.max( 1, result.getFloorMargin() ) );
		sclCorner.setEnabled( result.getFloorMargin() > 0 );

		Label lblCornerSizeInfo = new Label( composite, SWT.NONE );
		lblCornerSizeInfo.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 2, 1 ) );
		lblCornerSizeInfo.setImage( imgInfo );
		msg = "This specifies size of corners, in pixels. Maximum value depends on the current floor margin. Generally 0 means square, max - triangular.";
		UIUtils.addTooltip( lblCornerSizeInfo, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

		Label lblBorderColor = new Label( composite, SWT.NONE );
		lblBorderColor.setText( "Border Color" );

		Composite cColorBorder = new Composite( composite, SWT.NONE );
		cColorBorder.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1 ) );
		GridLayout gl_cColorBorder = new GridLayout( 2, false );
		gl_cColorBorder.marginWidth = 0;
		gl_cColorBorder.marginHeight = 0;
		cColorBorder.setLayout( gl_cColorBorder );

		lblColorBorder = new Label( cColorBorder, SWT.BORDER );
		lblColorBorder.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 1, 1 ) );
		lblColorBorder.setText( " " );

		Button btnSelectColorBorder = new Button( cColorBorder, SWT.NONE );
		GridData gd_btnSelectColorBorder = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
		gd_btnSelectColorBorder.widthHint = 80;
		btnSelectColorBorder.setLayoutData( gd_btnSelectColorBorder );
		btnSelectColorBorder.setText( "Select Color" );

		Label lblFloorColor = new Label( composite, SWT.NONE );
		lblFloorColor.setText( "Floor Color" );

		Composite cColorFloor = new Composite( composite, SWT.NONE );
		cColorFloor.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1 ) );
		GridLayout gl_cColorFloor = new GridLayout( 2, false );
		gl_cColorFloor.marginWidth = 0;
		gl_cColorFloor.marginHeight = 0;
		cColorFloor.setLayout( gl_cColorFloor );

		lblColorFloor = new Label( cColorFloor, SWT.BORDER );
		lblColorFloor.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 1, 1 ) );
		lblColorFloor.setText( " " );

		Button btnSelectColorFloor = new Button( cColorFloor, SWT.NONE );
		GridData gd_btnSelectColorFloor = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
		gd_btnSelectColorFloor.widthHint = 80;
		btnSelectColorFloor.setLayoutData( gd_btnSelectColorFloor );
		btnSelectColorFloor.setText( "Select Color" );

		Button btnDefault = new Button( shell, SWT.NONE );
		GridData gd_btnDefault = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
		gd_btnDefault.widthHint = 80;
		btnDefault.setLayoutData( gd_btnDefault );
		btnDefault.setText( "Defaults" );

		Button btnConfirm = new Button( shell, SWT.NONE );
		GridData gd_btnConfirm = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 );
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData( gd_btnConfirm );
		btnConfirm.setText( "Confirm" );

		Button btnCancel = new Button( shell, SWT.NONE );
		GridData gd_btnCancel = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData( gd_btnCancel );
		btnCancel.setText( "Cancel" );

		shell.pack();
		updateUI();
		btnConfirm.forceFocus();

		sclBorder.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					result.setBorderWidth( sclBorder.getSelection() );
				}
			}
		);

		sclMargin.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					result.setFloorMargin( sclMargin.getSelection() );
					sclCorner.setMaximum( Math.max( 1, result.getFloorMargin() ) );
					sclCorner.setEnabled( result.getFloorMargin() > 0 );
					result.setCornerSize( Math.max( 0, result.getFloorMargin() - sclCorner.getSelection() ) );
				}
			}
		);

		sclCorner.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					result.setCornerSize( Math.max( 0, result.getFloorMargin() - sclCorner.getSelection() ) );
				}
			}
		);

		btnCancel.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					result = null;
					dispose();
				}
			}
		);

		btnConfirm.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					lastResult = result;
					dispose();
				}
			}
		);

		btnDefault.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					result = new FloorImageFactory();
					updateUI();
				}
			}
		);

		btnSelectColorBorder.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					SquareColorPickerDialog cpd = new SquareColorPickerDialog( shell );
					cpd.setText( Superluminal.APP_NAME + " - Color Picker (Border Color)" );
					java.awt.Color c = result.getBorderColor();
					RGB input = new RGB( c.getRed(), c.getGreen(), c.getBlue() );
					RGB rgb = cpd.open( input );
					if ( rgb != null ) {
						java.awt.Color color = new java.awt.Color( rgb.red, rgb.green, rgb.blue );
						result.setBorderColor( color );
						updateUI();
					}
				}
			}
		);

		btnSelectColorFloor.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					SquareColorPickerDialog cpd = new SquareColorPickerDialog( shell );
					cpd.setText( Superluminal.APP_NAME + " - Color Picker (Floor Color)" );
					java.awt.Color c = result.getFloorColor();
					RGB input = new RGB( c.getRed(), c.getGreen(), c.getBlue() );
					RGB rgb = cpd.open( input );
					if ( rgb != null ) {
						java.awt.Color color = new java.awt.Color( rgb.red, rgb.green, rgb.blue );
						result.setFloorColor( color );
						updateUI();
					}
				}
			}
		);

		shell.addListener(
			SWT.Close, new Listener() {
				@Override
				public void handleEvent( Event e )
				{
					e.doit = false;
					result = null;
					dispose();
				}
			}
		);

		shell.addControlListener(
			new ControlAdapter() {
				@Override
				public void controlMoved( ControlEvent e )
				{
					if ( initd ) {
						lastPosition = shell.getLocation();
						lastPosition.x -= parent.getLocation().x;
						lastPosition.y -= parent.getLocation().y;
					}
				}
			}
		);

		if ( lastPosition != null ) {
			Point p = parent.getLocation();
			p.x += lastPosition.x;
			p.y += lastPosition.y;
			shell.setLocation( p );
		}
		else {
			Point size = shell.getSize();
			shell.setSize( size.x + 5, size.y );
			Point parSize = parent.getSize();
			Point parLoc = parent.getLocation();
			shell.setLocation( parLoc.x + parSize.x / 2 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2 );
		}

		initd = true;
	}

	public FloorImageFactory open()
	{
		shell.open();

		Display d = shell.getDisplay();
		while ( !shell.isDisposed() ) {
			if ( !d.readAndDispatch() )
				d.sleep();
		}

		return result;
	}

	public void dispose()
	{
		Cache.checkInColor( this, currentBorder );
		Cache.checkInColor( this, currentFloor );
		Cache.checkInImage( this, "cpath:/assets/help.png" );
		instance = null;
		shell.dispose();
	}

	private void updateUI()
	{
		sclBorder.setSelection( result.getBorderWidth() );
		sclMargin.setSelection( result.getFloorMargin() );
		sclCorner.setMaximum( Math.max( 1, result.getFloorMargin() ) );
		sclCorner.setEnabled( result.getFloorMargin() > 0 );
		sclCorner.setSelection( result.getFloorMargin() - result.getCornerSize() );

		if ( currentBorder != null )
			Cache.checkInColor( this, currentBorder );
		currentBorder = rgb( result.getBorderColor() );
		Color color = Cache.checkOutColor( this, currentBorder );
		lblColorBorder.setBackground( color );

		if ( currentFloor != null )
			Cache.checkInColor( this, currentFloor );
		currentFloor = rgb( result.getFloorColor() );
		color = Cache.checkOutColor( this, currentFloor );
		lblColorFloor.setBackground( color );
	}

	private RGB rgb( java.awt.Color color )
	{
		return new RGB( color.getRed(), color.getGreen(), color.getBlue() );
	}
}
