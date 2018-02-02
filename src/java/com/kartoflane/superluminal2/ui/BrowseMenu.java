package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


public class BrowseMenu extends Menu implements SelectionListener, Listener
{
	private final MenuItem mntmSystem;
	private final MenuItem mntmDatabase;


	public BrowseMenu( Control parent )
	{
		super( parent );

		mntmSystem = new MenuItem( this, SWT.NONE );
		mntmSystem.setText( "System" );
		mntmDatabase = new MenuItem( this, SWT.NONE );
		mntmDatabase.setText( "Database" );
	}

	public void addTo( Control c )
	{
		c.addListener( SWT.Selection, this );
	}

	public void removeFrom( Control c )
	{
		c.removeListener( SWT.Selection, this );
	}

	@Override
	protected void checkSubclass()
	{
	}

	public MenuItem getSystemItem()
	{
		return mntmSystem;
	}

	public MenuItem getDataItem()
	{
		return mntmDatabase;
	}

	public void addSystemListener( SelectionListener listener )
	{
		mntmSystem.addSelectionListener( listener );
	}

	public void removeSystemListener( SelectionListener listener )
	{
		mntmSystem.removeSelectionListener( listener );
	}

	public void addDataListener( SelectionListener listener )
	{
		mntmDatabase.addSelectionListener( listener );
	}

	public void removeDataListener( SelectionListener listener )
	{
		mntmDatabase.removeSelectionListener( listener );
	}

	public void handleEvent( Event e )
	{
		if ( e.type == SWT.Selection ) {
			widgetSelected( new SelectionEvent( e ) );
		}
	}

	public void widgetSelected( SelectionEvent e )
	{
		if ( e.getSource() instanceof Control == true ) {
			Control c = (Control)e.getSource();
			setLocation( c.toDisplay( 0, c.getSize().y ) );
			setVisible( true );
		}
	}

	public void widgetDefaultSelected( SelectionEvent e )
	{
	}
}
