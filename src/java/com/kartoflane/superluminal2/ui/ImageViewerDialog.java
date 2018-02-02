package com.kartoflane.superluminal2.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.mvc.views.Preview;
import com.kartoflane.superluminal2.utils.IOUtils;
import com.kartoflane.superluminal2.utils.Utils;


public class ImageViewerDialog
{
	private static ImageViewerDialog instance = null;

	private Preview preview = null;
	private Shell shell = null;
	private Canvas canvas;
	private Button btnClose;
	private Button btnShow;


	public ImageViewerDialog( Shell parent )
	{
		if ( instance != null )
			throw new IllegalStateException( "Previous instance has not been disposed!" );
		instance = this;

		shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL );
		shell.setText( Superluminal.APP_NAME + " - Image Viewer" );
		shell.setLayout( new GridLayout( 2, false ) );

		canvas = new Canvas( shell, SWT.DOUBLE_BUFFERED );
		canvas.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );

		RGB rgb = canvas.getBackground().getRGB();
		preview = new Preview();
		preview.setBackgroundColor( (int)( 0.9 * rgb.red ), (int)( 0.9 * rgb.green ), (int)( 0.9 * rgb.blue ) );
		preview.setDrawBackground( true );
		canvas.addPaintListener( preview );

		canvas.addControlListener(
			new ControlListener() {
				@Override
				public void controlMoved( ControlEvent e )
				{
				}

				@Override
				public void controlResized( ControlEvent e )
				{
					updatePreview();
					canvas.redraw();
				}
			}
		);

		btnShow = new Button( shell, SWT.NONE );
		GridData gd_btnShow = new GridData( SWT.LEFT, SWT.CENTER, true, false, 1, 1 );
		gd_btnShow.widthHint = 80;
		btnShow.setLayoutData( gd_btnShow );
		btnShow.setText( "Show File" );

		btnClose = new Button( shell, SWT.NONE );
		GridData gd_btnClose = new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 );
		gd_btnClose.widthHint = 80;
		btnClose.setLayoutData( gd_btnClose );
		btnClose.setText( "Close" );

		btnShow.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					String path = preview.getImagePath();
					if ( path == null )
						return;

					File file = new File( IOUtils.trimProtocol( path ) );
					if ( file.exists() ) {
						if ( Desktop.isDesktopSupported() ) {
							Desktop desktop = Desktop.getDesktop();
							if ( desktop != null ) {
								try {
									desktop.open( file.getParentFile() );
								}
								catch ( IOException ex ) {
								}
							}
						}
						else {
							Superluminal.log.error( "Unable to open file location - AWT Desktop not supported." );
						}
					}
					else {
						Superluminal.log.error( String.format( "Unable to open file location - file could not be found: '%s'", path ) );
					}
				}
			}
		);

		btnClose.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					dispose();
				}
			}
		);

		shell.addListener(
			SWT.Close, new Listener() {
				@Override
				public void handleEvent( Event e )
				{
					btnClose.notifyListeners( SWT.Selection, null );
					e.doit = false;
				}
			}
		);

		shell.setMinimumSize( 200, 200 );
		shell.pack();
		Point size = shell.getSize();
		shell.setSize( size.x + 5, size.y );
		Point parSize = parent.getSize();
		Point parLoc = parent.getLocation();
		shell.setLocation( parLoc.x + parSize.x / 3 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2 );
	}

	public static ImageViewerDialog getInstance()
	{
		return instance;
	}

	private void updatePreview()
	{
		Point iSize = preview.getImageSize();
		Point cSize = canvas.getSize();

		double ratio = (double)iSize.y / iSize.x;
		int w = (int)( cSize.y / ratio );
		int h = (int)( cSize.x * ratio );
		preview.setSize( Utils.min( w, cSize.x, iSize.x ), Utils.min( h, cSize.y, iSize.y ) );
		preview.setLocation( cSize.x / 2, cSize.y / 2 );
	}

	public void open( String path )
	{
		preview.setImage( path == null ? "db:img/nullResource.png" : path );
		updatePreview();
		canvas.redraw();

		btnShow.setEnabled( IOUtils.getProtocol( path ).equals( "file:" ) );
		shell.open();
	}

	public void dispose()
	{
		preview.dispose();
		shell.dispose();
		instance = null;
	}

	public boolean isVisible()
	{
		return shell.isVisible();
	}
}
